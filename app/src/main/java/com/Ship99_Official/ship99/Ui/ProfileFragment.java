package com.Ship99_Official.ship99.Ui;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.Ship99_Official.ship99.R;
import com.Ship99_Official.ship99.Track.InfoModel;
import com.Ship99_Official.ship99.Track.RequestModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;


import java.net.URLEncoder;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private ImageView qrCodeImage;
    private RelativeLayout settings,contactUs,rateUs;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private TextView phoneNumber, nameOfUser, activeRequestsNumber, shippedRequestsNumber,
        cancelledRequestsNumber, totalBalanceNumber, totalEarnedNumber;
    private CircleImageView profilePhoto;

    private Long numOfActiveRequests = Long.valueOf(0), numOfCancelledRequests = Long.valueOf(0), numOfShippedRequests = Long.valueOf(0);

    private Long balance = Long.valueOf(0),earned= Long.valueOf(0);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        qrCodeImage = (ImageView) v.findViewById(R.id.imageQrCode);

//        reference = (DatabaseReference) FirebaseDatabase.getInstance()
//                .getReference("Requests").orderByChild("userId").equalTo(mAuth.getCurrentUser().getUid());

        contactUs = (RelativeLayout) v.findViewById(R.id.contact);
        settings = (RelativeLayout) v.findViewById(R.id.settings);

        rateUs = (RelativeLayout) v.findViewById(R.id.rateUs);

        profilePhoto = (CircleImageView) v.findViewById(R.id.profilePhoto);
        phoneNumber = (TextView) v.findViewById(R.id.phonenumberofuser);
        nameOfUser = (TextView) v.findViewById(R.id.username);
        activeRequestsNumber = (TextView) v.findViewById(R.id.activeRequestsNumber);
        shippedRequestsNumber = (TextView) v.findViewById(R.id.shippedRequestsNumber);
        cancelledRequestsNumber = (TextView) v.findViewById(R.id.cancelledRequestsNumber);
        totalBalanceNumber = (TextView) v.findViewById(R.id.totalBalanceNumber);
        totalEarnedNumber = (TextView) v.findViewById(R.id.totalEarnedNumber);

        mAuth = FirebaseAuth.getInstance();


        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        BitMatrix bitMatrix = null;
        try {
            bitMatrix = multiFormatWriter.encode(mAuth.getCurrentUser().getUid().toString(), BarcodeFormat.QR_CODE,250,250);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

        qrCodeImage.setImageBitmap(bitmap);

        qrCodeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View child = getLayoutInflater().inflate(R.layout.image_layout, null);
                ImageView image = child.findViewById(R.id.imageee);
                image.setImageBitmap(bitmap);

                Dialog settingsDialog = new Dialog(getContext());
                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

                settingsDialog.setContentView(child);

                settingsDialog.show();
            }
        });



        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent i = newFacebookIntent(getActivity().getPackageManager(),"https://www.facebook.com/AhmedOAlamin/");
              startActivity(i);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),Settings.class);
                startActivity(intent);
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setTitle("Ship99");
                alert.setMessage("Send us a Message");

                // Set an EditText view to get user input
                final EditText input = new EditText(getContext());
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        // Do something with value!
                        openWhatsApp("+201143756046",input.getText().toString());
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });


                alert.show();


            }

        });


        // need to store acual photo and name of user when regestration
//        profilePhoto.setImageDrawable(getResources().getDrawable(R.drawable.photoofme));
//        nameOfUser.setText("Ahmed Alamin");
        //////////////////////////////////////

//        phoneNumber.setText(mAuth.getCurrentUser().getPhoneNumber());

        Query query = FirebaseDatabase.getInstance()
                .getReference("Requests").orderByChild("userId").equalTo(mAuth.getCurrentUser().getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Users/"+mAuth.getCurrentUser().getUid());


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    InfoModel model = snapshot.getValue(InfoModel.class);
                    phoneNumber.setText(model.getPhonNumber());
                    nameOfUser.setText(model.getName());
                    Picasso.with(profilePhoto.getContext()).load(model.getPhoto()).fit().centerCrop()
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.loading)
                            .into(profilePhoto);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        RequestModel pojo = (RequestModel) issue.getValue(RequestModel.class);
                        if (pojo.getStatus().equals("Active")) {
                            numOfActiveRequests++;
                            activeRequestsNumber.setText(numOfActiveRequests.toString());
                            if (pojo.getTotalPrice() != null){
                                balance = balance + Long.valueOf(pojo.getTotalPrice());
                            }
                            Log.e("Key", issue.getKey());
                        }else if (pojo.getStatus().equals("Shipped")){
                            numOfShippedRequests++;
                            shippedRequestsNumber.setText(numOfShippedRequests.toString());
                            if (pojo.getTotalPrice() != null) {
                                earned = earned + Long.valueOf(pojo.getTotalPrice());
                            }
                        }else if (pojo.getStatus().equals("Cancelled")){
                            numOfCancelledRequests++;
                            cancelledRequestsNumber.setText(numOfCancelledRequests.toString());
                        }
                    }
                    totalBalanceNumber.setText(balance.toString());
                    totalEarnedNumber.setText(earned.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });





        return v;

    }

//    private void sendMessage(String message)
//    {
//
//        // Creating new intent
//        Intent intent
//                = new Intent(Intent.ACTION_SEND);
//
//        intent.setType("text/plain");
//        intent.setPackage("com.whatsapp");
//
//        // Give your message here
//        intent.putExtra(
//                Intent.EXTRA_TEXT,
//                message);
//
//        // Checking whether Whatsapp
//        // is installed or not
//        if (intent.resolveActivity(getContext().getPackageManager()) == null) {
//            Toast.makeText(
//                    getContext(),
//                    "Please install whatsapp first.",
//                    Toast.LENGTH_SHORT)
//                    .show();
//            return;
//        }
//
//        // Starting Whatsapp
//        startActivity(intent);
//    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    private void openWhatsApp(String numero,String mensaje){

        try{
            PackageManager packageManager = getActivity().getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone="+ numero +"&text=" + URLEncoder.encode(mensaje, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }else {
                Toast.makeText(getActivity(), "whatsapp not found!", Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e) {
            Log.e("ERROR WHATSAPP",e.toString());
            Toast.makeText(getActivity(), "Error whatsapp", Toast.LENGTH_SHORT).show();
        }

    }
}
