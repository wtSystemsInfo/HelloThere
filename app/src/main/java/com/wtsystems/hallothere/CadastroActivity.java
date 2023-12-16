package com.wtsystems.hallothere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.messaging.FirebaseMessaging;
import com.wtsystems.hallothere.FireBase.FireBaseConfig;
import com.wtsystems.hallothere.Helper.Base64Custom;
import com.wtsystems.hallothere.Helper.UserFireBase;
import com.wtsystems.hallothere.Model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private Button btnVoltar, btnCadastrar;
    private EditText inputNome, inputEmail, inputSenha;

    private FirebaseAuth autenticacao;

    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        getWindow().setStatusBarColor(Color.parseColor("#AFAFAF"));
        btnVoltar = findViewById(R.id.btnVoltar);
        btnCadastrar = findViewById(R.id.Cadastrar);
        inputNome = findViewById(R.id.inputNomeCadastro);
        inputEmail = findViewById(R.id.inputEmailCadastro);
        inputSenha = findViewById(R.id.inputSenhaCadastro);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validaCampos() == false){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CadastroActivity.this);
                    builder.setTitle("Cadastro de Usuário")
                            .setMessage("Existem campos inválidos!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Não acontece nada
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else{
                    String nome = inputNome.getText().toString();
                    String email = inputEmail.getText().toString();
                    String senha = inputSenha.getText().toString();
                    Usuario user = new Usuario();
                    user.setNome(nome);
                    user.setEmail(email);
                    user.setSenha(senha);
                    RecuperarToken(user);
                }
            }
        });


    }


    private boolean validaCampos(){
        if(inputNome==null || inputNome.getText().toString().isEmpty()){
            Toast.makeText(this, "Campo Nome em Branco!", Toast.LENGTH_LONG);
            return false;
        }
        if(inputEmail==null || inputEmail.getText().toString().isEmpty()){
            Toast.makeText(this, "Formato do E-mail inválido!", Toast.LENGTH_LONG);
            return false;
        }else{
            if(!inputEmail.getText().toString().contains("@")){
                if(!(inputEmail.getText().toString().contains(".com") ^ inputEmail.getText().toString().contains(".net"))){
                    Toast.makeText(this, "Formato do E-mail inválido!", Toast.LENGTH_LONG);
                    return false;

                }
            }
        }
        if(inputSenha==null || inputSenha.getText().toString().isEmpty()){
            Toast.makeText(this, "Campo Senha em Branco!", Toast.LENGTH_LONG);
            return false;

        }
        return true;
    }

    private boolean salvarUsuarioFireBase(Usuario user){
        autenticacao = FireBaseConfig.getFireBaseAuth();
        autenticacao.createUserWithEmailAndPassword(user.getEmail(), user.getSenha()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CadastroActivity.this);
                    builder.setTitle("Cadastro de Usuário")
                            .setMessage("Cadastro realizado com sucesso!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    try {
                                        String idUsuario = Base64Custom.code64Base(user.getEmail());
                                        user.setUserID(idUsuario);
                                        user.salvarUsuario();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    UserFireBase.updateNameUser(user.getNome());
                                    Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }else{
                    String excecao = "";
                    try{
                        throw task.getException();
                    } catch(FirebaseAuthWeakPasswordException e) {
                        excecao = "Digite uma senha mais forte!";
                    } catch(FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail inválido! Digite um e-mail válido!";
                    } catch(FirebaseAuthUserCollisionException e){
                        excecao = "Essa conta já foi cadastrada!";
                    } catch (Exception e) {
                        excecao = "erro ao cadastrar usuário " + e.getMessage();
                        Log.e("CadastroActivity", "Exceção durante o cadastro do usuário", e);
                        e.printStackTrace();
                    }
                    if(!excecao.isEmpty()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CadastroActivity.this);
                        builder.setTitle("Cadastro de Usuário")
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
        return true;
    }

    public void RecuperarToken(Usuario usuario){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    token = task.getResult();
                    usuario.setToken(token);
                    salvarUsuarioFireBase(usuario);
                }
            }
        });
    }


}