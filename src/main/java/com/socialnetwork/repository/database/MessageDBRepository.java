package com.socialnetwork.repository.database;

import com.socialnetwork.domain.Message;
import com.socialnetwork.exceptions.RepositoryException;
import com.socialnetwork.repository.Repository;
import com.socialnetwork.utils.Constants;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDBRepository implements Repository<Message, Long> {
    private final String url;
    private final String username;
    private final String password;

    public MessageDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    private Message extractMessage(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        LocalDateTime sentAt = LocalDateTime.parse(resultSet.getString("sent_at"), Constants.DATE_TIME_FORMATTER);
        String subject = resultSet.getString("subject");
        String text = resultSet.getString("text");
        String sender = resultSet.getString("sender");
        String receiver = resultSet.getString("receiver");

        Message message = new Message(sentAt, subject, text, sender, receiver);
        message.setID(id);

        return message;
    }

    @Override
    public int size() {
        String sql = "SELECT COUNT(*) AS size FROM messages";
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
    public List<Message> getAll() {
        List<Message> messages = new ArrayList<>();
        String sql = """
                SELECT M.id AS id,
                to_char(M.sent_at, ?) AS sent_at,
                M.subject AS subject,
                M.text AS text,
                M.sender AS sender,
                M.receiver AS receiver
                FROM messages M
            """;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, Constants.DATE_TIME_FORMAT_POSTGRESQL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                messages.add(extractMessage(resultSet));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.exit(1);
        }
        return messages;
    }

    @Override
    public void add(Message entity) throws RepositoryException {
        String sql = "INSERT INTO messages (sent_at, subject, text, sender, receiver) VALUES (to_timestamp(?, ?)::timestamp, ?, ?, ?, ?)";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getSentAt().format(Constants.DATE_TIME_FORMATTER));
            statement.setString(2, Constants.DATE_TIME_FORMAT_POSTGRESQL);
            statement.setString(3, entity.getSubject());
            statement.setString(4, entity.getText());
            statement.setString(5, entity.getSender());
            statement.setString(6, entity.getReceiver());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RepositoryException("Message already exists!\n");
        }
    }

    @Override
    public void remove(Message entity) throws RepositoryException {
        String sql = "DELETE FROM messages WHERE messages.id = ?::int";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(entity.getID()));
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RepositoryException("Message does not exists!\n");
        }
    }

    @Override
    public Message find(Long id) throws RepositoryException {
        String sql = "SELECT * FROM messages WHERE messages.id = ?::int";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(id));
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new RepositoryException("Entity not found!\n");
            }
            return extractMessage(resultSet);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Message entity) throws RepositoryException {
        String sql = "UPDATE messages SET sent_at = to_timestamp(?, ?)::timestamp, subject = ?, text = ?, sender = ?, receiver = ? WHERE messages.id = ?::int";
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getSentAt().format(Constants.DATE_TIME_FORMATTER));
            statement.setString(2, Constants.DATE_TIME_FORMAT_POSTGRESQL);
            statement.setString(3, entity.getSubject());
            statement.setString(4, entity.getText());
            statement.setString(5, entity.getSender());
            statement.setString(6, entity.getReceiver());
            statement.setString(7, String.valueOf(entity.getID()));
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RepositoryException("Message does not exists!\n");
        }
    }

    public List<Message> getAllForSomeone(String user) throws RepositoryException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE receiver = ? ORDER BY sent_at DESC";
        try(Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                messages.add(extractMessage(resultSet));
            }

            return  messages;
        } catch (SQLException exception) {
            throw new RepositoryException("No messages!\n");
        }
    }
}
