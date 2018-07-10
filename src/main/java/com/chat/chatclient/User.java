package com.chat.chatclient;

import net.sf.json.JSONObject;

/**
 * Create by Guolianxing on 2018/7/6.
 */
public class User {
    private Integer userId;
    private String username;
    private String password;
    private String photo;

    public User() {
    }

    public User(Integer userId, String username, String password, String photo) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.photo = photo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("userId", userId);
        jsonObject.accumulate("username", username);
        jsonObject.accumulate("password", password);
        jsonObject.accumulate("photo", photo);
        return jsonObject;
    }
}
