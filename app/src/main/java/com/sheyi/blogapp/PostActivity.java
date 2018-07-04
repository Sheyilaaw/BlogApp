package com.sheyi.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class PostActivity extends AppCompatActivity {
    private ImageButton mSelectImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitBtn;
    private static final int GALLERY_REQUEST = 1;

    private  Uri mImageUri ;
    private StorageReference mStorage;
    private DatabaseReference mDataBase;
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mPostTitle = findViewById(R.id.titleField);
        mPostDesc = findViewById(R.id.descField);
        mSelectImage = findViewById(R.id.imageSelect);
        mSubmitBtn = findViewById(R.id.submit);
        mProgress =     new ProgressDialog(this);
        mSelectImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent,GALLERY_REQUEST);
                    }
                }
        );

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    private  void startPosting(){

        final String titleValue = mPostTitle.getText().toString().trim();
        final String descValue  = mPostDesc.getText().toString().trim();
        if(TextUtils.isEmpty(titleValue))
            Toast.makeText(getApplicationContext(),"Kindly Enter Title",Toast.LENGTH_SHORT).show();

        else if(TextUtils.isEmpty(descValue))
            Toast.makeText(getApplicationContext(),"Kindly Enter Description",Toast.LENGTH_SHORT).show();

        else if(mImageUri==null)
            Toast.makeText(getApplicationContext(),"Kindly Upload Image",Toast.LENGTH_SHORT).show();

        else {
            mProgress.setMessage("Posting To Blog...");
            mProgress.show();
            StorageReference filepath = mStorage.child("Blog_Images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    DatabaseReference newPost = mDataBase.push();
                    newPost.child("Title").setValue(titleValue);
                    newPost.child("Desc").setValue(descValue);
                    if (downloadUrl != null) {
                        newPost.child("image").setValue(downloadUrl.toString());
                    }
                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(),"Posted Successfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST){
            mImageUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();
            mSelectImage.setImageURI(resultUri);
        }
    }
}
