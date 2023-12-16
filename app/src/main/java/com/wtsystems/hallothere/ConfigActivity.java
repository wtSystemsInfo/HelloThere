package com.wtsystems.hallothere;

import static com.wtsystems.hallothere.Helper.UserFireBase.getIdUser;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wtsystems.hallothere.FireBase.FireBaseConfig;
import com.wtsystems.hallothere.Helper.Base64Custom;
import com.wtsystems.hallothere.Helper.Permission;
import com.wtsystems.hallothere.Helper.UserFireBase;
import com.wtsystems.hallothere.Model.Usuario;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;


public class ConfigActivity extends AppCompatActivity {

    private Toolbar toolbarConfig;

    private ImageView imgEdit;

    private ImageButton btnCamera, btnGallery;

    private EditText inputName;

    private Button btnNome;

    private ActivityResultLauncher<Intent> resultLauncherCamera;
    private ActivityResultLauncher<Intent> resultLauncherGallery;

    private static final int SELECAO_CAMERA = 2081038118;
    private static final int SELECAO_GALLERY = 466078721;

    private int optionImage;

    private CircleImageView imageViewProfile;

    private StorageReference storageReference;

    private Usuario userLogin;

    private static final String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        getWindow().setStatusBarColor(Color.parseColor("#AFAFAF"));
        toolbarConfig = findViewById(R.id.toolbarConfig);
        toolbarConfig.setTitle("Configurações");
        toolbarConfig.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(toolbarConfig);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgEdit = findViewById(R.id.imgEditNome);
        inputName = findViewById(R.id.editTextNome);
        btnCamera = findViewById(R.id.imgBtnCamera);
        btnGallery = findViewById(R.id.imgBtnGallery);
        imageViewProfile = findViewById(R.id.profile_image);
        btnNome = findViewById(R.id.btnSalvarNome);
        optionImage = 0;

        storageReference = FireBaseConfig.getFireBaseStorage();

        userLogin = UserFireBase.getUserLogIn();

        FirebaseUser user = UserFireBase.getActualUser();
        Uri url = user.getPhotoUrl();


        if(url != null){
            Glide.with(ConfigActivity.this)
                    .load(url)
                    .into(imageViewProfile);
        }else{
            imageViewProfile.setImageResource(R.drawable.vaderperfil);
        }

        inputName.setText(user.getDisplayName());

        Permission.validatePermission(permissions, this, 1);

        resultLauncherCamera = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // O código para processar o resultado aqui
                    }
                });

        resultLauncherGallery = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // O código para processar o resultado aqui
                    }
                });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputName.setEnabled(true);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null){
                    optionImage = SELECAO_CAMERA;
                    resultLauncherCamera.launch(cameraIntent);
                }

            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (galleryIntent.resolveActivity(getPackageManager()) != null){
                    optionImage = SELECAO_GALLERY;
                    resultLauncherGallery.launch(galleryIntent);
                }
            }
        });

        btnNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean returnedValue = UserFireBase.updateNameUser(inputName.getText().toString());
                if(returnedValue){
                    userLogin.setNome(inputName.getText().toString());
                    userLogin.updateusuario();
                    Toast.makeText(ConfigActivity.this, "Nome alterado com sucesso!", Toast.LENGTH_LONG).show();
                    inputName.setEnabled(false);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissionsResult : grantResults){
            if(permissionsResult == PackageManager.PERMISSION_DENIED){
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfigActivity.this);
                builder.setTitle("PERMISSÕES")
                        .setMessage("A Aplicação não funcionará corretamente sem as permissões concedidas!")
                        .setCancelable(false)
                        .setPositiveButton("CONTINUAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap image = null;


            try{
                switch(optionImage){
                    case SELECAO_CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;

                    case SELECAO_GALLERY:
                        Uri localImage = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), localImage);
                        image = fixOrientation(image, localImage);
                        break;
                }

                if(image != null){
                    imageViewProfile.setImageBitmap(image);

                    //Recuperar os dados para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();



                    //Salvar imagem no firebase
                    StorageReference imageRef = storageReference.child("image")
                            .child("profile")
                            .child(UserFireBase.getIdUser())
                            .child("profile.jpeg");

                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfigActivity.this,
                                    "Falha ao carregar a imagem!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ConfigActivity.this,
                                    "Sucesso ao carregar a imagem!",
                                    Toast.LENGTH_LONG).show();

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    updatePicture(url);
                                }
                            });
                        }
                    });
                }

            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    private void updatePicture(Uri url){

        boolean returnData = UserFireBase.updatePictureUser(url);
        if(returnData){
            userLogin.setPicture(url.toString());
            userLogin.updateusuario();
            Toast.makeText(ConfigActivity.this, "Foto salva com sucesso!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(ConfigActivity.this, "Erro ao salvar foto!", Toast.LENGTH_LONG).show();
        }
    }


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


}