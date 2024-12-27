package com.example.mobilepayroll;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NewAdapter extends FirestoreRecyclerAdapter<PayModel, NewAdapter.holder> {

    private OnDataChangedListener onDataChangedListener;
    public NewAdapter(@NonNull FirestoreRecyclerOptions<PayModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NewAdapter.holder holder, int i, @NonNull PayModel payModel) {

            holder.fullName.setText(payModel.FullName);
            holder.payrollTitle.setText(payModel.PayrollTitle);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ViewPayslip.class);
                    intent.putExtra("FullName", payModel.FullName);
                    intent.putExtra("PayrollTitle", payModel.PayrollTitle);
                    intent.putExtra("Email", payModel.Email);
                    intent.putExtra("Department", payModel.Department);
                    intent.putExtra("Status", payModel.Status);
                    intent.putExtra("Earnings", payModel.TotalEarnings);
                    intent.putExtra("Deductions", payModel.TotalDeductions);
                    intent.putExtra("NetPay", payModel.NetPay);
                    v.getContext().startActivity(intent);
                }
            });

    }


    @NonNull
    @Override
    public NewAdapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_data2, parent, false);
        return new holder(view);
    }
    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (onDataChangedListener != null) {
            onDataChangedListener.onDataChanged(getItemCount());
        }
    }

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        this.onDataChangedListener = listener;
    }

    public interface OnDataChangedListener {
        void onDataChanged(int itemCount);
    }

    static class holder extends RecyclerView.ViewHolder {
        TextView fullName, payrollTitle;
        CardView cardView;

        public holder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.view_name2_1);
            payrollTitle = itemView.findViewById(R.id.payslipTitle);
            cardView = itemView.findViewById(R.id.Card_View);
        }
    }


}
