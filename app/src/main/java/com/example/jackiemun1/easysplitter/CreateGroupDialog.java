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

public class CreateGroupDialog extends DialogFragment {

    public interface GroupHandler {
        public void onNewGroupCreated(String groupName, int groupNumber);
    }

    private CreateGroupDialog.GroupHandler groupHandler;
    private EditText etRegGroupId;
    private EditText etRegGroupNumber;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof CreateGroupDialog.GroupHandler) {
            groupHandler = (CreateGroupDialog.GroupHandler) context;
        } else {
            throw new RuntimeException(getString(R.string.runtimeException));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.newGroup);

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialogue_register_group, null);

        etRegGroupId = rootView.findViewById(R.id.etRegGroupId);
        etRegGroupNumber = rootView.findViewById(R.id.etRegGroupNumber);

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
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(etRegGroupId.getText())) {
                        if(TextUtils.isEmpty(etRegGroupNumber.getText())){
                            groupHandler.onNewGroupCreated(etRegGroupId.getText().toString(), 1);
                        }
                        else {
                            int groupNumber = Integer.parseInt(etRegGroupNumber.getText().toString());
                            groupHandler.onNewGroupCreated(etRegGroupId.getText().toString(), groupNumber);
                        }
                        d.dismiss();

                    } else {
                        etRegGroupId.setError(getString(R.string.emptyFieldMsg));
                    }

                }
            });
        }
    }

}
