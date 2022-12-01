package com.example.carrent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.carrent.adapter.CarAdapter;
import com.example.carrent.model.Car;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class DataCarActivity extends AppCompatActivity {
    private Button btnTambahMobil;
    private ImageView ivMenu;
    private ConstraintLayout dashboard, car, worker;

    private RecyclerView recyclerViewMobil;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Car> listCar = new ArrayList<>();
    private CarAdapter carAdapter;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_car);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        btnTambahMobil = findViewById(R.id.bInputCar);

        car = findViewById(R.id.CarButtonSection);
        dashboard = findViewById(R.id.DashboardButtonSection);
        worker = findViewById(R.id.WorkerButtonSection);
        recyclerViewMobil = findViewById(R.id.recyclerViewMobil);
        ivMenu = findViewById(R.id.ivPopUp);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(DataCarActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Get Data...");
        carAdapter = new CarAdapter(getApplicationContext(), listCar);
        carAdapter.setDialog(new CarAdapter.Dialog() {
            @Override
            public void onClick(int position) {
                final CharSequence[] items = {"Edit", "Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(DataCarActivity.this);
                dialog.setTitle("Choose Action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                Intent intent = new Intent(getApplicationContext(), InputCarActivity.class);
                                intent.putExtra("id", listCar.get(position).getId());
                                intent.putExtra("nama", listCar.get(position).getName());
                                intent.putExtra("tahun", listCar.get(position).getYear());
                                intent.putExtra("harga", listCar.get(position).getPrice());
                                intent.putExtra("transmisi", listCar.get(position).getTransmission());
                                intent.putExtra("kapasitas", listCar.get(position).getPassenger());
                                intent.putExtra("gambar", listCar.get(position).getImage());
                                startActivity(intent);
                                break;
                            case 1:
                                deleteDataCars(listCar.get(position).getId(), listCar.get(position).getImage());
                                break;
                        }
                    }
                });
                dialog.show();
            }
        });

        RecyclerView.LayoutManager layoutManagerMobil = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decorationMobil = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerViewMobil.setLayoutManager(layoutManagerMobil);
        recyclerViewMobil.addItemDecoration(decorationMobil);
        recyclerViewMobil.setAdapter(carAdapter);

        btnTambahMobil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DataCarActivity.this, InputCarActivity.class);
                startActivity(intent);
            }
        });

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dashboard = new Intent(DataCarActivity.this, DashboardActivity.class);
                startActivity(dashboard);
            }
        });

        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent car = new Intent(DataCarActivity.this, DataCarActivity.class);
                startActivity(car);
            }
        });

        worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent worker = new Intent(DataCarActivity.this, WorkerActivity.class);
                startActivity(worker);
            }
        });

        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(view);
            }
        });
    }

    public void onStart() {
        super.onStart();
        getDataMobil();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            reloadLogin();
        }
    }

    private void getDataMobil(){
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
                                car.setId(document.getId());
                                listCar.add(car);
                            }
                            carAdapter.notifyDataSetChanged();
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

    private void deleteDataCars(String id, String imageCar){
        progressDialog.show();
        db.collection("cars").document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Failed delete data", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }else{
                            FirebaseStorage.getInstance().getReferenceFromUrl(imageCar).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    getDataMobil();
                                }
                            });
                        }
                    }
                });
    }
}