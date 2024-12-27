package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class EmployeeList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textAdmin, textAll, textProduction, textSupport, textLogistics;
    private UserAdapter userAdapter;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);

        // Initialize Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        // UI references
        TextView Display_FullName = findViewById(R.id.current_user);
        ImageButton add_employee = findViewById(R.id.add_employee_btn);
        recyclerView = findViewById(R.id.recyclerViewId);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView("ALL");

        // Search functionality
        SearchView searchView = findViewById(R.id.searchbar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    recyclerView("ALL");
                } else {
                    searchEmployees(newText);
                }
                return false;
            }
        });

        // Department filter buttons
        textAdmin = findViewById(R.id.textAdmin);
        textAll = findViewById(R.id.textAll);
        textProduction = findViewById(R.id.textProduction);
        textSupport = findViewById(R.id.textSupport);
        textLogistics = findViewById(R.id.textLogistics);

        textAdmin.setOnClickListener(v -> recyclerViewByDepartment("Admin"));
        textAll.setOnClickListener(v -> recyclerViewByDepartment("ALL"));
        textProduction.setOnClickListener(v -> recyclerViewByDepartment("Production"));
        textSupport.setOnClickListener(v -> recyclerViewByDepartment("Support"));
        textLogistics.setOnClickListener(v -> recyclerViewByDepartment("Logistics"));


        DocumentReference documentReference = db.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Display_FullName.setText(documentSnapshot.getString("fullName"));
                }
            }
        });

        // Bottom navigation setup
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_employees);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.bottom_profile:
                    startActivity(new Intent(getApplicationContext(), Profilepage_function.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.bottom_employees:
                    return true;
                case R.id.bottom_payroll:
                    startActivity(new Intent(getApplicationContext(), PayrollComputation.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.bottom_payslip:
                    startActivity(new Intent(getApplicationContext(), Payslips.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });


        add_employee.setOnClickListener(v -> {
            Intent GoToAddEmployeeList = new Intent(EmployeeList.this, AddEmployeeActivity.class);
            startActivity(GoToAddEmployeeList);
            finish();
        });
    }

    private void recyclerView(String searchText) {
        Query query;
        if (searchText.equals("ALL")) {
            query = db.collection("employees").whereEqualTo("userId", userId);
        } else {
            query = db.collection("employees")
                    .whereEqualTo("userId", userId)
                    .orderBy("fullName")
                    .startAt(searchText)
                    .endAt(searchText + "\uf8ff");
        }

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();

        userAdapter = new UserAdapter(options);
        recyclerView.setAdapter(userAdapter);
        userAdapter.startListening();
    }

    private void recyclerViewByDepartment(String department) {
        Query query;
        if (department.equals("ALL")) {
            query = db.collection("employees").whereEqualTo("userId", userId);
        } else {
            query = db.collection("employees")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("department", department);
        }

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();

        userAdapter = new UserAdapter(options);
        recyclerView.setAdapter(userAdapter);
        userAdapter.startListening();
    }

    private void searchEmployees(String searchText) {
        recyclerView(searchText);
    }
}
