package com.example.mobilepayroll;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class EmployeeDisplay extends AppCompatActivity {
    private static final int EDIT_EMPLOYEE_REQUEST_CODE = 1;

   private FirebaseFirestore db;
    private String fullName, department, email, imageUrl, phoneNumber, status, basicPay;
    private ImageView DisplayEmpPic;
    private TextView DisplayEmpName, DisplayEmpRole, DisplayEMpStatus, DisplayEmpEmail, DisplayPhoneNum;

    Dialog dialog;
    Button btnDialogNo, btnDialogYes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_display);
        db = FirebaseFirestore.getInstance();

        DisplayEmpPic = findViewById(R.id.displayphoto);
        DisplayEmpName = findViewById(R.id.Payslip_Name);
        DisplayEmpRole = findViewById(R.id.displayRole);
        DisplayEMpStatus = findViewById(R.id.payslip_status);
        DisplayEmpEmail = findViewById(R.id.payslip_email);
        DisplayPhoneNum = findViewById(R.id.payslip_earnings);

        Button EditEmp_Btn = findViewById(R.id.edit_Emp_Info);
        Button PayrollBtn = findViewById(R.id.pay_btn);
        Button DeleteEmpBtn = findViewById(R.id.delete_btnn);

        TextView BackToEmployeeList = findViewById(R.id.titleEmpInfo);

        EditEmp_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToEditEmployee = new Intent(EmployeeDisplay.this, EditEmployee.class);
                goToEditEmployee.putExtra("fullName", fullName);
                goToEditEmployee.putExtra("department", department);
                goToEditEmployee.putExtra("imageUrl", imageUrl);
                goToEditEmployee.putExtra("status", status);
                goToEditEmployee.putExtra("email", email);
                goToEditEmployee.putExtra("phoneNumber", phoneNumber);
                startActivityForResult(goToEditEmployee, EDIT_EMPLOYEE_REQUEST_CODE);
            }
        });

        PayrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoPayrollPage();
            }
        });

        DeleteEmpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDeleteDialog();
            }
        });

        BackToEmployeeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoEmployeeList = new Intent(EmployeeDisplay.this, EmployeeList.class);
                startActivity(gotoEmployeeList);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            fullName = intent.getStringExtra("fullName");
            department = intent.getStringExtra("department");
            imageUrl = intent.getStringExtra("imageUrl");
            status = intent.getStringExtra("status");
            email = intent.getStringExtra("email");
            phoneNumber = intent.getStringExtra("phoneNumber");
            basicPay = intent.getStringExtra("basicPay");

            DisplayEmpName.setText(fullName);
            DisplayEmpRole.setText(department);
            Picasso.get().load(imageUrl).into(DisplayEmpPic);
            DisplayEMpStatus.setText(status);
            DisplayEmpEmail.setText(email);
            DisplayPhoneNum.setText(phoneNumber);
        }
    }

    private void ShowDeleteDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.delete_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.white_bg));
        dialog.setCancelable(false);
        btnDialogNo = dialog.findViewById(R.id.btnDialogNo);
        btnDialogYes = dialog.findViewById(R.id.btnDialogYes);


        btnDialogYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEmployee();
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

    private void GotoPayrollPage() {
        Intent goToPayroll = new Intent(EmployeeDisplay.this, PayrollComputation.class);
        goToPayroll.putExtra("fullName", fullName);
        goToPayroll.putExtra("department", department);
        goToPayroll.putExtra("basicPay", basicPay);
        goToPayroll.putExtra("email", email);
        goToPayroll.putExtra("status", status);
        startActivity(goToPayroll);
        finish();
    }

    private void deleteEmployee() {
        db.collection("employees").document(fullName)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                            storageRef.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(EmployeeDisplay.this, "Employee Deleted!", Toast.LENGTH_SHORT).show();
                                                Intent GotoEmployelist = new Intent(EmployeeDisplay.this, EmployeeList.class);
                                                startActivity(GotoEmployelist);
                                                finish();
                                            } else {
                                                Toast.makeText(EmployeeDisplay.this, "Failed to delete image", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(EmployeeDisplay.this, "Failed to delete employee", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_EMPLOYEE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            fullName = data.getStringExtra("fullName");
            department = data.getStringExtra("department");
            imageUrl = data.getStringExtra("imageUrl");
            status = data.getStringExtra("status");
            email = data.getStringExtra("email");
            phoneNumber = data.getStringExtra("phoneNumber");

            DisplayEmpName.setText(fullName);
            DisplayEmpRole.setText(department);
            Picasso.get().load(imageUrl).into(DisplayEmpPic);
            DisplayEMpStatus.setText(status);
            DisplayEmpEmail.setText(email);
            DisplayPhoneNum.setText(phoneNumber);
        }
    }
}
