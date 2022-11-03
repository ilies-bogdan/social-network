package service;

import domain.Friendship;
import domain.User;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import repository.Repository;
import utils.Graph;
import domain.validators.Validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Network {
    private Repository<User, String> usersRepo;
    private final Validator<User> userVal;
    private Repository<Friendship, Set<User>> friendshipsRepo;

    // private static final UsersService usersSrv = new UsersService();

    public Network(Repository<User, String> usersRepo, Validator<User> userVal, Repository<Friendship, Set<User>> friendshipsRepo) {
        this.usersRepo = usersRepo;
        this.userVal = userVal;
        this.friendshipsRepo = friendshipsRepo;
    }

//    /**
//     * Singleton Design Pattern
//     * @param usersRepo - A repository that the service has access to
//     * @return a single instance of UserService.
//     */
//    public static UsersService getInstance(Repository<User, String> usersRepo) {
//        this.usersRepo = usersRepo;
//        return usersSrv;
//    }

    /**
     * Gets the size.
     *
     * @return number of users in the service.
     */
    public int usersSize() {
        return usersRepo.size();
    }

    /**
     * Gets all users in the service.
     *
     * @return a list of all the users.
     */
    public List<User> getAllUsers() {
        return usersRepo.getAll();
    }

    /**
     * Creates, validates and stores a User.
     *
     * @param username - String, can't be null
     * @param password - String, can't be null
     * @param email    - String, can't be null
     * @throws RepositoryException if the user has already been added.
     * @throws ValidationException if any of the user attributes are empty or if the Password is too short.
     */
    public void addUser(String username, String password, String email) throws RepositoryException, ValidationException {
        User user = new User(username, password, email);
        userVal.validate(user);
        usersRepo.add(user);
    }

    /**
     * Finds and removes a User and all of its related Friendships.
     *
     * @param username - String, can't be null
     * @throws RepositoryException if the user does not exist.
     */
    public void removeUser(String username) throws RepositoryException {
        User user = usersRepo.find(username);
        for (User friend : user.getFriends()) {
            Friendship friendship = new Friendship(user, friend);
            friendshipsRepo.remove(friendship);
        }
        usersRepo.remove(user);
    }

    public void updateUser(String username, String newPassword, String newEmail) throws RepositoryException {
        User user = usersRepo.find(username);
        if (newPassword == null || newPassword.trim().length() == 0) {
            newPassword = user.getPassword();
        }
        if (newEmail == null || newEmail.trim().length() == 0) {
            newEmail = user.getEmail();
        }
        usersRepo.update(new User(username, newPassword, newEmail));
    }

    /**
     * Gets all the friendships in the service.
     *
     * @return a list of all the friendships.
     */
    public List<Friendship> getAllFriendships() {
        return friendshipsRepo.getAll();
    }

    /**
     * Creates and stores a Friendship between two Users.
     *
     * @param username1 - The first new friend
     * @param username2 - The second new friend
     * @throws RepositoryException if either of the two users has not been found.
     */
    public void addFriendship(String username1, String username2) throws RepositoryException {
        User user1 = usersRepo.find(username1);
        User user2 = usersRepo.find(username2);
        Friendship friendship = new Friendship(user1, user2);
        friendshipsRepo.add(friendship);
    }

    /**
     * Removes a friendship.
     *
     * @param username1 - The first former friend
     * @param username2 - The second former friend
     * @throws RepositoryException if either of the two users has not been found.
     */
    public void removeFriendship(String username1, String username2) throws RepositoryException {
        User user1 = usersRepo.find(username1);
        User user2 = usersRepo.find(username2);
        Friendship friendship = new Friendship(user1, user2);
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

    private void mapNetworkToGraph(int[][] adj, int vertexCount) {
        List<User> users = usersRepo.getAll();
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                try {
                    Set<User> friendshipID = new HashSet<>();
                    friendshipID.add(users.get(i));
                    friendshipID.add(users.get(j));
                    friendshipsRepo.find(friendshipID);
                    // If find is successfull, update the adjacency matrix with 1.
                    adj[i][j] = 1;
                    adj[j][i] = 1;
                } catch (RepositoryException exception) {continue;}
            }
        }
    }

    /**
     * Maps the Network to an undirected graph, so that the number of communities in the
     * Network is equal to the number of connected components in the graph and then
     * computes that number.
     * @return the number of communities.
     */
    public int getNumberOfCommunities() {
        List<User> users = usersRepo.getAll();
        int vertexCount = users.size();

        int[][] adj = new int[vertexCount][vertexCount];
        initAdjacencyMatrix(adj,vertexCount);
        boolean[] visited = new boolean[vertexCount];
        initBooleanWithFalse(visited, vertexCount);

        mapNetworkToGraph(adj, vertexCount);

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
        int vertexCount = users.size();

        int[][] adj = new int[vertexCount][vertexCount];
        initAdjacencyMatrix(adj, vertexCount);
        boolean[] visited = new boolean[vertexCount];
        initBooleanWithFalse(visited, vertexCount);

        mapNetworkToGraph(adj, vertexCount);

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
