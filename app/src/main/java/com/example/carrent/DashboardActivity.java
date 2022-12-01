package com.example.carrent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.carrent.adapter.CarAdapter;
import com.example.carrent.adapter.UserAdapter;
import com.example.carrent.model.Car;
import com.example.carrent.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private ImageView ivMenu;
    private RecyclerView recyclerViewMobil, recyclerViewWorker;
    private ConstraintLayout dashboard, car, worker;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Car> listCar = new ArrayList<>();
    private List<User> listUser = new ArrayList<>();
    private CarAdapter carAdapter;
    private UserAdapter userAdapter;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        car = findViewById(R.id.CarButtonSection);
        dashboard = findViewById(R.id.DashboardButtonSection);
        worker = findViewById(R.id.WorkerButtonSection);
        recyclerViewMobil = findViewById(R.id.recyclerViewMobil);
        recyclerViewWorker = findViewById(R.id.recyclerViewWorker);
        ivMenu = findViewById(R.id.ivPopUp);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(DashboardActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Get Data...");
        carAdapter = new CarAdapter(getApplicationContext(), listCar);
        userAdapter = new UserAdapter(getApplicationContext(), listUser);

        RecyclerView.LayoutManager layoutManagerMobil = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decorationMobil = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerViewMobil.setLayoutManager(layoutManagerMobil);
        recyclerViewMobil.addItemDecoration(decorationMobil);
        recyclerViewMobil.setAdapter(carAdapter);

        RecyclerView.LayoutManager layoutManagerWorker = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decorationWorker = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerViewWorker.setLayoutManager(layoutManagerWorker);
        recyclerViewWorker.addItemDecoration(decorationWorker);
        recyclerViewWorker.setAdapter(userAdapter);

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dashboard = new Intent(DashboardActivity.this, DashboardActivity.class);
                startActivity(dashboard);
            }
        });

        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent car = new Intent(DashboardActivity.this, DataCarActivity.class);
                startActivity(car);
            }
        });

        worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(DashboardActivity.this, WorkerActivity.class);
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

    private void reloadLogin(){
        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        getData();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            reloadLogin();
        }
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
                                car.setId(document.getId());
                                listCar.add(car);
                            }
                            carAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getApplicationContext(), "Error get data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        listUser.clear();
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                User user = new User(document.getString("name"), document.getString("position"), document.getString("phone"), document.getString("gender"), document.getString("address"), document.getString("imageUser"));
                                user.setId(document.getId());
                                listUser.add(user);
                            }
                            userAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getApplicationContext(), "Error get data", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(DashboardActivity.this, v);
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

//    private void deleteDataMobil(String id){
//        progressDialog.show();
//        db.collection("users").document(id)
//                .delete()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (!task.isSuccessful()){
//                            Toast.makeText(getApplicationContext(), "Failed delete data", Toast.LENGTH_SHORT).show();
//                        }
//                        progressDialog.dismiss();
//                        getData();
//                    }
//                });
//    }
}