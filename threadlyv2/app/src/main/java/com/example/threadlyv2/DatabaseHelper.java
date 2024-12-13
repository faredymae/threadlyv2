package com.example.threadlyv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "threadlyv2.db";
    public static final String TABLE_NAME_USER = "allusers";
    public static final String TABLE_NAME_POST = "posts";
    public static final String TABLE_NAME_COMMENT = "comments";
    public static final String TABLE_NAME_LIKE = "like";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1); // Incremented version
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // Enable foreign key constraints
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Create allusers table
            db.execSQL("CREATE TABLE " + TABLE_NAME_USER + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "fullname TEXT NOT NULL, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "profile_picture TEXT)"); // Path or URL to the profile picture

            // Create posts table
            db.execSQL("CREATE TABLE " + TABLE_NAME_POST + " (" +
                    "post_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL, " +
                    "post_title TEXT NOT NULL, " +
                    "post_body TEXT NOT NULL, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "like_count INTEGER DEFAULT 0, " +
                    "comment_count INTEGER DEFAULT 0, " +
                    "deleted BOOLEAN DEFAULT 0, " + // Soft delete flag
                    "FOREIGN KEY(username) REFERENCES " + TABLE_NAME_USER + "(username))");

            // Create comments table
            db.execSQL("CREATE TABLE " + TABLE_NAME_COMMENT + " (" +
                    "comment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "post_id INTEGER NOT NULL, " +
                    "user_id INTEGER NOT NULL, " +
                    "comment_text TEXT NOT NULL, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "deleted BOOLEAN DEFAULT 0, " + // Soft delete flag
                    "FOREIGN KEY(post_id) REFERENCES " + TABLE_NAME_POST + "(post_id), " +
                    "FOREIGN KEY(user_id) REFERENCES " + TABLE_NAME_USER + "(id))");

            // Create likes table
            db.execSQL("CREATE TABLE " + TABLE_NAME_LIKE + " (" +
                    "like_id INTEGER PRIMARY KEY, " +   // Primary key for the like_id column
                    "post_id INTEGER NOT NULL, " +      // Post ID being liked
                    "user_id INTEGER NOT NULL, " +      // User ID who likes the post
                    "FOREIGN KEY(post_id) REFERENCES " + TABLE_NAME_POST + "(post_id), " +  // Foreign key to posts table
                    "FOREIGN KEY(user_id) REFERENCES " + TABLE_NAME_USER + "(id), " +       // Foreign key to users table
                    "UNIQUE(post_id, user_id))");        // Composite unique constraint to prevent duplicate likes

        } catch (Exception e) {
            Log.e("DB_ERROR", "Error creating tables: " + e.getMessage());
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {  // If the database version is less than 2, upgrade it
            // Add the like_id column to the likes table
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME_LIKE + " (" +
                    "like_id INTEGER PRIMARY KEY AUTOINCREMENT, ))");

        }
    }

    public Boolean insertData(String fullname, String username, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("fullname", fullname);
        contentValues.put("username", username);
        contentValues.put("password", password);

        long result = db.insert(TABLE_NAME_USER, null, contentValues);
        return result != -1;
    }

    public Boolean insertPost(String username, String postTitle, String postBody) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username); // Include username
        contentValues.put("post_title", postTitle);
        contentValues.put("post_body", postBody);


        try {
            long result = db.insert(TABLE_NAME_POST, null, contentValues);
            return result != -1;
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error inserting post: " + e.getMessage());
            return false;
        }
    }

    // Method to fetch all posts
    public Cursor getAllPosts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME_POST + " ORDER BY created_at DESC", null);
    }


    public Boolean checkUsername(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_USER + " WHERE username = ?", new String[]{username});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_USER + " WHERE username = ? AND password = ?", new String[]{username, password});

        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }




    public String getProfilePicture(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME_USER, new String[]{"profile_picture"},
                "id = ?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String imageUri = cursor.getString(cursor.getColumnIndex("profile_picture"));
            cursor.close();
            return imageUri; // Return the URI string or null if no profile picture is set
        }

        return null; // Return null if no profile picture is found
    }

    public void updateUserProfileImage(String username, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("profile_picture", imageUri);  // Assuming the column is 'profile_picture'

        // Update the 'allusers' table based on the username
        db.update("allusers", values, "username = ?", new String[]{username});
    }




    public Cursor getUserProfile(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("allusers", new String[]{"profile_picture"}, "username = ?", new String[]{username}, null, null, null);
    }

    public  String fetchFullnameForUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();  // Use 'this' since you're inside the DatabaseHelper class
        Cursor cursor = db.rawQuery("SELECT fullname FROM allusers WHERE username = ?", new String[]{username});

        if (cursor != null && cursor.moveToFirst()) {
            String fullname = cursor.getString(cursor.getColumnIndex("fullname"));
            cursor.close();
            return fullname;
        }

        return ""; // Return an empty string if no fullname is found
    }

    // Method to like a post
    public void likePost(int postId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Insert the like record into the likes table
        ContentValues values = new ContentValues();
        values.put("post_id", postId);
        values.put("user_id", userId);
        db.insert(TABLE_NAME_LIKE, null, values);

        // Increment the like count in the posts table
        db.execSQL("UPDATE " + TABLE_NAME_POST + " SET like_count = like_count + 1 WHERE post_id = ?", new Object[]{postId});

        db.close();
    }


    // Method to unlike a post
    public void unlikePost(int postId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete the like record from the likes table
        db.delete(TABLE_NAME_LIKE, "post_id = ? AND user_id = ?",
                new String[]{String.valueOf(postId), String.valueOf(userId)});

        // Decrement the like count in the posts table
        db.execSQL("UPDATE " + TABLE_NAME_POST + " SET like_count = like_count - 1 WHERE post_id = ?", new Object[]{postId});

        db.close();
    }


    // Method to check if a user has liked a post
    public boolean isPostLikedByUser(int postId, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to check if the user has already liked the post
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_LIKE +
                        " WHERE post_id = ? AND user_id = ?",
                new String[]{String.valueOf(postId), String.valueOf(userId)});

        boolean isLiked = cursor.getCount() > 0;
        cursor.close();
        return isLiked;
    }



    // Retrieve all comments for a specific post
    // In DatabaseHelper class
    public List<Comment> getCommentsForPost(int postId) {
        List<Comment> comments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT comments.comment_id, comments.post_id, comments.user_id, comments.comment_text, " +
                "comments.created_at, comments.deleted, allusers.fullname, allusers.username " +
                "FROM comments " +
                "JOIN allusers ON comments.user_id = allusers.id " +
                "WHERE comments.post_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(postId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int commentId = cursor.getInt(cursor.getColumnIndex("comment_id"));
                int postIdValue = cursor.getInt(cursor.getColumnIndex("post_id"));
                int userId = cursor.getInt(cursor.getColumnIndex("user_id"));
                String commentText = cursor.getString(cursor.getColumnIndex("comment_text"));
                String createdAt = cursor.getString(cursor.getColumnIndex("created_at"));
                boolean deleted = cursor.getInt(cursor.getColumnIndex("deleted")) == 1;
                String fullName = cursor.getString(cursor.getColumnIndex("fullname"));
                String username = cursor.getString(cursor.getColumnIndex("username"));

                // Create a new Comment object using the updated constructor
                Comment comment = new Comment(commentId, postIdValue, userId, commentText, createdAt, deleted, fullName, username);
                comments.add(comment);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return comments;
    }


    // (Optional) Delete or soft-delete a comment
    public int deleteComment(int commentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("deleted", 1); // Mark as deleted

        return db.update(TABLE_NAME_COMMENT, values, "comment_id = ?", new String[]{String.valueOf(commentId)});
    }

    public long insertComment(Comment comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("post_id", comment.getPostId());
        values.put("user_id", comment.getUserId());
        values.put("comment_text", comment.getCommentText());
        values.put("created_at", comment.getCreatedAt());
        values.put("deleted", comment.isDeleted() ? 1 : 0); // Convert boolean to integer

        return db.insert(TABLE_NAME_COMMENT, null, values);
    }


    // In your DatabaseHelper class
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1;

        Cursor cursor = db.rawQuery("SELECT id FROM allusers WHERE username = ?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
        }
        return userId;
    }

    public Cursor searchPosts(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "SELECT * FROM posts WHERE post_title LIKE ? OR post_body LIKE ?";
        return db.rawQuery(searchQuery, new String[]{"%" + query + "%", "%" + query + "%"});
    }

























}
