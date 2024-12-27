package com.example.mobilepayroll;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
                Toast.makeText(MainActivity.this, "Please fill in all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Auth.signInWithEmailAndPassword(getEmail, getPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (Auth.getCurrentUser().isEmailVerified()) {
                                Intent intent = new Intent(MainActivity.this,
                                        EmployeeList.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Verify email first",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(MainActivity.this,
                                        "Email address not found", Toast.LENGTH_SHORT).show();
                            } else if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(MainActivity.this,
                                        "Wrong email or password", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Login failed: " +
                                        task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        });

        SignUpPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GotoSignUpPage = new Intent(MainActivity.this, Signup.class);
                startActivity(GotoSignUpPage);
            }
        });
        Forgot_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ResetPassword = new EditText(v.getContext());
                AlertDialog.Builder password_reset = new AlertDialog.Builder(v.getContext());
                password_reset.setTitle("Reset Password");
                password_reset.setMessage("Enter email to reset password");
                password_reset.setView(ResetPassword);
                password_reset.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String NewPassword = ResetPassword.getText().toString();
                        Auth.sendPasswordResetEmail(NewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Reset link has been sent", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Failed to send reset link", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                password_reset.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                password_reset.create().show();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            if (!currentUser.isEmailVerified()) {
                FirebaseAuth.getInstance().signOut();
            } else {
                Intent GotoEmployeeList = new Intent(MainActivity.this, EmployeeList.class);
                startActivity(GotoEmployeeList);
                finish();
            }
        }
    }
}
