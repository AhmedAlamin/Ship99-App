package com.Ship99_Official.ship99.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Ship99_Official.ship99.R;
import com.Ship99_Official.ship99.Track.InfoModel;
import com.Ship99_Official.ship99.Track.RequestModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.Ship99_Official.ship99.Ui.RequestFragment.PERMISSION_REQUEST_CODE;


public class LoginPickNameAddress extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    private static int RESULT_LOAD_IMG = 2;
    private ProgressDialog progressDialog;
    private CircleImageView imageOfUser;
    private  FloatingActionButton editPhotoOfUser;
    private EditText nameOfUser,locationOfUser;
    private  DatabaseReference reference;
    private Button loginBtn;
    private String currentUserID,currentPhotoPath;
    private Toast myToast;
    private TextView statusofimage;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pick_name_address);

        statusofimage = (TextView) findViewById(R.id.statusofimage);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();
        reference = FirebaseDatabase.getInstance()
                .getReference("Users");

        Intent extras = getIntent();
        loginBtn = (Button) findViewById(R.id.loginbtn);
        Log.d("pojo","extras is "+ extras.getStringExtra("Settings")+" mAuth "+ mAuth.getCurrentUser() );


        if (extras.getStringExtra("Settings") == "1" && mAuth.getCurrentUser() != null){

            Log.d("pojo","extras is "+extras.getStringExtra("Settings")+"// exstras 2"+ extras.getStringExtra("Settings"));
            loginBtn.setText("Done");

            System.out.println(loginBtn.getText());

        }else if (extras.getStringExtra("Settings") == null && mAuth.getCurrentUser() !=null){

            Intent i = new Intent(this,ProfileActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });


        imageOfUser = (CircleImageView) findViewById(R.id.imageofuser);
        editPhotoOfUser = (FloatingActionButton) findViewById(R.id.changeimage);
        nameOfUser = (EditText) findViewById(R.id.nameet);
        locationOfUser = (EditText) findViewById(R.id.locationet);


        myToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        editPhotoOfUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage();
            }
        });





    }

    /**
     * Used to wait for the camera to set the photo
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                setPic(currentPhotoPath);

               // editPhotoOfUser.setText("selected");
            }
        }
        if (requestCode == RESULT_LOAD_IMG) {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = this.getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageOfUser.setImageBitmap(selectedImage);
//                    change_im.setText("selected");
                    statusofimage.setText("");

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    if(this != null){
                        myToast.setText(getString(R.string.failure));
                        myToast.show();
                    }
                }

            }
        }
    }


    /**
     * To change the image in three different ways:
     *  - Camera (call the dispatchTakePictureIntent)
     *  - Gallery
     *  - Remove image
     */
    public void changeImage() {
        androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(this, editPhotoOfUser);
        popup.getMenuInflater().inflate(
                R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            // implement click listener.
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.camera:
                        // create Intent with photoFile
                        dispatchTakePictureIntent();
                        return true;
                    case R.id.gallery:
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                        return true;

                    case R.id.removeImage:
                        removeimageOfUser();
                        return true;

                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    /**
     * Removes the profile image
     */
    public void removeimageOfUser(){
        imageOfUser.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.empty));
        nameOfUser.getText().clear();
        locationOfUser.getText().clear();
        statusofimage.setText("Not selected");
//        editPhotoOfUser.setText("Selcet image of your shipment");

    }

    /**
     * It creates the intent for the photo profile
     */
    private void dispatchTakePictureIntent() {

        if (ContextCompat.checkSelfPermission(getApplication().getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
        {
            Uri photoURI;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileproviderC",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }else{
            requestPermission();
        }


    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;

        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }





    /**
     * It upload the given bitmap on firebase. Upload path: <userID>/imageOfUser/img.jpg
     * @param bitmap
     */
    private void uploadFile(Bitmap bitmap) {
        Intent i = new Intent(LoginPickNameAddress.this,ProfileActivity.class);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference Ref = rootRef.child("Users");

        InfoModel info = new InfoModel();


        info.setName(nameOfUser.getText().toString());
        info.setLocation(locationOfUser.getText().toString());
        info.setPhonNumber(mAuth.getCurrentUser().getPhoneNumber());


        final StorageReference storageReference = FirebaseStorage
                .getInstance()
                .getReference()
                .child(currentUserID +"/imageOfUser/");


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                info.setPhoto(downloadUrl);

                                Ref.child(mAuth.getCurrentUser().getUid()).setValue(info);

                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();
                                removeimageOfUser();


                                startActivity(i);
                                finish();

                                //start next activity form here!
                            }
                        });
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getUploadSessionUri();

                        String s = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        if(this != null){
                            myToast.setText(getString(R.string.saved));
                            myToast.show();
                            removeimageOfUser();
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        if(this != null){
                            myToast.setText(getString(R.string.failure));
                            myToast.show();

                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        }

                    }
                });

    }


    /**
     * Function to create image file with ExternalFilesDir
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + "imageOfUser";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    /**
     * Sets the picture given the image path
     * @param currentPhotoPath
     */
    private void setPic(String currentPhotoPath) {


        // Get the dimensions of the View
        int targetW = imageOfUser.getWidth();
        int targetH = imageOfUser.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        if(bitmap != null) {

            try {
                bitmap = rotateImageIfRequired(bitmap, currentPhotoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }


            statusofimage.setText("");

            imageOfUser.setImageBitmap(bitmap);


        }
    }


    /**
     * Used to rotate the image when taken with landscape camera. It checks how many degree to rotate
     * @param img
     * @param currentPhotoPath
     * @return
     * @throws IOException
     */
    private static Bitmap rotateImageIfRequired(Bitmap img, String currentPhotoPath) throws IOException {

        ExifInterface ei = new ExifInterface(currentPhotoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }


    /**
     * It rotate the given image with the given degrees
     * @param img
     * @param degree
     * @return
     */
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    /**
     * It checks the validity of the inserted data, then it uploads them on firebase
     */
    private void saveChanges () {

        boolean wrongField = false;


        if (this != null)
            progressDialog = ProgressDialog.show(this, "", this.getString(R.string.loading));


        if (statusofimage.getText().equals("Not selected")) {
            Toast.makeText(this, this.getString(R.string.selcetImage), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            wrongField = true;
        }
        if (nameOfUser.getText().toString().equals("")) {
            Toast.makeText(this, this.getString(R.string.empty_field), Toast.LENGTH_LONG).show();
            nameOfUser.setBackground(ContextCompat.getDrawable(this, R.drawable.border_wrong_field));
            progressDialog.dismiss();
            wrongField = true;
        }
        if (locationOfUser.getText().toString().equals("")) {
            Toast.makeText(this, this.getString(R.string.empty_field), Toast.LENGTH_LONG).show();
            locationOfUser.setBackground(ContextCompat.getDrawable(this, R.drawable.border_wrong_field));
            progressDialog.dismiss();
            wrongField = true;
        }

         if (!nameOfUser.getText().toString().equals("")) {
            nameOfUser.setBackground(ContextCompat.getDrawable(this, R.drawable.border_right_field));
        }

         if (!locationOfUser.getText().toString().equals("")) {
            locationOfUser.setBackground(ContextCompat.getDrawable(this, R.drawable.border_right_field));
        }




        if (!wrongField) {

            // Save profile pic to the DB
            Bitmap img = ((BitmapDrawable) imageOfUser.getDrawable()).getBitmap();
            /*Navigation controller is moved inside this method. The image must be loaded totally to FireBase
                before come back to the AccountFragment. This is due to the fact that the image download is async */
            uploadFile(img);



            Toast.makeText(this, "Sending...", Toast.LENGTH_SHORT).show();
            clearText();

        }
    }

    private void clearText() {

            removeimageOfUser();


        }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();

    }
    }



