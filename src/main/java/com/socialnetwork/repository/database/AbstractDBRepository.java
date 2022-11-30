package com.socialnetwork.repository.database;

import com.socialnetwork.domain.Entity;
import com.socialnetwork.exceptions.RepositoryException;
import com.socialnetwork.exceptions.ValidationException;
import com.socialnetwork.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDBRepository<E extends Entity<ID>, ID> implements Repository<E, ID> {
    private final String url;
    private final String username;
    private final String password;

    public AbstractDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public int size() {
        String sql = sizeSqlString();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()) {
            return resultSet.getInt("count");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public abstract String sizeSqlString();

    @Override
    public List<E> getAll() {
        List<E> entities = new ArrayList<>();
        String sql = getAllSqlString();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                E entity = extractEntity(resultSet);
                entities.add(entity);
            }
        } catch (SQLException | ValidationException exception) {
            exception.printStackTrace();
            System.exit(1);
        }

        return entities;
    }

    public abstract String getAllSqlString();

    public abstract E extractEntity(ResultSet resultSet) throws SQLException, ValidationException;

    @Override
    public void add(E entity) {
        String sql = addSqlString();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            addSqlHandle(statement, entity);
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public abstract String addSqlString();

    public abstract void addSqlHandle(PreparedStatement statement, E entity) throws SQLException;

    @Override
    public void remove(E entity) {
        String sql = removeSqlString();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            removeSqlHandle(statement);
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public abstract String removeSqlString();

    public abstract void removeSqlHandle(PreparedStatement statement);

    @Override
    public E find(ID id) throws RepositoryException {
        String sql = findSqlString();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            findSqlHandle(statement);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new RepositoryException("Entity not found!\n");
            }

            return extractEntity(resultSet);
        } catch (SQLException | ValidationException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public abstract String findSqlString();

    public abstract void findSqlHandle(PreparedStatement statement);

    @Override
    public void update(E entity) {
        String sql = updateSqlString();
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql)) {
            updateSqlHandle(statement);
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public abstract String updateSqlString();

    public abstract void updateSqlHandle(PreparedStatement statement);
}
