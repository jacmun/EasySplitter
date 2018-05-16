package com.example.jackiemun1.easysplitter.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.jackiemun1.easysplitter.R;
import com.example.jackiemun1.easysplitter.data.Transaction;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private Context context;
    private List<Transaction> transactionList;
    private List<String> transactionKeys;
    private String uId;
    private int lastPosition = -1;
    private String group;

    public TransactionsAdapter(Context context, String uId, String group) {
        this.context = context;
        this.uId = uId;
        this.transactionList = new ArrayList<Transaction>();
        this.transactionKeys = new ArrayList<String>();
        this.group = group;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.transaction_row, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tvBuyer.setText(
                transactionList.get(holder.getAdapterPosition()).getBuyer());
        holder.tvDescription.setText("Description: " +
                transactionList.get(holder.getAdapterPosition()).getDescription());
        holder.tvPrice.setText("Price: $" +
                transactionList.get(holder.getAdapterPosition()).getPrice());

        if (transactionList.get(holder.getAdapterPosition()).getUid().equals(uId)) {
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeTransaction(holder.getAdapterPosition());
                }
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }

        setAnimation(holder.itemView, position);
    }

    public void removeTransaction(int index) {
        FirebaseDatabase.getInstance().getReference("groups").child(group).child("transactions").child(
                transactionKeys.get(index)).removeValue();
        transactionList.remove(index);
        transactionKeys.remove(index);
        notifyItemRemoved(index);
    }

    public void removeTransactionByKey(String key) {
        int index = transactionKeys.indexOf(key);
        if (index != -1) {
            transactionList.remove(index);
            transactionKeys.remove(index);
            notifyItemRemoved(index);
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context,
                    android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void addTransaction(Transaction newTransaction, String key) {
        transactionList.add(newTransaction);
        transactionKeys.add(key);
        notifyDataSetChanged();
    }

    public double totalExpenses(){
        double total = 0;
        for (int i = 0; i < transactionList.size(); i++) {
            total += transactionList.get(i).getPrice();
        }
        return total;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvBuyer;
        private TextView tvDescription;
        private TextView tvPrice;
        private Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            tvBuyer = itemView.findViewById(R.id.tvBuyer);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnDelete = itemView.findViewById(R.id.btnDelete);


        }
    }


}
