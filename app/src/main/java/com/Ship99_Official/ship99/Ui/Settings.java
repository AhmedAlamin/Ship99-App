package com.Ship99_Official.ship99.Ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.Ship99_Official.ship99.CountryData;
import com.Ship99_Official.ship99.R;
import com.Ship99_Official.ship99.Track.InfoModel;
import com.Ship99_Official.ship99.Track.RequestModel;
import com.Ship99_Official.ship99.VerifyPhoneActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;


public class Settings extends AppCompatActivity {

    RelativeLayout signout;
    TextView name,address,number;
    FirebaseAuth mAuth;
    FloatingActionButton change_im;
    CircleImageView imageView;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static int RESULT_LOAD_IMG = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);



        mAuth = FirebaseAuth.getInstance();

        change_im = (FloatingActionButton) findViewById(R.id.change_im) ;
        
        imageView = (CircleImageView) findViewById(R.id.ivBackground);
        signout = (RelativeLayout) findViewById(R.id.logout);
        name = (TextView) findViewById(R.id.nameOfClient);
        address = (TextView) findViewById(R.id.defaultAddress);
        number = (TextView) findViewById(R.id.NumberOfClient);

        change_im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Settings.this,LoginPickNameAddress.class);
                i.putExtra("Settings","1");
                startActivity(i);

            }
        });
        
       DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users/"+mAuth.getCurrentUser().getUid());


        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                InfoModel infoModel = new InfoModel();
                infoModel = snapshot.getValue(InfoModel.class);
                name.setText(infoModel.getName());
                number.setText(infoModel.getPhonNumber());
                address.setText(infoModel.getLocation());
                Picasso.with(imageView.getContext()).load(infoModel.getPhoto()).fit().centerCrop()
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.loading)
                        .into(imageView);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(Settings.this,"Signed out",Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(Settings.this,WelcomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();

            }
        });


    }



}
