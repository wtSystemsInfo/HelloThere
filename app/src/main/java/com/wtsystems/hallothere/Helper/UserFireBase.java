package com.wtsystems.hallothere.Helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.wtsystems.hallothere.FireBase.FireBaseConfig;
import com.wtsystems.hallothere.Model.Usuario;

public class UserFireBase {


    public static String getIdUser(){
        String email;

        String idEmail;

        FirebaseAuth usuario = FireBaseConfig.getFireBaseAuth();
        email = usuario.getCurrentUser().getEmail();
        idEmail = Base64Custom.code64Base(email);

        return idEmail;

    }


    public static FirebaseUser getActualUser(){
        FirebaseAuth user = FireBaseConfig.getFireBaseAuth();


        return user.getCurrentUser();

    }


    public static boolean updateNameUser(String nome){

        FirebaseUser user = getActualUser();

        try{
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar nome do perfil");
                    }
                }
            });

            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public static boolean updatePictureUser(Uri url){

        FirebaseUser user = getActualUser();

        try{
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar foto do perfil");
                    }
                }
            });

            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public static Usuario getUserLogIn(){
        FirebaseUser user = getActualUser();

        Usuario userLogIn = new Usuario();
        userLogIn.setEmail(user.getEmail());
        userLogIn.setNome(user.getDisplayName());

        if(user.getPhotoUrl() == null){
            userLogIn.setPicture("");
        }else{
            userLogIn.setPicture(user.getPhotoUrl().toString());
        }

        return userLogIn;
    }

}
