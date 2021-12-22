package com.Ship99_Official.ship99.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.Ship99_Official.ship99.R;
import com.Ship99_Official.ship99.Track.RequestModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditItemOfShipment extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    private static int RESULT_LOAD_IMG = 2;
    private ImageView productImage;

    private Map<String, EditText> editTextFields;
    private Spinner spinnerPickUpAddress,spinnerClientAddress,spinnerTypeOfProduct;
    private String pickupAddress,clientAddress,typeOfProduct,currentPhotoPath;;
    private TextView change_im;
    private Button requestButton;
    private ProgressDialog progressDialog;
    private Toast myToast;
    private String currentUserID,status_uid,index;
    private FirebaseAuth mAuth;
    private Long orderNum = Long.valueOf(1);
    private DatabaseReference reference,orderNumberRef;

 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item_of_shipment);

        Bundle extras = getIntent().getExtras();


        mAuth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference();




//        orderNumberRef = FirebaseDatabase.getInstance().getReference("Requests");

//        orderNumberRef.orderByChild("userId").equalTo(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot ds : snapshot.getChildren()) {
//                    RequestModel model = ds.getValue(RequestModel.class);
//                    if (Long.valueOf(model.getOrderNumber()) >= orderNum ){
//                        orderNum ++;
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        // listen for single change
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    orderNum = dataSnapshot.getChildrenCount();
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // throw an error if setValue() is rejected
//                throw databaseError.toException();
//            }
//        });






        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();

        myToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        //collects the editText
        editTextFields = new HashMap<>();
        editTextFields.put("Name", (EditText) findViewById(R.id.Name));
        editTextFields.put("NumberOfClient", (EditText) findViewById(R.id.number_of_client));
        editTextFields.put("TotalPrice", (EditText) findViewById(R.id.price_of_package));
        editTextFields.put("Weight", (EditText) findViewById(R.id.weight));
        editTextFields.put("nearest_sign_note", (EditText) findViewById(R.id.nearest_sign_note));
        editTextFields.put("notes_of_shipping", (EditText) findViewById(R.id.notes_of_shipping));
        editTextFields.put("nearestDistnation", (EditText) findViewById(R.id.nearest_sign_note));

        // handle the choose photo from galary
        change_im = (TextView) findViewById(R.id.product_image);

        productImage = findViewById(R.id.imageOfProductFram);

        requestButton = findViewById(R.id.btn_request);

        //retrieve the spinnerFood for the category
        spinnerPickUpAddress = (Spinner) findViewById(R.id.pickup_address);

        spinnerTypeOfProduct = (Spinner) findViewById(R.id.type_of_product);

        spinnerClientAddress = (Spinner) findViewById(R.id.client_address);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pickup_address_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPickUpAddress.setAdapter(adapter);


        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.client_address_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClientAddress.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.type_of_product, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeOfProduct.setAdapter(adapter3);




        spinnerPickUpAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                pickupAddress = parent.getItemAtPosition(position).toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> arent) {

            }
        });

        spinnerClientAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if italian then translate to eng before push to the DB
                clientAddress = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTypeOfProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                typeOfProduct = parent.getItemAtPosition(position).toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> arent) {

            }
        });



        if (extras != null){

            //retrieve the inserted data
            editTextFields.get("Name").setText(extras.get("name").toString());
            editTextFields.get("NumberOfClient").setText(extras.get("numberOfClient").toString());
            editTextFields.get("Weight").setText(extras.get("weight").toString());
            editTextFields.get("TotalPrice").setText(extras.get("totalPrice").toString());
            editTextFields.get("notes_of_shipping").setText(extras.get("numberOfClient").toString());
            editTextFields.get("nearest_sign_note").setText(extras.get("nearestSignNote").toString());
            orderNum = Long.parseLong(extras.get("orderNumber").toString());
            status_uid = extras.get("status_uid").toString();
//            index = extras.get("index").toString();

            Picasso.with(productImage.getContext()).load(extras.get("photoOfProduct").toString()).fit().centerCrop()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.loading)
                    .into(productImage);

            myToast.setText("change Image clicked "+ extras.get("name").toString());
            myToast.show();
        }




        change_im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeImage();

            }
        });



        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  progressDialog = ProgressDialog.show(this, "", getString(R.string.loading));
                saveChanges();
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
                change_im.setText("selected");
            }
        }
        if (requestCode == RESULT_LOAD_IMG) {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = this.getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    productImage.setImageBitmap(selectedImage);
                    change_im.setText("selected");

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
        androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(this, change_im);
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
                        removeproductImage();
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
    public void removeproductImage(){
        productImage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.image));
        change_im.setText("Selcet image of your shipment");

    }

    /**
     * It creates the intent for the photo profile
     */
    private void dispatchTakePictureIntent() {
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
    }

    /**
     * It upload the given bitmap on firebase. Upload path: <userID>/productImage/img.jpg
     * @param bitmap
     */
    private void uploadFile(Bitmap bitmap) {


        //retrieve the inserted data
        String name = editTextFields.get("Name").getText().toString();
        String numberOfClient = editTextFields.get("NumberOfClient").getText().toString();
        String wieght = editTextFields.get("Weight").getText().toString();
        String price = editTextFields.get("TotalPrice").getText().toString();
        String notes_of_shipping = editTextFields.get("notes_of_shipping").getText().toString();
        String nearest_sign_note = editTextFields.get("nearest_sign_note").getText().toString();


        StorageReference storageReference = FirebaseStorage
                .getInstance()
                .getReference()
                .child(currentUserID +"/productsImage/").child(String.valueOf((orderNum)));
        Log.d("pojo photo edit",orderNum.toString());


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
                                final String downloadUrl =
                                        uri.toString();
//                                FirebaseDatabase.getInstance()
//                                        .getReference("Users")
//                                        .child(currentUserID +"/Requests").push().child("PhotoOfProduct")
//                                        .setValue(downloadUrl);


//                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//                                final DatabaseReference reference = firebaseDatabase.getReference();
//                                Query query = reference.child("Requests").orderByChild("orderNumber").equalTo(orderNum);
//                                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
//                                        String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
//                                        String path = "/" + dataSnapshot.getKey() + "/" + key;
//                                        HashMap<String, Object> result = new HashMap<>();
//                                        result.put("name", name);
//                                        result.put("numberOfClient", numberOfClient);
//                                        result.put("clientAddress", clientAddress);
//                                        result.put("nearest_sign_note", nearest_sign_note);
//                                        result.put("notes_of_shipping", notes_of_shipping);
//                                        result.put("photoOfProduct", downloadUrl);
//                                        result.put("pickupAddress", pickupAddress);
//                                        result.put("status", "Active");
//                                        result.put("totalPrice", price);
//                                        result.put("weight", wieght);
//                                        result.put("userId", mAuth.getCurrentUser().getUid());
//                                        result.put("status_uid", status_uid);
//                                        reference.child(path).updateChildren(result);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });

                                Query query2 = reference.child("Requests").orderByChild("orderNumber").equalTo(orderNum);
                                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    public void onDataChange(DataSnapshot snapshot) {
                                        Log.d("pojo snapshopt",snapshot.toString());

                                            DataSnapshot nodeDataSnapshot = snapshot.getChildren().iterator().next();
                                            String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
                                            String path = "/" +snapshot.getKey() + "/" + key;
                                            reference.child(path).child("name").setValue(name);
                                            reference.child(path).child("numberOfClient").setValue(numberOfClient);
                                            reference.child(path).child("clientAddress").setValue(clientAddress);
                                            reference.child(path).child("nearest_sign_note").setValue(nearest_sign_note);
                                            reference.child(path).child("notes_of_shipping").setValue(notes_of_shipping);
                                            reference.child(path).child("photoOfProduct").setValue(downloadUrl);
                                            reference.child(path).child("pickupAddress").setValue(pickupAddress);
                                            reference.child(path).child("status").setValue("Active");
                                            reference.child(path).child("totalPrice").setValue(price);
                                            reference.child(path).child("weight").setValue(wieght);
                                            reference.child(path).child("userId").setValue((mAuth.getCurrentUser().getUid()));
                                            reference.child(path).child("status_uid").setValue(status_uid);


                                            Log.d("pojo ordernumber 77",snapshot.getKey().toString());
                                            finish();

//
                                        }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error){

                                            }});



//                              DatabaseReference r2 =  reference.orderByChild("orderNumber").equalTo(orderNum.toString()).getRef();
//                              r2.setValue(request);
                                Log.d("pojo","orderNumsum" + (orderNum+1.0)+ " order " +orderNum);


//                                    //insert the data into the DB
//                                        reference.child("Name").setValue(name);
//                                        reference.child("NumberOfClient").setValue(numberOfClient);
//                                        reference.child("Weight").setValue(wieght);
//                                        reference.child("TotalPrice").setValue(price);
//                                        reference.child("notes_of_shipping").setValue(notes_of_shipping);
//                                        reference.child("nearest_sign_note").setValue(nearest_sign_note);
//                                        reference.child("PickupAddress").setValue(pickupAddress);
//                                        reference.child("ClientAddress").setValue(clientAddress);
//                                        reference.child("photoOfProduct").setValue(downloadUrl);
//                                        reference.child("status").setValue("Pending");
//                                        reference.child("userId").setValue(mAuth.getCurrentUser().getUid());

//                                DatabaseReference root = FirebaseDatabase.getInstance().getReference("Users" + mAuth.getCurrentUser().getUid()).push();

//                                root.child(mAuth.getCurrentUser().getUid()).push();




//
//                                DatabaseReference idReference = FirebaseDatabase.getInstance()
//                                        .getReference("Requests/" + mAuth.getCurrentUser().getPhoneNumber());
//
//                                idReference.child("Id").setValue("0");
//
//                                DatabaseReference mCounterRef = idReference.child("Id");




//                                // listen for single change
//                                mCounterRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        String count =  (String) dataSnapshot.getValue();
//
//                                        System.out.println("count before setValue()=" + count);
//
//                                        mCounterRef.setValue(Long.valueOf(count) + 1);  // <= Change to ++count
//
//                                        System.out.println("count after setValue()=" + count);
//                                    }
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//                                        // throw an error if setValue() is rejected
//                                        throw databaseError.toException();
//                                    }
//                                });



                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();
                                removeproductImage();
                            }
                        });


                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getUploadSessionUri();

                        String s = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                        if(this != null){
                            myToast.setText(getString(R.string.saved));
                            myToast.show();
                            removeproductImage();
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
        String imageFileName = "JPEG_" + "productImage";
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
        int targetW = productImage.getWidth();
        int targetH = productImage.getHeight();

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


            productImage.setImageBitmap(bitmap);

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



    private void clearText() {


        change_im.setText("Selcet image of your shipment");

        for (String fieldName : editTextFields.keySet()) {
            EditText field = editTextFields.get(fieldName);

            field.setText("");
            field.setBackground(ContextCompat.getDrawable(this, R.color.editTextBG));
            removeproductImage();


//        editTextFields.get("Name").setText("");
//            editTextFields.get("NumberOfClient").setText("");
//            editTextFields.get("Weight").setText("");
//            editTextFields.get("TotalPrice").setText("");
//        editTextFields.get("nearest_sign_note").setText("");
//        editTextFields.get("notes_of_shipping").setText("");
//        editTextFields.get("nearestDistnation").setText("");


        }
    }

    /**
     * It checks the validity of the inserted data, then it uploads them on firebase
     */
    private void saveChanges () {

        boolean wrongField = false;


        if (this != null)
            progressDialog = ProgressDialog.show(this, "", this.getString(R.string.loading));


        if (change_im.getText() == "Selcet image of your shipment") {
            Toast.makeText(this, this.getString(R.string.selcetImage), Toast.LENGTH_LONG).show();
            change_im.setBackground(ContextCompat.getDrawable(this, R.drawable.border_wrong_field));
            progressDialog.dismiss();
            wrongField = true;
        }else {
            wrongField = false;
        }

        for (String fieldName : editTextFields.keySet()) {
            EditText field = editTextFields.get(fieldName);


            if (field != null) {
                if (field.getText().toString().equals("")) {
                    Toast.makeText(this, this.getString(R.string.empty_field), Toast.LENGTH_LONG).show();
                    field.setBackground(ContextCompat.getDrawable(this, R.drawable.border_wrong_field));
                    progressDialog.dismiss();
                    wrongField = true;
                } else if (!field.getText().toString().equals("")) {
                    field.setBackground(ContextCompat.getDrawable(this, R.drawable.border_right_field));
                }
            }
//                else
//                    return;
        }


        if (!wrongField) {

            // Save profile pic to the DB
            Bitmap img = ((BitmapDrawable) productImage.getDrawable()).getBitmap();
            /*Navigation controller is moved inside this method. The image must be loaded totally to FireBase
                before come back to the AccountFragment. This is due to the fact that the image download is async */
            uploadFile(img);



            Toast.makeText(this, "Request Sent", Toast.LENGTH_SHORT).show();
            clearText();

        }
    }
}