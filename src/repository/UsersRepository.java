package repository;

import domain.User;
import exceptions.RepositoryException;

import java.util.List;

public interface UsersRepository {
    int size();
    List<User> getAll();
    void addUser(User user) throws RepositoryException;
    void removeUser(User user) throws RepositoryException;
}
