package repository;

import domain.Entity;
import exceptions.RepositoryException;

import java.util.List;

public interface Repository<E extends Entity<ID>, ID> {
    int size();
    List<E> getAll();
    void add(E entity) throws RepositoryException;
    void remove(E entity) throws RepositoryException;
    E find(ID id) throws RepositoryException;
    void update(E entity) throws RepositoryException;
}
