package com.socialnetwork.repository;

import com.socialnetwork.domain.Entity;
import com.socialnetwork.exceptions.RepositoryException;

import java.util.List;

public interface Repository<E extends Entity<ID>, ID> {
    /**
     * Gets the size of the repository.
     * @return the number of entitites in the repository.
     */
    int size();

    /**
     * Gets all the entities in the repository.
     * @return all the entities.
     */
    List<E> getAll();

    /**
     * Adds an entity to the repository.
     * @param entity - The entitity to be added
     * @throws RepositoryException if the entity has already been added.
     */
    void add(E entity) throws RepositoryException;

    /**
     * Removes an entity from the repository.
     * @param entity - The entity to be removed
     * @throws RepositoryException if the entity has not yet been added.
     */
    void remove(E entity) throws RepositoryException;

    /**
     * Finds an entity in the repository by its ID.
     * @param id - The ID by which to find the entity
     * @return the entity with the given ID.
     * @throws RepositoryException if the entity has not yet been added.
     */
    E find(ID id) throws RepositoryException;

    /**
     * Updates an entity in the repository.
     * @param entity - The entity to be updated
     * @throws RepositoryException if the entity has not yet been added.
     */
    void update(E entity) throws RepositoryException;
}