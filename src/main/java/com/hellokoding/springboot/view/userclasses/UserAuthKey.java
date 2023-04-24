package com.hellokoding.springboot.view.userclasses;

public class UserAuthKey {

    private String authKey;

    @Override
    public String toString() {
        return "UserAuthKey{" +
                "authKey='" + authKey + '\'' +
                '}';
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

}
