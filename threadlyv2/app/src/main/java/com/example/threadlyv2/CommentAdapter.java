package com.example.threadlyv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final Context context;
    private List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged(); // Refresh the RecyclerView when the data changes
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);

        // Bind data to the views
        // Bind data to the views
        holder.fullNameTextView.setText(comment.getFullName()); // Use the full name directly
        holder.usernameTextView.setText("@" + comment.getUsername()); // Use the username directly
        holder.commentTextView.setText(comment.getCommentText());


        // Load profile picture using Glide or set a default image
        Glide.with(context)
                .load(R.drawable.ic_default_pfp) // Replace with actual profile picture URL if available
                .circleCrop()
                .placeholder(R.drawable.ic_default_pfp)
                .into(holder.profilePictureImageView);
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTextView, usernameTextView, commentTextView;
        ImageView profilePictureImageView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePictureImageView = itemView.findViewById(R.id.comment_profile_picture);
            fullNameTextView = itemView.findViewById(R.id.comment_fullname);
            usernameTextView = itemView.findViewById(R.id.comment_username);
            commentTextView = itemView.findViewById(R.id.comment_text);
        }
    }
}
