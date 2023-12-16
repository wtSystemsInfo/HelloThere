package com.wtsystems.hallothere.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {
    public static boolean validatePermission(String[] permissions, Activity activity, int requestCode){

        if(Build.VERSION.SDK_INT >= 23){
            List<String> listPermissions = new ArrayList<>();

            //Verificando se a permissão está concedida!
            for(String permission : permissions){
               Boolean checkedPermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
               if(!checkedPermission){
                   listPermissions.add(permission);
               }
            }

            //Se a lista estiver vaizia não precisa pedir permissão
            if(listPermissions.isEmpty()){
                return true;
            }

            String[] newPermissions = new String[listPermissions.size()];
            listPermissions.toArray(newPermissions);

            ActivityCompat.requestPermissions( activity, newPermissions,  requestCode);

        }


        return true;
    }
}
