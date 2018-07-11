package me.jtopete135.instagram;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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

import me.ProfileFragment;
import me.jtopete135.instagram.model.Post;

public class HomeActivty extends AppCompatActivity implements ProfileFragment.OnItemSelectedListener {

    private static String imagePath = "/sdcard/DCIM/Camera/IMG_20180709_172945.jpg";

    ProfileFragment pFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activty);

        pFragment = (ProfileFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frag_placeholder);


        final FragmentManager fragmentManager = getSupportFragmentManager();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_foreground);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        final Fragment homeFragment = new HomeFragment();
        final Fragment composeFragment = new ComposeFragment();
        final Fragment profileFragment = new ProfileFragment();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);


        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                                fragmentTransaction1.replace(R.id.frag_placeholder, homeFragment).commit();
                                return true;
                            case R.id.action_compose:
                                FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                                fragmentTransaction2.replace(R.id.frag_placeholder, composeFragment).commit();
                                return true;
                            case R.id.action_profile:
                                FragmentTransaction fragmentTransaction3 = fragmentManager.beginTransaction();
                                fragmentTransaction3.replace(R.id.frag_placeholder, profileFragment).commit();
                                return true;
                        }
                        return false;
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

    @Override
    public void onRssItemSelected(String fragment) {
        if (fragment == "profile"){
            ParseUser.logOut();
            Intent intent = new Intent(HomeActivty.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }
}
