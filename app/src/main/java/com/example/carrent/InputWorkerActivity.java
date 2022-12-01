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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class InputWorkerActivity extends AppCompatActivity {
    private ImageView workerPhoto;
    private EditText workerName, workerPosition, workerPhone, workerAddress;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnInput;
    private int GALLERY_REQUEST_CODE = 102, CAMERA_REQUEST_CODE = 101;
    private String id = "";

    private ProgressDialog progressDialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_worker);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        workerPhoto = findViewById(R.id.civWorkerPhoto);
        workerName = findViewById(R.id.etFullName);
        workerPosition = findViewById(R.id.etPosition);
        workerPhone = findViewById(R.id.etTelephone);
        workerAddress = findViewById(R.id.etAddress);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.Male);
        rbFemale = findViewById(R.id.Female);

        btnInput = findViewById(R.id.btnRegister);

        progressDialog = new ProgressDialog(InputWorkerActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Save data...");

        workerPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(workerName.getText().length()>0 && workerPosition.getText().length()>0 && workerPhone.getText().length()>0 && workerAddress.getText().length()>0) {
                    int SelectedId = rgGender.getCheckedRadioButtonId();
                    String value = "";

                    if(SelectedId == rbMale.getId()){
                        value = rbMale.getText().toString();
                    }else if(SelectedId == rbFemale.getId()){
                        value = rbFemale.getText().toString();
                    }
                    upload(workerName.getText().toString(), workerPosition.getText().toString(), workerPhone.getText().toString(), value, workerAddress.getText().toString());
                }else{
                    Toast.makeText(InputWorkerActivity.this, "Please fill all the field", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent intent = getIntent();
        if(intent != null){
            id = intent.getStringExtra("id");
            workerName.setText(intent.getStringExtra("name"));
            workerPosition.setText(intent.getStringExtra("position"));
            workerPhone.setText(intent.getStringExtra("phone"));
            workerAddress.setText(intent.getStringExtra("address"));
            Glide.with(getApplicationContext()).load(intent.getStringExtra("imageUser")).into(workerPhoto);
        }
    }

    private void selectImage(){
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(InputWorkerActivity.this);
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
    }

    private void upload(String fullName, String position, String phone, String valueRadio, String address){
        progressDialog.show();

        workerPhoto.setDrawingCacheEnabled(true);
        workerPhoto.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) workerPhoto.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        //UPLOAD
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("imageUser").child("IMG" + System.currentTimeMillis() + ".jpg");
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
                                    input(fullName, position, phone, valueRadio, address, task.getResult().toString());
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

    private void input(String fullName, String position, String phone, String gender, String address, String imageUser) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", fullName);
        user.put("position", position);
        user.put("phone", phone);
        user.put("gender", gender);
        user.put("address", address);
        user.put("imageUser", imageUser);

        progressDialog.show();
        if (id!=null){
            db.collection("users").document(id)
                    .set(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(InputWorkerActivity.this, "Data has been updated", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(InputWorkerActivity.this, "Failed to update data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            db.collection("users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "Input Success", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent intent = new Intent(InputWorkerActivity.this, WorkerActivity.class);
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
                    workerPhoto.post(() -> {
                        workerPhoto.setImageBitmap(bitmap);
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
                workerPhoto.post(() -> {
                    workerPhoto.setImageBitmap(bitmap);
                });
            });
        }
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Load image cancelled", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}