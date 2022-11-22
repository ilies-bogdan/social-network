package repository.database;

import domain.Friendship;
import domain.FriendshipStatus;
import domain.User;
import domain.validators.UserValidator;
import domain.validators.Validator;
import exceptions.CorruptedDataException;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import repository.Repository;
import utils.Constants;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FriendshipDBRepository implements Repository<Friendship, Set<User>> {
    private final String url;
    private final String username;
    private final String password;

    public FriendshipDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Extracts a friendship from an SQL result set.
     * @param resultSet - The SQL result set
     * @return the friendship.
     * @throws SQLException if there was a connection error.
     * @throws CorruptedDataException if the data read from the database is corrupted.
     */
    private Friendship extractFriendship(ResultSet resultSet) throws  SQLException, CorruptedDataException {
        long id1 = resultSet.getLong("id_user_01");
        String username1 = resultSet.getString("username_user_01");
        int passwordCode1 = resultSet.getInt("password_code_user_01");
        String salt1 = resultSet.getString("salt_user_01");
        String email1 = resultSet.getString("email_user_01");

        long id2 = resultSet.getLong("id_user_02");
        String username2 = resultSet.getString("username_user_02");
        int passwordCode2 = resultSet.getInt("password_code_user_02");
        String salt2 = resultSet.getString("salt_user_02");
        String email2 = resultSet.getString("email_user_02");

        LocalDateTime friendsFrom = LocalDateTime.parse(resultSet.getString("friends_from"), Constants.DATE_TIME_FORMATTER);
        FriendshipStatus status = FriendshipStatus.valueOf(resultSet.getString("status"));

        Validator<User> userValidator = new UserValidator();
        User u1 = new User(username1, passwordCode1, salt1, email1);
        u1.setID(id1);
        User u2 = new User(username2, passwordCode2, salt2, email2);
        u2.setID(id2);
        try {
            userValidator.validate(u1);
            userValidator.validate(u2);
        } catch (ValidationException exception) {
            throw new CorruptedDataException("Database data is corrupted!\n");
        }
        return new Friendship(u1, u2, friendsFrom, status);
    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(*) AS size FROM friendships";
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
    public List<Friendship> getAll() {
        List<Friendship> friendships = new ArrayList<>();
        String sql = """
                SELECT U1.id AS id_user_01,
                U1.username AS username_user_01,
                U1.password_code AS password_code_user_01,
                U1.salt AS salt_user_01,
                U1.email AS email_user_01,
                U2.id AS id_user_02,
                U2.username AS username_user_02,
                U2.password_code AS password_code_user_02,
                U2.salt AS salt_user_02,
                U2.email AS email_user_02,
                to_char(F.friends_from, ?) AS friends_from,
                F.status AS status
                FROM friendships F
                INNER JOIN users U1 ON F.id_user_01 = U1.id
                INNER JOIN users U2 ON U2.id = F.id_user_02
                """;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, Constants.DATE_TIME_FORMAT_POSTGRESQL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                friendships.add(extractFriendship(resultSet));
            }
        } catch (CorruptedDataException | SQLException exception) {
            exception.printStackTrace();
            System.exit(1);
        }
        return friendships;
    }

    @Override
    public void add(Friendship friendship) throws RepositoryException {
        String sql = "INSERT INTO friendships (id_user_01, id_user_02, friends_from, status) VALUES (?::int, ?::int, to_timestamp(?, ?)::timestamp, ?)";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(friendship.getU1().getID()));
            statement.setString(2, String.valueOf(friendship.getU2().getID()));
            statement.setString(3, friendship.getFriendsFrom().format(Constants.DATE_TIME_FORMATTER));
            statement.setString(4, Constants.DATE_TIME_FORMAT_POSTGRESQL);
            statement.setString(5, friendship.getStatus().name());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RepositoryException("Friendship already exists!\n");
        }
    }

    @Override
    public void remove(Friendship friendship) throws RepositoryException {
        String sql = "DELETE FROM friendships F WHERE F.id_user_01 = ?::int AND F.id_user_02 = ?::int OR F.id_user_01 = ?::int AND F.id_user_02 = ?::int";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(friendship.getU1().getID()));
            statement.setString(2, String.valueOf(friendship.getU2().getID()));
            statement.setString(3, String.valueOf(friendship.getU2().getID()));
            statement.setString(4, String.valueOf(friendship.getU1().getID()));
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RepositoryException("Friendship does not exist!\n");
        }
    }

    @Override
    public Friendship find(Set<User> id) throws RepositoryException {
        String sql = """
                SELECT U1.id AS id_user_01,
                U1.username AS username_user_01,
                U1.password_code AS password_code_user_01,
                U1.salt AS salt_user_01,
                U1.email AS email_user_01,
                U2.id AS id_user_02,
                U2.username AS username_user_02,
                U2.password_code AS password_code_user_02,
                U2.salt AS salt_user_02,
                U2.email AS email_user_02,
                to_char(F.friends_from, ?) AS friends_from,
                F.status AS status
                FROM friendships F
                INNER JOIN users U1 ON F.id_user_01 = U1.id
                INNER JOIN users U2 ON U2.id = F.id_user_02
                WHERE U1.id = ?::int AND U2.id = ?::int OR U1.id = ?::int AND U2.id = ?::int
                """;
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            List<User> users = new ArrayList<>(id);
            statement.setString(1, Constants.DATE_TIME_FORMAT_POSTGRESQL);
            statement.setString(2, String.valueOf(users.get(0).getID()));
            statement.setString(3, String.valueOf(users.get(1).getID()));
            statement.setString(4, String.valueOf(users.get(1).getID()));
            statement.setString(5, String.valueOf(users.get(0).getID()));
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new RepositoryException("Friendship not found!\n");
            }
            return extractFriendship(resultSet);
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
    public void update(Friendship friendship) throws RepositoryException {
        String sql = "UPDATE friendships F SET friends_from = to_timestamp(?, ?)::timestamp, status = ?" +
                "WHERE F.id_user_01 = ?::int AND F.id_user_02 = ?::int OR F.id_user_01 = ?::int AND F.id_user_02 = ?::int";
        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, friendship.getFriendsFrom().format(Constants.DATE_TIME_FORMATTER));
            statement.setString(2, Constants.DATE_TIME_FORMAT_POSTGRESQL);
            statement.setString(3, friendship.getStatus().name());
            statement.setString(4, String.valueOf(friendship.getU1().getID()));
            statement.setString(5, String.valueOf(friendship.getU2().getID()));
            statement.setString(6, String.valueOf(friendship.getU2().getID()));
            statement.setString(7, String.valueOf(friendship.getU1().getID()));
        } catch (SQLException exception) {
            throw new RepositoryException("Friendship does not exist!\n");
        }
    }
}
