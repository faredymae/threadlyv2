package com.example.threadlyv2;

public class Post {
    private String username;
    private String postTitle;
    private String postBody;
    private int likes;  // like counter
    private int comments;  // comment counter
    private String createdAt; // Time
    private String profileImageUri; // Add this field

    // Constructor
    public Post(String username, String postTitle, String postBody, int likes, int comments, String profileImageUri, String createdAt) {
        this.username = username;
        this.postTitle = postTitle;
        this.postBody = postBody;
        this.likes = likes;
        this.comments = comments;
        this.profileImageUri = profileImageUri; // Initialize profile image URI
        this.createdAt = createdAt; // Initialize createdAt
    }

    // Getters and setters
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}
