package com.Ship99_Official.ship99.Ui;

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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.Ship99_Official.ship99.R;
import com.Ship99_Official.ship99.Track.RequestModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class RequestFragment extends Fragment {

    static final int REQUEST_TAKE_PHOTO = 1;
    private static int RESULT_LOAD_IMG = 2;
    static final int PERMISSION_REQUEST_CODE =0 ;
    private static ImageView productImage;

    private Map<String, EditText> editTextFields;
    private Spinner spinnerPickUpAddress,spinnerClientAddress,spinnerTypeOfProduct;
    private String pickupAddress,clientAddress,typeOfProduct,currentPhotoPath;;
    private TextView change_im;
    private Button requestButton;
    private ProgressDialog progressDialog;
    private Toast myToast;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private Long orderNum = Long.parseLong("1");
    private DatabaseReference reference,orderNumberRef;




    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_request, container, false);



       orderNumberRef = FirebaseDatabase.getInstance().getReference("Requests");

        orderNumberRef.orderByChild("userId").equalTo(mAuth.getCurrentUser().getUid()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            RequestModel model = ds.getValue(RequestModel.class);
                            if (model.getOrderNumber() != null && Long.parseLong(model.getOrderNumber())  >= orderNum ){
                                orderNum = Long.parseLong( model.getOrderNumber()) +1;
                            }
                        }


                        Log.d("pojo summ ordernum",orderNum.toString() );

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//        orderNumberRef.orderByChild("userId").equalTo(mAuth.getCurrentUser().getUid()).addValueEventListener
//                (new ValueEventListener() {
//
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot ds : snapshot.getChildren()) {
//                            RequestModel model = ds.getValue(RequestModel.class);
//                            if (Long.valueOf(model.getOrderNumber()) >= orderNum) {
//                                orderNum++;
//                            }
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                    }
//
//                });


//        userRef.setValue(mAuth.getCurrentUser().getUid());

//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference Ref = rootRef.child("Users").child(uid);
//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(!dataSnapshot.exists()) {
//                    //create new user
//                    if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()) == false){
//                      //  Ref.setValue(mAuth.getCurrentUser().getPhoneNumber());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        };
//        Ref.addListenerForSingleValueEvent(eventListener);



        reference = FirebaseDatabase.getInstance()
                .getReference("Requests");

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


        myToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);

        //collects the editText
        editTextFields = new HashMap<>();
        editTextFields.put("Name", (EditText) v.findViewById(R.id.Name));
        editTextFields.put("NumberOfClient", (EditText) v.findViewById(R.id.number_of_client));
        editTextFields.put("TotalPrice", (EditText) v.findViewById(R.id.price_of_package));
        editTextFields.put("Weight", (EditText) v.findViewById(R.id.weight));
        editTextFields.put("nearest_sign_note", (EditText) v.findViewById(R.id.nearest_sign_note));
        editTextFields.put("notes_of_shipping", (EditText) v.findViewById(R.id.notes_of_shipping));
        editTextFields.put("nearestDistnation", (EditText) v.findViewById(R.id.nearest_sign_note));



        // handle the choose photo from galary
        change_im = (TextView) v.findViewById(R.id.product_image);

        productImage = v.findViewById(R.id.imageOfProductFram);



        change_im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeImage();



            }
        });


        //retrieve the spinnerFood for the category
        spinnerPickUpAddress = (Spinner) v.findViewById(R.id.pickup_address);

        spinnerTypeOfProduct = (Spinner) v.findViewById(R.id.type_of_product);

        spinnerClientAddress = (Spinner) v.findViewById(R.id.client_address);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.pickup_address_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPickUpAddress.setAdapter(adapter);


        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.client_address_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClientAddress.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getContext(),
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


        requestButton = v.findViewById(R.id.btn_request);


        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading));
                saveChanges();




            }
        });
        return v;
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
                    final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    productImage.setImageBitmap(selectedImage);
                    change_im.setText("selected");

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    if(getActivity() != null){
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
        androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(getContext(), change_im);
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
        productImage.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.image));
        change_im.setText("Selcet image of your shipment");
        
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
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
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /**
     * It creates the intent for the photo profile
     */
    private void dispatchTakePictureIntent() {
        Uri photoURI;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED)
            {

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(getContext(),
                            "com.example.android.fileproviderC",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }

            }else{
                requestPermission();
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
        DateFormat df = new SimpleDateFormat("d MMM yyyy 'at' HH:mm a");
        String date = df.format(Calendar.getInstance().getTime());




        StorageReference storageReference = FirebaseStorage
                .getInstance()
                .getReference()
                .child(currentUserID +"/productsImage/").child(String.valueOf((orderNum)));
        Log.d("pojo photo request",orderNum.toString());


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




                                RequestModel request = new RequestModel();

                                request.setName(name);
                                request.setNumberOfClient(numberOfClient);
                                request.setWeight(wieght);
                                request.setTotalPrice(price);
                                request.setNotes_of_shipping(notes_of_shipping);
                                request.setNearest_sign_note(nearest_sign_note);
                                request.setPickupAddress(pickupAddress);
                                request.setClientAddress(clientAddress);
                                request.setPhotoOfProduct(downloadUrl);
                                request.setStatus("Waiting For Picking");
                                request.setuserId(mAuth.getCurrentUser().getUid());
                                request.setStatus_pickupAddress("Waiting For Picking_"+pickupAddress);
                                request.setStatus_uid("Waiting For Picking"+"_"+mAuth.getCurrentUser().getUid());
                                request.setOrderNumber(String.valueOf((orderNum)));
                                request.setDate(date);
//                                request.setIndex(String.valueOf(orderNum));


                                reference.push().setValue(request);
                                Log.d("pojo","orderNumsum" +" order " +orderNum);


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
                        if(getActivity() != null){
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
                        if(getActivity() != null){
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
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

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






    private void clearText() {


        change_im.setText("Selcet image of your shipment");

        for (String fieldName : editTextFields.keySet()) {
            EditText field = editTextFields.get(fieldName);

            field.setText("");
            field.setBackground(ContextCompat.getDrawable(getContext(), R.color.editTextBG));
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


            if (getActivity() != null)
                progressDialog = ProgressDialog.show(getActivity(), "", getActivity().getString(R.string.loading));


            if (change_im.getText() == "Selcet image of your shipment") {
                Toast.makeText(getContext(), getContext().getString(R.string.selcetImage), Toast.LENGTH_LONG).show();
                change_im.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_wrong_field));
                progressDialog.dismiss();
                wrongField = true;
            }else {
                wrongField = false;
            }

            for (String fieldName : editTextFields.keySet()) {
                EditText field = editTextFields.get(fieldName);


                if (field != null) {
                    if (field.getText().toString().equals("")) {
                        Toast.makeText(getContext(), getContext().getString(R.string.empty_field), Toast.LENGTH_LONG).show();
                        field.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_wrong_field));
                        progressDialog.dismiss();
                        wrongField = true;
                    } else if (!field.getText().toString().equals("")) {
                        field.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_right_field));
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



                Toast.makeText(getActivity(), "Request Sent", Toast.LENGTH_SHORT).show();
                clearText();

            }
        }
    }






