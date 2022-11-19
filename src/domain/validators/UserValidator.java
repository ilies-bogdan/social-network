package domain.validators;

import domain.User;
import exceptions.ValidationException;
import utils.Constants;

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

        if (String.valueOf(user.getPasswordCode()).trim().length() == 0) {
            message += "Invalid password code!\n";
        }

        if (message.length() > 0) {
            throw new ValidationException(message);
        }
    }

    /**
     * Validates a password.
     * @param password - The password to be validated
     * @throws ValidationException if the password is too short.
     */
    public void validatePassword(String password) throws ValidationException {
        String message = "";
        if (password == null || password.trim().length() == 0) {
            message += "Password can not be empty!\n";
        }
        if (password.contains(",")) {
            message += "Not allowed character in password!\n";
        }
        if (password.length() < Constants.MINIMUM_PASSWORD_LENGTH) {
            message += "Password must be at least " + Constants.MINIMUM_PASSWORD_LENGTH + " characters long!\n";
        }

        if (message.length() > 0) {
            throw new ValidationException(message);
        }
    }
}