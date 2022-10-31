package repository.file;

import domain.HasID;
import exceptions.RepositoryException;
import repository.memory.InMemoryRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
                E entity = extractEntity(Arrays.asList(line.split(";")));
                super.entities.add(entity);
            });
        } catch (IOException exception) {
            System.out.println("Data loading error.\n");
            exception.printStackTrace();
        }
    }

    public abstract E extractEntity(List<String> attributes);
}
