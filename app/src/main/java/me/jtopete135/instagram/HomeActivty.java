package me.jtopete135.instagram;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HomeActivty extends AppCompatActivity implements ProfileFragment.OnItemSelectedListener {


    private final int RESIZE_WIDTH = 400;

    final private Fragment homeFragment = new HomeFragment();
    private  Fragment composeFragment = new ComposeFragment();
    final private Fragment profileFragment = new ProfileFragment();

    final FragmentManager fragmentManager = getSupportFragmentManager();

    public final String APP_TAG = "Instagram";
    public final static int COMPOSE_REQUEST_CODE = 1034;
    public final static int PROFILE_REQUEST_CODE = 1035;
    public String photoFileName = "photo.jpg";
    File photoFile;

    Boolean newFrag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activty);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        //have Acvtivity start on home fragment
        FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
        fragmentTransaction1.replace(R.id.frag_placeholder, homeFragment).commit();


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
                                try {
                                    onLaunchCamera(findViewById(R.id.frag_placeholder),COMPOSE_REQUEST_CODE);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return true;
                            case R.id.action_profile:
                                FragmentTransaction fragmentTransaction3 = fragmentManager.beginTransaction();
                                fragmentTransaction3.replace(R.id.frag_placeholder, profileFragment).commit();
                                return true;
                        }
                        findViewById(R.id.action_home).setSelected(false);
                        return false;
                    }
                });

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    public void onLaunchCamera(View view, int requestCode) throws IOException {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(HomeActivty.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);


        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, requestCode);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(newFrag){
            newFrag = false;
            ComposeFragment cfrag = ComposeFragment.newInstance(photoFile.getAbsolutePath());
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frag_placeholder, cfrag).commit();
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COMPOSE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                newFrag = true;
            }
            else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == PROFILE_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                // See code above
                File takenPhotoUri = getPhotoFileUri(photoFileName);
// by this point we have the camera photo on disk
                Bitmap rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
// See BitmapScaler.java: https://gist.github.com/nesquena/3885707fd3773c09f1bb
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, RESIZE_WIDTH);
                ImageView iv = (((ProfileFragment)profileFragment).ivProfileImage);
                Glide.with(this).load(resizedBitmap).apply(RequestOptions.circleCropTransform()).into(iv);
                ParseFile parseImage = new ParseFile(photoFile);
                //updates users profile image
                ParseUser user = ParseUser.getCurrentUser();
                user.put("profileImage",parseImage);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("ProfileActivity", "Profile pic updated", e);
                    }
                });
            }
            else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRssItemSelected(int code) {
        if (code == ProfileFragment.LOGOUT_CODE){
            ParseUser.logOut();
            Intent intent = new Intent(HomeActivty.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(code == ProfileFragment.EDIT_CODE){
            try {
                onLaunchCamera(findViewById(R.id.frag_placeholder),PROFILE_REQUEST_CODE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public static class BitmapScaler {
        // Scale and maintain aspect ratio given a desired width
        // BitmapScaler.scaleToFitWidth(bitmap, 100);
        public static Bitmap scaleToFitWidth(Bitmap b, int width)
        {
            float factor = width / (float) b.getWidth();
            return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
        }


        // Scale and maintain aspect ratio given a desired height
        // BitmapScaler.scaleToFitHeight(bitmap, 100);
        public static Bitmap scaleToFitHeight(Bitmap b, int height)
        {
            float factor = height / (float) b.getHeight();
            return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
        }
    }
}
