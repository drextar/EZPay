package com.fatecscs.ezpay.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fatecscs.ezpay.Model.Cartao;
import com.fatecscs.ezpay.R;

import java.util.List;


public class AdapterCartao extends RecyclerView.Adapter<AdapterCartao.MyViewHolder>{
    private List<Cartao> listaCartao;
    private Context context;

    public AdapterCartao(List<Cartao> l, Context c) {
        this.listaCartao = l;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_busca_cartao, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Cartao cartao = listaCartao.get(position);
        final String digitos = cartao.getNumero().substring(cartao.getNumero().length() - 4);
        holder.numero.setText("**** " + digitos);
        holder.bandeira.setImageResource(R.drawable.ic_credit_card_black_24dp);

        //String digitos = numero.substring(numero.length() - 4);
    }

    @Override
    public int getItemCount() {
        return listaCartao.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView numero;
        ImageView bandeira;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            numero = itemView.findViewById(R.id.txtDigitosPesquisa);
            bandeira = itemView.findViewById(R.id.imageBandeiraPesquisa);
        }
    }
}
