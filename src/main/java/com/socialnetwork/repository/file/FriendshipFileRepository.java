package com.socialnetwork.repository.file;

import com.socialnetwork.domain.Friendship;
import com.socialnetwork.domain.FriendshipStatus;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.validators.UserValidator;
import com.socialnetwork.domain.validators.Validator;
import com.socialnetwork.exceptions.CorruptedDataException;
import com.socialnetwork.exceptions.RepositoryException;
import com.socialnetwork.exceptions.ValidationException;
import com.socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class FriendshipFileRepository extends AbstractFileRepository<Friendship, Set<User>> {
    public FriendshipFileRepository(String fileName) {
        super(fileName);
    }

    /**
     * Adds a Friendship object to the Repository.
     * @param friendship - the Friendship to be added
     * @throws RepositoryException if both Users of the Friendship are the same.
     */
    @Override
    public void add(Friendship friendship) throws RepositoryException {
        if (friendship.getU1().equals(friendship.getU2())) {
            throw new RepositoryException("Invalid friendship!\n");
        }
        super.add(friendship);
    }

    @Override
    public Friendship extractEntity(List<String> attributes) throws CorruptedDataException, ValidationException {
        if (attributes.size() != 12) {
            throw new CorruptedDataException("File data is corrupted!\n");
        }
        Validator<User> userValidator = new UserValidator();
        User u1 = new User(attributes.get(1), Integer.parseInt(attributes.get(2)), attributes.get(3), attributes.get(4));
        u1.setID(Long.parseLong(attributes.get(0)));
        userValidator.validate(u1);
        User u2 = new User(attributes.get(6), Integer.parseInt(attributes.get(7)), attributes.get(8), attributes.get(9));
        u2.setID(Long.parseLong(attributes.get(5)));
        userValidator.validate(u2);
        return new Friendship(u1, u2 , LocalDateTime.parse(attributes.get(10), Constants.DATE_TIME_FORMATTER), FriendshipStatus.valueOf(attributes.get(11)));
    }

    @Override
    public String entityToStringFormat(Friendship friendship) {
        User u1 = friendship.getU1();
        User u2 = friendship.getU2();
        return u1.getID() + "," + u1.getUsername() + "," + u1.getPasswordCode() + "," + u1.getSalt() + "," + u1.getEmail() + "," +
                u2.getID() + "," + u2.getUsername() + "," + u2.getPasswordCode() + "," + u2.getSalt() + "," + u2.getEmail() + "," +
                friendship.getFriendsFrom().format(Constants.DATE_TIME_FORMATTER);
    }
}
