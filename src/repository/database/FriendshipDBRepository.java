package repository.database;

import domain.Friendship;
import domain.User;
import domain.validators.UserValidator;
import domain.validators.Validator;
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

    @Override
    public int size() {
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
                to_char(F.friends_from, ?) AS friends_from
                FROM friendships F
                INNER JOIN users U1 ON F.id_user_01 = U1.id
                INNER JOIN users U2 ON U2.id = F.id_user_02
                """;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, Constants.DATE_TIME_FORMAT_POSTGRESQL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
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

                Validator<User> userValidator = new UserValidator();
                User u1 = new User(username1, passwordCode1, salt1, email1);
                u1.setID(id1);
                userValidator.validate(u1);
                User u2 = new User(username2, passwordCode2, salt2, email2);
                u2.setID(id2);
                userValidator.validate(u2);

                Friendship friendship = new Friendship(u1, u2, friendsFrom);
                friendships.add(friendship);
            }
        } catch (SQLException | ValidationException exception) {
            exception.printStackTrace();
            System.exit(1);
        }

        return friendships;
    }

//    @Override
//    public void add(Friendship entity) throws RepositoryException {
//        String sql = "INSERT INTO friendships (id_user_01, username_user_01, password_code_user_01, salt_user_01, email_user_01," +
//                "id_user_02, username_user_02, password_code_user_02, salt_user_02, email_user_02, friends_from)" +
//                "VALUES (?::int, ?, ?::int, ?, ?, ?::int, ?, ?::int, ?, ?, to_timestamp(?, ?)::timestamp)";
//        try(Connection connection = DriverManager.getConnection(url, username, password);
//            PreparedStatement statement = connection.prepareStatement(sql)) {
//            statement.setString(1, String.valueOf(entity.getU1().getID()));
//            statement.setString(2, entity.getU1().getUsername());
//            statement.setString(3, String.valueOf(entity.getU1().getPasswordCode()));
//            statement.setString(4, entity.getU1().getSalt());
//            statement.setString(5, entity.getU1().getEmail());
//
//            statement.setString(6, String.valueOf(entity.getU2().getID()));
//            statement.setString(7, entity.getU2().getUsername());
//            statement.setString(8, String.valueOf(entity.getU2().getPasswordCode()));
//            statement.setString(9, entity.getU2().getSalt());
//            statement.setString(10, entity.getU2().getEmail());
//
//            statement.setString(11, entity.getFriendsFrom().format(Constants.DATE_TIME_FORMATTER));
//            statement.setString(12, Constants.DATE_TIME_FORMAT_POSTGRESQL);
//
//            statement.executeUpdate();
//        } catch (SQLException exception) {
//            exception.printStackTrace();
//        }
//    }

    @Override
    public void add(Friendship entity) throws RepositoryException {
        String sql = "INSERT INTO friendships (id_user_01, id_user_02, friends_from) VALUES (?::int, ?::int, to_timestamp(?, ?)::timestamp)";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(entity.getU1().getID()));
            statement.setString(2, String.valueOf(entity.getU2().getID()));
            statement.setString(3, entity.getFriendsFrom().format(Constants.DATE_TIME_FORMATTER));
            statement.setString(4, Constants.DATE_TIME_FORMAT_POSTGRESQL);
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void remove(Friendship entity) throws RepositoryException {
        String sql = "DELETE FROM friendships F WHERE F.id_user_01 = ?::int AND F.id_user_02 = ?::int OR F.id_user_01 = ?::int AND F.id_user_02 = ?::int";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(entity.getU1().getID()));
            statement.setString(2, String.valueOf(entity.getU2().getID()));
            statement.setString(3, String.valueOf(entity.getU2().getID()));
            statement.setString(4, String.valueOf(entity.getU1().getID()));
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
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
                to_char(F.friends_from, ?) AS friends_from
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
                throw new RepositoryException("Entity not found!\n");
            }

            long userID1 = resultSet.getLong("id_user_01");
            String username1 = resultSet.getString("username_user_01");
            int passwordCode1 = resultSet.getInt("password_code_user_01");
            String salt1 = resultSet.getString("salt_user_01");
            String email1 = resultSet.getString("email_user_01");
            User user1 = new User(username1, passwordCode1, salt1, email1);
            user1.setID(userID1);

            long userID2 = resultSet.getLong("id_user_02");
            String username2 = resultSet.getString("username_user_02");
            int passwordCode2 = resultSet.getInt("password_code_user_02");
            String salt2 = resultSet.getString("salt_user_02");
            String email2 = resultSet.getString("email_user_02");
            User user2 = new User(username2, passwordCode2, salt2, email2);
            user2.setID(userID2);

            LocalDateTime friendsFrom = LocalDateTime.parse(resultSet.getString("friends_from"), Constants.DATE_TIME_FORMATTER);

            return new Friendship(user1, user2, friendsFrom);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Friendship entity) throws RepositoryException {
//        String sql = "UPDATE friendships SET password_code_user_01 = ?::int, salt_user_01 = ?, email_user_01 = ?," +
//                " password_code_user_02 = ?::int, salt_user_02 = ?, email_user_02 = ? " +
//                "WHERE friendships.id_user_01 = ?::int AND friendships.id_user_02 = ?::int";
//        try(Connection connection = DriverManager.getConnection(url, username, password);
//            PreparedStatement statement = connection.prepareStatement(sql)) {
//            statement.setString(1, String.valueOf(entity.getU1().getPasswordCode()));
//            statement.setString(2, entity.getU1().getSalt());
//            statement.setString(3, entity.getU1().getEmail());
//            statement.setString(4, String.valueOf(entity.getU2().getPasswordCode()));
//            statement.setString(5, entity.getU2().getSalt());
//            statement.setString(6, entity.getU2().getEmail());
//            statement.executeUpdate();
//        } catch (SQLException exception) {
//            exception.printStackTrace();
//        }
    }
}
