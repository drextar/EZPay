package com.fatecscs.ezpay.Fragment;


        import android.Manifest;
        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.os.Build;
        import android.os.Bundle;

        import androidx.annotation.NonNull;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;
        import androidx.fragment.app.Fragment;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.provider.MediaStore;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.fatecscs.ezpay.Activity.CarrinhoActivity;
        import com.fatecscs.ezpay.Adapter.AdapterBusca;
        import com.fatecscs.ezpay.Helper.ConfiguracaoFireBase;
        import com.fatecscs.ezpay.Model.Produto;
        import com.fatecscs.ezpay.R;
        import com.google.android.material.floatingactionbutton.FloatingActionButton;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.Query;
        import com.google.firebase.database.ValueEventListener;
        import com.google.zxing.Result;
        import com.google.zxing.integration.android.IntentIntegrator;
        import com.google.zxing.integration.android.IntentResult;

        import java.util.ArrayList;
        import java.util.List;

        import static android.Manifest.permission.CAMERA;

/**
 * A simple {@link Fragment} subclass.
 */
public class CodigoBarrasFragment extends Fragment {
    private TextView resultado;
    private ImageView camera;
    private List<Produto> listaProduto;
    private DatabaseReference produtosRef;
    private AdapterBusca adapterBusca;
    private RecyclerView rvPesquisa;
    private FloatingActionButton carrinho;


    public CodigoBarrasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_codigo_barras, container, false);
        carrinho = view.findViewById(R.id.fabCarrinho);

        carrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CarrinhoActivity.class));
            }
        });

        listaProduto = new ArrayList<>();
        produtosRef = ConfiguracaoFireBase.getFirebase().child("produtos");

        //Recycler View
        rvPesquisa = view.findViewById(R.id.rvPesquisa);
        rvPesquisa.setHasFixedSize(true);
        rvPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterBusca = new AdapterBusca(listaProduto, getActivity());
        rvPesquisa.setAdapter(adapterBusca);

        camera = view.findViewById(R.id.scanCamera);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });
        return view;
    }

    public void scan(){
        IntentIntegrator intent = IntentIntegrator.forSupportFragment(CodigoBarrasFragment.this);
        intent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intent.setPrompt("CÓDIGO DE BARRAS");
        intent.setCameraId(0);
        intent.setBeepEnabled(false);
        intent.setBarcodeImageEnabled(false);
        intent.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        
        if(result != null){
            if(result.getContents() != null){
                pesquisarProduto(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    public void pesquisarProduto(String busca){
        //limpar lista
        listaProduto.clear();

        Query query = produtosRef.orderByChild("codigoBarras").startAt(busca).endAt(busca + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //limpar lista
                listaProduto.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren())  {
                    listaProduto.add(ds.getValue(Produto.class));
                }
                adapterBusca.notifyDataSetChanged();
                if(listaProduto.isEmpty()){
                    Toast.makeText(getContext(), "Produto não encontrado!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
