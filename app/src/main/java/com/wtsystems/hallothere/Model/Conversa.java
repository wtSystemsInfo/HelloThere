package com.wtsystems.hallothere.Model;

import com.google.firebase.database.DatabaseReference;
import com.wtsystems.hallothere.FireBase.FireBaseConfig;

public class Conversa {

    private String idUser;

    private String idDest;

    private String lastMsg;

    private Usuario userExib;

    public Conversa() {
    }

    public void salvar(){
        DatabaseReference dataBaseRef = FireBaseConfig.getFireBaseDatabase();
        DatabaseReference conversaRef = dataBaseRef.child("Conversas");
        if(this.getIdUser() != null || this.getIdDest() != null){
            conversaRef.child(this.getIdUser())
                    .child(this.getIdDest())
                    .setValue(this);
        }
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdDest() {
        return idDest;
    }

    public void setIdDest(String idDest) {
        this.idDest = idDest;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public Usuario getUserExib() {
        return userExib;
    }

    public void setUserExib(Usuario userExib) {
        this.userExib = userExib;
    }
}
