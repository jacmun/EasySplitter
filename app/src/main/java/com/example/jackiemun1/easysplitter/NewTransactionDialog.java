package com.example.jackiemun1.easysplitter;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.jackiemun1.easysplitter.data.Transaction;
import com.google.firebase.auth.FirebaseAuth;

public class NewTransactionDialog extends DialogFragment {

    public interface TransactionHandler {
        public void onNewTransactionCreated(Transaction newTransaction);
    }

    private TransactionHandler transactionHandler;
    private EditText etTransactionDes;
    private EditText etTransactionPrice;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TransactionHandler) {
            transactionHandler = (TransactionHandler) context;
        } else {
            throw new RuntimeException("The Activity does not implement the TransactionHandler interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New User");

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_transaction, null);

        etTransactionDes = rootView.findViewById(R.id.etTransactionDes);
        etTransactionPrice = rootView.findViewById(R.id.etTransactionPrice);

        builder.setView(rootView);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final android.support.v7.app.AlertDialog d = (android.support.v7.app.AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(etTransactionDes.getText())) {
                        etTransactionDes.setError("This field cannot be empty");
                    }
                    if (TextUtils.isEmpty(etTransactionPrice.getText())) {
                        etTransactionPrice.setError("This field cannot be empty");
                    } else {
                        Transaction newTransaction = new Transaction(
                                Double.parseDouble(etTransactionPrice.getText().toString()),
                                etTransactionDes.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                FirebaseAuth.getInstance().getCurrentUser().getUid());
                        transactionHandler.onNewTransactionCreated(newTransaction);
                        d.dismiss();
                    }
                }
            });
        }
    }
}