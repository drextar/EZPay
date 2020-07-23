package com.fatecscs.ezpay.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fatecscs.ezpay.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecuperarActivity extends AppCompatActivity {
    private TextView email;
    private Button recupera;
    private FirebaseAuth auth;
    private String emailRecupera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        /*Configura toolbar*/
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Esqueci a senha");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.txtEmail);
        recupera = findViewById(R.id.btnEnviar);
        emailRecupera = email.getText().toString();
        recupera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidEmail(email.getText().toString())){
                    auth.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RecuperarActivity.this, "Um email foi enviado para " + email.getText().toString(), Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                }
                            });

                } else {
                    Log.i("Email", email.getText().toString());
                    Toast.makeText(RecuperarActivity.this, "Email inválido!", Toast.LENGTH_SHORT).show();
                    email.setText("");
                }
            }
        });

    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
