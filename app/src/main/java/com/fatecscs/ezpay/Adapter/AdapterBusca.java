package com.fatecscs.ezpay.Adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fatecscs.ezpay.Activity.CarrinhoActivity;
import com.fatecscs.ezpay.Helper.UsuarioFirebase;
import com.fatecscs.ezpay.Model.Produto;
import com.fatecscs.ezpay.Model.ProdutoCarrinho;
import com.fatecscs.ezpay.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterBusca extends RecyclerView.Adapter<AdapterBusca.MyViewHolder> {
    private List<Produto> listaProduto;
    private Context context;
    private String imgProduto;

    public AdapterBusca(List<Produto> l, Context c) {
        this.listaProduto = l;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_busca_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = listaProduto.get(position);
        holder.nome.setText(produto.getNomeProduto());
        holder.preco.setText(produto.getPreco());
        holder.codigoBarras.setText(produto.getCodigoBarras());
        holder.qtde.setText("1");
        /*if(produto.getLocal() == null){
            holder.local.setText("");
        } else {
            holder.local.setText("Corredor/Prateleira: " + produto.getLocal());
        }*/


        if(produto.getImgProduto() != null){
            Uri uri = Uri.parse(produto.getImgProduto());
            Glide.with(context).load(uri).into(holder.foto);
            imgProduto = produto.getImgProduto();
        } else {
            holder.foto.setImageResource(R.drawable.default_product);
            imgProduto = null;
        }

    }

    @Override
    public int getItemCount() {
        return listaProduto.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView foto;
        TextView nome, preco, local, codigoBarras;
        EditText qtde;
        ImageView add, remove;
        FloatingActionButton btnCarrinho;
        ProdutoCarrinho produtoCarrinho;
        Integer qtdeBanco;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.imageFotoPesquisa);
            nome = itemView.findViewById(R.id.txtNome);
            preco = itemView.findViewById(R.id.txtPreco);
            qtde = itemView.findViewById(R.id.txtQtde);
            add = itemView.findViewById(R.id.btnAdiciona);
            remove = itemView.findViewById(R.id.btnRemove);
            btnCarrinho = itemView.findViewById(R.id.btnCarrinho);
            codigoBarras = itemView.findViewById(R.id.codigoBarras);

            //name.setText("Text " + (number1+number2));

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int qtdeAdd;
                    qtdeAdd = Integer.parseInt(qtde.getText().toString());
                    if(!(qtdeAdd >= 15)){
                        String soma = String.valueOf(qtdeAdd + 1);
                        qtde.setText(soma);
                    }
                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int qtdeAdd;
                    qtdeAdd = Integer.parseInt(qtde.getText().toString());
                    if(!(qtdeAdd <= 0)){
                        String soma = String.valueOf(qtdeAdd - 1);
                        qtde.setText(soma);
                    }
                }
            });



            btnCarrinho.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nomeCarrinho = nome.getText().toString();
                    String codigo = codigoBarras.getText().toString();
                    Double precoCarrinho = Double.parseDouble(preco.getText().toString());
                    final Integer qtdeCarrinho = Integer.parseInt(qtde.getText().toString());
                    Double precoFinal = precoCarrinho * qtdeCarrinho;
                    String precoAdiciona;

                    Double d = precoFinal;
                    String text = Double.toString(Math.abs(d));
                    int integerPlaces = text.indexOf('.');
                    int decimalPlaces = text.length() - integerPlaces - 1;
                    if(decimalPlaces == 1){
                        precoAdiciona = precoFinal + "0";
                    } else {
                        precoAdiciona = String.valueOf(precoFinal);
                    }

                    FirebaseUser user = UsuarioFirebase.getUsuarioAtual();





                    produtoCarrinho = new ProdutoCarrinho(nomeCarrinho, precoAdiciona, qtdeCarrinho, user.getUid(), imgProduto, codigo);
                    produtoCarrinho.salvarCarrinho();
                    Toast.makeText(itemView.getContext(), "Produto adicionado ao carrinho com sucesso!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
