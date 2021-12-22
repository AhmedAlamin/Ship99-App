package com.Ship99_Official.ship99.Track;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.Ship99_Official.ship99.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestViewHolder3 extends RecyclerView.ViewHolder {

   public CircleImageView img;
    public TextView orderId;
    public TextView clientAddress;
    public TextView status;


    public RequestViewHolder3(@NonNull View itemView)
    {
        super(itemView);
        img=(CircleImageView)itemView.findViewById(R.id.photoOfProduct);
        orderId=(TextView)itemView.findViewById(R.id.orderId);
        clientAddress=(TextView)itemView.findViewById(R.id.ClientAddress);
        status=(TextView)itemView.findViewById(R.id.status);


    }



}
