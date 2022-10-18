package repository;

import domain.User;
import exceptions.RepositoryException;

import java.util.ArrayList;
import java.util.List;

public class UsersMemoryRepository implements UsersRepository {
    protected List<User> users;

    public UsersMemoryRepository() {
        users = new ArrayList<>();
    }

    @Override
    public int size() {
        return users.size();
    }

    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public void addUser(User user) throws RepositoryException {
        for (User u : users) {
            if (u.equals(user)) {
                throw new RepositoryException("User already exists!\n");
            }
        }
        users.add(user);
    }

    @Override
    public void removeUser(User user) throws RepositoryException {
        for (User u : users) {
            if (u.equals(user)) {
                users.remove(user);
                return;
            }
        }
        throw new RepositoryException("User does not exist!\n");
    }
}