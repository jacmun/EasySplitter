package com.example.jackiemun1.easysplitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

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

//        if (!isFormValid()){
//            return;
//        }
//
//        showProgressDialog();
//
//
//        firebaseAuth.createUserWithEmailAndPassword(
//                etEmail.getText().toString(),
//                etPassword.getText().toString()
//        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                hideProgressDialog();
//                if(task.isSuccessful()) {
//                    FirebaseUser fbUser = task.getResult().getUser();
//
//                    fbUser.updateProfile(new UserProfileChangeRequest.Builder().
//                            setDisplayName(usernameFromEmail(fbUser.getEmail())).build());
//
//                    Toast.makeText(LoginActivity.this, "User created", Toast.LENGTH_SHORT).show();
//                } else{
//                    Toast.makeText(LoginActivity.this,
//                            task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
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
                    Intent intentMain = new Intent();
                    intentMain.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(intentMain);
                }
                else{
                    Toast.makeText(LoginActivity.this, "Error:" + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isFormValid() {
        if(TextUtils.isEmpty(etEmail.getText())){
            etEmail.setError("Required");
            return false;
        }

        if(TextUtils.isEmpty(etPassword.getText())){
            etPassword.setError("Required");
            return false;
        }
        return true;
    }

    private void showRegisterUserDialog() {
        new CreateUserDialog().show(getFragmentManager(), "RegisterUserDialog");
    }

    private void showRegisterGroupDialog() {
        new CreateGroupDialog().show(getFragmentManager(), "RegisterGroupDialog");
    }


    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
}
