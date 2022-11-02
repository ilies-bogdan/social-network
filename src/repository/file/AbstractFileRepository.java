package repository.file;

import domain.HasID;
import exceptions.RepositoryException;
import repository.memory.InMemoryRepository;

import java.io.*;
import java.nio.file.Files;
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
                E entity = extractEntity(Arrays.asList(line.split(",")));
                super.entities.add(entity);
            });
        } catch (IOException exception) {
            System.out.println("Data loading error.\n");
            exception.printStackTrace();
        }
    }

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

    public abstract E extractEntity(List<String> attributes);

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
}
