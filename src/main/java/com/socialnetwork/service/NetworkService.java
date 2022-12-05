package com.socialnetwork.service;

import com.socialnetwork.domain.Friendship;
import com.socialnetwork.domain.FriendshipStatus;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.dto.FriendshipDto;
import com.socialnetwork.domain.validators.UserValidator;
import com.socialnetwork.exceptions.RepositoryException;
import com.socialnetwork.exceptions.ValidationException;
import com.socialnetwork.repository.Repository;
import com.socialnetwork.utils.Constants;
import com.socialnetwork.utils.Graph;
import com.socialnetwork.utils.RandomString;
import com.socialnetwork.utils.observer.Observable;
import com.socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.*;

public class NetworkService implements Observable {
    private Repository<User, Long> usersRepo;
    private UserValidator userVal;
    private Repository<Friendship, Set<User>> friendshipsRepo;
    private List<Observer> observers;

    private static final NetworkService network = new NetworkService();

    private NetworkService() {}

    /**
     * Singleton Design Pattern
     * @return a single instance of NetworkService.
     */
    public static NetworkService getInstance() {
        return network;
    }

    public void initialize(Repository<User, Long> usersRepo, UserValidator userVal, Repository<Friendship, Set<User>> friendshipsRepo) {
        network.usersRepo = usersRepo;
        network.userVal = userVal;
        network.friendshipsRepo = friendshipsRepo;
        network.observers = new ArrayList<>();
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyAllObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    private long getUserIDFromUsername(String username) {
        return Math.abs((long) Objects.hash(username));
    }

    /**
     * Gets the size.
     * @return number of users in the service.
     */
    public int usersSize() {
        return usersRepo.size();
    }

    /**
     * Gets all users in the service.
     * @return a list of all the users.
     */
    public List<User> getAllUsers() {
        return usersRepo.getAll();
    }

    /**
     * Creates, validates and stores a User.
     * @param username - String, can't be null
     * @param password - String, can't be null
     * @param email    - String, can't be null
     * @throws RepositoryException if the user has already been added.
     * @throws ValidationException if any of the user attributes are empty or if the Password is too short.
     */
    public void addUser(String username, String password, String email) throws RepositoryException, ValidationException {
        String salt = RandomString.getRandomString(Constants.SALT_SIZE);
        int passwordCode = Objects.hash(password + salt);
        // To verify for password: Objects.hash(newPassword + user.getSalt())
        User user = new User(username, passwordCode, salt, email);
        user.setID(getUserIDFromUsername(username));
        userVal.validate(user);
        userVal.validatePassword(password);
        usersRepo.add(user);

        notifyAllObservers();
    }

    /**
     * Finds and removes a User and all of its related Friendships.
     * @param username - String, can't be null
     * @throws RepositoryException if the user does not exist.
     */
    public void removeUser(String username) throws RepositoryException {
        User user = usersRepo.find(getUserIDFromUsername(username));
        // Delete all Friendships of the User.
        List<Friendship> userFriendships = new ArrayList<>();
        for (Friendship friendship : friendshipsRepo.getAll()) {
            if (friendship.getU1().equals(user) || friendship.getU2().equals(user)) {
                // Add them to a list so that the iterator doesn't get confused when deleting.
                userFriendships.add(friendship);
            }
        }

        if (!userFriendships.isEmpty()) {
            for (Friendship friendship : userFriendships) {
                friendshipsRepo.remove(friendship);
            }
        }
        usersRepo.remove(user);

        notifyAllObservers();
    }

    /**
     * Updates a User's information.
     * @param username - The username of the User
     * @param newPassword - The new password (if empty keeps the old one)
     * @param newEmail - The new email (if empty keeps the old one)
     * @throws ValidationException if the new attributes are invalid.
     * @throws RepositoryException if the User does not exist.
     */
    public void updateUser(String username, String newPassword, String newEmail) throws ValidationException, RepositoryException {
        User user = usersRepo.find(getUserIDFromUsername(username));
        String salt = RandomString.getRandomString(Constants.SALT_SIZE);
        int passwordCode = Objects.hash(newPassword + salt);
        if (newPassword == null || newPassword.trim().length() == 0) {
            salt = user.getSalt();
            passwordCode = user.getPasswordCode();
        }
        if (newEmail == null || newEmail.trim().length() == 0) {
            newEmail = user.getEmail();
        }
        User newUser = new User(username, passwordCode, salt, newEmail);
        newUser.setID(user.getID());
        userVal.validate(newUser);
        userVal.validatePassword(newPassword);
        usersRepo.update(newUser);

        // Update friendships.
        // NOTE: not necessary for database repository.
        for (int i = 0; i < friendshipsRepo.size(); i++) {
            Friendship friendship = friendshipsRepo.getAll().get(i);
            if (friendship.getU1().equals(newUser)) {
                friendshipsRepo.update(new Friendship(newUser, friendship.getU2(), friendship.getFriendsFrom(), friendship.getStatus()));
            }
            if (friendship.getU2().equals(newUser)) {
                friendshipsRepo.update(new Friendship(friendship.getU1(), newUser, friendship.getFriendsFrom(), friendship.getStatus()));
            }
        }

        notifyAllObservers();
    }

    /**
     * Gets all the friendships in the service.
     * @return a list of all the friendships.
     */
    public List<Friendship> getAllFriendships() {
        return friendshipsRepo.getAll();
    }

    /**
     * Creates and stores a Friendship between two Users.
     * @param username1 - The first new friend
     * @param username2 - The second new friend
     * @param status - The status of the friendship
     * @throws RepositoryException if either of the two users has not been found.
     */
    public void addFriendship(String username1, String username2, FriendshipStatus status) throws RepositoryException {
        User user1 = usersRepo.find(getUserIDFromUsername(username1));
        User user2 = usersRepo.find(getUserIDFromUsername(username2));
        Friendship friendship = new Friendship(user1, user2, LocalDateTime.now(), status);
        friendshipsRepo.add(friendship);

        notifyAllObservers();
    }

    /**
     * Removes a friendship.
     * @param username1 - The first former friend
     * @param username2 - The second former friend
     * @throws RepositoryException if either of the two users has not been found.
     */
    public void removeFriendship(String username1, String username2) throws RepositoryException {
        User user1 = usersRepo.find(getUserIDFromUsername(username1));
        User user2 = usersRepo.find(getUserIDFromUsername(username2));
        Friendship friendship = new Friendship(user1, user2, null, null);
        friendshipsRepo.remove(friendship);

        notifyAllObservers();
    }

    /**
     * Updates a friendship.
     * @param username1 - The first friend
     * @param username2 - The second friend
     * @param friendsFrom - The timestamp from when the friendship was established
     * @param status - The status of the friendship
     * @throws RepositoryException if the friendship does not exist.
     */
    public void updateFriendship(String username1, String username2, LocalDateTime friendsFrom, FriendshipStatus status) throws RepositoryException {
        User user1 = usersRepo.find(getUserIDFromUsername(username1));
        User user2 = usersRepo.find(getUserIDFromUsername(username2));
        Friendship friendship = new Friendship(user1, user2, friendsFrom, status);
        friendshipsRepo.update(friendship);

        notifyAllObservers();
    }

    /**
     * Initialize adjacency matrix with 0.
     * @param adj - The adjacency matrix
     * @param vertexCount - The number of vertexes
     */
    private void initAdjacencyMatrix(int[][] adj, int vertexCount) {
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                adj[i][j] = 0;
            }
        }
    }

    /**
     * Initialize a boolean vector with false.
     * @param vector - Boolean vector
     * @param size - Number of elements in the vector
     */
    private void initBooleanWithFalse(boolean[] vector, int size) {
        for (int i = 0; i < size; i++) {
            vector[i] = false;
        }
    }

    /**
     * Maps the Network to an undirected graph, so that the number of communities in the
     * Network is equal to the number of connected components in the graph and then
     * computes that number.
     * @return the adjacency matrix of the graph.
     */
    private int[][] mapNetworkToGraph() {
        int vertexCount = usersRepo.size();
        int[][] adj = new int[vertexCount][vertexCount];
        initAdjacencyMatrix(adj,vertexCount);
        List<User> users = usersRepo.getAll();
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                try {
                    if (i != j) {
                        Set<User> friendshipID = new HashSet<>();
                        friendshipID.add(users.get(i));
                        friendshipID.add(users.get(j));
                        friendshipsRepo.find(friendshipID);
                        // If find is successful, update the adjacency matrix with 1.
                        adj[i][j] = 1;
                        adj[j][i] = 1;
                    }
                } catch (RepositoryException ignored) {}
            }
        }
        return adj;
    }

    /**
     * Gets the number of communities in the Network
     * (number of connected components in the graph).
     * @return the number of communities.
     */
    public int getNumberOfCommunities() {
        int vertexCount = usersRepo.size();

        int[][] adj = mapNetworkToGraph();
        boolean[] visited = new boolean[vertexCount];
        initBooleanWithFalse(visited, vertexCount);

        // Find number of connected components.
        int communitiesCount = 0;
        for (int i = 0; i < vertexCount; i++) {
            if (!visited[i]) {
                Graph.dfs(adj, visited, vertexCount, i);
                communitiesCount++;
            }
        }
        return communitiesCount;
    }

    /**
     * Gets the most sociable community in the Network
     * (connected component with the longest path)
     * @return a User list containing the Users that make up
     * the most sociable community
     */
    public List<User> mostSociableCommunity() {
        List<User> users = usersRepo.getAll();
        int vertexCount = usersRepo.size();

        int[][] adj = mapNetworkToGraph();
        boolean[] visited = new boolean[vertexCount];
        initBooleanWithFalse(visited, vertexCount);

        boolean[] startedBFSFrom = new boolean[vertexCount];
        initBooleanWithFalse(startedBFSFrom, vertexCount);

        List<User> result = new ArrayList<>();
        int maxLength = 0;
        for (int i = 0; i < vertexCount; i++) {
            if (!visited[i]) {
                // Find each connected component.
                Graph.dfs(adj, visited, vertexCount, i);

                // For each connected component, do a BFS from each
                // of the vertexes to find the longest path between
                // two vertexes.
                for (int j = 0; j < vertexCount; j++) {
                    if (visited[j] && !startedBFSFrom[j]) {
                        // Mark vertexes which already were the starting
                        // points for BFS.
                        startedBFSFrom[j] = true;

                        boolean[] visitedDuringBFS = new boolean[vertexCount];
                        initBooleanWithFalse(visitedDuringBFS, vertexCount);

                        int localLength = Graph.bfs(adj, visitedDuringBFS, vertexCount, j);

                        if (localLength > maxLength) { // New longest path has been found.
                            maxLength = localLength;

                            // Store in the result the users that make up
                            // the connected component with the current
                            // longest path.
                            result.clear();
                            for (int k = 0; k < vertexCount; k++) {
                                if (visitedDuringBFS[k]) {
                                    result.add(users.get(k));
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Handles a log in request from a user.
     * @param username - The user's username
     * @param password - The user's password
     * @return the user if the request is accepted, null otherwise.
     * @throws RepositoryException if the user with the given username does not exist.
     */
    public User handleLogInRequest(String username, String password) throws RepositoryException {
        User user = usersRepo.find(getUserIDFromUsername(username));
        if (user.getPasswordCode() ==  Objects.hash(password + user.getSalt())) {
            return user;
        }
        return null;
    }

    /**
     * Gets a user's friends.
     * @param user - The user
     * @return a list of the user's friends.
     */
    public List<User> getFriends(User user) {
        List<User> friends = new ArrayList<>();
        for (Friendship friendship : friendshipsRepo.getAll()) {
            if (friendship.getStatus().equals(FriendshipStatus.accepted)) { // Ignore the pending requests.
                if (friendship.getU1().equals(user)) {
                    friends.add(friendship.getU2());
                } else if (friendship.getU2().equals(user)) {
                    friends.add(friendship.getU1());
                }
            }
        }
        return friends;
    }

    /**
     * Gets a user's friendships.
     * @param user - The user
     * @return a list of the user's friendships.
     */
    public List<FriendshipDto> getFriendships(User user) {
        List<FriendshipDto> friendships = new ArrayList<>();

        for (Friendship friendship : friendshipsRepo.getAll()) {
            if (friendship.getStatus().equals(FriendshipStatus.accepted)) {
                if (friendship.getU1().equals(user)) {
                    friendships.add(new FriendshipDto(friendship.getU2().getUsername(), friendship.getFriendsFrom(), friendship.getStatus()));
                } else if (friendship.getU2().equals(user)) {
                    friendships.add(new FriendshipDto(friendship.getU1().getUsername(), friendship.getFriendsFrom(), friendship.getStatus()));
                }
            } else if (friendship.getStatus().equals(FriendshipStatus.sent) &&
            friendship.getU1().equals(user)) { // If the user has sent a friend request to someone.
                friendships.add(new FriendshipDto(friendship.getU2().getUsername(), friendship.getFriendsFrom(), friendship.getStatus()));
            }
        }

        return friendships;
    }

    /**
     * Gets a user's friend requests
     * @param user - The user
     * @return a list of the user's friend requests.
     */
    public List<FriendshipDto> getFriendRequests(User user) {
        List<FriendshipDto> friendRequests = new ArrayList<>();
        for (Friendship friendship : friendshipsRepo.getAll()) {
            if (friendship.getStatus().equals(FriendshipStatus.sent)) {
                 if(friendship.getU2().equals(user)) {
                     friendRequests.add(new FriendshipDto(friendship.getU1().getUsername(), friendship.getFriendsFrom(), FriendshipStatus.received));
                }
            }
        }
        return friendRequests;
    }

    /**
     * Adds a friend for a user.
     * @param user - The user who adds the friend
     * @param friendUsername - The username of the added friend
     * @throws RepositoryException if the user with the given username is not found.
     */
    public void addFriend(User user, String friendUsername) throws RepositoryException {
        User friend = usersRepo.find(getUserIDFromUsername(friendUsername));

        for (Friendship friendship : friendshipsRepo.getAll()) {
            if (friendship.getU1().equals(user) && friendship.getU2().equals(friend) ||
            friendship.getU1().equals(friend) && friendship.getU2().equals(user)) {
                throw new RepositoryException("Already friends or a friend request has already been sent!");
            }
        }

        friendshipsRepo.add(new Friendship(user, friend, LocalDateTime.now(), FriendshipStatus.sent));
        notifyAllObservers();
    }

    /**
     * Removes a friend for a user.
     * @param user - The user who removes the friend
     * @param friendUsername - The username of the removed friend
     * @throws RepositoryException if the user with the given username is not found.
     */
    public void removeFriend(User user, String friendUsername) throws RepositoryException {
        User friend = usersRepo.find(getUserIDFromUsername(friendUsername));
        friendshipsRepo.remove(new Friendship(user, friend, null, null));

        notifyAllObservers();
    }

    /**
     * Accepts a friend request from the user with the given username.
     * @param user - The user who accepts
     * @param friendUsername - The user who requested
     * @throws RepositoryException if either of the users is not found.
     */
    public void acceptFriendRequest(User user, String friendUsername) throws RepositoryException {
        User friend = usersRepo.find(getUserIDFromUsername(friendUsername));
        friendshipsRepo.update(new Friendship(user, friend, LocalDateTime.now(), FriendshipStatus.accepted));

        notifyAllObservers();
    }

    /**
     * Rejects a friend request from the user with the given username.
     * @param user - The user who rejects
     * @param friendUsername - The user who requested
     * @throws RepositoryException if either of the users is not found.
     */
    public void rejectFriendRequest(User user, String friendUsername) throws RepositoryException {
        User friend = usersRepo.find(getUserIDFromUsername(friendUsername));
        friendshipsRepo.remove(new Friendship(user, friend, null, null));

        notifyAllObservers();
    }
}