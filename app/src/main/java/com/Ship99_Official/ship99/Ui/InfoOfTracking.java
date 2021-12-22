package com.Ship99_Official.ship99.Ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.Ship99_Official.ship99.R;
import com.Ship99_Official.ship99.Track.RequestModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class InfoOfTracking extends AppCompatActivity {

    ImageView one,two,three,four,five,six;
    TextView text1,text2,text3,text4,text5,text6,text7,text8;
    String day,monthString,year;

    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_track);

        Bundle extras = getIntent().getExtras();

        init();


        String status = (String) extras.getString("status");
        String date = extras.getString("date");

//        DateFormat df = new SimpleDateFormat("d MMM yyyy 'at' HH:mm a");

//        String dtStart = "2010-10-15T09:27:37Z";

        try {
            SimpleDateFormat format = new SimpleDateFormat("d MMM yyyy 'at' HH:mm a");
            Date date2 = format.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date2);
            calendar.add(Calendar.DATE,2);

            Date resultdate = new Date(calendar.getTimeInMillis());

            SimpleDateFormat sdf1 = new SimpleDateFormat("d MMM yyyy 'at' HH:mm a");
            sdf1.format(calendar.getTime());

            day          = (String) DateFormat.format("d",   resultdate); // 20
            monthString  = (String) DateFormat.format("MMM",  resultdate); // Jun
            year         = (String) DateFormat.format("yyyy", resultdate); // 2013

            text7.setText(day);
            text8.setText(monthString +"\n"+ year);

            Log.d("pojo date is ",day +" "+ monthString +" "+ year);
        } catch (ParseException e) {
            e.printStackTrace();
        }




            if (status.equals("Waiting For Picking")) {
            text1.setText("Event occurres " + date);
            text2.setText("Checking in progress...");
            text3.setText("");
            text4.setText("");
            text5.setText("");
            text6.setText("");

            one.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            two.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.emptycheckbox));
            three.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.emptycheckbox));
            four.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.emptycheckbox));
            five.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.emptycheckbox));
            six.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.emptycheckbox));

//i will make driver in client tabe orders slect button when he picked up order successfuly
        } else if(status.equals("pickedUp")){
                text1.setText("Event occurres " + date);
                text2.setText("Done");
                text3.setText("Checking in progress...");
                text4.setText("");
                text5.setText("");
                text6.setText("");

            one.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            two.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            three.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.emptycheckbox));
            four.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.emptycheckbox));
            five.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.emptycheckbox));
            six.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.emptycheckbox));


        }else if (status.equals("ReachedHub")){

                text1.setText("Event occurres " + date);
                text2.setText("Done");
                text3.setText("Done");
                text4.setText("Checking in progress...");
                text5.setText("");
                text6.setText("");
            one.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            two.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            three.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            four.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.emptycheckbox));
            five.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.emptycheckbox));
            six.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.emptycheckbox));


        }else if (status.equals("preparing")){
                text1.setText("Event occurres " + date);
                text2.setText("Done");
                text3.setText("Done");
                text4.setText("Done");
                text5.setText("Checking in progress...");
                text6.setText("");
            one.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            two.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            three.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            four.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            five.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.emptycheckbox));
            six.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.emptycheckbox));

        }else if (status.equals("inIt'sWay")){
                text1.setText("Event occurres " + date);
                text2.setText("Done");
                text3.setText("Done");
                text4.setText("Done");
                text5.setText("Almost Done");
                text6.setText("");

            one.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            two.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            three.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            four.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            five.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            six.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.emptycheckbox));

        }else if (status.equals("Delivered")){
                text1.setText("Event occurres " + date);
                text2.setText("Done");
                text3.setText("Done");
                text4.setText("Done");
                text5.setText("Done");
                text6.setText("Done");
            one.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            two.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            three.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            four.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            five.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));
            six.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trueicone));

        } else {
            Toast.makeText(this,"Sorry There is an error",Toast.LENGTH_SHORT).show();
        }


            

        }


    public void init(){

        one = (ImageView) findViewById(R.id.onecheck);
        two = (ImageView) findViewById(R.id.twocheck);
        three = (ImageView) findViewById(R.id.threecheck);
        four = (ImageView) findViewById(R.id.fourcheck);
        five = (ImageView) findViewById(R.id.fivecheck);
        six = (ImageView) findViewById(R.id.sixcheck);


        text1 = (TextView) findViewById(R.id.undert1);


        text2 = (TextView) findViewById(R.id.undert2);


        text3 = (TextView) findViewById(R.id.undert3);


        text4 = (TextView) findViewById(R.id.undert4);


        text5 = (TextView) findViewById(R.id.undert5);


        text6 = (TextView) findViewById(R.id.undert6);

        text7 = (TextView) findViewById(R.id.datenumber);
        text8 = (TextView) findViewById(R.id.datemonthandyear);

        reference = FirebaseDatabase.getInstance()
                .getReference("Requests");


    }
}
