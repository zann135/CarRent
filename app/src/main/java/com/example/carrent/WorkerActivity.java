package com.example.carrent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkerActivity extends AppCompatActivity {
    private Button btnTambahWorker;
    private ImageView ivMenu;
    private FirebaseUser firebaseUser;
    private ConstraintLayout dashboard, car, worker;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        dashboard = findViewById(R.id.DashboardButtonSection);
        car = findViewById(R.id.CarButtonSection);
        worker = findViewById(R.id.WorkerButtonSection);
        ivMenu = findViewById(R.id.ivPopUp);

        mAuth = FirebaseAuth.getInstance();

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
}