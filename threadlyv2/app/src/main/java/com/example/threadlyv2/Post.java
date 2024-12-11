package com.example.threadlyv2;

public class Post {
    private String username;
    private String postTitle;
    private String postBody;
    private int likes;
    private int comments;

    // Constructor
    public Post(String username, String postTitle, String postBody, int likes, int comments) {
        this.username = username;
        this.postTitle = postTitle;
        this.postBody = postBody;
        this.likes = likes;
        this.comments = comments;
    }

    // Getters and setters
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
