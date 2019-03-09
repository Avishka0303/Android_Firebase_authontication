package com.example.predatorx21.fireemailauth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG="MainActivity";

    public static FirebaseAuth mAuth;
    public static GoogleSignInClient signInClient;

    private static final int RC_KEY=9001;
    private EditText emailTxt,passTxt;

    //initialize android lifecycle states .---------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //initialize the sign in client.
        signInClient=GoogleSignIn.getClient(this,gso);

        //get an instance of firebase auth.
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.everifyBtn).setOnClickListener(this);
        findViewById(R.id.signNewbtn).setOnClickListener(this);
        findViewById(R.id.signInbtn).setOnClickListener(this);
        findViewById(R.id.signOutbtn).setOnClickListener(this);
        findViewById(R.id.gsignbtn).setOnClickListener(this);

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
        }else if (view.getId()==R.id.gsignbtn){
            googleSignIn();
        }
    }

    //========================================================================RESERVED FOR GOOGLE================================================

    private void googleSignIn() {
        Intent siginIntent=signInClient.getSignInIntent();
        startActivityForResult(siginIntent,RC_KEY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_KEY) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent=new Intent(".InformationActivity");
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    //=================================================================================RESERVED FINISH FOR GOOGLE========================================================


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
                    Intent intent=new Intent(".InformationActivity");
                    startActivity(intent);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed. You are not registered here",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        FirebaseUser user=mAuth.getCurrentUser();
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
