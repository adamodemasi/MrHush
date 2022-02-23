package msc.unical.it.mrhush.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.InputStream;

import msc.unical.it.mrhush.Handlers.HabitsHandler;
import msc.unical.it.mrhush.Handlers.ImageHelper;
import msc.unical.it.mrhush.Handlers.PrefManager;
import msc.unical.it.mrhush.Logic.MrHushService;
import msc.unical.it.mrhush.R;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1234;
    Intent serviceIntent;
    private PrefManager p;
    private SignInButton loginButton;
    private Button settingsButton;
    private Button homeButton;
    private Button logoutButton;
    private TextView pleaseLogin;
    private ImageView imgProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serviceIntent = new Intent(getApplicationContext(), MrHushService.class);
        startService(serviceIntent);

        setContentView(R.layout.activity_login);

        loginButton = (SignInButton) findViewById(R.id.sign_in_button);
        settingsButton = (Button) findViewById(R.id.settings);
        homeButton = (Button) findViewById(R.id.GoToMyHabits);
        logoutButton = (Button) findViewById(R.id.logout);
        pleaseLogin=(TextView) findViewById(R.id.pleaselogin);
        imgProfilePic = (ImageView) findViewById(R.id.img_profile_pic);

        p = new PrefManager(this);
        String user = p.isAuthenticatedUser();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        settingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(LoginActivity.this, SettingsActivity.class);
                startActivity(settings);
            }
        });

        homeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToHomepage = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(goToHomepage);
            }
        });
        logoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener
                                (LoginActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        p.removeAuthUser();
                                        HabitsHandler.getInstance().setAuthUserID(null);
                                        loginButton.setVisibility(View.VISIBLE);
                                        logoutButton.setVisibility(View.GONE);
                                        homeButton.setVisibility(View.GONE);
                                        settingsButton.setVisibility(View.GONE);
                                        imgProfilePic.setVisibility(View.GONE);
                                        pleaseLogin.setVisibility(View.VISIBLE);
                                    }

                                });
            }
        });

        if (user != null) {
            loginButton.setVisibility(View.GONE);
            settingsButton.setVisibility(View.VISIBLE);
            homeButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            pleaseLogin.setVisibility(View.GONE);
            imgProfilePic.setVisibility(View.VISIBLE);
            String photo = p.getProfilePicURL();

            if (photo != null) {
                ImageView imgProfilePic = (ImageView) findViewById(R.id.img_profile_pic);
                new LoadProfileImage(imgProfilePic).execute(photo);
            }

        }
        else
            pleaseLogin.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            p.setAuthenticatedUser(account.getId());

            if (account.getPhotoUrl() != null) {
                p.setProfilePicURL(account.getPhotoUrl().toString());

                new LoadProfileImage(imgProfilePic).execute(account.getPhotoUrl().toString());
            }

            // Signed in successfully, show authenticated UI.
            loginButton.setVisibility(View.GONE);
            homeButton.setVisibility(View.VISIBLE);
            settingsButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            pleaseLogin.setVisibility(View.GONE);
            imgProfilePic.setVisibility(View.VISIBLE);


        } catch (ApiException e) {
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... uri) {
            String url = uri[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {

                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            if (result != null) {

                Bitmap resized = Bitmap.createScaledBitmap(result, 200, 200, true);
                bmImage.setImageBitmap(ImageHelper.getRoundedCornerBitmap(getApplicationContext(), resized,
                        250, 200, 200, false, false, false, false));
            }
        }
    }
}

