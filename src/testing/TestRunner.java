package testing;

import domain.User;
import exceptions.RepositoryException;
import repository.UsersMemoryRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestRunner {
    private List<User> createUsers() {
        User u1 = new User("Bogdan", "qwerty", "iliesbogdan147@gmail.com");
        User u2 = new User("Cristi", "123456", "playnt98@gmail.com");
        User u3 = new User("Adi", "asdfgh", "adiromanov44@gmail.com");

        List<User> userList = new ArrayList<>(Arrays.asList(u1, u2, u3));
        return userList;
    }

    private void runUsersMemoryRepositoryTests() {
        List<User> userList = createUsers();
        UsersMemoryRepository usersRepo = new UsersMemoryRepository();
        try {
            usersRepo.addUser(userList.get(0));
        } catch (RepositoryException re) {
            assert false;
        }
        try {
            usersRepo.addUser(userList.get(1));
        } catch (RepositoryException re) {
            assert false;
        }
        assert usersRepo.size() == 2;
        try {
            usersRepo.addUser(userList.get(0));
        } catch (RepositoryException re) {
            assert re.getErrorMessage().equals("User already exists!\n");
        }
        try {
            usersRepo.removeUser(userList.get(2));
        } catch (RepositoryException re) {
            assert re.getErrorMessage().equals("User does not exist!\n");
        }
        try {
            usersRepo.removeUser(userList.get(1));
        } catch (RepositoryException re) {
            assert false;
        }
        assert usersRepo.size() == 1;
    }

    public void runTests() {
        runUsersMemoryRepositoryTests();
    }
}