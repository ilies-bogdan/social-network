package service;

import domain.Friendship;
import domain.User;
import domain.validators.UserValidator;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import repository.Repository;
import utils.Constants;
import utils.Graph;
import domain.validators.Validator;
import utils.RandomString;

import java.time.LocalDateTime;
import java.util.*;

public class NetworkService {
    private Repository<User, Long> usersRepo;
    private UserValidator userVal;
    private Repository<Friendship, Set<User>> friendshipsRepo;

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
        user.setID(Math.abs((long) Objects.hash(username)));
        userVal.validate(user);
        userVal.validatePassword(password);
        usersRepo.add(user);
    }

    /**
     * Finds and removes a User and all of its related Friendships.
     * @param username - String, can't be null
     * @throws RepositoryException if the user does not exist.
     */
    public void removeUser(String username) throws RepositoryException {
        User user = usersRepo.find(Math.abs((long) Objects.hash(username)));
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
        User user = usersRepo.find(Math.abs((long) Objects.hash(username)));
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

        // Update Friendships.
        for (int i = 0; i < friendshipsRepo.size(); i++) {
            Friendship friendship = friendshipsRepo.getAll().get(i);
            if (friendship.getU1().equals(newUser)) {
                friendshipsRepo.update(new Friendship(newUser, friendship.getU2(), friendship.getFriendsFrom()));
            }
            if (friendship.getU2().equals(newUser)) {
                friendshipsRepo.update(new Friendship(friendship.getU1(), newUser, friendship.getFriendsFrom()));
            }
        }
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
     * @throws RepositoryException if either of the two users has not been found.
     */
    public void addFriendship(String username1, String username2) throws RepositoryException {
        User user1 = usersRepo.find(Math.abs((long) Objects.hash(username1)));
        User user2 = usersRepo.find(Math.abs((long) Objects.hash(username2)));
        Friendship friendship = new Friendship(user1, user2, LocalDateTime.now());
        friendshipsRepo.add(friendship);
    }

    /**
     * Removes a friendship.
     * @param username1 - The first former friend
     * @param username2 - The second former friend
     * @throws RepositoryException if either of the two users has not been found.
     */
    public void removeFriendship(String username1, String username2) throws RepositoryException {
        User user1 = usersRepo.find(Math.abs((long) Objects.hash(username1)));
        User user2 = usersRepo.find(Math.abs((long) Objects.hash(username2)));
        Friendship friendship = new Friendship(user1, user2, null);
        friendshipsRepo.remove(friendship);
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
}
