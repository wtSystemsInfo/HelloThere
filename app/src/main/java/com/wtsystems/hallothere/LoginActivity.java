package com.wtsystems.hallothere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.wtsystems.hallothere.FireBase.FireBaseConfig;
import com.wtsystems.hallothere.Model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputSenha;
    private TextView txtCad;

    private Button btnLogar;
    private Switch swSenha;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setStatusBarColor(Color.parseColor("#AFAFAF"));
        inputEmail = findViewById(R.id.inputEmail);
        inputSenha = findViewById(R.id.inputPassword);
        txtCad = findViewById(R.id.txtCadastro);
        btnLogar = findViewById(R.id.btnLogar);
        swSenha = findViewById(R.id.switchSenha);

        txtCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logarUser();
            }
        });


        swSenha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    inputSenha.setInputType(InputType.TYPE_CLASS_TEXT );
                }else{
                    inputSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
                }
            }
        });

    }

    private void logarUser(){
        if(validarCampos()){
            String email = inputEmail.getText().toString();
            String senha = inputSenha.getText().toString();
            Usuario user = new Usuario();
            user.setEmail(email);
            user.setSenha(senha);
            FirebaseApp.initializeApp(this);
            autenticacao = FireBaseConfig.getFireBaseAuth();
            autenticacao.signInWithEmailAndPassword(user.getEmail(), user.getSenha()).addOnCompleteListener(this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            }else{
                                String excecao = "";
                                try{
                                    throw task.getException();
                                }catch (FirebaseAuthInvalidUserException e){
                                    excecao = "Usuário não cadastrado!";

                                }catch (FirebaseAuthInvalidCredentialsException e){
                                    excecao = "E-mail digitado não cadastrado!";

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                if(excecao.isEmpty()){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setTitle("LOGIN")
                                            .setMessage("Houve um erro ao fazer o Log In")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //Não acontece nada
                                                }
                                            });

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setTitle("LOGIN")
                                            .setMessage(excecao)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //Não acontece nada
                                                }
                                            });

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }


                            }
                        }
                    });

        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("LOGIN")
                    .setMessage("Existem campos inválidos!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Não acontece nada
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }

    private boolean validarCampos(){
        if(inputEmail==null || inputEmail.getText().toString().isEmpty()){
            return false;
        }
        if(inputSenha==null || inputSenha.getText().toString().isEmpty()){
            return false;
        }
        return true;
    }


    @Override
    protected void onResume(){
        super.onResume();
        if(autenticacao != null){
            FirebaseUser currentUser = autenticacao.getCurrentUser();
            if(currentUser != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        }
    }
}