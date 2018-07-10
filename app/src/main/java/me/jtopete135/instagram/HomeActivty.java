package me.jtopete135.instagram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

import me.jtopete135.instagram.model.Post;

public class HomeActivty extends AppCompatActivity {

    private static String imagePath = "/sdcard/DCIM/Camera/IMG_20180709_172945.jpg";
    private EditText etDescription;
    private Button btnCreate;
    private Button btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activty);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_foreground);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        etDescription = findViewById(R.id.etDescription);
        btnCreate = findViewById(R.id.btnCreate);
        btnRefresh = findViewById(R.id.btnRefresh);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String description = etDescription.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();
                final File file = new File(imagePath);
                final ParseFile parseFile = new ParseFile(file);
                createPost(description, parseFile, user );
            }
        });

        //Listener for refresh button
        btnRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                loadTopPosts();
            }
        });


    }

    private void createPost(String description, ParseFile imageFile, ParseUser user){
        final Post newPost = new Post();
        newPost.setDescription(description);
        newPost.setImage(imageFile);
        newPost.setUser(user);

        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Log.d("HomeActivity", "create post success");
                }
                else{
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadTopPosts(){
        final Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();

        //get posts
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e == null){
                    for(int i = 0; i < objects.size();i++){
                        Log.d("HomeActivity","Post[" + i + "] ="
                                + objects.get(i).getDescription()
                                + "\nusername = " + objects.get(i).getUser().getUsername());
                    }

                }
                else{
                    e.printStackTrace();
                }
            }
        });

    }
}
