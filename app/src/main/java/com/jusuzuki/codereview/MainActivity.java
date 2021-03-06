package com.jusuzuki.codereview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.signupActivityButton) Button signupActivityButton;
    @Bind(R.id.gotoLoginButton) Button gotoLoginButton;
    @Bind(R.id.logoutButton) Button logoutButton;
    @Bind(R.id.uploadPhotoButton) Button uploadPhotoButton;
    @Bind(R.id.takePhotoButton) Button takePhotoButton;
    @Bind(R.id.usernameDisplay) TextView usernameDisplay;
    @Bind(R.id.viewPhoto) ImageView mImageView;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    protected Uri mMediaUri;
    String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null){
            logoutButton.setVisibility(View.VISIBLE);
            usernameDisplay.setText("Welcome " + currentUser.getUsername().toString());
        } else {
            usernameDisplay.setVisibility(View.INVISIBLE);
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Toast.makeText(getApplicationContext(), "You have been logged out", Toast.LENGTH_SHORT).show();
                usernameDisplay.setVisibility(View.INVISIBLE);
                logoutButton.setVisibility(View.INVISIBLE);
            }
        });

        signupActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        gotoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                choosePhotoIntent.setType("image/*");
                startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
            }
        });

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);
            galleryAddPic();
            Toast.makeText(getApplicationContext(),"HEY THERE!!",Toast.LENGTH_SHORT).show();
            if (requestCode == PICK_PHOTO_REQUEST){
                if (data == null){
                    Toast.makeText(getApplicationContext(),"PROBLEM DATA NULL",Toast.LENGTH_SHORT).show();
                }
                else {
                    mMediaUri = data.getData();
                    try {
                        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mMediaUri);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmapImage.compress(Bitmap.CompressFormat.PNG, 10, stream);
                        byte[] byteArray = stream.toByteArray();
                        ParseFile file = new ParseFile("image.png", byteArray);
                        ParseObject object = new ParseObject("Image");
                        object.put("image", file);
                        ParseACL parseACL = new ParseACL();
                        parseACL.setPublicReadAccess(true);
                        object.setACL(parseACL);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if( e == null) {
                                    Toast.makeText(getApplication().getBaseContext(), "Your image has been posted", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplication().getBaseContext(), "There was an error - please try again", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    } catch (IOException e) {
                        Toast.makeText(getApplication().getBaseContext(), "There was an error - please try again", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                }
            }
        }
        else if (resultCode != RESULT_CANCELED){
            Toast.makeText(getApplicationContext(),"PROBLEM",Toast.LENGTH_SHORT).show();
        }
    }


}
