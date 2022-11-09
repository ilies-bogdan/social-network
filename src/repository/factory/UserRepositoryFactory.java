package repository.factory;

import domain.User;
import repository.Repository;
import repository.database.UserDBRepository;
import repository.file.UserFileRepository;
import repository.memory.InMemoryRepository;

public class UserRepositoryFactory implements RepositoryFactory<User, Long> {
    private static final UserRepositoryFactory userRepositoryFactory = new UserRepositoryFactory();

    private UserRepositoryFactory() {}

    public static UserRepositoryFactory getInstance() {
        return userRepositoryFactory;
    }

    @Override
    public Repository<User, Long> createRepository(RepositoryStrategy strategy, String fileName, String url, String username, String password) {
        if (strategy.equals(RepositoryStrategy.memory)) {
            return new InMemoryRepository<>();
        }
        if (strategy.equals(RepositoryStrategy.file)) {
            return new UserFileRepository(fileName);
        }
        if (strategy.equals(RepositoryStrategy.database)) {
            return new UserDBRepository(url, username, password);
        }
        return null;
    }
}
