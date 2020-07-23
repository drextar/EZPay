package com.fatecscs.ezpay.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fatecscs.ezpay.Activity.CarrinhoActivity;
import com.fatecscs.ezpay.Helper.ConfiguracaoFireBase;
import com.fatecscs.ezpay.Helper.UsuarioFirebase;
import com.fatecscs.ezpay.Model.ProdutoCarrinho;
import com.fatecscs.ezpay.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCarrinho extends RecyclerView.Adapter<AdapterCarrinho.MyViewHolder> {
    private List<ProdutoCarrinho> listCarrinho;
    private Context context;
    private List<ProdutoCarrinho> listaCarrinho;
    private AdapterCarrinho adapterCarrinho;
    private Double totalCarrinho = 0.0;
    private DatabaseReference dbRef;


    public AdapterCarrinho(List<ProdutoCarrinho> l, Context c) {
        this.listCarrinho = l;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_carrinho, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProdutoCarrinho produtoCarrinho = listCarrinho.get(position);
        holder.nomeProduto.setText(produtoCarrinho.getNome());
        holder.precoProduto.setText(produtoCarrinho.getPreco());
        holder.qtdeProduto.setText(Integer.toString(produtoCarrinho.getQtde()));
        holder.codigoBarras.setText(produtoCarrinho.getCodigoBarras());


        final String nomeCarrinho = holder.nomeProduto.getText().toString();
        final String precoFinal = holder.precoProduto.getText().toString();
        final String qtdeCarrinho = holder.qtdeProduto.getText().toString();

        if(produtoCarrinho.getImgProduto() != null){
            Uri uri = Uri.parse(produtoCarrinho.getImgProduto());
            Glide.with(context).load(uri).into(holder.imgProduto);
        } else {
            holder.imgProduto.setImageResource(R.drawable.default_product);
        }

    }

    @Override
    public int getItemCount() {
        return listCarrinho.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imgProduto;
        TextView nomeProduto, precoProduto, qtdeProduto, codigoBarras;
        FloatingActionButton remove;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            imgProduto = itemView.findViewById(R.id.imgProduto);
            nomeProduto = itemView.findViewById(R.id.txtNome);
            precoProduto = itemView.findViewById(R.id.txtPreco);
            qtdeProduto = itemView.findViewById(R.id.txtQtde);
            codigoBarras = itemView.findViewById(R.id.codigoBarras);
            listaCarrinho = new ArrayList<>();
            adapterCarrinho = new AdapterCarrinho(listaCarrinho, context);
            remove = itemView.findViewById(R.id.btnRemove);

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nomeCarrinho = nomeProduto.getText().toString();
                    String precoFinal = precoProduto.getText().toString();
                    String qtdeCarrinho = qtdeProduto.getText().toString();
                    String codigo = codigoBarras.getText().toString();
                    FirebaseUser user = UsuarioFirebase.getUsuarioAtual();
                    ProdutoCarrinho produtoCarrinho = new ProdutoCarrinho(nomeCarrinho, precoFinal, Integer.parseInt(qtdeCarrinho), user.getUid(), codigo);
                    produtoCarrinho.removeCarrinho(nomeCarrinho);
                    Toast.makeText(itemView.getContext(), "Produto removido com sucesso", Toast.LENGTH_LONG).show();
                    CarrinhoActivity.b.performClick();
                }
            });

        }
    }
}
