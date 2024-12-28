package com.example.mobilepayroll;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Auth = FirebaseAuth.getInstance();
        EditText usernameEditText = findViewById(R.id.loginEmail);
        EditText passwordEditText = findViewById(R.id.loginPassword);
        Button loginButton = findViewById(R.id.login_btn);
        TextView SignUpPage = findViewById(R.id.signup_link);
        TextView Forgot_Password = findViewById(R.id.ForgotPass);

        loginButton.setOnClickListener(v -> {
            String getEmail = usernameEditText.getText().toString();
            String getPassword = passwordEditText.getText().toString();

            if (TextUtils.isEmpty(getEmail) || TextUtils.isEmpty(getPassword)) {
                Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Logging in...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Auth.signInWithEmailAndPassword(getEmail, getPassword)
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = Auth.getCurrentUser();

                            if (currentUser != null && currentUser.isEmailVerified()) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("users")
                                        .document(currentUser.getUid())
                                        .get()
                                        .addOnCompleteListener(roleTask -> {
                                            if (roleTask.isSuccessful() && roleTask.getResult() != null) {
                                                String role = roleTask.getResult().getString("role");
                                                Log.d("Role Fetch", "Role: " + role);

                                                if (role == null) {
                                                    Toast.makeText(MainActivity.this, "User role not set. Please contact support.", Toast.LENGTH_SHORT).show();
                                                } else if ("Admin".equals(role)) {
                                                    Intent intent = new Intent(MainActivity.this, AdminDashboard.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else if ("User".equals(role)) {
                                                    Intent intent = new Intent(MainActivity.this, EmployeeList.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(MainActivity.this, "Unknown role", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(MainActivity.this, "Failed to fetch role", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(MainActivity.this, "Verify email first", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(MainActivity.this, "Email address not found", Toast.LENGTH_SHORT).show();
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(MainActivity.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        SignUpPage.setOnClickListener(v -> {
            Intent GotoSignUpPage = new Intent(MainActivity.this, Signup.class);
            startActivity(GotoSignUpPage);
        });

        Forgot_Password.setOnClickListener(v -> {
            EditText ResetPassword = new EditText(v.getContext());
            AlertDialog.Builder password_reset = new AlertDialog.Builder(v.getContext());
            password_reset.setTitle("Reset Password");
            password_reset.setMessage("Enter email to reset password");
            password_reset.setView(ResetPassword);

            password_reset.setPositiveButton("Yes", (dialog, which) -> {
                String NewPassword = ResetPassword.getText().toString();

                if (TextUtils.isEmpty(NewPassword)) {
                    Toast.makeText(MainActivity.this, "Please enter an email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                Auth.sendPasswordResetEmail(NewPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Reset link has been sent", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to send reset link", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            password_reset.setNegativeButton("No", (dialog, which) -> {
                // Dismiss the dialog
            });

            password_reset.create().show();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            if (!currentUser.isEmailVerified()) {
                FirebaseAuth.getInstance().signOut();
            } else {
                // Auto-login to the appropriate activity based on user's role
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .document(currentUser.getUid())
                        .get()
                        .addOnCompleteListener(roleTask -> {
                            if (roleTask.isSuccessful() && roleTask.getResult() != null) {
                                String role = roleTask.getResult().getString("role");

                                if ("Admin".equals(role)) {
                                    Intent intent = new Intent(MainActivity.this, AdminDashboard.class);
                                    startActivity(intent);
                                    finish();
                                } else if ("User".equals(role)) {
                                    Intent intent = new Intent(MainActivity.this, EmployeeList.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, "Unknown role", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to fetch role", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}
