package repository.file;

import domain.User;

import java.util.List;

public class UserFileRepository extends AbstractFileRepository<User, String> {
    public UserFileRepository(String fileName) {
        super(fileName);
    }

    @Override
    public User extractEntity(List<String> attributes) {
        return new User(attributes.get(0), attributes.get(1), attributes.get(2));
    }

    @Override
    public String entityToStringFormat(User user) {
        return user.getUsername() + "," + user.getPassword() + "," + user.getEmail();
    }
}
