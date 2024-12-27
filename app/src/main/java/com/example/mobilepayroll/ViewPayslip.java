package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewPayslip extends AppCompatActivity {
    private TextView payslip, payslip_name, payslip_email, payslip_dept, payslip_status, payslip_earnings
            , payslip_deduction, payslip_net_salary;
    private Button delete_payslip;
    private String fullName, department, email, status, earnings, deduction, netpay, payslipTitle, documentId;
    private FirebaseFirestore db;
    private DocumentReference payslipRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_payslip);

        payslip = findViewById(R.id.payslip_title);
        payslip_name = findViewById(R.id.payslip_name);
        payslip_email = findViewById(R.id.payslip_email);
        payslip_dept = findViewById(R.id.payslip_dept);
        payslip_status = findViewById(R.id.payslip_status);
        payslip_earnings = findViewById(R.id.payslip_earnings);
        payslip_deduction = findViewById(R.id.payslip_deduction);
        payslip_net_salary = findViewById(R.id.payslip_netpay);

        delete_payslip = findViewById(R.id.payslip_delete);

        Intent intent = getIntent();
        if (intent != null) {
            fullName = intent.getStringExtra("FullName");
            department = intent.getStringExtra("Department");
            status = intent.getStringExtra("Status");
            email = intent.getStringExtra("Email");
            earnings = intent.getStringExtra("TotalEarnings");
            deduction = intent.getStringExtra("TotalDeduction");
            netpay = intent.getStringExtra("NetPay");
            payslipTitle = intent.getStringExtra("PayrollTitle");

            payslip.setText(payslipTitle);
            payslip_name.setText(fullName);
            payslip_email.setText(email);
            payslip_dept.setText(department);
            payslip_status.setText(status);
            payslip_earnings.setText(earnings);
            payslip_deduction.setText(deduction);
            payslip_net_salary.setText(netpay);
        }

        db = FirebaseFirestore.getInstance();
        String documentId  = fullName + getCurrentDate();
        payslipRef = db.collection("payslips").document(documentId);

        delete_payslip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePayslip();
            }
        });
    }

    private void deletePayslip() {
        payslipRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ViewPayslip.this, "Payslip deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ViewPayslip.this, "Failed to delete payslip", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}
