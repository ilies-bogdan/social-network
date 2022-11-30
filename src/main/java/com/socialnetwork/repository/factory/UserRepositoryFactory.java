package com.socialnetwork.repository.factory;

import com.socialnetwork.domain.User;
import com.socialnetwork.repository.Repository;
import com.socialnetwork.repository.database.UserDBRepository;
import com.socialnetwork.repository.file.UserFileRepository;
import com.socialnetwork.repository.memory.InMemoryRepository;

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
