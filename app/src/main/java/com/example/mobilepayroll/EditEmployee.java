package com.example.mobilepayroll;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EditEmployee extends AppCompatActivity {
    private TextView Display_Name;
    private EditText Display_Email, Display_Phone, Display_Role, Display_Status;
    private ImageView Display_Image;
    private String fullName, department, email, imageUrl, phoneNumber, status, basicPay;
    private Button btnSave;
    private StorageReference storageReference;
    private Uri imageUri;

    private Dialog dialog;
    private Button btnDialogNo, btnDialogYes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);

        Display_Name = findViewById(R.id.Payslip_Name);
        Display_Email = findViewById(R.id.payslip_email);
        Display_Phone = findViewById(R.id.display_phone);
        Display_Role = findViewById(R.id.displayRole);
        Display_Image = findViewById(R.id.displayphoto);
        Display_Status = findViewById(R.id.payslip_status);
        btnSave = findViewById(R.id.btnUpdateChanges);
        TextView BackButton = findViewById(R.id.titleEditEmpInfo);

        storageReference = FirebaseStorage.getInstance().getReference();

        Intent getdata = getIntent();
        if (getdata != null) {
            fullName = getdata.getStringExtra("fullName");
            email = getdata.getStringExtra("email");
            phoneNumber = getdata.getStringExtra("phoneNumber");
            department = getdata.getStringExtra("department");
            imageUrl = getdata.getStringExtra("imageUrl");
            status = getdata.getStringExtra("status");

            Display_Name.setText(fullName);
            Display_Role.setText(department);
            Display_Status.setText(status);
            Display_Email.setText(email);
            Display_Phone.setText(phoneNumber);
            Picasso.get().load(imageUrl).into(Display_Image);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = Display_Name.getText().toString();
                String updatedDepartment = Display_Role.getText().toString();
                String updatedStatus = Display_Status.getText().toString();
                String updatedEmail = Display_Email.getText().toString();
                String updatedPhone = Display_Phone.getText().toString();
                updateEmployeeDocument(updatedName, updatedDepartment, updatedStatus, updatedEmail, updatedPhone, imageUrl);
            }
        });

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog();
            }
        });

        Display_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 1000);
            }
        });
    }

    private void ShowDialog() {

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.cancel_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.white_bg));
        dialog.setCancelable(false);
        btnDialogNo = dialog.findViewById(R.id.btnDialogNo);
        btnDialogYes = dialog.findViewById(R.id.btnDialogYes);


        btnDialogYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent BackToEmployeeListPageFunction = new Intent(EditEmployee.this, EmployeeDisplay.class);
                startActivity(BackToEmployeeListPageFunction);
                dialog.dismiss();
            }
        });


        btnDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void updateEmployeeDocument(String updatedName, String updatedDepartment, String updatedStatus,
                                        String updatedEmail, String updatedPhone, String updatedImage) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference employeeRef = db.collection("employees").document(fullName);
        employeeRef.update("fullName", updatedName,
                        "department", updatedDepartment,
                        "status", updatedStatus, "email", updatedEmail, "phoneNumber", updatedPhone, "imageUrl", updatedImage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditEmployee.this, "Employee details updated successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(EditEmployee.this, EmployeeDisplay.class);
                        intent.putExtra("fullName", updatedName);
                        intent.putExtra("email", updatedEmail);
                        intent.putExtra("department", updatedDepartment);
                        intent.putExtra("status", updatedStatus);
                        intent.putExtra("phoneNumber", updatedPhone);
                        intent.putExtra("imageUrl", imageUrl);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditEmployee.this, "Failed to update employee details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(Display_Image);

            if (imageUri != null) {
                String documentId = getIntent().getStringExtra("email");
                if (documentId != null) {
                    uploadImage(documentId);
                } else {
                    Toast.makeText(EditEmployee.this, "Error: Document ID not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EditEmployee.this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void uploadImage(String email) {
        if (imageUri != null) {
            StorageReference ref = storageReference.child("employees/" + email + "/" + System.currentTimeMillis() + ".jpg");
            ref.putFile(imageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ref.getDownloadUrl().addOnCompleteListener(uriTask -> {
                                if (uriTask.isSuccessful()) {
                                    imageUrl = uriTask.getResult().toString();
                                    String updatedName = Display_Name.getText().toString();
                                    String updatedDepartment = Display_Role.getText().toString();
                                    String updatedStatus = Display_Status.getText().toString();
                                    String updatedEmail = Display_Email.getText().toString();
                                    String updatedPhone = Display_Phone.getText().toString();
                                    updateEmployeeDocument(updatedName, updatedDepartment, updatedStatus, updatedEmail, updatedPhone, imageUrl);

                                } else {
                                    Toast.makeText(EditEmployee.this, "Failed to get image URL", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(EditEmployee.this, "Upload Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(EditEmployee.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

}
