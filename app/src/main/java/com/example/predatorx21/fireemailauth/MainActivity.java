package com.example.predatorx21.fireemailauth;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG="MainActivity";
    private FirebaseAuth mAuth;

    private EditText emailTxt,passTxt;
    //initialize android lifecycle states .---------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get an instance of firebase auth.
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.everifyBtn).setOnClickListener(this);
        findViewById(R.id.signNewbtn).setOnClickListener(this);
        findViewById(R.id.signInbtn).setOnClickListener(this);
        findViewById(R.id.signOutbtn).setOnClickListener(this);

        emailTxt=findViewById(R.id.emailF);
        passTxt=findViewById(R.id.passF);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    //----------------------------------------------------------------------------------------------
    @Override
    public void onClick(View view) {
        String email=emailTxt.getText().toString();
        String pass=passTxt.getText().toString();

        if(view.getId()==R.id.everifyBtn){
            emailVerification();
        }else if(view.getId()==R.id.signNewbtn){
            createAccount(email,pass);
        }else if(view.getId()==R.id.signInbtn){
            signIn(email,pass);
        }else if(view.getId()==R.id.signOutbtn){
            signOut();
        }
    }

    //-------------------------------------------------SIGN OUT---------------------------------------------------
    private void signOut() {
        mAuth.signOut();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user==null){
            Toast.makeText(MainActivity.this, "Sign outed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //-----------------------------------------------SIGN IN------------------------------------------------------
    private void signIn(String email, String password) {
        if(!validateForm()){
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    Toast.makeText(MainActivity.this, "Sign in email : success",
                            Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    //updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed. You are not registered here",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            Toast.makeText(MainActivity.this,"sign in sucessfull......",Toast.LENGTH_SHORT);
        }
    }

    private void updateUI(Object o) {

    }

    //-------------------------------------------------CREATE ACCOUNT---------------------------------------------

    private void createAccount(String email, String password) {
        if(!validateForm()){
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    //updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }
            }
        });
    }


    private void emailVerification() {
        if(!validateForm()){
            return;
        }
        findViewById(R.id.everifyBtn).setEnabled(false);
        final FirebaseUser user=mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                findViewById(R.id.everifyBtn).setEnabled(true);
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this,
                            "Verification email sent to " + user.getEmail(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "sendEmailVerification", task.getException());
                    Toast.makeText(MainActivity.this,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //------------------------------------------------------------------VALIDATE THE FORM---------------------------------------
    private boolean validateForm() {
        boolean flag=true;

        String email=emailTxt.getText().toString();
        String pass=passTxt.getText().toString();

        if(TextUtils.isEmpty(email)){
            emailTxt.setError("Required.");
            flag=false;
        }
        if(TextUtils.isEmpty(pass)){
            passTxt.setError("Required.");
            flag=false;
        }
        return flag;
    }
}
