package validators;

import domain.User;
import exceptions.ValidationException;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User user) throws ValidationException {
        String message = "";
        if (user.getUsername().equals("")) {
            message += "Username can not be empty!\n";
        }
        if (user.getEmail().equals("")) {
            message += "Email can not be empty!\n";
        }
        if (user.getPassword().equals("")) {
            message += "Password can not be empty!\n";
        }
        if (message.length() > 0) {
            throw new ValidationException(message);
        }
    }
}