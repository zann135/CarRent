package com.example.carrent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class InputCarActivity extends AppCompatActivity {
    private ImageView carPhoto;
    private EditText carName, carYear, carPrice, carTransmission, carCapacity;
    private Button btnInput;
    private int GALLERY_REQUEST_CODE = 102, CAMERA_REQUEST_CODE = 101;
    private String id = "";

    private ProgressDialog progressDialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_car);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        carPhoto = findViewById(R.id.ivCarPhoto);
        carName = findViewById(R.id.etCarNameInput);
        carYear = findViewById(R.id.etCarYearInput);
        carPrice = findViewById(R.id.etCarPriceInput);
        carTransmission = findViewById(R.id.etCarTransmissionInput);
        carCapacity = findViewById(R.id.etCarSeatInput);
        btnInput = findViewById(R.id.btnCarInput);

        progressDialog = new ProgressDialog(InputCarActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Save data...");

        carPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(carName.getText().length()>0 && carYear.getText().length()>0 && carPrice.getText().length()>0 && carTransmission.getText().length()>0 && carCapacity.getText().length()>0) {
                    progressDialog.show();
                    upload(carName.getText().toString(), carYear.getText().toString(), carPrice.getText().toString(), carTransmission.getText().toString(), carCapacity.getText().toString());
                }
            }
        });

        Intent intent = getIntent();
        if(intent != null){
            id = intent.getStringExtra("id");
            carName.setText(intent.getStringExtra("nama"));
            carYear.setText(intent.getStringExtra("tahun"));
            carPrice.setText(intent.getStringExtra("harga"));
            carTransmission.setText(intent.getStringExtra("transmisi"));
            carCapacity.setText(intent.getStringExtra("kapasitas"));
            Glide.with(InputCarActivity.this).load(intent.getStringExtra("gambar")).into(carPhoto);
        }
    }

    private void selectImage(){
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(InputCarActivity.this);
        builder.setTitle(getString(R.string.app_name));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, (dialog, item)-> {
            if (items[item].equals("Take Photo")) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else if (items[item].equals("Choose from Gallery")) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                Intent setType = intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY_REQUEST_CODE);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    };

    private void upload(String nama, String tahun, String harga, String transmisi, String kapasitas){
        progressDialog.show();

        carPhoto.setDrawingCacheEnabled(true);
        carPhoto.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) carPhoto.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        //UPLOAD
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("imageCar").child("IMG" + System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = reference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata()!=null){
                    if (taskSnapshot.getMetadata().getReference()!=null){
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.getResult()!=null){
                                    input(nama, tahun, harga, transmisi, kapasitas, task.getResult().toString());
                                }else{
                                    Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        progressDialog.show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    progressDialog.show();
                }
            }
        });
    }

    private void input(String nama, String tahun, String harga, String transmisi, String kapasitas, String imgCar) {
        Map<String, Object> car = new HashMap<>();
        car.put("nama", nama);
        car.put("tahun", tahun);
        car.put("harga", harga);
        car.put("transmisi", transmisi);
        car.put("jumlah_orang", kapasitas);
        car.put("imageCar", imgCar);

        progressDialog.show();
        if (id != null){
            db.collection("cars").document(id)
                    .set(car)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(InputCarActivity.this, "Data has been updated", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(InputCarActivity.this, "Failed to update data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
        db.collection("cars")
                .add(car)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Input Success", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Intent intent = new Intent(InputCarActivity.this, DataCarActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            final Uri path = data.getData();
            Thread thread = new Thread(() -> {
                try{
                    InputStream inputStream = getContentResolver().openInputStream(path);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    carPhoto.post(() -> {
                        carPhoto.setImageBitmap(bitmap);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            final Bundle extras = data.getExtras();
            Thread thread = new Thread(() -> {
                Bitmap bitmap = (Bitmap) extras.get("data");
                carPhoto.post(() -> {
                    carPhoto.setImageBitmap(bitmap);
                });
            });
        }
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Load image cancelled", Toast.LENGTH_SHORT).show();
            return;
        }
    }

}