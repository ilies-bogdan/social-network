package com.socialnetwork.domain;

import java.util.Objects;

public class User implements Entity<Long> {
    private Long id;
    private String username;
    private int passwordCode;
    private String salt;
    private String email;

    public User(String username, int password, String salt, String email) {
        this.username = username;
        this.passwordCode = password;
        this.salt = salt;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPasswordCode() {
        return passwordCode;
    }

    public void setPasswordCode(int passwordCode) {
        this.passwordCode = passwordCode;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "Username: " + username;
    }

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }
}
