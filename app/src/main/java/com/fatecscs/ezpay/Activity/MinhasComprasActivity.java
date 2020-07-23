package com.fatecscs.ezpay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.fatecscs.ezpay.Adapter.AdapterVendas;
import com.fatecscs.ezpay.Helper.ConfiguracaoFireBase;
import com.fatecscs.ezpay.Helper.UsuarioFirebase;
import com.fatecscs.ezpay.Model.Vendas;
import com.fatecscs.ezpay.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MinhasComprasActivity extends AppCompatActivity {
    private RecyclerView rvCompras;
    private AdapterVendas adapterVendas;
    private List<Vendas> listaVendas;
    private DatabaseReference dbRef;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_compras);

        /*Configura toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Minhas Compras");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        iniciaComponentes();

        //Recycler View
        rvCompras.setHasFixedSize(true);
        rvCompras.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapterVendas = new AdapterVendas(listaVendas, getApplicationContext());
        rvCompras.setAdapter(adapterVendas);

        buscaCompras();
    }

    public void iniciaComponentes(){
        rvCompras = findViewById(R.id.rvCompras);
        listaVendas = new ArrayList<>();
        user = UsuarioFirebase.getUsuarioAtual();
    }

    public void buscaCompras(){
        listaVendas.clear();
        adapterVendas.notifyDataSetChanged();
        dbRef = ConfiguracaoFireBase.getFirebase().child("usuarios").child(user.getUid()).child("compras");
        dbRef.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            listaVendas.clear();
            if(dataSnapshot.exists()){
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Vendas venda = ds.getValue(Vendas.class);
                    listaVendas.add(venda);
                }
                adapterVendas.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
