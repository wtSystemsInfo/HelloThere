package com.wtsystems.hallothere;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wtsystems.hallothere.Adapter.MensagemAdapter;
import com.wtsystems.hallothere.FireBase.FireBaseConfig;
import com.wtsystems.hallothere.Helper.Base64Custom;
import com.wtsystems.hallothere.Helper.UserFireBase;
import com.wtsystems.hallothere.Model.Conversa;
import com.wtsystems.hallothere.Model.Mensagem;
import com.wtsystems.hallothere.Model.Notification;
import com.wtsystems.hallothere.Model.NotificationData;
import com.wtsystems.hallothere.Model.Usuario;
import com.wtsystems.hallothere.api.NotificationService;
import com.wtsystems.hallothere.databinding.ActivityChatBinding;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {


    //Variáveis
    private AppBarConfiguration appBarConfiguration;
    private ActivityChatBinding binding;

    private Toolbar toolbarChat;

    private TextView nomeChat;

    private ImageView fotoChat;

    private Usuario userDest;
    private Usuario meUser;

    private String foto;

    private String tokkenDest;

    private EditText mensagem;

    private String mensagemEnviada;

    private FloatingActionButton btnEnviar;

    private String iduserLogin, idUserDest;

    private RecyclerView recyclerMsg;

    private MensagemAdapter msgAdapter;

    private List<Mensagem> listMsg = new ArrayList<>();

    private DatabaseReference dataBase;

    private DatabaseReference msgsRef;

    private ChildEventListener childEventListenerMsg;

    private ImageView btnCamera;

    private static final int SELECAO_GALLERY = 466078721;

    private int optionImage;

    private ActivityResultLauncher<Intent> resultLauncherGallery;

    private StorageReference storage;

    private Retrofit retrofit;

    private String urlBase;

    private DatabaseReference userRef;

    private String fotoContato;

    private String token;


    private static final String[] permissions = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Config Iniciais

        getWindow().setStatusBarColor(Color.parseColor("#AFAFAF"));
        toolbarChat = findViewById(R.id.toolbarChat);
        toolbarChat.setTitle("");
        toolbarChat.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(toolbarChat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nomeChat = findViewById(R.id.txtNomeChat);
        fotoChat = findViewById(R.id.circleImgChat);
        btnEnviar = findViewById(R.id.btnEnviar);
        mensagem = findViewById(R.id.editTextMensagem);
        recyclerMsg = findViewById(R.id.recyclerMensagem);
        btnCamera = findViewById(R.id.img_GalleryMsg);

        //Configuração da incialização do Retrofit
        urlBase = "https://fcm.googleapis.com/fcm/";
        retrofit = new Retrofit.Builder()
                .baseUrl(urlBase)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Recuperando dados do usuário Destinatario para o chat

        meUser =  UserFireBase.getUserLogIn();
        recuperarToken(meUser);


        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            userDest = (Usuario) bundle.getSerializable("chatContato");
            nomeChat.setText(userDest.getNome());
            foto = userDest.getPicture();

            idUserDest = Base64Custom.code64Base(userDest.getEmail());
            userRef = FireBaseConfig.getFireBaseDatabase().child("users").child(idUserDest).child("picture");

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // O dado existe no caminho especificado
                         fotoContato = snapshot.getValue(String.class);

                        Uri url = Uri.parse(fotoContato);
                        Glide.with(ChatActivity.this).load(url).into(fotoChat);


                    } else {
                        fotoChat.setImageResource(R.mipmap.ic_logo);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            userRef = FireBaseConfig.getFireBaseDatabase().child("users").child(idUserDest).child("token");

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // O dado existe no caminho especificado
                        tokkenDest = snapshot.getValue(String.class);

                    } else {
                        tokkenDest = null;

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




        }

        //Recuperando os dados do usuário Logado

        iduserLogin = UserFireBase.getIdUser();



        //Configuração do adapter
        msgAdapter = new MensagemAdapter(listMsg, getApplicationContext());


        //Configurando o Recycler View das Mensagens
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMsg.setLayoutManager(layoutManager);
        recyclerMsg.setHasFixedSize(true);
        recyclerMsg.setAdapter(msgAdapter);


        dataBase = FireBaseConfig.getFireBaseDatabase();
        msgsRef = dataBase.child("Mensagens")
                .child(iduserLogin)
                .child(idUserDest);


        //Configurando o Storage
        storage = FireBaseConfig.getFireBaseStorage();


        //Configurar o resultLaucher da galeria
        resultLauncherGallery = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // O código para processar o resultado aqui
                    }
                });


        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mensagemEnviada = mensagem.getText().toString();

                if(!mensagemEnviada.isEmpty()){
                    Mensagem msg = new Mensagem();
                    msg.setIdUser(iduserLogin);
                    msg.setMensagem(mensagemEnviada);


                    //Salvar Mensagem - Remetente
                    salvarMsg(iduserLogin, idUserDest, msg);

                    //Salvar Mensagem - Destinatario
                    salvarMsg(idUserDest, iduserLogin, msg);

                    //Salvar Conversa
                    salvarConversa(msg);

                    mensagem.setText("");

                    enviarNotificacao(msg);
                }else{
                    Toast.makeText(ChatActivity.this, "Campo mensagem em branco!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (galleryIntent.resolveActivity(getPackageManager()) != null){
                    optionImage = SELECAO_GALLERY;
                    resultLauncherGallery.launch(galleryIntent);
                }
            }
        });

    }


    //Salvando mensagens
    private void salvarMsg(String idRemetente, String idDest, Mensagem mensagem){

        DatabaseReference database = FireBaseConfig.getFireBaseDatabase();
        DatabaseReference msgRef = database.child("Mensagens");
        msgRef
                .child(idRemetente)
                .child(idDest)
                .push()
                .setValue(mensagem);
    }


    //Recuperando as mensagens
    private void getMensagens(){

        listMsg.clear();
        childEventListenerMsg = msgsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensagem mensagem = snapshot.getValue(Mensagem.class);
                listMsg.add(mensagem);
                msgAdapter.notifyDataSetChanged();
                recyclerMsg.smoothScrollToPosition(listMsg.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //Verificando os dados recuperados da galeria

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap image = null;


            try{
                Uri localImage = data.getData();
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), localImage);
                image = fixOrientation(image, localImage);

            } catch(Exception e){
                e.printStackTrace();
            }
            if (image != null) {
                //Recuperando dados para o Firebase
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] dataImage = baos.toByteArray();

                //Criar nome da imagem
                String imgName = UUID.randomUUID().toString();

                //Cofigurar a referência do firebase
                StorageReference imgRef = storage.child("image")
                        .child("pictures")
                        .child(iduserLogin)
                        .child(imgName);

                UploadTask uploadTask = imgRef.putBytes(dataImage);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this,
                                "Falha ao carregar a imagem!",
                                Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String downloadUrl = task.getResult().toString();
                                Mensagem msg = new Mensagem();
                                msg.setIdUser(iduserLogin);
                                msg.setMensagem("picture.jpeg");
                                msg.setImage(downloadUrl);

                                //Salvar mensagem para o remetente
                                salvarMsg(iduserLogin, idUserDest, msg);

                                //Salvar mensagem para o destinatario
                                salvarMsg(idUserDest, iduserLogin, msg);

                                Toast.makeText(ChatActivity.this,
                                        "Imagem enviada!",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });


            }

        }
    }


    //Corrigir a orientação da Imagem

    private Bitmap fixOrientation(Bitmap bitmap, Uri imageUri) {
        try {
            // Obtenha a rotação da imagem a partir dos metadados da galeria
            String[] projection = {MediaStore.Images.Media.ORIENTATION};
            Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);
            int rotation = 0;
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
                rotation = cursor.getInt(columnIndex);
                cursor.close();
            }

            // Aplique a rotação à imagem
            Matrix matrix = new Matrix();
            matrix.setRotate(rotation);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            return bitmap; // Em caso de erro, retornar a imagem original
        }
    }

    private void salvarConversa(Mensagem msg){
        //salvando conversa para quem está enviando
        Conversa conversaUser = new Conversa();
        conversaUser.setIdUser(iduserLogin);
        conversaUser.setIdDest(idUserDest);
        conversaUser.setLastMsg(msg.getMensagem());
        conversaUser.setUserExib(userDest);
        conversaUser.salvar();
        //salvando conversa para quem está recebendo
        Conversa conversaUser2 = new Conversa();
        conversaUser2.setIdUser(idUserDest);
        conversaUser2.setIdDest(iduserLogin);
        conversaUser2.setLastMsg(msg.getMensagem());
        conversaUser2.setUserExib(meUser);
        conversaUser2.salvar();

    }



    private void enviarNotificacao( Mensagem msg){


        Usuario userLogin = UserFireBase.getUserLogIn();
        //Motando o objeto notificação
        Notification notification = new Notification("Nova Mensagem de " + userLogin.getNome(), msg.getMensagem());
        NotificationData notificationData = new NotificationData(tokkenDest, notification);
        NotificationService service = retrofit.create(NotificationService.class);
        Call<NotificationData> call = service.saveNotification(notificationData);
        call.enqueue(new Callback<NotificationData>() {
            @Override
            public void onResponse(Call<NotificationData> call, Response<NotificationData> response) {
                if(response.isSuccessful()){

                }
            }

            @Override
            public void onFailure(Call<NotificationData> call, Throwable t) {

            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        getMensagens();
    }

    @Override
    protected  void onStop(){
        super.onStop();
        msgsRef.removeEventListener(childEventListenerMsg);
    }

    public void recuperarToken(Usuario usuario) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    token = task.getResult();
                    usuario.setToken(token);
                    usuario.updateusuario();
                }
            }
        });

    }
}