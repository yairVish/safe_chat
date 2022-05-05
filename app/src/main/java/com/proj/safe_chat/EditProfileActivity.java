package com.proj.safe_chat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.oginotihiro.cropview.CropUtil;
import com.oginotihiro.cropview.CropView;
import com.proj.safe_chat.tools.MyCameraApi;
import com.yalantis.ucrop.UCrop;

import java.io.File;


public class EditProfileActivity extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 2004;
    private ImageView profileImage;
    private MyCameraApi cameraApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        profileImage = findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileImage();
            }
        });
    }

    private void onProfileImage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Choose");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //openCamera(REQUEST_CODE2);
                        /*if (ContextCompat
                                .checkSelfPermission(EditAddCard.this, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_DENIED) {
                            ActivityCompat
                                    .requestPermissions(EditAddCard.this,
                                            new String[]{Manifest.permission.CAMERA}, 123);
                        } else {
                            //dispatchTakePictureIntent(REQUEST_CODE);
                        }*/
                        cameraApi = new MyCameraApi(EditProfileActivity.this);
                        cameraApi.film(PICK_IMAGE_REQUEST);
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton(
                "Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (ActivityCompat.checkSelfPermission(EditProfileActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, PICK_IMAGE_REQUEST);
                        }else{
                            //if(ActivityCompat.shouldShowRequestPermissionRationale(AddImageActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                            //}
                            ActivityCompat.requestPermissions(EditProfileActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                        }
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_REQUEST:
                    Uri selectedImageUri = null;
                    if(data != null)
                        selectedImageUri = data.getData();
                    if(selectedImageUri==null) {
                        Bitmap photo = BitmapFactory.decodeFile(cameraApi.getCurrentPhotoPath());
                        // = (Bitmap) data.getExtras().get("data");
                        //selectedImageUri = getImageUri(getApplicationContext(), photo);
                    }
                    if (null != selectedImageUri) {
                        String nns = "sample";
                        nns += ".jpg";
                        UCrop.of(selectedImageUri, Uri.fromFile(new File(getCacheDir(), nns)))
                                .withAspectRatio(1, 1)
                                .start(this);
                    }
                    break;
                case UCrop.REQUEST_CROP:
                    final Uri resultUri = UCrop.getOutput(data);
                    profileImage.setImageURI(resultUri);
                    break;
            }
        }
    }
}