package me.jtopete135.instagram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseFile;
import com.parse.ParseImageView;

import java.util.List;

import me.jtopete135.instagram.model.Post;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    Context context;
    private List<Post> mPosts;

    public FeedAdapter(List<Post> posts) {
        mPosts = posts;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View feedView = inflater.inflate(R.layout.post_list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(feedView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        TextView tvUsername = holder.tvUsername;
        TextView tvDescription = holder.tvDescription;
        ParseImageView ivPostImage = holder.ivPostImage;


        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());


        ParseFile imageFile = post.getImage();


        ivPostImage.setParseFile(post.getImage());
        ivPostImage.loadInBackground();

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvUsername;
        public TextView tvDescription;
        public ParseImageView ivPostImage;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            ivPostImage = (ParseImageView) itemView.findViewById(R.id.ivPostImage);


        }
    }
}
