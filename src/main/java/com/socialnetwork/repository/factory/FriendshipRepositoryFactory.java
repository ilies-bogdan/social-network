package com.socialnetwork.repository.factory;

import com.socialnetwork.domain.Friendship;
import com.socialnetwork.domain.User;
import com.socialnetwork.repository.Repository;
import com.socialnetwork.repository.database.FriendshipDBRepository;
import com.socialnetwork.repository.file.FriendshipFileRepository;
import com.socialnetwork.repository.memory.InMemoryRepository;

import java.util.Set;

public class FriendshipRepositoryFactory implements RepositoryFactory<Friendship, Set<User>> {
    private static final FriendshipRepositoryFactory friendshipRepositoryFactory = new FriendshipRepositoryFactory();

    private FriendshipRepositoryFactory() {}

    public static FriendshipRepositoryFactory getInstance() {
        return friendshipRepositoryFactory;
    }

    @Override
    public Repository<Friendship, Set<User>> createRepository(RepositoryStrategy strategy, String fileName, String url, String username, String password) {
        if (strategy.equals(RepositoryStrategy.memory)) {
            return new InMemoryRepository<>();
        }
        if (strategy.equals(RepositoryStrategy.file)) {
            return new FriendshipFileRepository(fileName);
        }
        if (strategy.equals(RepositoryStrategy.database)) {
            return new FriendshipDBRepository(url, username, password);
        }
        return null;
    }
}
