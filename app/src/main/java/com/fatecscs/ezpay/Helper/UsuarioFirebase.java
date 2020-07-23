package com.fatecscs.ezpay.Helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fatecscs.ezpay.Model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFireBase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static void atualizaNomeUsuario(String nome){
        try{
            //Usuario logado
            FirebaseUser user = getUsuarioAtual();

            //Configura objeto para alteracao do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar nome de perfil");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Usuario getDadosUsuarioLogado(){
        FirebaseUser fbUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail(fbUser.getEmail());
        usuario.setNome(fbUser.getDisplayName());
        usuario.setId(fbUser.getUid());

        return usuario;
    }
}
