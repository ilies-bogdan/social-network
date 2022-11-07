package repository.file;

import domain.HasID;
import domain.User;
import domain.validators.UserValidator;
import domain.validators.Validator;
import exceptions.CorruptedDataException;
import exceptions.RepositoryException;
import exceptions.ValidationException;
import repository.memory.InMemoryRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractFileRepository<E extends HasID<ID>, ID> extends InMemoryRepository<E, ID> {
    String fileName;

    public AbstractFileRepository(String fileName) {
        super();
        this.fileName = fileName;
        loadData();
    }

    private void loadData() {
        Path path = Paths.get(fileName);
        try {
            super.entities.clear();
            List<String> lines = Files.readAllLines(path);
            lines.forEach(line -> {
                try {
                    E entity = extractEntity(Arrays.asList(line.split(",")));
                    super.add(entity);
                } catch (CorruptedDataException | ValidationException | RepositoryException exception) {
                    exception.printStackTrace();
                    System.exit(1);
                }
            });
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Writes the data from memory to file.
     */
    private void writeData() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            for (E entity : super.entities) {
                writer.write(entityToStringFormat(entity));
                writer.newLine();
            }
            writer.close();
        } catch (IOException exception) {
            System.out.println("Data writing error.\n");
            exception.printStackTrace();
        }
    }

    /**
     * Appends an entity to file.
     * @param entity - The entity to append
     */
    private void appendData(E entity) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.write(entityToStringFormat(entity));
            writer.newLine();
            writer.close();
        } catch (IOException exception) {
            System.out.println("Data writing error.\n");
            exception.printStackTrace();
        }
    }

    /**
     * Gets a concrete Entity, being given its attributes.
     * @param attributes - The attributes of the Entity as a List of String
     * @return the Entity with the given attributes.
     * @throws CorruptedDataException if the data read from file is corrupted.
     */
    public abstract E extractEntity(List<String> attributes) throws CorruptedDataException, ValidationException;

    /**
     * Writes the Entity in a String format where the attributes are each separated by ','
     * @param e - The Entity to be written in String format
     * @return - The String format of the Entity
     */
    public abstract String entityToStringFormat(E e);

    @Override
    public int size() {
        loadData();
        return super.size();
    }

    @Override
    public List<E> getAll() {
        loadData();
        return super.getAll();
    }

    @Override
    public void add(E e) throws RepositoryException {
        loadData();
        super.add(e);
        appendData(e);
    }

    @Override
    public void remove(E e) throws RepositoryException {
        loadData();
        super.remove(e);
        writeData();
    }

    @Override
    public E find(ID id) throws RepositoryException {
        loadData();
        return super.find(id);
    }

    @Override
    public void update(E e) throws RepositoryException {
        loadData();
        super.update(e);
        writeData();
    }
}
