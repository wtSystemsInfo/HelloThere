package com.wtsystems.hallothere.FireBase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FireBaseConfig {
    private static DatabaseReference database;
    private static FirebaseAuth auth;

    private static StorageReference storage;

    //Retornar a instância do FireBase
    public static DatabaseReference getFireBaseDatabase(){
        if(database == null){
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    //Retornar a instância do Auth do FireBase
    public static FirebaseAuth getFireBaseAuth(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static StorageReference getFireBaseStorage(){
        if(storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }

        return storage;
    }

}
