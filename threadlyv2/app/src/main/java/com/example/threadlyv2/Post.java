package com.example.threadlyv2;

public class Post {
    private int postID;
    private String fullname;
    private String username;
    private String postTitle;
    private String postBody;
    private int likeCount;  // like counter
    private int commentCount;  // comment counter
    private String createdAt; // Time
    private String profileImageUri; // Add this field
    private boolean likedByUser;  // To track if the current user has liked the post



    // Constructor
    public Post(int postID, String fullname, String username, String postTitle, String postBody, int likeCount, int comments, String profileImageUri, String createdAt, boolean likedByUser) {
        this.postID = postID;
        this.fullname = fullname;
        this.username = username;
        this.postTitle = postTitle;
        this.postBody = postBody;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.profileImageUri = profileImageUri; // Initialize profile image URI
        this.createdAt = createdAt; // Initialize createdAt
        this.likedByUser = likedByUser;
    }

    // Getters and setters


    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isLikedByUser() {
        return likedByUser;
    }

    public void setLikedByUser(boolean likedByUser) {
        this.likedByUser = likedByUser;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
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


}
