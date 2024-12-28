package com.example.mobilepayroll;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class AdminDashboard extends AppCompatActivity {

    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        Button gotoProfilePage = findViewById(R.id.profile_page);
        TextView showAdmin = findViewById(R.id.show_admin);

        // Fetch data from Firestore and update the UI
        DocumentReference documentReference = db.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    showAdmin.setText(documentSnapshot.getString("fullName"));
                }
            }
        });

        // Set up button click listener
        gotoProfilePage.setOnClickListener(v -> {
            Intent GoToProfilePage = new Intent(AdminDashboard.this, Profilepage_function.class);
            startActivity(GoToProfilePage);
            finish();
        });
    }
}
