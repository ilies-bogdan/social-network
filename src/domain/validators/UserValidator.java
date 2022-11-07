package domain.validators;

import domain.User;
import exceptions.ValidationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidator implements Validator<User> {
    /**
     * Validates a User.
     * @param user - The User to be validated
     * @throws ValidationException if either one of the User fields is empty or contains the ',' character.
     *                             if the Password is too short.
     *                             if the Email is invalid.
     */
    @Override
    public void validate(User user) throws ValidationException {
        String message = "";
        if (user.getUsername() == null || user.getUsername().trim().length() == 0) {
            message += "Username can not be empty!\n";
        } else if (user.getUsername().contains(",")) {
            message += "Not allowed character in username!\n";
        }

        if (user.getEmail() == null || user.getEmail().trim().length() == 0) {
            message += "Email can not be empty!\n";
        } else if (user.getEmail().contains(",")) {
            message += "Not allowed character in email!\n";
        } else {
            String regex = "^.+@.+[.].+$";
            Pattern pattern = Pattern.compile(regex);

            Matcher matcher = pattern.matcher(user.getEmail());
            if (!matcher.find()) {
                message += "Invalid email!\n";
            }
        }

        if (String.valueOf(user.getPasswordCode()) == null || String.valueOf(user.getPasswordCode()) .trim().length() == 0) {
            message += "Password can not be empty!\n";
        } else if (String.valueOf(user.getPasswordCode()) .contains(",")) {
            message += "Not allowed character in password!\n";
        } else if (String.valueOf(user.getPasswordCode()) .length() < 8) {
            message += "Password must be at least 8 characters long!\n";
        }

        if (message.length() > 0) {
            throw new ValidationException(message);
        }
    }
}