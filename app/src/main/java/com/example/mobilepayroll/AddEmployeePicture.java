package com.example.mobilepayroll;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class AddEmployeePicture extends AppCompatActivity {
    private ImageView employeeProfPic;
    private StorageReference storageReference;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee_picture);
        TextView backToAddEmployeeList = findViewById(R.id.titleEmployeePicture);
        TextView uploadEmpPic = findViewById(R.id.Upload_emp_pic);
        Button doneUploadPic = findViewById(R.id.ButtonToEmpList);
        employeeProfPic = findViewById(R.id.EmployeePicture);
        storageReference = FirebaseStorage.getInstance().getReference();

        uploadEmpPic.setOnClickListener(v -> {
            Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(openGallery, 1000);
        });

        doneUploadPic.setOnClickListener(v -> {
            if (imageUri != null) {
                String documentId = getIntent().getStringExtra("Email");
                if (documentId != null) {
                    uploadImage(documentId);
                } else {
                    Toast.makeText(AddEmployeePicture.this, "Error: Document ID not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddEmployeePicture.this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        });
        backToAddEmployeeList.setOnClickListener(v -> {
            Intent backIntent = new Intent(AddEmployeePicture.this, AddEmployeeActivity.class);
            startActivity(backIntent);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(employeeProfPic);
        }
    }
    private void uploadImage(String email) {
        if (imageUri != null) {
            StorageReference ref = storageReference.child("employees/" + email + "/" + System.currentTimeMillis() + ".jpg");
            ref.putFile(imageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddEmployeePicture.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                            ref.getDownloadUrl().addOnCompleteListener(uriTask -> {
                                if (uriTask.isSuccessful()) {
                                    String imageUrl = uriTask.getResult().toString();
                                    saveEmployeeToFirestore(imageUrl);
                                } else {
                                    Toast.makeText(AddEmployeePicture.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(AddEmployeePicture.this, "Upload Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(AddEmployeePicture.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveEmployeeToFirestore(String imageUrl) {
        Intent intent = getIntent();
        String fullName = intent.getStringExtra("FullName");
        String email = intent.getStringExtra("Email");
        String phoneNumber = intent.getStringExtra("PhoneNumber");
        String department = intent.getStringExtra("Department");
        String basicPay = intent.getStringExtra("BasicPay");
        String  status = intent.getStringExtra("Status");


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Employee employee = new Employee(fullName, email, phoneNumber, department, basicPay, imageUrl, status, userId);

        db.collection("employees").document(fullName)
                .set(employee)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddEmployeePicture.this, "Employee Added Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddEmployeePicture.this, EmployeeList.class));
                        finish();
                    } else {
                        Toast.makeText(AddEmployeePicture.this, "Failed to Add Employee", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

