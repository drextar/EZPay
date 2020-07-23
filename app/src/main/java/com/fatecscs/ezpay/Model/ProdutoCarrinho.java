package com.fatecscs.ezpay.Model;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fatecscs.ezpay.Helper.ConfiguracaoFireBase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProdutoCarrinho{
    private String nome, preco, id, imgProduto, codigoBarras;
    private Integer qtde;
    Integer qtdeBanco;

    public ProdutoCarrinho(){}

    public ProdutoCarrinho(String nome, String preco, Integer qtde, String id, String codigoBarras){
        this.nome = nome;
        this.preco = preco;
        this.qtde = qtde;
        this.id = id;
        this.codigoBarras = codigoBarras;
    }

    public ProdutoCarrinho(String nome, String preco, Integer qtde, String id, String imgProduto, String codigoBarras){
        this.nome = nome;
        this.preco = preco;
        this.qtde = qtde;
        this.id = id;
        this.imgProduto = imgProduto;
        this.codigoBarras = codigoBarras;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getImgProduto() {
        return imgProduto;
    }

    public void setImgProduto(String imgProduto) {
        this.imgProduto = imgProduto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public Integer getQtde() {
        return qtde;
    }

    public void setQtde(Integer qtde) {
        this.qtde = qtde;
    }

    public void salvarCarrinho(){
        DatabaseReference dbRef = ConfiguracaoFireBase.getFirebase();

        final DatabaseReference qtdeRef = dbRef.child("produtos").child(getCodigoBarras()).child("qtde");
        final DatabaseReference atualizaQtde = dbRef.child("produtos").child(getCodigoBarras());

        qtdeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                qtdeBanco = dataSnapshot.getValue(Integer.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Map<String, Object> qtdeFinal = new HashMap<String,Object>();
                Integer novaQtde = qtdeBanco - getQtde();
                qtdeFinal.put("qtde", novaQtde);
                atualizaQtde.updateChildren(qtdeFinal);
                Log.i("Valor banco", String.valueOf(qtdeBanco));
            }
        }, 500);


        DatabaseReference carrinhoRef = dbRef.child("usuarios").child(getId()).child("carrinho").child(getNome());
        carrinhoRef.setValue(this);
    }

    public void removeCarrinho(String nome){
        DatabaseReference dbRef = ConfiguracaoFireBase.getFirebase();

        final DatabaseReference qtdeRef = dbRef.child("produtos").child(getCodigoBarras()).child("qtde");
        final DatabaseReference atualizaQtde = dbRef.child("produtos").child(getCodigoBarras());

        qtdeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                qtdeBanco = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Map<String, Object> qtdeFinal = new HashMap<String,Object>();
                Integer novaQtde = qtdeBanco + getQtde();
                qtdeFinal.put("qtde", novaQtde);
                atualizaQtde.updateChildren(qtdeFinal);
                Log.i("Valor banco", String.valueOf(qtdeBanco));
            }
        }, 500);

        dbRef.child("usuarios").child(getId()).child("carrinho").child(nome).removeValue();
    }
}
