package com.quizgame.quizgame.User.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.quizgame.quizgame.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Tambah_Foto extends AppCompatActivity {

    private CircleImageView img;
    private Button btn_gantiFoto, btn_uploadFoto;
    private DatabaseReference dbUsers;
    private StorageReference storageFoto;
    private StorageTask mUploadTask;
    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String urlFoto, UID, loadFoto;
    private FirebaseAuth userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_foto);

        getID();

        userAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = userAuth.getCurrentUser();
        UID = currentUser.getUid();

        dbUsers = FirebaseDatabase
                .getInstance("https://quizgame-2b068-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users");

        storageFoto = FirebaseStorage.getInstance().getReference();

        loadImage();

        btn_gantiFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFoto(view);
            }
        });

        btn_uploadFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageUri != null){
                    uploadFile(new CallbackFoto() {
                        @Override
                        public void onCallback(String URL) {
                            dbUsers.child(UID).child("foto").setValue(urlFoto);
                            Toast.makeText(Tambah_Foto.this, "Foto berhasil diupload", Toast.LENGTH_SHORT).show();
                            mImageUri = null;
                        }
                    });
                } else {
                    Toast.makeText(Tambah_Foto.this, "Pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getID(){
        img = findViewById(R.id.img);
        btn_gantiFoto = findViewById(R.id.btn_gantFoto);
        btn_uploadFoto = findViewById(R.id.btn_uploadFoto);
    }

    public void uploadFoto(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri);
            Glide.with(getApplicationContext()).load(mImageUri).into(img);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(CallbackFoto callbackFoto) {
        if (mImageUri != null) {
            StorageReference fileReference = storageFoto.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urlFoto = uri.toString();
                                    callbackFoto.onCallback(urlFoto);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Tambah_Foto.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            urlFoto = "Tidak ada";
            callbackFoto.onCallback(urlFoto);
        }
    }

    public interface CallbackFoto{
        void onCallback(String URL);
    }

    private void loadImage(){
        dbUsers.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadFoto = snapshot.child("foto").getValue().toString();
                if (loadFoto.equals("empty")){
                    Glide.with(getApplicationContext())
                            .load("https://firebasestorage.googleapis.com/v0/b/quizgame-2b068.appspot.com/o/man.png?alt=media&token=4c9e26c9-d3f1-4923-9a06-5832b14a312c")
                            .into(img);
                }else{
                    Glide.with(getApplicationContext()).load(loadFoto).into(img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}