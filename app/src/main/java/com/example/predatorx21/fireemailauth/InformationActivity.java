package com.example.predatorx21.fireemailauth;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

public class InformationActivity extends AppCompatActivity {

    private FirebaseUser user;
    private TextView username,emailtxt,userid,contactno;
    private ImageView profilePicView;
    private Button signoutbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        username=(TextView) findViewById(R.id.usernametxt);
        emailtxt=(TextView) findViewById(R.id.emailtxt);
        userid=(TextView) findViewById(R.id.uidtxt);
        contactno=(TextView) findViewById(R.id.contxt);
        profilePicView=(ImageView)findViewById(R.id.imageView);

        user=MainActivity.mAuth.getCurrentUser();
        setInitialInfo(user);
    }

    private void setInitialInfo(FirebaseUser user) {

        String userName=user.getDisplayName();
        String userEmail=user.getEmail();
        String userPhoneNumber=user.getPhoneNumber();
        String userId=user.getUid();
        Uri photoUrl=user.getPhotoUrl();

        if(photoUrl==null){
            Toast.makeText(InformationActivity.this, "No Image URL founded.", Toast.LENGTH_SHORT).show();
        }

        username.setText(userName);
        emailtxt.setText(userEmail);
        userid.setText(userId);
        contactno.setText(userPhoneNumber);
        profilePicView.setImageURI(photoUrl);


    }
}
