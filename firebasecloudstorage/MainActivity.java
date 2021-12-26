package com.example.firebasecloudstorage;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    Button btnUpload;
    ImageView imgMountains;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference mountainsRef,riversRef;
    UploadTask uploadTask;

    private MyLifecycleObserver mObserver;

//    ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgMountains=findViewById(R.id.imgMainMountains);
        storage=FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        mObserver=new MyLifecycleObserver(this.getActivityResultRegistry());
        getLifecycle().addObserver(mObserver);

//        mGetContent =registerForActivityResult(
//                new ActivityResultContracts.GetContent(),
//                new ActivityResultCallback<Uri>() {
//                    @Override
//                    public void onActivityResult(Uri result) {
//
//                    }
//                }
//        );
    }
    @Override
    public void onResume(){
        super.onResume();
        File file;
        InputStream inputStream=null;
        Uri uri=SharingData.uri;
        if(SharingData.uri!=null)
        {
            SharingData.uri=null;
            file=new File(uri.getPath());
            try {
                inputStream=new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            riversRef=storageRef.child("river.jpg");
            uploadTask=riversRef.putStream(inputStream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            } ).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            })
            ;
        }
    }
    public void onFailure(@NonNull Exception exception){
        //Manejar uploads fallidos
    }
    public void onSuccess(@NonNull Exception exception){
        //Manejar uploads fallidos
    }
    public void onClickUploadByByteArray(View v){

        mountainsRef = storageRef.child("mountains.jpg");
        //StorageReference mountainsImageRef =storageRef.child("imager/mountains.ref");

        imgMountains.setDrawingCacheEnabled(true);
        imgMountains.buildDrawingCache();
        Bitmap bitmap=((BitmapDrawable)imgMountains.getDrawable()).getBitmap();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data=baos.toByteArray();

        uploadTask=mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Manejar errores
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageMetadata MD=taskSnapshot.getMetadata();//Contains data as size, content.type,etc
            }
        });
    }

    public void onClickUploadByStream (View v){
//        mGetContent.launch("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //Verifica permisos para Android 6.0+
            int permissionCheck = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.i("Mensaje", "No se tiene permiso para leer.");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
            } else {
                Log.i("Mensaje", "Se tiene permiso para leer!");
            }
        }
        mObserver.selectImage();
    }
}