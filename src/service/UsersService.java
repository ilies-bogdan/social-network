package service;

import domain.User;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import repository.Repository;
import validators.Validator;

import java.util.List;

public class UsersService {
    private Repository<User, String> usersRepo;
    private final Validator<User> userVal;

    // private static final UsersService usersSrv = new UsersService();

    public UsersService(Repository<User, String> usersRepo, Validator<User> userVal) {
        this.usersRepo = usersRepo;
        this.userVal = userVal;
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
     * @return number of entries in the service.
     */
    public int size() {
        return usersRepo.size();
    }

    /**
     * Gets all entries in the service.
     * @return a list of all the entries.
     */
    public List<User> getAll() {
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
     * Finds and removes a User.
     * @param username - String, can't be null
     * @throws RepositoryException if the user does not exist.
     */
    public void removeUser(String username) throws RepositoryException {
        User user = usersRepo.find(username);
        usersRepo.remove(user);
    }
}
