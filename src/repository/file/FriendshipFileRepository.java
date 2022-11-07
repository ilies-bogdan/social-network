package repository.file;

import domain.Friendship;
import domain.User;
import exceptions.CorruptedDataException;
import utils.Constants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class FriendshipFileRepository extends AbstractFileRepository<Friendship, Set<User>> {
    public FriendshipFileRepository(String fileName) {
        super(fileName);
    }

    @Override
    public Friendship extractEntity(List<String> attributes) throws CorruptedDataException {
        if (attributes.size() != 7) {
            throw new CorruptedDataException("File data is corrupted!\n");
        }
        return new Friendship(new User(attributes.get(0), attributes.get(1), attributes.get(2)),
                new User(attributes.get(3), attributes.get(4), attributes.get(5)),
                LocalDateTime.parse(attributes.get(6), Constants.DATE_TIME_FORMATTER));
    }

    @Override
    public String entityToStringFormat(Friendship friendship) {
        return friendship.getU1().getUsername() + "," + friendship.getU1().getPassword() + "," + friendship.getU1().getEmail() + "," +
                friendship.getU2().getUsername() + "," + friendship.getU2().getPassword() + "," + friendship.getU2().getEmail() + "," +
                friendship.getFriendsFrom().format(Constants.DATE_TIME_FORMATTER);
    }
}
