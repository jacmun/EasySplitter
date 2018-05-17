package com.example.jackiemun1.easysplitter;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class CreateUserDialog extends DialogFragment {

    public interface UserHandler {
        public void initNewUser(String email, String password, String username);
    }

    private UserHandler userHandler;
    private EditText etRegEmail;
    private EditText etRegPassword;
    private EditText etUsername;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UserHandler) {
            userHandler = (UserHandler) context;
        } else {
            throw new RuntimeException(getString(R.string.runtimeException2));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.newUser);

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialogue_register_user, null);

        etRegEmail = rootView.findViewById(R.id.etRegEmail);
        etRegPassword = rootView.findViewById(R.id.etRegPassword);
        etUsername = rootView.findViewById(R.id.etUsername);

        builder.setView(rootView);

        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
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
            getPositiveButton(d);
        }
    }

    private void getPositiveButton(final AlertDialog d) {
        Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etRegEmail.getText())) {
                    etRegEmail.setError(getString(R.string.emptyFieldMsg));
                }
                if (TextUtils.isEmpty(etRegPassword.getText())) {
                    etRegPassword.setError(getString(R.string.emptyFieldMsg));
                }
                if (TextUtils.isEmpty(etUsername.getText())) {
                    etUsername.setError(getString(R.string.emptyFieldMsg));
                }
                else {
                    userHandler.initNewUser(etRegEmail.getText().toString(),
                            etRegPassword.getText().toString(), etUsername.getText().toString());
                    d.dismiss();
                }
            }
        });
    }

}


