package com.fatecscs.ezpay.Adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fatecscs.ezpay.Activity.AdicionaPagamentoActivity;
import com.fatecscs.ezpay.Model.Cartao;
import com.fatecscs.ezpay.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class AdapterMetodosPagamento extends RecyclerView.Adapter<AdapterMetodosPagamento.MyViewHolder>{
    private List<Cartao> listaCartao;
    private Context context;

    public AdapterMetodosPagamento(List<Cartao> l, Context c) {
        this.listaCartao = l;
        this.context = c;
    }

    @NonNull
    @Override
    public AdapterMetodosPagamento.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_metodos_pagamento, parent, false);
        return new AdapterMetodosPagamento.MyViewHolder(itemLista);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterMetodosPagamento.MyViewHolder holder, int position) {
        final Cartao cartao = listaCartao.get(position);
        final String digitos = cartao.getNumero().substring(cartao.getNumero().length() - 4);
        holder.numero.setText("**** " + digitos);
        holder.bandeira.setImageResource(R.drawable.ic_credit_card_black_24dp);
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartao.remove(digitos);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        AdicionaPagamentoActivity.b.performClick();
                    }
                }, 1000);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCartao.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView numero;
        ImageView bandeira, btnRemove;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            numero = itemView.findViewById(R.id.txtDigitosPesquisa);
            bandeira = itemView.findViewById(R.id.imageBandeiraPesquisa);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
