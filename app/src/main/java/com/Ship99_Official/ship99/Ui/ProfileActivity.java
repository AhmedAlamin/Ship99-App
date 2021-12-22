package com.Ship99_Official.ship99.Ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.Ship99_Official.ship99.ConnectionManager;
import com.Ship99_Official.ship99.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.zip.Inflater;

public class ProfileActivity extends AppCompatActivity {

    ConnectionManager connectionManager;
    FrameLayout  requestView ;
    Button requestBtn;

    /**
     * Broadcast receiver to check if network connection is turned off
     */
    BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(!connectionManager.haveNetworkConnection(context)) {
                connectionManager.showDialog(context);
                requestBtn.setClickable(false);
                requestBtn.setBackgroundColor(Color.GRAY);
                Toast.makeText(context,"No Internet Found",Toast.LENGTH_SHORT).show();
            }else if (connectionManager.haveNetworkConnection(context)){
                requestBtn.setClickable(true);
                requestBtn.setBackgroundColor(Color.rgb(202,121,0));

            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();


        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver,filter);




    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkReceiver);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        connectionManager = new ConnectionManager();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_request, null);

        requestView = (FrameLayout ) view.findViewById(R.id.requestFragmentid);
        requestBtn = (Button) requestView.findViewById(R.id.btn_request);

//        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//
//                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//                startActivity(intent);
//            }
//        });

        //Initialize Bottom Navigation View.
        BottomNavigationView navView = findViewById(R.id.bottomNav_view);

        //Pass the ID's of Different destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_request, R.id.navigation_history, R.id.navigation_profile, R.id.navigation_track )
                .build();

        //Initialize NavController.
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
