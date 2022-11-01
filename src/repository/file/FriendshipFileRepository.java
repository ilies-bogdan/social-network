package repository.file;

import domain.Friendship;
import domain.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendshipFileRepository extends AbstractFileRepository<Friendship, Set<User>> {
    public FriendshipFileRepository(String fileName) {
        super(fileName);
    }

    @Override
    public Friendship extractEntity(List<String> attributes) {
        return new Friendship(new User(attributes.get(0), attributes.get(1), attributes.get(2)),
                new User(attributes.get(3), attributes.get(4), attributes.get(5)));
    }

    @Override
    public String entityToStringFormat(Friendship friendship) {
        return friendship.getU1().getUsername() + ";" + friendship.getU1().getPassword() + ";" + friendship.getU1().getEmail() + ";" +
                friendship.getU2().getUsername() + ";" + friendship.getU2().getPassword() + ";" + friendship.getU2().getEmail();
    }
}
