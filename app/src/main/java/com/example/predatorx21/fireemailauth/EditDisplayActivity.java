package com.example.predatorx21.fireemailauth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class EditDisplayActivity extends Activity implements View.OnClickListener{

    private static final int SELECT_PHOTO=1;

    private Button saveBtn,changeImage,showDetail;
    private EditText username,age,contact,status;
    private ImageView profilePic;

    //object referece for database reference.
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_display);

        //initialize the editable componenets
        username=(EditText)findViewById(R.id.unameTxt);
        age=(EditText)findViewById(R.id.ageTxt);
        contact=(EditText)findViewById(R.id.cnoTxt);
        status=(EditText)findViewById(R.id.statusTxt);
        profilePic=(ImageView)findViewById(R.id.profilePic);

        //initialize all buttons
        saveBtn=(Button)findViewById(R.id.saveBtn);
        changeImage=(Button)findViewById(R.id.chImagBtn);
        showDetail=(Button)findViewById(R.id.showProfileBtn);

        //add action listener to the button .
        saveBtn.setOnClickListener(this);
        changeImage.setOnClickListener(this);

        //initialize the storage reference.
        storageReference=FirebaseStorage.getInstance().getReference();

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.saveBtn){
            saveDetails();
        }else if(view.getId()==R.id.chImagBtn){
            changeImage();
        }else if(view.getId()==R.id.showProfileBtn){
            Intent intent=new Intent(".ShowProfileActivity");
            startActivity(intent);
        }
    }

    private void changeImage() {

        Intent photoPicIntent=new Intent(Intent.ACTION_PICK);
        photoPicIntent.setType("image/*");
        startActivityForResult(photoPicIntent,SELECT_PHOTO);

        //store image
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri;
        if(requestCode==SELECT_PHOTO && resultCode==RESULT_OK){
            imageUri=data.getData();

            try {
                final InputStream imageStream=getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage=MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                profilePic.setImageBitmap(null);
                profilePic.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            storeImage(imageUri);
        }
    }

    private void storeImage(Uri imageUri){
        storageReference.child("Images").child("").putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(EditDisplayActivity.this,"Image Upload Successfull",Toast.LENGTH_SHORT);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void saveDetails() {
        if(!isValid()){
            return;
        }
        databaseReference=FirebaseDatabase.getInstance().getReference();

        String un=username.getText().toString();
        String ag=age.getText().toString();
        String con=contact.getText().toString();
        String statust=status.getText().toString();

        String userId=MainActivity.mAuth.getCurrentUser().getUid();

        HashMap<String,String> detail=new HashMap<>();
        detail.put("Name",un);
        detail.put("Age",ag);
        detail.put("Contact",con);
        detail.put("Status",statust);
        databaseReference.child(userId).setValue(detail);
    }

    private boolean isValid() {
        boolean flag=true;

        String un=username.getText().toString();
        String ag=age.getText().toString();
        String con=contact.getText().toString();
        String statust=status.getText().toString();

        if(TextUtils.isEmpty(un)||TextUtils.isEmpty(ag)||TextUtils.isEmpty(con))  flag=false;
        if(TextUtils.isEmpty(un))   username.setError("Required. ");
        if(TextUtils.isEmpty(ag))   age.setError("Required. ");
        if(TextUtils.isEmpty(con))  contact.setError("Required. ");
        if(TextUtils.isEmpty(statust))  status.setError("Required. ");

        return flag;
    }
}
