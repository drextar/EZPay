package com.fatecscs.ezpay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.fatecscs.ezpay.Fragment.BuscaFragment;
import com.fatecscs.ezpay.Fragment.CodigoBarrasFragment;
import com.fatecscs.ezpay.Fragment.PerfilFragment;
import com.fatecscs.ezpay.Helper.ConfiguracaoFireBase;
import com.fatecscs.ezpay.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Configura toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("EZPay");
        setSupportActionBar(toolbar);

        //configuracoes de objetos
        auth = ConfiguracaoFireBase.getFirebaseAutenticacao();

        //configuracao bottom navigation view
        configuraBottomNavigationView();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPager, new CodigoBarrasFragment()).commit();
    }

    private void configuraBottomNavigationView(){

        BottomNavigationViewEx bnve = findViewById(R.id.bottomNavigation);

        bnve.enableAnimation(true);
        bnve.enableItemShiftingMode(false);
        bnve.enableShiftingMode(false);
        bnve.setTextVisibility(false);

        //habilita navegação
        habilitaNavegacao(bnve);

    }

    private void habilitaNavegacao(BottomNavigationViewEx bnve){
        bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch(menuItem.getItemId()){
                    case R.id.ic_scan:
                        fragmentTransaction.replace(R.id.viewPager, new CodigoBarrasFragment()).commit();
                        return true;
                    case R.id.ic_search:
                        fragmentTransaction.replace(R.id.viewPager, new BuscaFragment()).commit();
                        return true;
                    case R.id.ic_profile:
                        fragmentTransaction.replace(R.id.viewPager, new PerfilFragment()).commit();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_sair:
                deslogarUsuario();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deslogarUsuario(){
        try{
            auth.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
