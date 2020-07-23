package com.fatecscs.ezpay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fatecscs.ezpay.Helper.ConfiguracaoFireBase;
import com.fatecscs.ezpay.Model.Usuario;
import com.fatecscs.ezpay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button btnEntrar;
    private ProgressBar progressBar;
    private Usuario usuario;
    private FirebaseAuth auth;
    private TextView esqueceu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        verificaUsuarioLogado();
        inicializarComponentes();

        //Fazer login do usuario
        progressBar.setVisibility(View.GONE);
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if( !email.isEmpty() ){
                    if( !senha.isEmpty() ){
                        usuario = new Usuario(email, senha);
                        validarLogin(usuario);
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Preencha o campo de senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(LoginActivity.this,
                            "Preencha o campo de e-mail!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        esqueceu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RecuperarActivity.class));
            }
        });
    }

    //Verifica se o usuario já está logado
    public void verificaUsuarioLogado(){
        auth = ConfiguracaoFireBase.getFirebaseAutenticacao();
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    public void validarLogin(Usuario usuario){
        progressBar.setVisibility(View.VISIBLE);
        auth = ConfiguracaoFireBase.getFirebaseAutenticacao();
        auth.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Usuário não encontrado",
                            Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void abrirCadastro(View view){
        Intent it = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(it);
    }

    public void inicializarComponentes(){
        campoEmail  = findViewById(R.id.txtEmail);
        campoSenha  = findViewById(R.id.txtSenha);
        btnEntrar   = findViewById(R.id.btnEntrar);
        progressBar = findViewById(R.id.progressLogin);
        esqueceu    = findViewById(R.id.txtEsqueceu);

        campoEmail.requestFocus();
    }
}
