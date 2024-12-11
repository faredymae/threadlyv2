package com.example.threadlyv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "threadlyv2.db";
    public static final String TABLE_NAME_USER = "allusers";
    public static final String TABLE_NAME_POST = "posts";
    public static final String TABLE_NAME_COMMENT = "comments";
    public static final String TABLE_NAME_LIKE = "like";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 4); // Incremented version
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
                    "post_id INTEGER NOT NULL, " +
                    "user_id INTEGER NOT NULL, " +
                    "PRIMARY KEY(post_id, user_id), " + // Composite key to prevent duplicate likes
                    "FOREIGN KEY(post_id) REFERENCES " + TABLE_NAME_POST + "(post_id), " +
                    "FOREIGN KEY(user_id) REFERENCES " + TABLE_NAME_USER + "(id))");
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error creating tables: " + e.getMessage());
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_POST);
        onCreate(db);
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



















}
