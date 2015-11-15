package com.jusuzuki.codereview;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

/**
 * Created by jusuzuki on 11/14/15.
 */
public class CodeReviewApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "egCjRv5O7CKsXiy9uhW4HQSY6Jena1LJzGqsu3Us", "tgrTMSmEnu00Tq19ou7fNpahatxwjzizZBIt8JJw");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }



}
