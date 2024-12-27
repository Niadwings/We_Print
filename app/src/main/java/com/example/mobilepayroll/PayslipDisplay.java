package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PayslipDisplay extends AppCompatActivity {

    TextView nameTextView, designationTextView, displayStatus, displayEmail, displayEarnings,
            displayDeductions, displayNet, payslipTitle;
    Button sendEmailButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payslip_display);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        nameTextView = findViewById(R.id.payslip_name);
        designationTextView = findViewById(R.id.payslip_dept);
        displayEmail = findViewById(R.id.payslip_email);
        displayEarnings = findViewById(R.id.payslip_earnings);
        displayDeductions = findViewById(R.id.payslip_deduction);
        displayNet = findViewById(R.id.payslip_netpay);
        payslipTitle = findViewById(R.id.payslip_title);
        displayStatus = findViewById(R.id.display_status);

        sendEmailButton = findViewById(R.id.sendEmail_btn);

        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("FullName");
            String designation = intent.getStringExtra("Department");
            String email = intent.getStringExtra("Email");
            String status = intent.getStringExtra("Status");
            String payrollTitle = intent.getStringExtra("PayrollTitle");
            String earnings = intent.getStringExtra("TotalEarnings");
            String deductions = intent.getStringExtra("TotalDeduction");
            String netPay = intent.getStringExtra("NetPay");

            payslipTitle.setText(payrollTitle);
            nameTextView.setText(name);
            designationTextView.setText(designation);
            displayStatus.setText(status);
            displayEmail.setText(email);
            displayEarnings.setText(earnings);
            displayDeductions.setText(deductions);
            displayNet.setText(netPay);

            sendEmailButton.setOnClickListener(v -> saveToFirestoreAndSendEmail(email, payrollTitle, name, designation, earnings, deductions, netPay));
        }
    }

    private void saveToFirestoreAndSendEmail(String email, String payrollTitle, String name, String designation, String earnings, String deductions, String netPay) {
        // Check if a payroll for the employee on the current day already exists
        String documentId = name + getCurrentDate();
        DocumentReference payrollRef = db.collection("payslips").document(documentId);
        payrollRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Toast.makeText(this, "A payroll for this employee already exists today", Toast.LENGTH_SHORT).show();
            } else {
                // Save to Firestore
                Map<String, Object> payslipData = new HashMap<>();
                payslipData.put("FullName", name);
                payslipData.put("Department", designation);
                payslipData.put("Email", email);
                payslipData.put("Status", displayStatus.getText().toString());
                payslipData.put("PayrollTitle", payrollTitle);
                payslipData.put("TotalEarnings", earnings);
                payslipData.put("TotalDeduction", deductions);
                payslipData.put("NetPay", netPay);

                payrollRef.set(payslipData).addOnSuccessListener(documentReference -> {
                    sendEmail(email, payrollTitle, name, designation, earnings, deductions, netPay);
                }).addOnFailureListener(e -> {
                    // Handle failure to save to Firestore
                    Toast.makeText(this, "Failed to save payslip data", Toast.LENGTH_SHORT).show();
                });
            }
        }).addOnFailureListener(e -> {
            // Handle failure to check existing payroll
            Toast.makeText(this, "Failed to check existing payroll", Toast.LENGTH_SHORT).show();
        });
    }


    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private void sendEmail(String email, String payrollTitle, String name, String designation, String earnings, String deductions, String netPay) {
        String subject = "Subject: " + payrollTitle;
        String body = "Hello," + name + "\n\nPlease see attached payslip." + "\n\nEarnings: " + earnings + "\nDeductions: " + deductions + "\nNet Pay: " + netPay + "\n\nRegards,\nWePrint Admin";

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }
}
