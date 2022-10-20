package service;

import domain.Friendship;
import domain.User;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import repository.Repository;
import validators.Validator;

import java.util.List;
import java.util.Set;

public class ApplicationService {
    private Repository<User, String> usersRepo;
    private final Validator<User> userVal;
    private Repository<Friendship, Set<String>> friendshipsRepo;

    // private static final UsersService usersSrv = new UsersService();

    public ApplicationService(Repository<User, String> usersRepo, Validator<User> userVal, Repository<Friendship, Set<String>> friendshipsRepo) {
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
     * @param email - String, can't be null
     * @throws RepositoryException if the user has already been added.
     * @throws ValidationException if any of the user attributes are empty.
     */
    public void addUser(String username, String password, String email) throws RepositoryException, ValidationException {
        User user = new User(username, password, email);
        userVal.validate(user);
        usersRepo.add(user);
    }

    /**
     * Finds and removes a User and all of its related Friendships.
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
        User user1 = usersRepo.find(username1);
        User user2 = usersRepo.find(username2);
        Friendship friendship = new Friendship(user1, user2);
        friendshipsRepo.add(friendship);
        user1.addFriend(user2);
        user2.addFriend(user1);
    }

    /**
     * Removes a friendship.
     * @param username1 - The first former friend
     * @param username2 - The second former friend
     * @throws RepositoryException if either of the two users has not been found.
     */
    public void removeFriendship(String username1, String username2) throws RepositoryException {
        User user1 = usersRepo.find(username1);
        User user2 = usersRepo.find(username2);
        Friendship friendship = new Friendship(user1, user2);
        friendshipsRepo.remove(friendship);
        user1.removeFriend(user2);
        user2.removeFriend(user1);
    }
}
