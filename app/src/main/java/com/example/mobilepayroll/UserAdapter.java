package com.example.mobilepayroll;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class UserAdapter extends FirestoreRecyclerAdapter<UserModel, UserAdapter.holder> {
    public UserAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserAdapter.holder holder, int i, @NonNull UserModel userModel) {
        holder.fullName.setText(userModel.fullName);
        holder.department.setText(userModel.department);
        Picasso.get().load(userModel.imageUrl).into(holder.employee_pic);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EmployeeDisplay.class);
                intent.putExtra("fullName", userModel.fullName);
                intent.putExtra("department", userModel.department);
                intent.putExtra("imageUrl", userModel.imageUrl);
                intent.putExtra("email", userModel.email);
                intent.putExtra("status", userModel.status);
                intent.putExtra("phoneNumber", userModel.phoneNumber);
                intent.putExtra("basicPay", userModel.basicPay);
                v.getContext().startActivity(intent);
                Toast.makeText(v.getContext(), "You are viewing " + userModel.fullName + "'s profile", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @NonNull
    @Override
    public UserAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_data, parent, false);
        return new holder(view);
    }

    static class holder extends RecyclerView.ViewHolder {
        TextView fullName, department;
        ImageView employee_pic;
        CardView cardView;
        public holder(@NonNull View itemView) {
            super(itemView);
            View view = itemView;

            fullName = view.findViewById(R.id.view_name2_1);
            department = view.findViewById(R.id.textJobRole);
            employee_pic = view.findViewById(R.id.emp_pic);
            cardView = view.findViewById(R.id.Card_View);
        }
    }

}
