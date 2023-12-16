package com.wtsystems.hallothere.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.wtsystems.hallothere.FireBase.FireBaseConfig;
import com.wtsystems.hallothere.Helper.UserFireBase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String userID;
    private String nome;
    private String email;
    private String senha;
    private String picture;

    private String token;


    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void salvarUsuario(){
        DatabaseReference firebaseRef = FireBaseConfig.getFireBaseDatabase();
        DatabaseReference user = firebaseRef.child("users").child(getUserID());
        user.setValue(this);
    }

    public void updateusuario(){
        String idUser = UserFireBase.getIdUser();
        DatabaseReference firebase = FireBaseConfig.getFireBaseDatabase();
        DatabaseReference userRef = firebase.child("users")
                .child(idUser);


        Map <String, Object> vlrUser = convertToMap();
        userRef.updateChildren(vlrUser);

    }

    @Exclude
    public Map<String, Object> convertToMap(){
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", getEmail());
        userMap.put("nome", getNome());
        userMap.put("picture", getPicture());
        if(getToken()!= null){
            userMap.put("token", getToken());
        }


        return userMap;
    }
}
