package repository.factory;

import domain.Entity;
import repository.Repository;

public interface RepositoryFactory<E extends Entity<ID>, ID> {
    /**
     * Creates a Repository with the given strategy.
     * @param strategy - The strategy of the repository
     * @param fileName - The name of the file if the strategy is "file"
     * @param url - The URL of the database if the strategy is "database"
     * @param username - The username of the databse if the strategy is "databse"
     * @param password - The password of the databse if the strategy is "databse"
     * @return the created repository.
     */
    Repository<E, ID> createRepository(RepositoryStrategy strategy, String fileName, String url, String username, String password);
}
