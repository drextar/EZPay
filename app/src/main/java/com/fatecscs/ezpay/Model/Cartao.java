package com.fatecscs.ezpay.Model;

import com.fatecscs.ezpay.Helper.ConfiguracaoFireBase;
import com.google.firebase.database.DatabaseReference;

public class Cartao {
    private String numero;
    private String vencimento;
    private String id;
    private String cpf;
    private String titular;

    public Cartao(){}

    public Cartao(String numero, String vencimento, String id, String cpf, String titular){
        this.numero = numero;
        this.vencimento = vencimento;
        this.id = id;
        this.cpf = cpf;
        this.titular = titular;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
        this.vencimento = vencimento;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public void salvar(String numero){
        String digitos = numero.substring(numero.length() - 4);
        DatabaseReference dbRef = ConfiguracaoFireBase.getFirebase();
        DatabaseReference cartaoRef = dbRef.child("usuarios").child(getId()).child("cartoes").child(digitos);
        cartaoRef.setValue(this);
    }

    public void remove(String digitos){
        DatabaseReference dbRef = ConfiguracaoFireBase.getFirebase();
        dbRef.child("usuarios").child(getId()).child("cartoes").child(digitos).removeValue();
    }
}
