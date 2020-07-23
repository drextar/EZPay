package com.fatecscs.ezpay.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fatecscs.ezpay.Adapter.AdapterCartao;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagamentoActivity extends AppCompatActivity {
    private EditText numero, vencimento, cvv, nome, cpf;
    private ImageView limpar;
    private Switch salvarCartao;
    private FloatingActionButton pagar;
    private Cartao cartao;
    private String cartaoFinal;
    private RecyclerView rvCartao;
    private List<Cartao> listaCartao;
    private DatabaseReference dbRef, origem, destino, compras;
    private long maxid=0;
    private FirebaseUser user;
    private String totalCarrinho;

    private AdapterCartao adapterCartao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        /*Configura toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Pagamento");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);


        iniciaComponentes();

        limpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpaDados();
            }
        });
        user = UsuarioFirebase.getUsuarioAtual();
        origem = ConfiguracaoFireBase.getFirebase().child("usuarios").child(user.getUid()).child("carrinho");
        compras = ConfiguracaoFireBase.getFirebase().child("usuarios").child(user.getUid()).child("compras");
        destino = FirebaseDatabase.getInstance().getReference().child("vendas");
        destino.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    maxid=(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        listaCartao = new ArrayList<>();

        rvCartao.setHasFixedSize(true);
        rvCartao.setLayoutManager(new LinearLayoutManager(this));
        adapterCartao = new AdapterCartao(listaCartao, this);
        rvCartao.setAdapter(adapterCartao);
        //Configura evento de clique
        rvCartao.addOnItemTouchListener(new RecyclerItemClickListener(
                this, rvCartao, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Cartao cartaoSelecionado = listaCartao.get(position);
                String digitos = cartaoSelecionado.getNumero().substring(cartaoSelecionado.getNumero().length() - 4);
                cartaoFinal = cartaoSelecionado.getNumero();
                numero.setText("**** " + digitos);
                vencimento.setText(cartaoSelecionado.getVencimento());
                nome.setText(cartaoSelecionado.getTitular());
                cpf.setText(cartaoSelecionado.getCpf());
                cvv.setText("");
                bloquearCampos();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }
        ));
        carregaCartao();

        pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validaData() == true){
                    if(validaDados() == 0){
                        if(salvarCartao.isChecked()){
                            if(numero.getText().toString().indexOf('*') < 0){
                                salvarCartao(numero.getText().toString());
                            } else {
                                finalizaPagamento(cartaoFinal);
                            }
                        } else {
                            finalizaPagamento(cartaoFinal);
                        }
                    } else if (validaDados() == 1) {
                        Toast.makeText(PagamentoActivity.this,
                                "Preencha todos os campos de pagamento",
                                Toast.LENGTH_SHORT).show();
                    } else if (validaDados() == 2){
                        Toast.makeText(PagamentoActivity.this, "CPF inválido", Toast.LENGTH_SHORT).show();
                    } else if (validaDados() == 3){
                        Toast.makeText(PagamentoActivity.this, "Número de cartão inválido", Toast.LENGTH_LONG).show();
                    }   else if (validaDados() == 4){
                        Toast.makeText(PagamentoActivity.this, "Código de segurança inválido", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    public boolean validaData(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat yf = new SimpleDateFormat("yy");
        SimpleDateFormat mf = new SimpleDateFormat("MM");
        final Integer year = Integer.parseInt(yf.format(c));
        final Integer month = Integer.parseInt(mf.format(c));
        String txtValidade      = vencimento.getText().toString();
        Integer mes              = Integer.parseInt(txtValidade.substring(0,2));
        Integer ano              = Integer.parseInt(txtValidade.substring(txtValidade.length() - 2));


        if(mes <= 0 || mes > 12){
            Toast.makeText(this, "Mês inválido", Toast.LENGTH_SHORT).show();
            vencimento.setText("");
            vencimento.requestFocus();
            return false;
        } else if (ano < year){
            Toast.makeText(this, "Ano inválido", Toast.LENGTH_SHORT).show();
            vencimento.setText("");
            vencimento.requestFocus();
            return false;
        } else if (ano == year){
            if(mes <= month){
                Toast.makeText(this, "Cartão inválido", Toast.LENGTH_SHORT).show();
                vencimento.setText("");
                vencimento.requestFocus();
                return false;
            } else {
                return true;
            }
        }

        return true;
    }

    public void bloquearCampos(){
        numero.setFocusable(false);
        vencimento.setFocusable(false);
        cpf.setFocusable(false);
        nome.setFocusable(false);

    }

    public int validaDados(){
        if(numero.getText().toString().equals("") || vencimento.getText().toString().equals("") ||
                cvv.getText().toString().equals("") || nome.getText().toString().equals("") ||
                cpf.getText().toString().equals("")){
            return 1;
        } else if (!ValidaCPF.isCPF(MaskEditUtil.unmask(cpf.getText().toString()))){
            return 2;
        } else if (numero.isFocusable() && (MaskEditUtil.unmask(numero.getText().toString()).length() < 14)){
            return 3;
        } else if ((MaskEditUtil.unmask(cvv.getText().toString()).length() < 3)){
            return 4;
        } else {
            return 0;
        }
    }

    public void limpaDados(){
        numero.setText("");
        vencimento.setText("");
        cvv.setText("");
        nome.setText("");
        cpf.setText("");
        cvv.setText("");
        liberaCampos();
    }
    public void liberaCampos(){
        numero.setFocusableInTouchMode(true);
        vencimento.setFocusableInTouchMode(true);
        cpf.setFocusableInTouchMode(true);
        nome.setFocusableInTouchMode(true);
        numero.setFocusable(true);
        vencimento.setFocusable(true);
        cpf.setFocusable(true);
        nome.setFocusable(true);
        cartaoFinal = null;
    }

    public void carregaCartao(){
        limpaDados();
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
                adapterCartao.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void finalizaPagamento(String numero){
        user = UsuarioFirebase.getUsuarioAtual();
        volta();

    }

    public void volta(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                atualizaValores();
                CarrinhoActivity.b.performClick();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }, 1000);
    }

    public void atualizaValores(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedDate = df.format(c);

        totalCarrinho = getIntent().getStringExtra("TOTAL_CARRINHO");

        origem.removeValue();
        Map<String, Object> date = new HashMap<String,Object>();
        Map<String, Object> idC = new HashMap<String,Object>();
        Map<String, Object> id = new HashMap<String,Object>();
        Map<String, Object> totC = new HashMap<String,Object>();
        totC.put("total", totalCarrinho);
        date.put("data", formattedDate);
        idC.put("idCompra", maxid+1);
        id.put("id", user.getUid());
        destino.child(String.valueOf(maxid+1)).updateChildren(totC);
        destino.child(String.valueOf(maxid+1)).updateChildren(date);
        destino.child(String.valueOf(maxid+1)).updateChildren(idC);
        destino.child(String.valueOf(maxid+1)).updateChildren(id);
        compras.child(String.valueOf(maxid+1)).updateChildren(totC);
        compras.child(String.valueOf(maxid+1)).updateChildren(date);
        compras.child(String.valueOf(maxid+1)).updateChildren(idC);
        compras.child(String.valueOf(maxid+1)).updateChildren(id);
        Toast.makeText(this, "Pagamento realizado com sucesso!", Toast.LENGTH_SHORT).show();
    }
    public void salvarCartao(String num){
            FirebaseUser user = UsuarioFirebase.getUsuarioAtual();
            String txtNome          = nome.getText().toString();
            String txtCpf           = cpf.getText().toString();
            String txtNumero        = numero.getText().toString();
            String txtValidade      = vencimento.getText().toString();

            cartao = new Cartao(txtNumero, txtValidade, user.getUid(), txtCpf, txtNome);
            cartao.salvar(txtNumero);
            finalizaPagamento(num);

    }

    public void iniciaComponentes(){
        numero          = findViewById(R.id.txtNumero);
        vencimento      = findViewById(R.id.txtVencimento);
        cvv             = findViewById(R.id.txtCvv);
        nome            = findViewById(R.id.txtNome);
        cpf             = findViewById(R.id.txtCpf);
        salvarCartao    = findViewById(R.id.salvarCartao);
        pagar           = findViewById(R.id.btnPagar);
        rvCartao        = findViewById(R.id.rvCartoes);
        limpar          = findViewById(R.id.imgLimpar);
        cpf.addTextChangedListener(MaskEditUtil.insert(MaskEditUtil.CPF_MASK, cpf));
        vencimento.addTextChangedListener(MaskEditUtil.insert(MaskEditUtil.MESANO_MASK, vencimento));
        numero.addTextChangedListener(MaskEditUtil.insert(MaskEditUtil.CARTAO_MASK, numero));
        cvv.addTextChangedListener(MaskEditUtil.insert(MaskEditUtil.CVV_MASK, cvv));

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
