package me.jtopete135.instagram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.jtopete135.instagram.model.Post;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private Context context;
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

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(Post post) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        DateFormat df = new SimpleDateFormat(twitterFormat,Locale.getDefault());
        String date = df.format(post.getDate());
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(date).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        TextView tvUsername = holder.tvUsername;
        TextView tvDescription = holder.tvDescription;
        TextView tvUsername2 = holder.tvUsername2;
        TextView tvTime = holder.tvTime;
        ParseImageView ivPostImage = holder.ivPostImage;
        ParseImageView ivProfileImage = holder.ivProfileImage;
        String time = getRelativeTimeAgo(post);


        tvUsername2.setText(post.getUser().getUsername());
        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        tvTime.setText(time);

        if(post.getUser().get("profileImage") != null){
            ParseFile file = post.getUser().getParseFile("profileImage");
            Glide.with(context).load(file.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivProfileImage);
            ivProfileImage.loadInBackground();
        }


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
        public TextView tvUsername2;
        public TextView tvTime;
        public ParseImageView ivProfileImage;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            ivPostImage = (ParseImageView) itemView.findViewById(R.id.ivPostImage);
            tvUsername2 = (TextView) itemView.findViewById(R.id.tvUsername2);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            ivProfileImage = (ParseImageView) itemView.findViewById(R.id.ivProfileImage);



        }
    }


}
