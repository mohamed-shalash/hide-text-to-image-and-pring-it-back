package com.example.textimagehider;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Deencrypt_activity extends AppCompatActivity {
    ImageView im;
    TextView text;
    Button decrypt;
    FloatingActionButton fab;
    BitmapDrawable drawable;
    Bitmap bitmap;
    String imagestring;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deencrypt);

        im=findViewById(R.id.deencrypt_iv);
        text=findViewById(R.id.deencrypt_tv);
        decrypt=findViewById(R.id.deencrypt_unencrypt);
        fab=findViewById(R.id.deencrypt_fab);

        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }



        ActivityResultLauncher<Intent> activityResult =registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        im.setImageURI(result.getData().getData());
                        Uri uri =result.getData().getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), uri);
                        }catch (IOException e) {
                            // TODO Handle the exception
                        }
                    }
                }
        );

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResult.launch(intent);
            }
        });

        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawable = (BitmapDrawable) im.getDrawable();
                bitmap=drawable.getBitmap();
                imagestring = getstringimage(bitmap);

                Python py = Python.getInstance();
                PyObject pyobj = py.getModule("py_text_image_2");

                PyObject obj = pyobj.callAttr("main",imagestring);
                String str = obj.toString();

                text.setText(str);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private String getstringimage(Bitmap bitmap){
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] imagebyte =baos.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return encodedImage;
    }
}