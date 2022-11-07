package repository.file;

import domain.User;
import domain.validators.UserValidator;
import domain.validators.Validator;
import exceptions.CorruptedDataException;
import exceptions.ValidationException;

import java.util.List;

public class UserFileRepository extends AbstractFileRepository<User, String> {
    public UserFileRepository(String fileName) {
        super(fileName);
    }

    @Override
    public User extractEntity(List<String> attributes) throws CorruptedDataException, ValidationException {
        if (attributes.size() != 4) {
            throw new CorruptedDataException("File data is corrupted!\n");
        }
        User user = new User(attributes.get(0), Integer.parseInt(attributes.get(1)), attributes.get(2), attributes.get(3));
        Validator<User> userValidator = new UserValidator();
        userValidator.validate(user);
        return user;
    }

    @Override
    public String entityToStringFormat(User user) {
        return user.getUsername() + "," + user.getPasswordCode() + "," + user.getSalt() +  "," + user.getEmail();
    }
}
