package com.fatecscs.ezpay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fatecscs.ezpay.Helper.ConfiguracaoFireBase;
import com.fatecscs.ezpay.Helper.UsuarioFirebase;
import com.fatecscs.ezpay.Helper.ValidaCPF;
import com.fatecscs.ezpay.Model.Usuario;
import com.fatecscs.ezpay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import com.fatecscs.ezpay.Helper.MaskEditUtil;

public class CadastroActivity extends AppCompatActivity {
    private EditText nome, cpf, email, senha, confirmaSenha;
    private Button btnCadastro;
    private ProgressBar progressBar;
    private Usuario usuario;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        inicializarComponentes();

        //Cadastrar usuario
        progressBar.setVisibility(View.GONE);
        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtNome      = nome.getText().toString();
                String txtCpf       = cpf.getText().toString();
                String txtEmail     = email.getText().toString();
                String txtSenha     = senha.getText().toString();
                String txtCsenha    = confirmaSenha.getText().toString();
                String validaCpf;


                if( !txtNome.isEmpty() ){
                    if( !txtCpf.isEmpty() ){
                        if( !txtEmail.isEmpty() ){
                            if( !txtSenha.isEmpty() ){
                                if( !txtCsenha.isEmpty() ){
                                    if( txtSenha.equals(txtCsenha) ){
                                        if(ValidaCPF.isCPF(MaskEditUtil.unmask(txtCpf))){
                                            usuario = new Usuario(txtNome, txtCpf, txtEmail, txtSenha);
                                            cadastrarUsuario(usuario);
                                        } else {
                                            Toast.makeText(CadastroActivity.this, "CPF inválido",
                                                    Toast.LENGTH_SHORT).show();
                                            cpf.setText("");
                                            cpf.requestFocus();
                                        }
                                    } else {
                                        Toast.makeText(CadastroActivity.this,
                                                "Senhas diferem!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(CadastroActivity.this,
                                            "Confirme a senha!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(CadastroActivity.this,
                                        "Preencha a senha!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CadastroActivity.this,
                                    "Preencha o E-mail!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CadastroActivity.this,
                                "Preencha o CPF!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha o nome!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Responsavel por cadastar usuario com email e senha
    //E fazer validacoes na realizacao do cadastro
    public void cadastrarUsuario(final Usuario usuario){
        progressBar.setVisibility(View.VISIBLE);
        auth = ConfiguracaoFireBase.getFirebaseAutenticacao();
        auth.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String idUsuario = task.getResult().getUser().getUid();
                            usuario.setId( idUsuario);
                            usuario.salvar();

                            //Salva dados no profile do Firebase
                            UsuarioFirebase.atualizaNomeUsuario(usuario.getNome());

                            Toast.makeText(CadastroActivity.this,
                                    "Cadastro realizado com sucesso!",
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            String erroExcecao = "";
                            try{
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e ){
                                erroExcecao = "Digite uma senha mais forte!";
                            } catch (FirebaseAuthInvalidCredentialsException e ){
                                erroExcecao = "Por favor, digite um e-mail válido!";
                            } catch (FirebaseAuthUserCollisionException e){
                                erroExcecao = "Este e-mail já está cadastrado!";
                            } catch (Exception e){
                                erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(CadastroActivity.this,
                                    "Erro: " + erroExcecao,
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    public void inicializarComponentes(){
        nome            = findViewById(R.id.txtNome);
        cpf             = findViewById(R.id.txtCpf);
        email           = findViewById(R.id.txtEmail);
        senha           = findViewById(R.id.txtSenha);
        confirmaSenha   = findViewById(R.id.txtConfirma);
        progressBar     = findViewById(R.id.progressCadastro);
        btnCadastro     = findViewById(R.id.btnCadastro);
        nome.requestFocus();

        cpf.addTextChangedListener(MaskEditUtil.insert(MaskEditUtil.CPF_MASK, cpf));
    }
}
