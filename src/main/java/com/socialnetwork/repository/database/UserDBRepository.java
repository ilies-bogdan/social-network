package com.socialnetwork.repository.database;

import com.socialnetwork.domain.User;
import com.socialnetwork.domain.validators.UserValidator;
import com.socialnetwork.domain.validators.Validator;
import com.socialnetwork.exceptions.CorruptedDataException;
import com.socialnetwork.exceptions.RepositoryException;
import com.socialnetwork.exceptions.ValidationException;
import com.socialnetwork.repository.Repository;

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

    /**
     * Extracts a user from an SQL result set.
     * @param resultSet - The SQL result set
     * @return the user.
     * @throws SQLException if there was a connection error.
     * @throws CorruptedDataException if the data read from the database is corrupted.
     */
    private User extractUser(ResultSet resultSet) throws SQLException, CorruptedDataException {
        long id = resultSet.getLong("id");
        String username = resultSet.getString("username");
        int passwordCode = resultSet.getInt("password_code");
        String salt = resultSet.getString("salt");
        String email = resultSet.getString("email");

        User user = new User(username, passwordCode, salt, email);
        user.setID(id);
        Validator<User> userValidator = new UserValidator();
        try {
            userValidator.validate(user);
        } catch (ValidationException exception) { // Something must have gone wrong with the data.
            throw new CorruptedDataException("Database data is corrupted!\n");
        }
        return user;
    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(*) AS size FROM users";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt("size");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
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
                users.add(extractUser(resultSet));
            }
        } catch (CorruptedDataException | SQLException exception) {
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
            throw new RepositoryException("User already exists!\n");
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
            throw new RepositoryException("User does not exists!\n");
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
            return extractUser(resultSet);
        } catch (CorruptedDataException exception) {
            exception.printStackTrace();
            System.exit(1);
        }
        catch (SQLException exception) {
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
            throw new RepositoryException("User does not exists!\n");
        }
    }
}