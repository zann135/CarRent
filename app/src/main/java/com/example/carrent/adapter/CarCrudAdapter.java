package com.example.carrent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carrent.R;
import com.example.carrent.model.Car;

import java.util.List;

public class CarCrudAdapter extends RecyclerView.Adapter<CarCrudAdapter.ViewHolder> {
    private Context context;
    private List<Car> list;

    public CarCrudAdapter(Context context, List<Car> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CarCrudAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_car_crud, parent, false);
        return new CarCrudAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarCrudAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(list.get(position).getName());
        holder.tvPrice.setText(list.get(position).getPrice());
        holder.tvYear.setText(list.get(position).getYear());
        holder.tvPassenger.setText(list.get(position).getPassenger());
        holder.tvTransmission.setText(list.get(position).getTransmission());
        Glide.with(context).load(list.get(position).getImage()).into(holder.ivCarPhoto);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvPrice, tvYear, tvPassenger, tvTransmission;
        ImageView ivCarPhoto;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCarName);
            tvPrice = itemView.findViewById(R.id.tvCarPrice);
            tvYear = itemView.findViewById(R.id.tvCarYear);
            tvPassenger = itemView.findViewById(R.id.tvCarSeat);
            tvTransmission = itemView.findViewById(R.id.tvCarTransmission);
            ivCarPhoto = itemView.findViewById(R.id.ivCarPhoto);
        }
    }
}
