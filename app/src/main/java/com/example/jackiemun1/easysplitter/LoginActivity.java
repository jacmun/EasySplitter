package com.example.jackiemun1.easysplitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jackiemun1.easysplitter.data.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements CreateUserDialog.UserHandler,
        CreateGroupDialog.GroupHandler{

    public static final String KEY_EDIT = "KEY_EDIT";

    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etGroupId)
    EditText etGroupId;

    FirebaseAuth firebaseAuth = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @OnClick(R.id.btnRegisterUser)
    void registerClicked(){
        showRegisterUserDialog();

    }

    @OnClick(R.id.btnRegisterGroup)
    void registerGroupClicked() {
        showRegisterGroupDialog();
    }

    @OnClick(R.id.btnLogin)
    void loginClicked(){
        if(!isFormValid()){
            return;
        }
        showProgressDialog();

        firebaseAuth.signInWithEmailAndPassword(
                etEmail.getText().toString(), etPassword.getText().toString()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final String groupName = etGroupId.getText().toString();
                    loginToGroup(groupName);

                }
                else{
                    hideProgressDialog();
                    Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginToGroup(final String groupName) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(getString(R.string.groups));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                makeSureGroupExists(dataSnapshot, groupName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void makeSureGroupExists(DataSnapshot dataSnapshot, String groupName) {
        if(dataSnapshot.child(groupName).exists()){
            hideProgressDialog();
            Intent intentMain = new Intent();
            intentMain.setClass(LoginActivity.this, MainActivity.class);
            intentMain.putExtra(getString(R.string.groupName), groupName);
            startActivity(intentMain);
        }
        else{
            hideProgressDialog();
            Toast.makeText(LoginActivity.this, R.string.noGroupMsg, Toast.LENGTH_SHORT).show();
        }
    }

    public void initNewUser(String email, String password, final String username) {
        showProgressDialog();

        firebaseAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgressDialog();
                if (task.isSuccessful()) {
                    FirebaseUser fbUser = task.getResult().getUser();

                    fbUser.updateProfile(new UserProfileChangeRequest.Builder().
                            setDisplayName(username).build());

                    Toast.makeText(LoginActivity.this, R.string.userCreated, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this,
                            task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void onNewGroupCreated(final String groupName,final int groupNumber){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(getString(R.string.groups));

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(groupName).exists()) {
                    Toast.makeText(LoginActivity.this, R.string.takenGroupId, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(getString(R.string.createGroup), getString(R.string.here));
                    FirebaseDatabase.getInstance().getReference().child(getString(R.string.groups)).
                            child(groupName).child(getString(R.string.groupMembers)).setValue(groupNumber);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isFormValid() {
        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            etEmail.setError(getString(R.string.required));
            return false;
        }

        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError(getString(R.string.required));
            return false;
        }

        return true;
    }


    private void showRegisterUserDialog() {
        new CreateUserDialog().show(getFragmentManager(), getString(R.string.registerUser));
    }

    private void showRegisterGroupDialog() {
        new CreateGroupDialog().show(getFragmentManager(), getString(R.string.registerGroup));
    }


    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loadingMsg));
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private String usernameFromEmail(String email) {
        if (email.contains(getString(R.string.atSymbol))) {
            return email.split(getString(R.string.atSymbol))[0];
        } else {
            return email;
        }
    }
}
