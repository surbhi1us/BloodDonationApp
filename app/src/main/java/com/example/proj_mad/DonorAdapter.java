package com.example.proj_mad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.MyViewHolder> {

    private List<MainModel> donorList; //list of donors

    public DonorAdapter(List<MainModel> donorList) {
        this.donorList = donorList;
    }//recieves and saves list in adapter,will show in recyclerview

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new MyViewHolder(view);
    } //loads new card xml and returns views inside card

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MainModel donor = donorList.get(position);
        holder.name.setText("Name: " + donor.getName());
        holder.blood.setText("Blood Group: " + donor.getBlood());
        holder.email.setText("Email: " + donor.getEmail());
    } //fills data in each card for each donor

    @Override
    public int getItemCount() {
        return donorList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, blood, email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.donorname);
            blood = itemView.findViewById(R.id.donorbg);
            email = itemView.findViewById(R.id.donoremail);
        }
    } //holds references to views inside each card
}
