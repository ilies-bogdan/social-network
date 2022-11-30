package com.socialnetwork.repository.file;

import com.socialnetwork.domain.User;
import com.socialnetwork.domain.validators.UserValidator;
import com.socialnetwork.domain.validators.Validator;
import com.socialnetwork.exceptions.CorruptedDataException;
import com.socialnetwork.exceptions.ValidationException;

import java.util.List;

public class UserFileRepository extends AbstractFileRepository<User, Long> {
    public UserFileRepository(String fileName) {
        super(fileName);
    }

    @Override
    public User extractEntity(List<String> attributes) throws CorruptedDataException, ValidationException {
        if (attributes.size() != 5) {
            throw new CorruptedDataException("File data is corrupted!\n");
        }
        User user = new User(attributes.get(1), Integer.parseInt(attributes.get(2)), attributes.get(3), attributes.get(4));
        user.setID(Long.parseLong(attributes.get(0)));
        Validator<User> userValidator = new UserValidator();
        userValidator.validate(user);
        return user;
    }

    @Override
    public String entityToStringFormat(User user) {
        return user.getID() + "," + user.getUsername() + "," + user.getPasswordCode() + "," + user.getSalt() +  "," + user.getEmail();
    }
}
