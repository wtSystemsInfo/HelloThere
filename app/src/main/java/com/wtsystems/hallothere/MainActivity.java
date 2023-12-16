package com.wtsystems.hallothere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.wtsystems.hallothere.FireBase.FireBaseConfig;
import com.wtsystems.hallothere.Fragment.ContatosFragment;
import com.wtsystems.hallothere.Fragment.ConversasFragment;
import com.wtsystems.hallothere.Helper.UserFireBase;
import com.wtsystems.hallothere.Model.Usuario;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbarMain;

    private FirebaseAuth autenticacao;

    private ViewPager viewPager;
    private SmartTabLayout viewPagerTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(Color.parseColor("#AFAFAF"));
        toolbarMain = findViewById(R.id.toolbarMain);
        toolbarMain.setTitle("HelloThere");
        toolbarMain.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(toolbarMain);
        autenticacao = FireBaseConfig.getFireBaseAuth();

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                        .add("CONVERSAS", ConversasFragment.class)
                        .add("CONTATOS", ContatosFragment.class)
                        .create()
        );

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getTitle().toString()){

            case "Sair":
                logOut();
                finish();
                break;

            case "Configurações":
                goConfig();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logOut(){

        try{
            autenticacao.signOut();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void goConfig(){
        Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume(){
        super.onResume();
        autenticacao = FireBaseConfig.getFireBaseAuth();
        if(autenticacao != null){
            FirebaseUser currentUser = autenticacao.getCurrentUser();
            if(currentUser == null) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        }

    }
}