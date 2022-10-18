package service;

import domain.User;
import exceptions.RepositoryException;
import repository.UsersRepository;

import java.util.List;

public class UsersService {
    private UsersRepository usersRepo;

    private static final UsersService usersSrv = new UsersService();

    private UsersService() {}

    /**
     * Singleton Design Pattern
     * @param usersRepo - A repository that the service has access to
     * @return a single instance of UserService.
     */
    public UsersService getInstance(UsersRepository usersRepo) {
        this.usersRepo = usersRepo;
        return usersSrv;
    }

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

    public void addUser(String username, String password, String email) throws RepositoryException {
        User user = new User(username, password, email);
        usersRepo.addUser(user);
    }

    public void removeUser(String username) throws RepositoryException {
        User user = usersRepo.findUser(username);
        if (user.getUsername() != null) {
            usersRepo.removeUser(user);
        }
    }
}
