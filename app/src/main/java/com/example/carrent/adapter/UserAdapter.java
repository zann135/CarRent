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
import com.example.carrent.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private Context context;
    private List<User> list;
    private Dialog dialog;

    public interface Dialog{
        void onClick(int position);
    }

    public Dialog getDialog() {
        return dialog;
    }

    public UserAdapter(Context context, List<User> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvFullname.setText(list.get(position).getFullname());
        holder.tvPosition.setText(list.get(position).getPosition());
        holder.tvPhone.setText(list.get(position).getPhone());
        holder.tvGender.setText(list.get(position).getGender());
        holder.tvAddress.setText(list.get(position).getAddress());
        Glide.with(context).load(list.get(position).getImageUser()).into(holder.imageUser);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvFullname, tvGender, tvPhone, tvAddress, tvPosition;
        ImageView imageUser;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullname = itemView.findViewById(R.id.tvUserName);
            tvGender = itemView.findViewById(R.id.tvUserGender);
            tvPhone = itemView.findViewById(R.id.tvUserPhone);
            tvAddress = itemView.findViewById(R.id.tvUserAddress);
            tvPosition = itemView.findViewById(R.id.tvUserPosition);
            imageUser = itemView.findViewById(R.id.civUserPhoto);
        }
    }
}
