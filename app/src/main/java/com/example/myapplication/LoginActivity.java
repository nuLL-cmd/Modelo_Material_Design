package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText edt_login;
    private EditText edt_password;
    private FirebaseAuth firebaseAuth;
    private boolean status = true;
    private String email;
    private String senha;
    private Button btn_login;
    private boolean bool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edt_login = findViewById(R.id.edt_login);
        edt_password = findViewById(R.id.edt_password);
        firebaseAuth = FirebaseAuth.getInstance();
        btn_login = findViewById(R.id.btn_logar);


    }

    public void userLogin(View view) {
        loginMethod();
    }

    public void loginMethod() {
        validate();
        if (status) {
            firebaseAuth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                nextActivity();
                            }
                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(LoginActivity.this);
                    alerta.setTitle("Humm, algo deu errado!");
                    alerta.setMessage("Verifique seu email e/ou senha. \n Da uma olhadinha também na sua internet!");
                    alerta.setIcon(R.drawable.about_gray);
                    alerta.setCancelable(false);
                    edt_password.requestFocus();
                    alerta.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            edt_login.requestFocus();
                        }
                    });
                    alerta.show();
                }
            });
        }
    }

    public void nextActivity() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null && !HomeActivity.active) {
            String uid = firebaseUser.getUid();
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("uid", uid);
            Boolean state = true;
            intent.putExtra("state", state);
            startActivity(intent);
            finish();
        }

    }

    public void nextActivityCad(View view) {
        if(!RegisterActivity.active){
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    public void validate() {
        email = edt_login.getText().toString();
        senha = edt_password.getText().toString();
        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Campos não podem ser vazios!!", Toast.LENGTH_SHORT).show();
            btn_login.setEnabled(true);
            if (email.isEmpty())
                edt_login.requestFocus();
            else
                edt_password.requestFocus();
            status = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        nextActivity();
    }
}
