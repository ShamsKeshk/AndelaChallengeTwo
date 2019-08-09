package com.example.andelachallengetwo;

public class User {

    private String userName;
    private String userId;
    private boolean isAdmin;
    private String userEmail;
    private String userImage;


    public User() {
    }

    public User(String userId , String userName, String userEmail, String userImage, boolean isAdmin) {
        this.userName = userName;
        this.userId = userId;
        this.isAdmin = isAdmin;
        this.userEmail = userEmail;
        this.userImage = userImage;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
