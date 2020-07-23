package com.fatecscs.ezpay.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.fatecscs.ezpay.Activity.CarrinhoActivity;
import com.fatecscs.ezpay.Adapter.AdapterBusca;
import com.fatecscs.ezpay.Helper.ConfiguracaoFireBase;
import com.fatecscs.ezpay.Model.Produto;
import com.fatecscs.ezpay.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuscaFragment extends Fragment {
    private SearchView svPesquisa;
    private RecyclerView rvPesquisa;
    private FloatingActionButton fabCarrinho;

    private List<Produto> listaProduto;
    private DatabaseReference produtosRef;
    private AdapterBusca adapterBusca;


    public BuscaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_busca, container, false);

        svPesquisa = view.findViewById(R.id.svPesquisa);
        rvPesquisa = view.findViewById(R.id.rvPesquisa);
        fabCarrinho = view.findViewById(R.id.fabCarrinho);

        fabCarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CarrinhoActivity.class));
            }
        });

        listaProduto = new ArrayList<>();
        produtosRef = ConfiguracaoFireBase.getFirebase().child("produtos");

        //Recycler View
        rvPesquisa.setHasFixedSize(true);
        rvPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterBusca = new AdapterBusca(listaProduto, getActivity());
        rvPesquisa.setAdapter(adapterBusca);

        //SearchView
        svPesquisa.setQueryHint("Buscar Produtos");
        svPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toUpperCase();
                pesquisarProduto(textoDigitado);
                return true;
            }
        });
        return view;
    }

    public void pesquisarProduto(String busca){
        //limpar lista
        listaProduto.clear();

        //Pesquisa caso tenha texto na pesquisa
        if(busca.length() > 0){
            Query query = produtosRef.orderByChild("nomeProduto").startAt(busca).endAt(busca + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //limpar lista
                    listaProduto.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren())  {
                        listaProduto.add(ds.getValue(Produto.class));
                    }
                    adapterBusca.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
