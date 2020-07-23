package com.fatecscs.ezpay.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fatecscs.ezpay.Adapter.AdapterCarrinho;
import com.fatecscs.ezpay.Helper.ConfiguracaoFireBase;
import com.fatecscs.ezpay.Helper.UsuarioFirebase;
import com.fatecscs.ezpay.Model.ProdutoCarrinho;
import com.fatecscs.ezpay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarrinhoActivity extends AppCompatActivity {
    private Button btnFinaliza;
    public static FloatingActionButton btnAtualiza;
    public static View b;
    private RecyclerView rvCarrinho;
    public List<ProdutoCarrinho> listaCarrinho;
    public AdapterCarrinho adapterCarrinho;
    private DatabaseReference dbRef;
    private Double totalCarrinho = 0.0;
    private TextView txtTotal;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        /*Configura toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Carrinho");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        txtTotal = findViewById(R.id.txtTotal);
        txtTotal.setText(String.valueOf(totalCarrinho));

        rvCarrinho = findViewById(R.id.rvCarrinho);
        listaCarrinho = new ArrayList<>();
        rvCarrinho.setHasFixedSize(true);
        rvCarrinho.setLayoutManager(new LinearLayoutManager(this));
        adapterCarrinho = new AdapterCarrinho(listaCarrinho, this);
        rvCarrinho.setAdapter(adapterCarrinho);

        carregaCarrinho();

        btnAtualiza = findViewById(R.id.btnAtualiza);
        b = findViewById(R.id.btnAtualiza);
        b.setVisibility(View.GONE);
        btnAtualiza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizaCarrinho();
            }
        });



        btnFinaliza = findViewById(R.id.btnFinaliza);
        btnFinaliza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(totalCarrinho != 0.0){
                    Intent intent = new Intent(getBaseContext(), PagamentoActivity.class);
                    intent.putExtra("TOTAL_CARRINHO", String.valueOf(totalCarrinho));
                    startActivity(intent);
                }
            }
        });

    }

    public void carregaCarrinho(){
        listaCarrinho.clear();
        totalCarrinho = 0.00;
        user = UsuarioFirebase.getUsuarioAtual();
        dbRef = ConfiguracaoFireBase.getFirebase().child("usuarios").child(user.getUid()).child("carrinho");
        dbRef.addListenerForSingleValueEvent(valueEventListener);
    }

    public void atualizaCarrinho(){
        listaCarrinho.clear();
        adapterCarrinho.notifyDataSetChanged();
        totalCarrinho = 0.0;
        FirebaseUser user = UsuarioFirebase.getUsuarioAtual();
        dbRef = ConfiguracaoFireBase.getFirebase().child("usuarios").child(user.getUid()).child("carrinho");
        dbRef.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            listaCarrinho.clear();
            if(dataSnapshot.exists()){
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ProdutoCarrinho pCarrinho = ds.getValue(ProdutoCarrinho.class);
                    totalCarrinho += Double.parseDouble(pCarrinho.getPreco());
                    listaCarrinho.add(pCarrinho);
                }
                adapterCarrinho.notifyDataSetChanged();

            }
            Double d = totalCarrinho;
            String text = Double.toString(Math.abs(d));
            int integerPlaces = text.indexOf('.');
            int decimalPlaces = text.length() - integerPlaces - 1;
            if(decimalPlaces == 1){
                String preco = totalCarrinho + "0";
                txtTotal.setText(preco);
            } else {
                String preco = String.valueOf(totalCarrinho);
                txtTotal.setText(preco);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
