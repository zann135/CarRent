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
import android.widget.TextView;
import android.widget.Toast;

import com.example.carrent.adapter.UserAdapter;
import com.example.carrent.model.User;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkerActivity extends AppCompatActivity {
    private Button btnTambahWorker;
    private ImageView ivMenu;
    private ConstraintLayout dashboard, car, worker;

    private RecyclerView recyclerViewWorker;
    private ProgressDialog progressDialog;
    private List<User> listUser = new ArrayList<>();
    private UserAdapter userAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnTambahWorker = findViewById(R.id.bInputWorker);
        dashboard = findViewById(R.id.DashboardButtonSection);
        car = findViewById(R.id.CarButtonSection);
        worker = findViewById(R.id.WorkerButtonSection);
        ivMenu = findViewById(R.id.ivPopUp);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(WorkerActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Get Data...");
        userAdapter = new UserAdapter(getApplicationContext(), listUser);
        userAdapter.setDialog(new UserAdapter.Dialog() {
            @Override
            public void onClick(int position) {
                final CharSequence[] items = {"Edit", "Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(WorkerActivity.this);
                dialog.setTitle("Choose Action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                Intent intent = new Intent(getApplicationContext(), InputWorkerActivity.class);
                                intent.putExtra("id", listUser.get(position).getId());
                                intent.putExtra("position", listUser.get(position).getPosition());
                                intent.putExtra("phone", listUser.get(position).getPhone());
                                intent.putExtra("address", listUser.get(position).getAddress());
                                intent.putExtra("imageUser", listUser.get(position).getImageUser());
                                startActivity(intent);
                                break;
                            case 1:
                                deleteDataWorkers(listUser.get(position).getId(), listUser.get(position).getImageUser());
                                break;
                        }
                    }
                });
                dialog.show();
            }
        });

        recyclerViewWorker = findViewById(R.id.recyclerViewWorker);

        RecyclerView.LayoutManager layoutManagerWorker = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decorationWorker = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerViewWorker.setLayoutManager(layoutManagerWorker);
        recyclerViewWorker.addItemDecoration(decorationWorker);
        recyclerViewWorker.setAdapter(userAdapter);

        btnTambahWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkerActivity.this, InputWorkerActivity.class);
                startActivity(intent);
            }
        });

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searching = new Intent(WorkerActivity.this, DashboardActivity.class);
                startActivity(searching);
            }
        });

        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searching = new Intent(WorkerActivity.this, DataCarActivity.class);
                startActivity(searching);
            }
        });

        worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(WorkerActivity.this, WorkerActivity.class);
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

    public void onStart() {
        super.onStart();
        getDataWorker();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            reloadLogin();
        }
    }

    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(WorkerActivity.this, v);
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
        startActivity(new Intent(WorkerActivity.this, LoginActivity.class));
    }

    private void getDataWorker(){
        progressDialog.show();
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

    private void deleteDataWorkers(String id, String imageUser){
        progressDialog.show();
        db.collection("users").document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Failed delete data", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }else{
                            FirebaseStorage.getInstance().getReferenceFromUrl(imageUser).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    getDataWorker();
                                }
                            });
                        }
                    }
                });
    }
}