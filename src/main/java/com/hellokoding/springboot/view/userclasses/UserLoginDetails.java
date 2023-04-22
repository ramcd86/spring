package com.hellokoding.springboot.view.userclasses;

public class UserLoginDetails {

    private String email;
    private String password;

    @Override
    public String toString() {
        return "UserLoginDetails{" +
                "username='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String username) {
        this.email = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
