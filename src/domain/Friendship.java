package domain;

import utils.Constants;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Friendship implements HasID<Set<User>> {
    private User u1;
    private User u2;
    private LocalDateTime friendsFrom;

    public Friendship(User u1, User u2, LocalDateTime friendsFrom) {
        this.u1 = u1;
        this.u2 = u2;
        this.friendsFrom = friendsFrom;
    }

    public User getU1() {
        return u1;
    }

    public void setU1(User u1) {
        this.u1 = u1;
    }

    public User getU2() {
        return u2;
    }

    public void setU2(User u2) {
        this.u2 = u2;
    }

    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Friendship that = (Friendship) o;
        return Objects.equals(this.getID(), that.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(u1, u2);
    }

    @Override
    public String toString() {
        return u1.getUsername() + " and " + u2.getUsername() + " are friends since " + friendsFrom.format(Constants.DATE_TIME_FORMATTER) + "!";
    }

    @Override
    public Set<User> getID() {
        Set<User> id = new HashSet<>();
        // The ID is an interchangeable combination of the two Users.
        id.add(u1);
        id.add(u2);
        return id;
    }

    @Override
    public void setID(Set<User> ID) {}
}
