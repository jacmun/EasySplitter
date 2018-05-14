package com.example.jackiemun1.easysplitter;

import android.app.Dialog;
import android.app.DialogFragment;
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
        //public void onNewUserCreated(User user);

        //public void onUserUpdated(User user);
    }

    private UserHandler userHandler;
    private EditText etRegEmail;
    private EditText etRegPassword;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UserHandler) {
            userHandler = (UserHandler) context;
        } else {
            throw new RuntimeException("The Activity does not implement the UserHandler interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New User");

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialogue_register_user, null);

        etRegEmail = rootView.findViewById(R.id.etRegEmail);
        etRegPassword = rootView.findViewById(R.id.etRegPassword);

        if (getArguments() != null &&
                getArguments().containsKey(LoginActivity.KEY_EDIT)) {
            //User userToEdit = (User) getArguments().getSerializable(LoginActivity.KEY_EDIT);
            //etRegEmail.setText(userToEdit.getUserName());

        }

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
                    if (!TextUtils.isEmpty(etRegEmail.getText())) {
                        if (getArguments() != null &&
                                getArguments().containsKey(LoginActivity.KEY_EDIT)) {
                            //User userToEdit = (User) getArguments().getSerializable(LoginActivity.KEY_EDIT);
                            //userToEdit.setUserName(etRegEmail.getText().toString());
                            //userHandler.onUserUpdated(userToEdit);
                        } else {
                            //User user = new User(
                                    //etRegEmail.getText().toString()
                            //);

                            //userHandler.onNewUserCreated(user);
                        }

                        d.dismiss();

                    } else {
                        etRegEmail.setError("This field cannot be empty");
                    }

                }
            });
        }
    }

}


