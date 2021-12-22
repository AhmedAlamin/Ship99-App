package com.Ship99_Official.ship99.Ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.Ship99_Official.ship99.R;
import com.Ship99_Official.ship99.Track.RequestModel;
import com.Ship99_Official.ship99.Track.RequestViewHolder;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrackFragment extends Fragment{

    //     FirebaseRecyclerAdapter adapter;
    private RecyclerView recview;
    private FirebaseAuth mAuth;
    private Query reference, userRef;
    private FirebaseRecyclerOptions<RequestModel> options;
    private FirebaseRecyclerAdapter<RequestModel, RequestViewHolder> adapter;
    private Long orderNum = Long.valueOf(0);
    private ImageView imageView;
    private List<RequestModel> model;
    private ProgressBar progressbar;
    private ArrayList<String> arrayList;

    private EditText mSearchField;
    private ImageButton mSearchBtn;
   private RadioGroup rg;
   private  String searching;

    public TrackFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment




        View v = inflater.inflate(R.layout.reservation_frag_layout, container, false);


        rg = (RadioGroup) v.findViewById(R.id.radiogroub);

        progressbar = v.findViewById(R.id.progressbar);
        imageView = (ImageView) v.findViewById(R.id.reservation_empty_view);

        mSearchField = (EditText) v.findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) v.findViewById(R.id.search_btn);

        mAuth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference("Requests");




        recview = (RecyclerView) v.findViewById(R.id.reservationsRV);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));
        recview.setHasFixedSize(true);





        Query query = FirebaseDatabase.getInstance().getReference("Requests").orderByChild("status_uid")
                .equalTo("Waiting For Picking_"+mAuth.getCurrentUser().getUid());



        options = new FirebaseRecyclerOptions.Builder<RequestModel>()
                .setQuery(query, RequestModel.class)
                .build();

        searching = "orderNumber";

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.orderNumber:
                        // do operations specific to this selection

                        searching ="orderNumber";
                        break;
                    case R.id.nameradio:
                        // do operations specific to this selection
                        searching = "name";
                        break;
                    case R.id.distnationradio:
                        // do operations specific to this selection
                        searching = "clientAddress";
                        break;
                }
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                Toast.makeText(getContext(),"hi",Toast.LENGTH_SHORT).show();
                String searchText = mSearchField.getText().toString();


                Query query2 = reference.orderByChild(searching).startAt(searchText).endAt(searchText + "\uf8ff");
                options = new FirebaseRecyclerOptions.Builder<RequestModel>()
                        .setQuery(query2, RequestModel.class)
                        .build();


                adapter = new FirebaseRecyclerAdapter<RequestModel, RequestViewHolder>(options) {

                    @Override
                    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        View view = inflater.inflate(R.layout.singlerow, parent, false);
                        return new RequestViewHolder(view);
                    }

                    @Override
                    public int getItemCount() {
                        return super.getItemCount();

                    }


                    @Override
                    protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull RequestModel model) {


                        if (model.getuserId().equals(mAuth.getCurrentUser().getUid())){

                                holder.price.setText(model.getTotalPrice());

                                holder.orderId.setText(String.valueOf(model.getOrderNumber()));

                                holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View view) {
//                            Toast.makeText(getContext(),"Long pressed "+position,Toast.LENGTH_SHORT).show();

                                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                                        alert.setTitle("Ship99");
                                        alert.setMessage("Are you sure you want to delete this shipment?");

//                            // Set an EditText view to get user input
//                            final EditText input = new EditText(getContext());
//                            alert.setView(input);

                                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {

                                                // Do something with value!
                                                reference.orderByChild("orderNumber")
                                                        .equalTo(model.getOrderNumber())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.hasChildren()) {

                                                                    DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                                                    firstChild.getRef().removeValue();
                                                                    notifyDataSetChanged();

                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }

                                                        });

                                                StorageReference storageReference = FirebaseStorage
                                                        .getInstance()
                                                        .getReference()
                                                        .child(model.getuserId() +"/productsImage/").child(model.getOrderNumber());

                                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // File deleted successfully
                                                        Toast.makeText(getContext(),"Deleted Successfully",Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        // Uh-oh, an error occurred!

                                                    }
                                                });

                                            }
                                        });

                                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                // Canceled.
                                            }
                                        });

                                        alert.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {




                                                Intent ii = new Intent(getContext() ,EditItemOfShipment.class);
                                                ii.putExtra("orderNumber",model.getOrderNumber());
                                                ii.putExtra("clientAddress",model.getClientAddress());
                                                ii.putExtra("photoOfProduct",model.getPhotoOfProduct());
                                                ii.putExtra("pickupAddress",model.getPickupAddress());
                                                ii.putExtra("nearestSignNote",model.getNearest_sign_note());
                                                ii.putExtra("name",model.getName());
                                                ii.putExtra("numberOfClient",model.getNumberOfClient());
                                                ii.putExtra("weight",model.getWeight());
                                                ii.putExtra("totalPrice",model.getTotalPrice());
                                                ii.putExtra("notesOfShipping",model.getNotes_of_shipping());
                                                ii.putExtra("status_uid",model.getStatus_uid());
//                                    ii.putExtra("index",model.getIndex());

                                                startActivity(ii);
                                                Toast.makeText(getContext(),"for edit",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        alert.show();

                                        return true;
                                    }
                                });

                                holder.cardView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent(getContext(),InfoOfTracking.class);
                                        i.putExtra("status",model.getStatus());
                                        i.putExtra("date",model.getDate());
                                        startActivity(i);
                                    }
                                });

                                holder.clientAddress.setText(model.getClientAddress());

                                holder.status.setText(model.getStatus());

                                holder.name.setText(model.getName());

                                holder.numberOfClient.setText(model.getNumberOfClient());

                                Picasso.with(holder.img.getContext()).load(model.getPhotoOfProduct()).fit().centerCrop()
                                        .placeholder(R.drawable.loading)
                                        .error(R.drawable.loading)
                                        .into(holder.img);

                        }

                        else {
                            Toast.makeText(getContext(),"Find Nothing",Toast.LENGTH_SHORT).show();
                        }


                    }

                };

                recview.setAdapter(adapter);

                adapter.startListening();

            }
        });



        adapter = new FirebaseRecyclerAdapter<RequestModel, RequestViewHolder>(options) {

            @Override
            public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = inflater.inflate(R.layout.singlerow, parent, false);
                return new RequestViewHolder(view);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();

            }


            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull RequestModel model) {



                if (model.getStatus() == "Shipped"){
                    Toast.makeText(getContext(),"there is shipped",Toast.LENGTH_SHORT).show();
                }else {
                    holder.price.setText(model.getTotalPrice());

                    holder.orderId.setText(String.valueOf(model.getOrderNumber()));

                    holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
//                            Toast.makeText(getContext(),"Long pressed "+position,Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                            alert.setTitle("Ship99");
                            alert.setMessage("Are you sure you want to delete this shipment?");

//                            // Set an EditText view to get user input
//                            final EditText input = new EditText(getContext());
//                            alert.setView(input);

                            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    // Do something with value!
                                    reference.orderByChild("orderNumber")
                                            .equalTo(model.getOrderNumber())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChildren()) {

                                                        DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                                        firstChild.getRef().removeValue();
                                                        notifyDataSetChanged();

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }

                                            });

                                    StorageReference storageReference = FirebaseStorage
                                            .getInstance()
                                            .getReference()
                                            .child(model.getuserId() +"/productsImage/").child(model.getOrderNumber());

                                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // File deleted successfully
                                            Toast.makeText(getContext(),"Deleted Successfully",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Uh-oh, an error occurred!

                                        }
                                    });

                                }
                            });

                            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Canceled.
                                }
                            });

                            alert.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {




                                    Intent ii = new Intent(getContext() ,EditItemOfShipment.class);
                                    ii.putExtra("orderNumber",model.getOrderNumber());
                                    ii.putExtra("clientAddress",model.getClientAddress());
                                    ii.putExtra("photoOfProduct",model.getPhotoOfProduct());
                                    ii.putExtra("pickupAddress",model.getPickupAddress());
                                    ii.putExtra("nearestSignNote",model.getNearest_sign_note());
                                    ii.putExtra("name",model.getName());
                                    ii.putExtra("numberOfClient",model.getNumberOfClient());
                                    ii.putExtra("weight",model.getWeight());
                                    ii.putExtra("totalPrice",model.getTotalPrice());
                                    ii.putExtra("notesOfShipping",model.getNotes_of_shipping());
                                    ii.putExtra("status_uid",model.getStatus_uid());
//                                    ii.putExtra("index",model.getIndex());

                                    startActivity(ii);
                                    Toast.makeText(getContext(),"for edit",Toast.LENGTH_SHORT).show();
                                }
                            });

                            alert.show();

                            return true;
                        }
                    });

                    holder.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getContext(),InfoOfTracking.class);
                            i.putExtra("status",model.getStatus());
                            i.putExtra("date",model.getDate());
                            startActivity(i);
                        }
                    });

                    holder.clientAddress.setText(model.getClientAddress());

                    holder.status.setText(model.getStatus());

                    holder.name.setText(model.getName());

                    if (model.getNumberOfClient() == null) {
                        Toast.makeText(getContext(), "null object", Toast.LENGTH_SHORT).show();
                    } else {
                        holder.numberOfClient.setText(model.getNumberOfClient());
                    }

                    Log.d("pojo photoTarckFragment",model.getPhotoOfProduct());
                    Picasso.with(holder.img.getContext()).load(model.getPhotoOfProduct()).fit().centerCrop()
                            .placeholder(R.drawable.loading)
                            .error(R.drawable.loading)
                            .into(holder.img);

//                    Picasso.with(holder.img.getContext()).load(model.getPhotoOfProduct()).into(holder.img);


                    //  Glide.with(holder.img.getContext()).load(model.getPhotoOfProduct()).into(holder.img);

                }



            }

        };

        recview.setAdapter(adapter);




        return v;
    }



    @Override
    public void onStart() {
        super.onStart();


        if (adapter != null) {
            adapter.startListening();
        }

    }


    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.startListening();
        }

    }

    @Override
    public void onResume() {
        super.onResume();


        if (adapter != null) {
            adapter.startListening();
        }

    }
}
