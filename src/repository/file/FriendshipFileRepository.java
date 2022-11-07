package repository.file;

import domain.Friendship;
import domain.User;
import domain.validators.UserValidator;
import domain.validators.Validator;
import exceptions.CorruptedDataException;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import utils.Constants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class FriendshipFileRepository extends AbstractFileRepository<Friendship, Set<User>> {
    public FriendshipFileRepository(String fileName) {
        super(fileName);
    }

    @Override
    public void add(Friendship friendship) throws RepositoryException {
        if (friendship.getU1().equals(friendship.getU2())) {
            throw new RepositoryException("Invalid friendship!\n");
        }
        super.add(friendship);
    }

    @Override
    public Friendship extractEntity(List<String> attributes) throws CorruptedDataException, ValidationException {
        if (attributes.size() != 9) {
            throw new CorruptedDataException("File data is corrupted!\n");
        }
        Validator<User> userValidator = new UserValidator();
        User u1 = new User(attributes.get(0), Integer.parseInt(attributes.get(1)), attributes.get(2), attributes.get(3));
        userValidator.validate(u1);
        User u2 = new User(attributes.get(4), Integer.parseInt(attributes.get(5)), attributes.get(6), attributes.get(7));
        userValidator.validate(u2);
        return new Friendship(u1, u2 , LocalDateTime.parse(attributes.get(8), Constants.DATE_TIME_FORMATTER));
    }

    @Override
    public String entityToStringFormat(Friendship friendship) {
        return friendship.getU1().getUsername() + "," + friendship.getU1().getPasswordCode() + "," +friendship.getU1().getSalt() + "," + friendship.getU1().getEmail() + "," +
                friendship.getU2().getUsername() + "," + friendship.getU2().getPasswordCode() + "," + friendship.getU2().getSalt() + "," + friendship.getU2().getEmail() + "," +
                friendship.getFriendsFrom().format(Constants.DATE_TIME_FORMATTER);
    }
}
