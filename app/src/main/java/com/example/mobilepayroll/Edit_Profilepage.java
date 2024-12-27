

package com.example.mobilepayroll;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyCallback;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Edit_Profilepage extends AppCompatActivity {
    private FirebaseAuth Auth;
    private FirebaseFirestore db;
    private String userID;
    private ImageView admin_profile_image;
    private DocumentSnapshot documentSnapshot;
    private StorageReference storageReference;
    private Dialog dialog;
    private Button btnDialogNo, btnDialogYes;
    private Button deleteUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profilepage);
        TextView Profile_Name = findViewById(R.id.edit_profile_Name);
        TextView Change_AdminEmail = findViewById(R.id.edit_profile_Email);
        EditText Change_AdminPassword = findViewById(R.id.edit_profile_Password);
        EditText Change_ProfilePositon = findViewById(R.id.edit_profile_Job);
        Auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = Auth.getCurrentUser().getUid();
        admin_profile_image = findViewById(R.id.profile_image);
        ImageButton add_profile_image = findViewById(R.id.floatingCameraIcon);
        Button SaveEditButton = findViewById(R.id.save_btn);
        TextView BackButton = findViewById(R.id.edit_profile_back_btn);

        deleteUserButton = findViewById(R.id.payslip_delete);
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileref = storageReference.child("users/" + Auth.getCurrentUser().getUid() + "/profile.jpg");
        profileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(admin_profile_image);
            }
        });
        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException error) {
                if (snapshot != null) {
                    documentSnapshot = snapshot;
                    Profile_Name.setText(snapshot.getString("fullName"));
                    Change_AdminEmail.setText(snapshot.getString("email"));
                    Change_ProfilePositon.setText(snapshot.getString("position"));
                }
            }
        });



        SaveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  String GetNewEmail = Change_AdminEmail.getText().toString();
                String GetNewPassword = Change_AdminPassword.getText().toString();
                String GetNewPosition = Change_ProfilePositon.getText().toString();

                if (!TextUtils.isEmpty(GetNewPassword) && isValidPassword(GetNewPassword)) {
                    FirebaseUser user = Auth.getCurrentUser();
                    if (user != null) {
                        String email = user.getEmail();
                        user.updatePassword(GetNewPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Auth.signOut();
                                            Toast.makeText(Edit_Profilepage.this,
                                                    "Password changed successfully! Please log in again.",
                                                    Toast.LENGTH_SHORT).show();

                                            Intent GoToLoginPage = new Intent(Edit_Profilepage.this, MainActivity.class);
                                            GoToLoginPage.putExtra("email", email); // Pass the email to the login activity
                                            startActivity(GoToLoginPage);
                                            finish(); 
                                        } else {
                                            Toast.makeText(Edit_Profilepage.this,
                                                    "Failed to change password: " + task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }


                if (!GetNewPosition.equals(documentSnapshot.getString("position"))) {
                    documentReference.update("position", GetNewPosition).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent GotoProfilePageFuction = new Intent(Edit_Profilepage.this, Profilepage_function.class);
                                startActivity(GotoProfilePageFuction);
                                finish();
                            } else {
                                Toast.makeText(Edit_Profilepage.this, "Position Failed to Update", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog();
            }
        });


        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDeleteDialog();

            }
        });

        add_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent OpenGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(OpenGallery, 1000);

            }
        });

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
                DeleteUser();
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

    private void DeleteUser() {
        FirebaseUser user = Auth.getCurrentUser();
        String UserID = Auth.getCurrentUser().getUid();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocRef = db.collection("users").document(UserID);
            userDocRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Auth.signOut();
                                    Intent GoToLoginPage = new Intent(Edit_Profilepage.this, MainActivity.class);
                                    startActivity(GoToLoginPage);
                                    Toast.makeText(Edit_Profilepage.this, "Account Deleted", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(Edit_Profilepage.this, "Failed to Delete Account", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(Edit_Profilepage.this, "Failed to Delete Data", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
                Intent BackToProfilePageFunction = new Intent(Edit_Profilepage.this, Profilepage_function.class);
                startActivity(BackToProfilePageFunction);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                uploadImageToFirebaseStorage(imageUri);
            }

        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference fileref = storageReference.child("users/" + Auth.getCurrentUser().getUid() + "/profile.jpg");
        fileref.putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            fileref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> uriTask) {
                                    if (uriTask.isSuccessful()) {
                                        Uri uri = uriTask.getResult();
                                        Picasso.get().load(uri).into(admin_profile_image);
                                        saveImageUrlToFirestore(uri.toString());
                                    } else {
                                        Toast.makeText(Edit_Profilepage.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(Edit_Profilepage.this, "Upload Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        DocumentReference userRef = db.collection("users").document(userID);
        userRef.update("profileImageUrl", imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Edit_Profilepage.this, "Image URL saved", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Edit_Profilepage.this, "Failed to save Image URL", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6 && password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$");
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
