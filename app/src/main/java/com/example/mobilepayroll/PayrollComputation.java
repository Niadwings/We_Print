
package com.example.mobilepayroll;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PayrollComputation extends AppCompatActivity {

    private Dialog dialog;
    private Button btnDialogNo, btnDialogYes;

    private TextView Emp_Rate, Emp_Name, Emp_Designation, DisplayTotalEarnings, DisplayTotalDeduction,
            DisplayNetPay, Payroll_Tittle, CancelPayroll, Emp_OvertimeRate, Emp_OverTimePay, Emp_BasicPay, Emp_Email, Emp_Status;

    private EditText  Emp_Total_Days, Emp_TotalWeeks, Emp_AdditionalPayment, Emp_SpecialAllowance,

    Emp_Tax, Emp_SSS, Emp_PHealth, Emp_PagIbig, Emp_CashAdvance, Emp_MealAllowance, Emp_Shop;

    private Button Savebtn;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payroll_computation);
        db = FirebaseFirestore.getInstance();
        Emp_Name = findViewById(R.id.Payroll_EmpName);
        Emp_Designation = findViewById(R.id.Emp_Designation);
        Emp_Rate = findViewById(R.id.EmployeeRate);
        Emp_Total_Days = findViewById(R.id.EmployeeTotalDays);
        Emp_TotalWeeks = findViewById(R.id.EmployeeWeeklyHrs);
        Emp_BasicPay = findViewById(R.id.EmployeeBasicPay);
        Emp_OvertimeRate = findViewById(R.id.EmployeeOverTime);
        Emp_OverTimePay = findViewById(R.id.EmployeeOverTimePay);
        Emp_Email = findViewById(R.id.EmpEmail);
        Emp_Status = findViewById(R.id.EmpStatus);
        Emp_AdditionalPayment = findViewById(R.id.EmployeeAdditionalPay);
        Emp_SpecialAllowance = findViewById(R.id.EmployeeSpecialAllowance);
        Payroll_Tittle = findViewById(R.id.Payslip_Title);
        DisplayTotalEarnings = findViewById(R.id.DisplayTotalEarnings);
        DisplayTotalDeduction = findViewById(R.id.DisplayTotalDeductions);
        Emp_Tax = findViewById(R.id.EmployeeTax);
        Emp_SSS = findViewById(R.id.EmployeeSSS);
        Emp_PHealth = findViewById(R.id.EmployeePhilHealth);
        Emp_PagIbig = findViewById(R.id.EmployeePagIbig);
        Emp_CashAdvance = findViewById(R.id.EmployeeCashAdvance);
        Emp_MealAllowance = findViewById(R.id.EmployeeMealAllowance);
        Emp_Shop = findViewById(R.id.EmployeeShop);
        DisplayNetPay = findViewById(R.id.DisplayNetPay);
        CancelPayroll = findViewById(R.id.cancel_btn);
        Savebtn = findViewById(R.id.saveComputationBtn);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.cancel_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.white_bg));
        dialog.setCancelable(false);

        btnDialogNo = dialog.findViewById(R.id.btnDialogNo);
        btnDialogYes = dialog.findViewById(R.id.btnDialogYes);


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        Payroll_Tittle.setText("Payroll Title - " + currentDate);

        Intent intent = getIntent();
        if (intent != null) {
            String fullName = intent.getStringExtra("fullName");
            String department = intent.getStringExtra("department");
            String basicPay = intent.getStringExtra("basicPay");
            String email = intent.getStringExtra("email");
            String status = intent.getStringExtra("status");
            Emp_Status.setText(status);
            Emp_Name.setText(fullName);
            Emp_Designation.setText(department);
            Emp_Rate.setText(basicPay);
            Emp_Email.setText(email);
        }

        Savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                savePayrollData();
            }
        });


        btnDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnDialogYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CancelPayroll = new Intent(PayrollComputation.this, EmployeeList.class);
                startActivity(CancelPayroll);
                dialog.dismiss();
            }
        });

        CancelPayroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });


        Emp_Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String fullName = s.toString().trim();
                loadUserData(fullName);
            }
        });

        Emp_Rate.addTextChangedListener(textWatcher);
        Emp_Total_Days.addTextChangedListener(textWatcher);
        Emp_TotalWeeks.addTextChangedListener(textWatcher);
        Emp_AdditionalPayment.addTextChangedListener(textWatcher);
        Emp_SpecialAllowance.addTextChangedListener(textWatcher);

        Emp_Tax.addTextChangedListener(deductionWatcher);
        Emp_SSS.addTextChangedListener(deductionWatcher);
        Emp_PHealth.addTextChangedListener(deductionWatcher);
        Emp_PagIbig.addTextChangedListener(deductionWatcher);
        Emp_CashAdvance.addTextChangedListener(deductionWatcher);
        Emp_MealAllowance.addTextChangedListener(deductionWatcher);
        Emp_Shop.addTextChangedListener(deductionWatcher);

        DisplayTotalEarnings.addTextChangedListener(netPayWatcher);
        DisplayTotalDeduction.addTextChangedListener(netPayWatcher);
    }


    private void loadUserData(String fullName) {
        CollectionReference employeesRef = db.collection("employees");
        Query query = employeesRef.whereEqualTo("fullName", fullName).limit(1);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        // Process the document data here
                        String basicPay = document.getString("basicPay");
                        Emp_Rate.setText(basicPay);
                        String designation = document.getString("department");
                        Emp_Designation.setText(designation);
                        String email = document.getString("email");
                        Emp_Email.setText(email);
                        String status = document.getString("status");
                        Emp_Name.setText(fullName);
                        Emp_Status.setText(status);
                    }
                } else {
                    Log.e("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    TextWatcher netPayWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            calculateTotalNetPay();
        }
    };
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            calculateTotalEarnings();
        }
    };

    private final TextWatcher deductionWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            calculateTotalDeduction();
        }
    };

    private void calculateTotalEarnings() {
        double EmpRate = parseDouble(Emp_Rate.getText().toString());
        double TotalDaysOfWork = parseDouble(Emp_Total_Days.getText().toString());
        double EmpBasicPay = EmpRate * TotalDaysOfWork;
        double OvertimeRate = EmpRate / 8 * 1.25;
        double TotalOverTimePay = parseDouble(Emp_TotalWeeks.getText().toString()) * OvertimeRate;
        double AdditionalPayment = parseDouble(Emp_AdditionalPayment.getText().toString());
        double SpecialAllowance = parseDouble(Emp_SpecialAllowance.getText().toString());
        double TotalEarnings = EmpBasicPay + TotalOverTimePay + SpecialAllowance + AdditionalPayment;

        Emp_OvertimeRate.setText(String.valueOf(OvertimeRate));
        Emp_OverTimePay.setText(String.valueOf(TotalOverTimePay));
        Emp_BasicPay.setText(String.valueOf(EmpBasicPay));
        DisplayTotalEarnings.setText(String.format(Locale.getDefault(), "₱%.2f", TotalEarnings));
    }

    private void calculateTotalDeduction() {
        double Emptax = parseDouble(Emp_Tax.getText().toString());
        double EmpSSS = parseDouble(Emp_SSS.getText().toString());
        double EMpPhealth = parseDouble(Emp_PHealth.getText().toString());
        double EMpPagIbig = parseDouble(Emp_PagIbig.getText().toString());
        double EMpCashAdvance = parseDouble(Emp_CashAdvance.getText().toString());
        double EmpMealAllowance = parseDouble(Emp_MealAllowance.getText().toString());
        double EmpShop = parseDouble(Emp_Shop.getText().toString());
        double TotalDeduction = Emptax + EmpSSS + EMpPhealth + EMpPagIbig + EMpCashAdvance + EmpMealAllowance + EmpShop;

        DisplayTotalDeduction.setText(String.format(Locale.getDefault(), "₱%.2f", TotalDeduction));
        calculateTotalNetPay();
    }

    private void calculateTotalNetPay() {
        double TotalEarnings = parseDouble(DisplayTotalEarnings.getText().toString().replace("₱", ""));
        double TotalDeduction = parseDouble(DisplayTotalDeduction.getText().toString().replace("₱", ""));
        double NetPay = TotalEarnings - TotalDeduction;

        DisplayNetPay.setText(String.format(Locale.getDefault(), "₱%.2f", NetPay));
    }

    private void savePayrollData() {
        String FullName = Emp_Name.getText().toString().trim();
        String Department = Emp_Designation.getText().toString();
        String PayrollTitle = Payroll_Tittle.getText().toString();
        String Email = Emp_Email.getText().toString();
        String Status = Emp_Status.getText().toString();
        double TotalEarnings = parseDouble(DisplayTotalEarnings.getText().toString().replace("₱", ""));
        double TotalDeduction = parseDouble(DisplayTotalDeduction.getText().toString().replace("₱", ""));
        double NetPay = parseDouble(DisplayNetPay.getText().toString().replace("₱", ""));

        Intent payrollData = new Intent(PayrollComputation.this, PayslipDisplay.class);
        payrollData.putExtra("FullName", FullName);
        payrollData.putExtra("Department", Department);
        payrollData.putExtra("Email", Email);
        payrollData.putExtra("Status", Status);
        payrollData.putExtra("PayrollTitle", PayrollTitle);
        payrollData.putExtra("TotalEarnings", String.format(Locale.getDefault(), "₱%.2f", TotalEarnings));
        payrollData.putExtra("TotalDeduction", String.format(Locale.getDefault(), "₱%.2f", TotalDeduction));
        payrollData.putExtra("NetPay", String.format(Locale.getDefault(), "₱%.2f", NetPay));
        startActivity(payrollData);
        finish();
    }
    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
