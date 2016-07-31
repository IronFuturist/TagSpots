package com.megliosolutions.tagspots.Objects;

/**
 * Created by Meglio on 6/13/16.
 */
public class UserObject {

    private String email;
    private String username;
    private String password;
    private String name;
    private String moto;

    public UserObject() {
    }

    public UserObject(String email, String username, String password, String name, String moto) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.name = name;
        this.moto = moto;
    }

    public String getName() {
        return name;
    }

    public String getMoto() {
        return moto;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMoto(String moto) {
        this.moto = moto;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

