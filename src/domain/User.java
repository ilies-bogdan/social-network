package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User implements HasID<String> {
    private String username;
    private String password;
    private String email;
    List<User> friends;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.friends = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return username.equals(user.username) || email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "Username: " + username  +", Email: " + email;
    }

    @Override
    public String getID() {
        return username;
    }

    @Override
    public void setID(String id) {
        this.username = id;
    }
}
