package domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Friendship implements HasID<Set<String>> {
    private User u1;
    private User u2;

    public Friendship(User u1, User u2) {
        this.u1 = u1;
        this.u2 = u2;
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
        return u1.getUsername() + " and " + u2.getUsername() + " are friends!";
    }

    @Override
    public Set<String> getID() {
        Set<String> res = new HashSet<>();
        res.add(u1.getUsername());
        res.add(u2.getUsername());
        return res;
    }

    @Override
    public void setID(Set<String> ID) {}
}
