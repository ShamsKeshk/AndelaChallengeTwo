package com.example.andelachallengetwo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.provider.MediaStore.Images.Media;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdminActivity extends AppCompatActivity {

    @BindView(R.id.et_holiday_title_id)
    TextInputEditText mEditTextHoidayTitle;
    @BindView(R.id.et_holiday_description_id)
    TextInputEditText mEditTextHoidayDescription;
    @BindView(R.id.et_holiday_price_id)
    TextInputEditText mEditTextHoidayOfferPrice;
    @BindView(R.id.btn_upload_image)
    Button mButtonUploadImage;
    @BindView(R.id.iv_holiday_image_id)
    CircleImageView mImageViewHoliday;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private DatabaseReference mDatabaseReference;
    private static int RESULT_LOAD_IMAGE = 1;
    Uri selectedImage;

    Uri downloadUriImage;

    private static final double MB = 1000000.0;
    private static final double MB_THRESHHOLD = 5.0;
    private byte[] mBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);

        mProgressBar.setVisibility(View.GONE);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        mDatabaseReference = database.getReference();

        mButtonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                mImageViewHoliday.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            uploadImage(selectedImage);
        }
    }

    private void uploadImage(Uri filePath) {

        mProgressBar.setVisibility(View.VISIBLE);

        if(filePath != null)
        {
            StorageReference storageReference =
                    FirebaseStorage.getInstance()
                    .getReference().child(FIREBASE_HOLIDAY_IMAGE_STORAGE+"/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());

            UploadTask uploadTask = storageReference.putFile(filePath);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                       downloadUriImage  = task.getResult();
                        Toast.makeText(AdminActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_holiday_offer){
            saveHolidayOffer();
            return true;
        }else if (item.getItemId() == R.id.open_home_activity){
            Intent intent = new Intent(AdminActivity.this,HomeActivity.class);
            startActivity(intent);
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void saveHolidayOffer(){
        HolidayDeal holidayDeal = createHolidayDeal();
        if (holidayDeal != null) {
            mDatabaseReference.child("holiday_offers").child(FirebaseAuth.getInstance().getUid()).push().
                    setValue(holidayDeal).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(AdminActivity.this,HomeActivity.class));
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(),"Fail " + task.getException(),Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    String title;

    private HolidayDeal createHolidayDeal(){
        title = mEditTextHoidayTitle.getText().toString();
        if (title.matches("")) {
            Toast.makeText(this, "You did not enter a title", Toast.LENGTH_SHORT).show();
            return null;
        }
        String description = mEditTextHoidayDescription.getText().toString();
        if (description.matches("")) {
            Toast.makeText(this, "You did not enter a description", Toast.LENGTH_SHORT).show();
            return null;
        }
        String priceString = mEditTextHoidayOfferPrice.getText().toString();
        if (priceString.matches("")) {
            Toast.makeText(this, "Please enter the offer price !!", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (downloadUriImage == null){
            Toast.makeText(this, "Please upload image for this offer !!", Toast.LENGTH_SHORT).show();
            return null;
        }

        float price = Float.parseFloat(priceString);

        return new HolidayDeal(title,description,price,downloadUriImage.toString());

    }

    public void uploadNewPhoto(Uri imageUri){
        BackgroundImageResize resize = new BackgroundImageResize(null);
        resize.execute(imageUri);
    }

    private static final String TAG = AdminActivity.class.getSimpleName();
    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {

        Bitmap mBitmap;
        public BackgroundImageResize(Bitmap bm) {
            if(bm != null){
                mBitmap = bm;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "compressing image", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected byte[] doInBackground(Uri... params ) {
            Log.d(TAG, "doInBackground: started.");

            if(mBitmap == null){

                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(AdminActivity.this.getContentResolver(), params[0]);
                    Log.d(TAG, "doInBackground: bitmap size: megabytes: " + mBitmap.getByteCount()/MB + " MB");
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: IOException: ", e.getCause());
                }
            }

            byte[] bytes = null;
            for (int i = 1; i < 11; i++){
                if(i == 10){
                    Toast.makeText(getApplicationContext(), "That image is too large.", Toast.LENGTH_SHORT).show();
                    break;
                }
                bytes = getBytesFromBitmap(mBitmap,100/i);
                Log.d(TAG, "doInBackground: megabytes: (" + (11-i) + "0%) "  + bytes.length/MB + " MB");
                if(bytes.length/MB  < MB_THRESHHOLD){
                    return bytes;
                }
            }
            return bytes;
        }


        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            mProgressBar.setVisibility(View.GONE);
            mBytes = bytes;
            //execute the upload
            executeUploadTask();
        }
    }

    // convert from bitmap to byte array
    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }
    public String FIREBASE_IMAGE_STORAGE = "images/users";
    public String FIREBASE_HOLIDAY_IMAGE_STORAGE = "images/holidays";

    private void executeUploadTask(){
        //specify where the photo will be stored
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(FIREBASE_HOLIDAY_IMAGE_STORAGE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid()
                        +title ); //just replace the old image with the new one

        if(mBytes.length/MB < MB_THRESHHOLD) {

            // Create file metadata including the content type
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .setContentLanguage("en") //see nodes below
                    /*
                    Make sure to use proper language code ("English" will cause a crash)
                    I actually submitted this as a bug to the Firebase github page so it might be
                    fixed by the time you watch this video. You can check it out at https://github.com/firebase/quickstart-unity/issues/116
                     */
                    .setCustomMetadata("Mitch's special meta data", "JK nothing special here")
                    .setCustomMetadata("location", "Iceland")
                    .build();
            //if the image size is valid then we can submit to database
            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(mBytes, metadata);
            //uploadTask = storageReference.putBytes(mBytes); //without metadata

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        selectedImage = downloadUri;
                        Toast.makeText(getApplicationContext(),"Image Uri Ready To Upload or already Uploaded",Toast.LENGTH_LONG).show();
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

//            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    //Now insert the download url into the firebase database
////                    Uri firebaseURL = taskSnapshot.getDownloadUrl();
//                    Toast.makeText(AdminActivity.this, "Upload Success", Toast.LENGTH_SHORT).show();
////                    Log.d(TAG, "onSuccess: firebase download url : " + firebaseURL.toString());
////                    FirebaseDatabase.getInstance().getReference().push()
////                            .child("images/holidays/")
////                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
////                            .setValue(firebaseURL.toString());
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    Toast.makeText(AdminActivity.this, "could not upload photo", Toast.LENGTH_SHORT).show();
//
//                }
//            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                    Toast.makeText(AdminActivity.this, " progress" + "%", Toast.LENGTH_SHORT).show();
//
//                }
//            })
//            ;
        }else{
            Toast.makeText(this, "Image is too Large", Toast.LENGTH_SHORT).show();
        }

    }




}
