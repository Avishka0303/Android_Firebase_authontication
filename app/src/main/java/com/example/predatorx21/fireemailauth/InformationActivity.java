package com.example.predatorx21.fireemailauth;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InformationActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser user;
    private TextView username, emailtxt, userid, contactno,infoTxt;
    private ImageView profilePicView;
    private Button signoutbtn,emailBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        username = (TextView) findViewById(R.id.usernametxt);
        emailtxt = (TextView) findViewById(R.id.emailtxt);
        userid = (TextView) findViewById(R.id.uidtxt);
        contactno = (TextView) findViewById(R.id.contxt);
        profilePicView = (ImageView) findViewById(R.id.imageView);
        signoutbtn = (Button) findViewById(R.id.signOutBtn);
        infoTxt=(TextView)findViewById(R.id.notifiTxt);
        emailBtn=(Button)findViewById(R.id.emailverifyBtn);

        signoutbtn.setOnClickListener(this);
        emailBtn.setOnClickListener(this);

        user = MainActivity.mAuth.getCurrentUser();
        setInitialInfo(user);
    }

    private void setInitialInfo(FirebaseUser user) {

        String userName = user.getDisplayName();
        String userEmail = user.getEmail();
        String userPhoneNumber = user.getPhoneNumber();
        String userId = user.getUid();
        Uri photoUrl = user.getPhotoUrl();
        boolean isverified=user.isEmailVerified();

        if(isverified) {
            infoTxt.setText(" Your email being verified.");
            emailBtn.setVisibility(View.INVISIBLE);
        }else{
            infoTxt.setText(" Your email has not being verified yet.");
            emailBtn.setVisibility(View.VISIBLE);
        }

        if (photoUrl == null) {
            Toast.makeText(InformationActivity.this, "No Image URL founded.", Toast.LENGTH_SHORT).show();
        }

        username.setText(userName);
        emailtxt.setText(userEmail);
        userid.setText(userId);

        profilePicView.setImageURI(photoUrl);
        profilePicView.setImageURI(photoUrl);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signOutBtn) {
            signOutFromGoogle();
        }else if(view.getId()==R.id.emailverifyBtn){
            verifyTheEmail();
        }
    }

    private void verifyTheEmail() {
        final FirebaseUser user=MainActivity.mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(InformationActivity.this,
                            "Verification email sent to " + user.getEmail(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("InformationActivity", "sendEmailVerification", task.getException());
                    Toast.makeText(InformationActivity.this,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signOutFromGoogle() {

        MainActivity.mAuth.signOut();
        FirebaseAuth.getInstance().signOut();
        MainActivity.signInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(InformationActivity.this, "Sign Out From Google Started. and REvoked hahaha", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}