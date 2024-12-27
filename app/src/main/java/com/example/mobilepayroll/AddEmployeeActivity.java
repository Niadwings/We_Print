package com.example.mobilepayroll;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEmployeeActivity extends AppCompatActivity {

   private String[] items = {"Regular", "Probationary", "Part-time"};
   private AutoCompleteTextView autoCompleteTxt;
   private ArrayAdapter<String> adapterItems;

    private Dialog dialog;
    private Button btnDialogNo, btnDialogYes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
        EditText empFullName = findViewById(R.id.add_fname);
        EditText empEmail = findViewById(R.id.add_email);
        EditText empPhone = findViewById(R.id.add_phone);
        EditText empDepartment = findViewById(R.id.add_designation);
        EditText empBasicPay = findViewById(R.id.add_basicpay);
        Button button = findViewById(R.id.next_btn);
        TextView BackButton = findViewById(R.id.titleAddEmployee);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getFullName = empFullName.getText().toString();
                String getEmail = empEmail.getText().toString();
                String getPhoneNumber = empPhone.getText().toString();
                String getDepartment = empDepartment.getText().toString();
                String getBasicPay = empBasicPay.getText().toString();
                String getEmpStatus = autoCompleteTxt.getText().toString();

                if (TextUtils.isEmpty(getFullName) || TextUtils.isEmpty(getEmail) || TextUtils.isEmpty(getDepartment) || TextUtils.isEmpty(getBasicPay) || TextUtils.isEmpty(getPhoneNumber)) {
                    Toast.makeText(AddEmployeeActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidPhoneNumber(getPhoneNumber)) {
                    Toast.makeText(AddEmployeeActivity.this, "Invalid phone number format. Please enter only numbers", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!TextUtils.isDigitsOnly(getBasicPay)) {
                    Toast.makeText(AddEmployeeActivity.this, "Invalid basic pay format. Please enter only numbers", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(AddEmployeeActivity.this, AddEmployeePicture.class);
                intent.putExtra("FullName", getFullName);
                intent.putExtra("Email", getEmail);
                intent.putExtra("PhoneNumber", getPhoneNumber);
                intent.putExtra("Department", getDepartment);
                intent.putExtra("BasicPay", getBasicPay);
                intent.putExtra("Status",getEmpStatus);
                startActivity(intent);
                finish();
            }
        });

        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_status, items);
        autoCompleteTxt.setAdapter(adapterItems);
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog();
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
                Intent BackToEmployeeListPageFunction = new Intent(AddEmployeeActivity.this, EmployeeList.class);
                startActivity(BackToEmployeeListPageFunction);
                finish();
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

    public boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^[0-9()-]+$");
    }
}
