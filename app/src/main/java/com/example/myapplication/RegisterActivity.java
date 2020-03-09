package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private Toolbar toolbar_register;
    private EditText edt_nome_register;
    private EditText edt_email_register;
    private String filename;
    private EditText edt_password_register;
    private CadProvider cadProvider;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private String nome;
    private String email;
    private String password;
    private CircleImageView img_prfil;
    private StorageReference storageReference;
    private Uri uri;
    private boolean status;
    static boolean active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edt_email_register = findViewById(R.id.edt_email_register);
        edt_nome_register = findViewById(R.id.edt_nome_register);
        edt_password_register = findViewById(R.id.edt_password_register);
        img_prfil = findViewById(R.id.img_perfil);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

    }

    private void validate() {

        nome = edt_nome_register.getText().toString();
        email = edt_email_register.getText().toString();
        password = edt_password_register.getText().toString();

        if (nome.isEmpty() || nome == null || password.isEmpty() || password == null || email == null || email.isEmpty()) {
            AlertDialog.Builder alerta = new AlertDialog.Builder(RegisterActivity.this);
            alerta.setTitle("Falta alguma coisa :D");
            alerta.setCancelable(false);
            alerta.setMessage("Verifique se todos os campos estão preenchidos!");
            alerta.setIcon(R.drawable.about_gray);
            alerta.setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (nome.isEmpty())
                        edt_nome_register.requestFocus();
                    else if (email.isEmpty())
                        edt_email_register.requestFocus();
                    else
                        edt_password_register.requestFocus();
                }
            });
            alerta.show();

            status = false;
        } else {
            cadProvider = new CadProvider(nome, email, password);
            status = true;
        }
    }

    /*Primeiro cria um usuario pelo Auth e no segundo
    metodo onComplete chama o metodo para upar a foto no Storage*/
    public void cadUserAndLogin(View view) {
        validate();
        ;
        if (status) {
            firebaseAuth.createUserWithEmailAndPassword(cadProvider.getEmail(), cadProvider.getPassword())
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                savUserPickInFireStorage();
                            }
                        }
                    }).addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Log.i("logx", "Result: " + authResult.getUser());
                }
            }).addOnFailureListener(RegisterActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("logx", "Result: " + e.getMessage());
                }
            });
            firebaseFirestore = FirebaseFirestore.getInstance();

        }
    }

    public void savUserPickInFireStorage() {
        /*Segundo salva a foto no Storagee e no metodo onSucces invoce o metodo para obter a url da imagem
         * em seguida chama o metodo para criar a coleção no CloudFireStone*/
        filename = UUID.randomUUID().toString();
        storageReference = firebaseStorage.getReference("/images/" + filename);
        storageReference.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        cadProvider.setUrl(uri.toString());
                        saveUserInFireStone(cadProvider);
                    }
                });
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
    /*Terceiro salva o usuario (coleção no cloud fireStone e no metodo onSuccess exibe
    a menssagem de ok e os medtodos para ir para a activity home etc)*/
    public void saveUserInFireStone(final CadProvider cadProvider) {
        firebaseFirestore.collection("Users").add(cadProvider)
                .addOnSuccessListener(this, new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        AlertDialog.Builder alerta = new AlertDialog.Builder(RegisterActivity.this);
                        alerta.setTitle("Bem vindo!");
                        alerta.setCancelable(false);
                        alerta.setIcon(R.drawable.sucess);
                        alerta.setMessage("Cadastro efetuado com sucesso!!");
                        alerta.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int which) {
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.putExtra("cadProvider", cadProvider);
                                startActivity(intent);
                                finish();
                            }
                        }).show();
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void getImageUser(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                img_prfil.setImageDrawable(new BitmapDrawable(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }
}

