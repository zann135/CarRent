package com.example.carrent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.carrent.adapter.CarAdapter;
import com.example.carrent.adapter.CarCrudAdapter;
import com.example.carrent.model.Car;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DataCarActivity extends AppCompatActivity {
    private Button btnTambahMobil;
    private ImageView ivMenu;
    private ConstraintLayout dashboard, car, worker;

    private RecyclerView recyclerViewMobil;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Car> listCar = new ArrayList<>();
    private CarCrudAdapter carCrudAdapter;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_car);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        car = findViewById(R.id.CarButtonSection);
        dashboard = findViewById(R.id.DashboardButtonSection);
        worker = findViewById(R.id.WorkerButtonSection);
        recyclerViewMobil = findViewById(R.id.recyclerViewMobil);
        ivMenu = findViewById(R.id.ivPopUp);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(DataCarActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Get Data...");
        carCrudAdapter = new CarCrudAdapter(getApplicationContext(), listCar);

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searching = new Intent(DataCarActivity.this, DashboardActivity.class);
                startActivity(searching);
            }
        });

        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searching = new Intent(DataCarActivity.this, DataCarActivity.class);
                startActivity(searching);
            }
        });

        worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(DataCarActivity.this, WorkerActivity.class);
                startActivity(profile);
            }
        });

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(view);
            }
        });
    }

    private void getData(){
        progressDialog.show();
        db.collection("cars")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        listCar.clear();
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Car car = new Car(document.getString("nama"), document.getString("harga"), document.getString("imageCar"), document.getString("tahun"), document.getString("jumlah_orang"), document.getString("transmisi"));
                                listCar.add(car);
                            }
                            carCrudAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getApplicationContext(), "Error get data", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(DataCarActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.logout){
                    mAuth.signOut();
                    reloadLogin();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void reloadLogin(){
        startActivity(new Intent(DataCarActivity.this, LoginActivity.class));
    }
}