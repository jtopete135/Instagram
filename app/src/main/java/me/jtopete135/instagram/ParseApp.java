package me.jtopete135.instagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import me.jtopete135.instagram.model.Post;

public class ParseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("mr-t")
                .clientKey("a_team")
                .server("http://jtopete135-fbu-instagram.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);


    }
}
