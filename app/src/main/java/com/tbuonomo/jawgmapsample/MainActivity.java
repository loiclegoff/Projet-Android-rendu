package com.tbuonomo.jawgmapsample;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class
MainActivity extends AppCompatActivity {

  private Intent backgroundActivity;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.login);

    //Start the background activity
    backgroundActivity = new Intent(MainActivity.this, BackgroundActivity.class);
    startService(backgroundActivity);

    //Getting components
    final RelativeLayout loginView = findViewById(R.id.loginView);
    final TextView title = findViewById(R.id.title);
    final Button loginButton = findViewById(R.id.loginButton);
    final EditText username = findViewById(R.id.username);
    final EditText password = findViewById(R.id.password);
    final TextInputLayout usernameLayout = findViewById(R.id.usernameLayout);
    final TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);
    final TextView forgotText = findViewById(R.id.forgotText);
    loginView.setAlpha(0);

    //Loading fonts
    Typeface cabinSketch = Typeface.createFromAsset(getAssets(), "CabinSketch.ttf");

    //Setting fonts
    title.setTypeface(cabinSketch);
    loginButton.setTypeface(cabinSketch);
    username.setTypeface(cabinSketch);
    password.setTypeface(cabinSketch);
    usernameLayout.setTypeface(cabinSketch);
    passwordLayout.setTypeface(cabinSketch);


    //Title animation at the beginning
    ObjectAnimator translateTitle = ObjectAnimator.ofFloat(title,"translationY", -400);
    translateTitle.setDuration(1000);
    translateTitle.start();

    //Listener to launch the second animation at the end of the first one
    translateTitle.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {

        //Animation on the text fields
        ObjectAnimator openCard = ObjectAnimator.ofFloat(loginView,"alpha",1);
        openCard.setDuration(1000);
        openCard.start();
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });

    //Login button listener
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        //TODO: POST request to the server with credentials
        Intent intent = new Intent(MainActivity.this, ScreenSlidePagerActivity.class);
        startActivity(intent);
      }
    });

    //Forgot text listener
    forgotText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        //TODO: Make credentials recovery methods
        Toast.makeText(MainActivity.this, "Work in progress...", Toast.LENGTH_SHORT).show();
      }
    });

  }

  @Override public void onStart() {
    super.onStart();
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override public void onPause() {
    super.onPause();
  }

  @Override public void onStop() {
    super.onStop();
  }

  @Override public void onLowMemory() {
    super.onLowMemory();
  }

  @Override protected void onDestroy() {
    stopService(backgroundActivity);
    super.onDestroy();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }
}
