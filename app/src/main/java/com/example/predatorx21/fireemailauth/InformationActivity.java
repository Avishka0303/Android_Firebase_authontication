package com.example.predatorx21.fireemailauth;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InformationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG="InformationActivity";
    private FirebaseUser user;

    //fire base database elements.
    FirebaseDatabase database;
    DatabaseReference myref,myname;

    private EditText msgTxt;
    private TextView username, emailtxt, userid, contactno,infoTxt,result;
    private ImageView profilePicView;
    private Button signoutbtn,emailBtn,recBtn,senBtn,editBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        username = (TextView) findViewById(R.id.usernametxt);
        emailtxt = (TextView) findViewById(R.id.emailtxt);
        userid = (TextView) findViewById(R.id.uidtxt);
        contactno = (TextView) findViewById(R.id.contxt);
        result = (TextView) findViewById(R.id.resultTxt);
        profilePicView = (ImageView) findViewById(R.id.imageView);
        signoutbtn = (Button) findViewById(R.id.signOutBtn);
        infoTxt=(TextView)findViewById(R.id.notifiTxt);
        emailBtn=(Button)findViewById(R.id.emailverifyBtn);
        recBtn=(Button)findViewById(R.id.recieveBtn);
        senBtn=(Button)findViewById(R.id.sendBtn);
        editBtn=(Button)findViewById(R.id.editDetailBtn);
        msgTxt=(EditText)findViewById(R.id.msgTxt);

        signoutbtn.setOnClickListener(this);
        emailBtn.setOnClickListener(this);
        senBtn.setOnClickListener(this);
        recBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);

        user = MainActivity.mAuth.getCurrentUser();
        setInitialInfo(user);

        //database initialization
        database=FirebaseDatabase.getInstance();
        myref=database.getReference("message");

        if(myref!=null){
            Toast.makeText(InformationActivity.this,"My ref got the message instance",Toast.LENGTH_SHORT);
        }

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
        contactno.setText(userPhoneNumber);

        profilePicView.setImageURI(photoUrl);
        profilePicView.setImageURI(photoUrl);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signOutBtn) {
            signOutFromGoogle();
        }else if(view.getId()==R.id.emailverifyBtn){
            verifyTheEmail();
        }else if(view.getId()==R.id.sendBtn){
            sendMessage();
        }else if(view.getId()==R.id.recieveBtn){
            recieveMessage();
        }else if(view.getId()==R.id.editDetailBtn){
            Intent intent=new Intent(".EditDisplayActivity");
            startActivity(intent);
        }
    }

    //send msg to the message
    private void sendMessage() {
        String message=msgTxt.getText().toString();
        if(TextUtils.isEmpty(message)){
           msgTxt.setError("Msg Requied.");
        }
        myref.setValue(message);
    }

    //database msg recieve
    private void recieveMessage() {
        Toast.makeText(InformationActivity.this,"Value : reading",Toast.LENGTH_SHORT);
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                result.setText(value);
                Toast.makeText(InformationActivity.this,"Value : "+value,Toast.LENGTH_SHORT);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

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