package com.example.threadlyv2;

public class Comment {
    private int commentId;       // Corresponds to "comment_id"
    private int postId;          // Corresponds to "post_id"
    private int userId;          // Corresponds to "user_id"
    private String commentText;  // Corresponds to "comment_text"
    private String createdAt;    // Corresponds to "created_at"
    private boolean deleted;     // Corresponds to "deleted"

    // Constructor
    public Comment(int commentId, int postId, int userId, String commentText, String createdAt, boolean deleted) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.commentText = commentText;
        this.createdAt = createdAt;
        this.deleted = deleted;
    }

    // Getter and Setter methods

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
