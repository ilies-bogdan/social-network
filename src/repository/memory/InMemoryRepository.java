package repository.memory;

import domain.HasID;
import exceptions.RepositoryException;
import repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryRepository<E extends HasID<ID>, ID> implements Repository<E, ID> {
    protected List<E> entities;

    public InMemoryRepository() {
        entities = new ArrayList<>();
    }

    /**
     * Gets the size of the repository.
     * @return the size.
     */
    @Override
    public int size() {
        return entities.size();
    }

    /**
     * Gets all entries.
     * @return an Entity List.
     */
    @Override
    public List<E> getAll() {
        return entities;
    }

    /**
     * Adds an Entity to the repository.
     * @param entity - The Entity to be added
     * @throws RepositoryException if the Entiry already is in the repository.
     */
    @Override
    public void add(E entity) throws RepositoryException {
        for (E e : entities) {
            if (e.equals(entity)) {
                throw new RepositoryException("Entity already exists!\n");
            }
        }
        entities.add(entity);
    }

    /**
     * Removes an Entity from the repository.
     * @param entity - The Entity to be removed
     * @throws RepositoryException if the Entity is not in the repository.
     */
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
     * @return the user if it was found.
     * @throws RepositoryException if the user has not been found.
     */
    @Override
    public E find(ID id) throws RepositoryException {
        for (E e : entities) {
            if (e.getID().equals(id)) {
                return e;
            }
        }
        throw new RepositoryException("Entity not found!\n");
    }
}