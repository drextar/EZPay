package com.fatecscs.ezpay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fatecscs.ezpay.Adapter.AdapterMetodosPagamento;
import com.fatecscs.ezpay.Helper.ConfiguracaoFireBase;
import com.fatecscs.ezpay.Helper.MaskEditUtil;
import com.fatecscs.ezpay.Helper.RecyclerItemClickListener;
import com.fatecscs.ezpay.Helper.UsuarioFirebase;
import com.fatecscs.ezpay.Helper.ValidaCPF;
import com.fatecscs.ezpay.Model.Cartao;
import com.fatecscs.ezpay.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdicionaPagamentoActivity extends AppCompatActivity {

    private TextView nome;
    private EditText cpf, validade, numeroCartao;
    private Button salvar;
    public static FloatingActionButton atualizar;
    public static View b;
    private Cartao cartao;
    private RecyclerView rvCartao;
    private List<Cartao> listaCartao;
    private AdapterMetodosPagamento adapterMetodosPagamento;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adiciona_pagamento);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        /*Configura toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Métodos de pagamento");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        inicializaComponentes();

        listaCartao = new ArrayList<>();

        rvCartao.setHasFixedSize(true);
        rvCartao.setLayoutManager(new LinearLayoutManager(this));
        adapterMetodosPagamento = new AdapterMetodosPagamento(listaCartao, this);
        rvCartao.setAdapter(adapterMetodosPagamento);
        carregaCartao();
        b = findViewById(R.id.btnAtualiza);
        atualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AdicionaPagamentoActivity.this, "Cartão removido com sucesso!", Toast.LENGTH_SHORT).show();
                atualizaCartoes();
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validaCampos();
            }
        });
    }

    public void atualizaCartoes(){
        listaCartao.clear();
        adapterMetodosPagamento.notifyDataSetChanged();
        FirebaseUser user = UsuarioFirebase.getUsuarioAtual();
        dbRef = ConfiguracaoFireBase.getFirebase().child("usuarios").child(user.getUid()).child("cartoes");
        dbRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public void carregaCartao(){
        listaCartao.clear();
        FirebaseUser user = UsuarioFirebase.getUsuarioAtual();
        dbRef = ConfiguracaoFireBase.getFirebase().child("usuarios").child(user.getUid()).child("cartoes");
        dbRef.addListenerForSingleValueEvent(valueEventListener);

    }

    ValueEventListener valueEventListener  = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            listaCartao.clear();
            if(dataSnapshot.exists()){
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Cartao card = ds.getValue(Cartao.class);
                    listaCartao.add(card);
                }
                adapterMetodosPagamento.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void inicializaComponentes(){
        nome            = findViewById(R.id.txtTitular);
        numeroCartao    = findViewById(R.id.txtNumeroCartao);
        validade        = findViewById(R.id.txtValidade);
        cpf             = findViewById(R.id.txtCpf);
        salvar           = findViewById(R.id.btnSalvar);
        rvCartao        = findViewById(R.id.rvMetodosSalvos);
        atualizar       = findViewById(R.id.btnAtualiza);

        cpf.addTextChangedListener(MaskEditUtil.insert(MaskEditUtil.CPF_MASK, cpf));
        validade.addTextChangedListener(MaskEditUtil.insert(MaskEditUtil.MESANO_MASK, validade));
        numeroCartao.addTextChangedListener(MaskEditUtil.insert(MaskEditUtil.CARTAO_MASK, numeroCartao));
    }

    public void validaCampos(){

        if(validaData() == true){
            if(nome.getText().toString().equals("") || nome.getText() == null ||
                    numeroCartao.getText().toString().equals("") || numeroCartao.getText() == null ||
                    validade.getText().toString().equals("") || validade.getText() == null ||
                    cpf.getText().toString().equals("") || cpf.getText() == null){
                Toast.makeText(this,
                        "Preencha todos os campos de pagamento!",
                        Toast.LENGTH_SHORT).show();
            } else if (!ValidaCPF.isCPF(MaskEditUtil.unmask(cpf.getText().toString()))){
                Toast.makeText(this, "CPF inválido", Toast.LENGTH_SHORT).show();
                cpf.requestFocus();
            } else if (MaskEditUtil.unmask(numeroCartao.getText().toString()).length() < 14){
                Toast.makeText(this, "Por favor preencha os números do cartão corretamente", Toast.LENGTH_SHORT).show();
                numeroCartao.requestFocus();
            } else {
                salvarCartao();
            }
        }
    }

    public boolean validaData(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat yf = new SimpleDateFormat("yy");
        SimpleDateFormat mf = new SimpleDateFormat("MM");
        final Integer year = Integer.parseInt(yf.format(c));
        final Integer month = Integer.parseInt(mf.format(c));
        String txtValidade      = validade.getText().toString();
        Integer mes              = Integer.parseInt(txtValidade.substring(0,2));
        Integer ano              = Integer.parseInt(txtValidade.substring(txtValidade.length() - 2));


        if(mes <= 0 || mes > 12){
            Toast.makeText(this, "Mês inválido", Toast.LENGTH_SHORT).show();
            validade.setText("");
            validade.requestFocus();
            return false;
        } else if (ano < year){
            Toast.makeText(this, "Ano inválido", Toast.LENGTH_SHORT).show();
            validade.setText("");
            validade.requestFocus();
            return false;
        } else if (ano == year){
            if(mes <= month){
                Toast.makeText(this, "Cartão inválido", Toast.LENGTH_SHORT).show();
                validade.setText("");
                validade.requestFocus();
                return false;
            } else {
                return true;
            }
        }

        return true;
    }

    public void salvarCartao(){
        FirebaseUser user = UsuarioFirebase.getUsuarioAtual();
        String txtNome          = nome.getText().toString();
        String txtCpf           = cpf.getText().toString();
        String txtNumero        = MaskEditUtil.unmask(numeroCartao.getText().toString());
        String txtValidade      = validade.getText().toString();


        cartao = new Cartao(txtNumero, txtValidade, user.getUid(), txtCpf, txtNome);
        cartao.salvar(txtNumero);
        Toast.makeText(this, "Método de pagamento adicionado com sucesso!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
