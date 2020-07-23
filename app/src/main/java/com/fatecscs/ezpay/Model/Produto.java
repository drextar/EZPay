package com.fatecscs.ezpay.Model;

import com.fatecscs.ezpay.Helper.ConfiguracaoFireBase;
import com.google.firebase.database.DatabaseReference;

public class Produto {
    private String nomeProduto, imgProduto, local;
    private String codigoBarras;
    private String preco;
    private Integer qtde;

    public void Produto(String codigoBarras, String nomeProduto, String preco, Integer qtde){
        this.codigoBarras   = codigoBarras;
        this.nomeProduto    = nomeProduto;
        this.preco          = preco;
        this.qtde           = qtde;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto.toUpperCase();
    }

    public String getImgProduto() {
        return imgProduto;
    }

    public void setImgProduto(String imgProduto) {
        this.imgProduto = imgProduto;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
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

    public void salvar(String codigoBarras){
        DatabaseReference dbRef = ConfiguracaoFireBase.getFirebase();
        DatabaseReference produtoRef = dbRef.child("produtos").child(codigoBarras);
        produtoRef.setValue(this);
    }
}
