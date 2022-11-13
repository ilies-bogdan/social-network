package repository.database;

import domain.User;
import domain.validators.UserValidator;
import domain.validators.Validator;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDBRepository implements Repository<User, Long> {
    private final String url;
    private final String username;
    private final String password;

    public UserDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String username = resultSet.getString("username");
                int passwordCode = resultSet.getInt("password_code");
                String salt = resultSet.getString("salt");
                String email = resultSet.getString("email");

                User user = new User(username, passwordCode, salt, email);
                user.setID(id);
                Validator<User> userValidator = new UserValidator();
                userValidator.validate(user);
                users.add(user);
            }
        } catch (SQLException | ValidationException exception) {
            exception.printStackTrace();
            System.exit(1);
        }

        return users;
    }

    @Override
    public void add(User entity) throws RepositoryException {
        String sql = "INSERT INTO users (id, username, password_code, salt, email) VALUES (?::int, ?, ?::int, ?, ?)";
        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(entity.getID()));
            statement.setString(2, entity.getUsername());
            statement.setString(3, String.valueOf(entity.getPasswordCode()));
            statement.setString(4, entity.getSalt());
            statement.setString(5, entity.getEmail());

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void remove(User entity) throws RepositoryException {
        String sql = "DELETE FROM users WHERE users.id = ?::int";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(entity.getID()));
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public User find(Long id) throws RepositoryException {
        String sql = "SELECT * FROM users WHERE users.id = ?::int";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(id));
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new RepositoryException("Entity not found!\n");
            }

            long userID = resultSet.getLong("id");
            String username = resultSet.getString("username");
            int passwordCode = resultSet.getInt("password_code");
            String salt = resultSet.getString("salt");
            String email = resultSet.getString("email");

            User user = new User(username, passwordCode, salt, email);
            user.setID(userID);
            return user;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(User entity) throws RepositoryException {
        String sql = "UPDATE users SET password_code = ?::int, salt = ?, email = ? WHERE users.id = ?::int";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(entity.getPasswordCode()));
            statement.setString(2, entity.getSalt());
            statement.setString(3, entity.getEmail());
            statement.setString(4, String.valueOf(entity.getID()));
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
