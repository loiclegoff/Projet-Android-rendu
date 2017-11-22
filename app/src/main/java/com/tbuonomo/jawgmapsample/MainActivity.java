package com.tbuonomo.jawgmapsample;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbuonomo.jawgmapsample.data.model.IPMLoginResponse;
import com.tbuonomo.jawgmapsample.data.remote.ApiUtils;
import com.tbuonomo.jawgmapsample.data.remote.IPMService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class
MainActivity extends AppCompatActivity {

  private Intent backgroundActivity;
  private IPMService mIPMService;
  private String username;
  private String password;
  private static final String TAG = "LoginPage";

  private EditText usernameInput;
  private EditText passwordInput;
  private String token;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.login);

    //Start the background activity
    backgroundActivity = new Intent(MainActivity.this, BackgroundActivity.class);
    startService(backgroundActivity);

    //Init Web service
    mIPMService = ApiUtils.getIPMService();
    token = "";

    //Getting components
    final RelativeLayout loginView = findViewById(R.id.loginView);
    final TextView title = findViewById(R.id.title);
    final Button loginButton = findViewById(R.id.loginButton);
    final TextInputLayout usernameLayout = findViewById(R.id.usernameLayout);
    final TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);
    final TextView forgotText = findViewById(R.id.forgotText);
    usernameInput = findViewById(R.id.username);
    passwordInput = findViewById(R.id.password);
    loginView.setAlpha(0);

    //Loading fonts
    Typeface cabinSketch = Typeface.createFromAsset(getAssets(), "CabinSketch.ttf");

    //Setting fonts
    title.setTypeface(cabinSketch);
    loginButton.setTypeface(cabinSketch);
    usernameInput.setTypeface(cabinSketch);
    passwordInput.setTypeface(cabinSketch);
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

        username = usernameInput.getText().toString();
        password = passwordInput.getText().toString();
        checkLogin(username, password);

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


  public void checkLogin(String username, String password) {
    mIPMService.getToken(username,password).enqueue(new Callback<IPMLoginResponse>() {

      @Override
      public void onResponse(Call<IPMLoginResponse> call, Response<IPMLoginResponse> response) {
        if(response.isSuccessful()) {
          showResponse(response.body());
          Log.i(TAG, "post submitted to API." + response.body().toString());
        }
      }

      @Override
      public void onFailure(Call<IPMLoginResponse> call, Throwable t) {
        Toast.makeText(MainActivity.this, "Unable to connect to the server", Toast.LENGTH_SHORT).show();
      }
    });
  }

  public void showResponse(IPMLoginResponse response) {
    Log.d(TAG, "response: "+ response);

    if(response.getSuccess()){
      token = response.getToken();
      Toast.makeText(MainActivity.this, "Connection established", Toast.LENGTH_SHORT).show();
      Intent intent = new Intent(MainActivity.this, ScreenSlidePagerActivity.class);
      intent.putExtra("USER_TOKEN",token);
      startActivity(intent);

    } else {
      usernameInput.setText("");
      passwordInput.setText("");
      Toast.makeText(MainActivity.this, "Wrong username or Password", Toast.LENGTH_SHORT).show();
    }
  }


}
