package repository.factory;

import domain.Friendship;
import domain.User;
import repository.Repository;
import repository.database.FriendshipDBRepository;
import repository.file.FriendshipFileRepository;
import repository.memory.InMemoryRepository;

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
