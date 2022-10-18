package repository;

import domain.HasID;
import exceptions.RepositoryException;

import java.util.ArrayList;
import java.util.List;

public class InMemoryRepository<E extends HasID<ID>, ID> implements Repository<E, ID> {
    protected List<E> entities;

    public InMemoryRepository() {
        entities = new ArrayList<>();
    }

    @Override
    public int size() {
        return entities.size();
    }

    @Override
    public List<E> getAll() {
        return entities;
    }

    @Override
    public void add(E entity) throws RepositoryException {
        for (E e : entities) {
            if (e.equals(entity)) {
                throw new RepositoryException("Entity already exists!\n");
            }
        }
        entities.add(entity);
    }

    @Override
    public void remove(E entity) throws RepositoryException {
        for (E e : entities) {
            if (e.equals(entity)) {
                entities.remove(entity);
                return;
            }
        }
        throw new RepositoryException("Entity does not exist!\n");
    }

    /**
     * Finds a user by username.
     * @param id - The ID being looked for
     * @return the user if it was found or null otherwise.
     */
    @Override
    public E find(ID id) {
        for (E e : entities) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }
}