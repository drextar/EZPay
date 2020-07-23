package com.fatecscs.ezpay.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fatecscs.ezpay.Activity.AdicionaPagamentoActivity;
import com.fatecscs.ezpay.Activity.MinhasComprasActivity;
import com.fatecscs.ezpay.Helper.UsuarioFirebase;
import com.fatecscs.ezpay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {
    private FloatingActionButton btnSalvar;
    private Button btnMetodos;
    private TextView senha,csenha,nome,email, mCompras;
    private EditText cpf;
    private FirebaseAuth auth;
    private ImageView visibilidadeSenha, visibilidadeConfirma;
    private int x = 0;


    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        auth.getInstance();
        visibilidadeSenha       = view.findViewById(R.id.imgVisivel);
        visibilidadeConfirma    = view.findViewById(R.id.imgVisivelC);
        nome                    = view.findViewById(R.id.txtNome);
        email                   = view.findViewById(R.id.txtEmail);
        senha                   = view.findViewById(R.id.txtSenha);
        csenha                  = view.findViewById(R.id.txtConfirmaSenha);
        cpf                     = view.findViewById(R.id.txtCpf);
        btnSalvar               = view.findViewById(R.id.btnSalvar);
        btnMetodos              = view.findViewById(R.id.btnMetodos);
        mCompras                = view.findViewById(R.id.mCompras);
        carregaDados();

        mCompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MinhasComprasActivity.class));
            }
        });

        visibilidadeSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x == 0){
                    senha.setTransformationMethod(null);
                    visibilidadeSenha.setImageResource(R.drawable.ic_visibility_off_white_24dp);
                    x = 1;
                } else {
                    visibilidadeSenha.setImageResource(R.drawable.ic_visibility_white_24dp);
                    senha.setTransformationMethod(new PasswordTransformationMethod());
                    x = 0;
                }

            }
        });

        visibilidadeConfirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x == 0){
                    csenha.setTransformationMethod(null);
                    visibilidadeConfirma.setImageResource(R.drawable.ic_visibility_off_white_24dp);
                    x = 1;
                } else {
                    visibilidadeConfirma.setImageResource(R.drawable.ic_visibility_white_24dp);
                    csenha.setTransformationMethod(new PasswordTransformationMethod());
                    x = 0;
                }

            }
        });

        btnMetodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AdicionaPagamentoActivity.class));
            }
        });


        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!senha.getText().toString().isEmpty()){
                    if(senha.getText().toString().equals(csenha.getText().toString())){
                        alteraSenha(senha.getText().toString());
                    } else {
                        Toast.makeText(getActivity(), "Senhas não coincidem!", Toast.LENGTH_SHORT).show();
                        senha.requestFocus();
                        csenha.setText("");
                    }

                } else {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.viewPager, new CodigoBarrasFragment()).commit();
                }
            }
        });

        return view;
    }

    public void carregaDados(){

        FirebaseUser user = UsuarioFirebase.getUsuarioAtual();
        nome.setText(user.getDisplayName());
        email.setText(user.getEmail());
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("usuarios").child(user.getUid()).child("cpf");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cpf.setText(dataSnapshot.getValue().toString());
                //cpf.addTextChangedListener(MaskEditUtil.insert(MaskEditUtil.CPF_MASK, cpf));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void alteraSenha(String senha){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            user.updatePassword(senha).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.viewPager, new CodigoBarrasFragment()).commit();


                    } else {
                        String erroExcecao = "";
                        try{
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            erroExcecao = "Digite uma senha mais forte";
                        } catch (Exception e){
                            erroExcecao = "ao alterar a senha do usuário: " + e.getMessage();
                            e.printStackTrace();
                        }
                        Toast.makeText(getActivity(),
                                "Erro: " + erroExcecao,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
