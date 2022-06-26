package com.proj.safe_chat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.proj.safe_chat.tools.KeysJsonI;
import com.proj.safe_chat.tools.MyCameraApi;
import com.proj.safe_chat.tools.MySocket;
import com.proj.safe_chat.tools.MySocketSingleton;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

//מסך ערכית הפרופיל
public class EditProfileActivity extends AppCompatActivity implements KeysJsonI {
    private final int PICK_IMAGE_REQUEST = 2004;
    private CircleImageView profileImage;
    private MyCameraApi cameraApi;
    private EditText editName, editEmail;
    private Button btnEdit;
    private int i = 0;
    private MySocket mySocket;
    private final String TAG = getClass().getName();

    //נקרא כאשר האקטיביטי נוצר
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        profileImage = findViewById(R.id.profile_image);
        editName = findViewById(R.id.edit_text_name);
        editEmail = findViewById(R.id.edit_text_email);
        btnEdit = findViewById(R.id.btn_edit);


        Log.d("TAG", "help11111: ");
        mySocket = MySocketSingleton.getMySocket();
        mySocket.setContext(this);
        editName.setText(getIntent().getExtras().getString("name"));
        editEmail.setText(getIntent().getExtras().getString("email"));

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileImage();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEdit();
            }
        });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mySocket.getProfileImage(getIntent().getExtras().getString("uid"), new MySocket.OnReceiveImage() {
                    @Override
                    public void OnReceive(Bitmap bitmap, String uid) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                profileImage.setImageBitmap(bitmap);
                            }
                        });
                    }
                });
            }
        });thread.start();
    }

    //נקרא כאשר המשתמש לוחץ כי הוא מאשר לערוך את הפרופיל עם הפרטים ששם
    private void onEdit() {
        if(editName.getText().toString().trim().length() <=0
                || editEmail.getText().toString().trim().length() <=0){
            Toast.makeText(this, "Field cannot be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        String base64Image = "Empty";
        if(profileImage.getDrawable()!=null) {
            Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            base64Image = android.util.Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        }


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TYPE_KEY, EDIT_VALUE);
            jsonObject.put(UID_KEY, getIntent().getExtras().getString("uid"));
            jsonObject.put(EMAIL_KEY, editEmail.getText().toString().trim());
            jsonObject.put(NAME_KEY, editName.getText().toString());
            jsonObject.put(IMAGE_KEY, base64Image);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "jsonObject.toString(): "+jsonObject.toString());
                    mySocket.send(jsonObject.toString().getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        //finish();
    }

    //נקרא כאשר נלחץ התמונת פרופיל לעריכה
    private void onProfileImage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Choose");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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

    //כאשר אושר אחד ההרשאות או שונה
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

    //מקבל result מאקטיביטים אחרים
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            Log.d("TAG", "yeye1: "+requestCode);
            switch (requestCode) {
                case PICK_IMAGE_REQUEST:
                    Uri mImageUri = null;
                    if(data!=null) {
                        mImageUri = data.getData();
                    }
                        if(mImageUri == null){
                            Log.d(TAG, "cameraApi: "+cameraApi);
                            Bitmap photo = BitmapFactory.decodeFile(cameraApi.getCurrentPhotoPath());
                            mImageUri = cameraApi.getImageUri(getApplicationContext(), photo);
                        }
                        String nns = "sample"+i;
                        nns += ".jpg";
                        UCrop.of(mImageUri, Uri.fromFile(new File(getCacheDir(), nns)))
                                .withAspectRatio(1, 1)
                                .start(this);
                        i++;
                    break;
                case UCrop.REQUEST_CROP:
                    final Uri resultUri = UCrop.getOutput(data);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap,520,520, true);
                        profileImage.setImageBitmap(bitmapResized);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("TAG", "yeye3: "+resultUri);
                    break;
            }
        }
    }
}