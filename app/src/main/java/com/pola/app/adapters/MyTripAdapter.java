package com.pola.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pola.app.R;
import com.pola.app.Utils.StringUtil;
import com.pola.app.beans.TripDetailsBean;
import com.pola.app.delegates.CustomItemClickListener;

import java.util.List;

/**
 * Created by Ajay on 19-Sep-17.
 */
public class MyTripAdapter extends RecyclerView.Adapter<MyTripAdapter.ViewHolder> {
    private List<TripDetailsBean> trips;
    Context mContext;
    CustomItemClickListener listener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyTripAdapter(List<TripDetailsBean> trips,Context context, CustomItemClickListener listener) {
        this.trips = trips;
        this.mContext=context;
        this.listener=listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyTripAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_my_trip_list, parent, false);
        // set the view's size, margins, paddings and layout parameters
        final ViewHolder vh = new ViewHolder(v);

        //set onclick listner
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, vh.getPosition());
            }
        });
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        //is owner
        if (trips.get(position).getTripUserId() == 0) {
            holder.dateTime.setText(StringUtil.convertSQLDate2Java(trips.get(position).getStartDateTime()));
            holder.start.setText(trips.get(position).getStart());
            holder.drop.setText(trips.get(position).getDrop());
            holder.ownerName.setVisibility(View.GONE);
            holder.passengers.setVisibility(View.VISIBLE);
            holder.passengersText.setText("" + trips.get(position).getPassengers());
            holder.remainingPassengers.setVisibility(View.VISIBLE);
            holder.remainingPassengersText.setText("" + trips.get(position).getRemainingPassengers() + " seats remaining");
            holder.pendingRequests.setVisibility(View.VISIBLE);
            holder.pendingRequestsText.setText("" + trips.get(position).getRemainingRequests() + " requests pending");
            holder.call.setVisibility(View.GONE);
            holder.passengerRequests.setVisibility(View.VISIBLE);
        }
        else{
            holder.dateTime.setText(StringUtil.convertSQLDate2Java(trips.get(position).getWalkStartDateTime()));
            holder.start.setText(trips.get(position).getWalkStartLoc());
            holder.drop.setText(trips.get(position).getWalkDropLoc());
            holder.ownerName.setVisibility(View.VISIBLE);
            holder.ownerPhone.setVisibility(View.VISIBLE);
            holder.ownerNameText.setText(trips.get(position).getFullName());
            holder.ownerPhoneText.setText(trips.get(position).getMobile());
            holder.passengers.setVisibility(View.GONE);
            holder.passengerRequests.setVisibility(View.GONE);
        }

        if(trips.get(position).getStatus().contains("ancelle") || trips.get(position).getStatus().contains("Rej")) {
            holder.status.setTextColor(Color.RED);
            holder.passengerRequests.setVisibility(View.GONE);
            holder.call.setVisibility(View.GONE);
        }else if(trips.get(position).getStatus().contains("Requested")) {
            holder.status.setTextColor(mContext.getResources().getColor(R.color.blue));
            holder.paid.setVisibility(View.GONE);
        }else if(trips.get(position).getStatus().contains("Accepted")) {
            holder.status.setTextColor(mContext.getResources().getColor(R.color.green));
            holder.call.setVisibility(View.VISIBLE);
            holder.paid.setVisibility(View.GONE);
        }else if(trips.get(position).getStatus().contains("Paid")) {
            holder.status.setTextColor(mContext.getResources().getColor(R.color.Teal_700));
            holder.call.setVisibility(View.GONE);
        }else if(trips.get(position).getStatus().contains("Completed")) {
            holder.status.setTextColor(mContext.getResources().getColor(R.color.Teal_700));
            holder.call.setVisibility(View.GONE);
            if(trips.get(position).getTripUserId() != 0)
                holder.paid.setVisibility(View.VISIBLE);
        }else if(trips.get(position).getStatus().contains("On")) {
            holder.status.setTextColor(mContext.getResources().getColor(R.color.Intro2));
            holder.call.setVisibility(View.GONE);
        }

        holder.status.setText(trips.get(position).getStatus());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return trips.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView ownerNameText,ownerPhoneText, dateTime, start, drop, fare, passengersText, status,remainingPassengersText,pendingRequestsText;
        public LinearLayout request,ownerName,passengers,remainingPassengers,pendingRequests,passengerRequests,call,paid,ownerPhone;

        public ViewHolder(View v) {
            super(v);
            ownerNameText = (TextView) v.findViewById(R.id.owner_name_text);
            ownerName = (LinearLayout) v.findViewById(R.id.owner_name);
            ownerPhoneText = (TextView) v.findViewById(R.id.owner_phone_text);
            ownerPhone = (LinearLayout) v.findViewById(R.id.owner_phone);
            dateTime = (TextView) v.findViewById(R.id.date_time);
            start = (TextView) v.findViewById(R.id.start);
            drop = (TextView) v.findViewById(R.id.drop);
            fare = (TextView) v.findViewById(R.id.fare);
            passengers = (LinearLayout) v.findViewById(R.id.passengers);
            passengersText = (TextView) v.findViewById(R.id.passengers_text);
            status = (TextView) v.findViewById(R.id.trip_status);
            request = (LinearLayout) v.findViewById(R.id.request);
            remainingPassengers = (LinearLayout) v.findViewById(R.id.remaining_passengers);
            pendingRequests = (LinearLayout) v.findViewById(R.id.pending_requests);
            passengerRequests = (LinearLayout) v.findViewById(R.id.passenger_requests);
            remainingPassengersText = (TextView) v.findViewById(R.id.remaining_passengers_text);
            pendingRequestsText = (TextView) v.findViewById(R.id.pending_requests_text);
            call = (LinearLayout) v.findViewById(R.id.call);
            paid = (LinearLayout) v.findViewById(R.id.paid);
        }
    }
}