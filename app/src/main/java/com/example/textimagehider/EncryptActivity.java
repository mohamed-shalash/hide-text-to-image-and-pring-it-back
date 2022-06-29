package com.example.textimagehider;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EncryptActivity extends AppCompatActivity {
    ImageView im;
    EditText text,name;
    Button encrypt,save;
    LinearLayout ll1,ll2;
    FloatingActionButton fab;
    BitmapDrawable drawable;
    Bitmap bitmap;
    String imagestring,savePath="/document/image/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        im=findViewById(R.id.encrypt_imageView);
        text=findViewById(R.id.encrypt_tv_text);
        name=findViewById(R.id.encrypt_name_tosave);
        encrypt=findViewById(R.id.encrypt_encrypt);
        save=findViewById(R.id.encrypt_save);
        ll1=findViewById(R.id.encrypt_ll1);
        ll2=findViewById(R.id.encrypt_ll2);
        fab=findViewById(R.id.encrypt_back);

        ll2.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);

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
                            //savePath =uri.getPath();
                            System.out.println(uri.getPath());
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

        encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawable = (BitmapDrawable) im.getDrawable();
                bitmap=drawable.getBitmap();
                if(text.getText().toString()!=null) {
                    imagestring = getstringimage(bitmap);

                    Python py = Python.getInstance();
                    PyObject pyobj = py.getModule("py_image_text");

                    PyObject obj = pyobj.callAttr("main", imagestring, text.getText().toString());
                    String str = obj.toString();
                    byte[] data = android.util.Base64.decode(str, Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

                    im.setImageBitmap(bmp);
                    Toast.makeText(getBaseContext(), "Done", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getBaseContext(), "please enter your text", Toast.LENGTH_LONG).show();
                }
                ll2.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                ll1.setVisibility(View.GONE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_image(bitmap);


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
        bitmap.compress(Bitmap.CompressFormat.PNG,0,baos);
        byte[] imagebyte =baos.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return encodedImage;
    }
    private void save_image(Bitmap bitmap){
        Bitmap bitmap2 = bitmap;

        // Save image to gallery
        String savedImageURL = MediaStore.Images.Media.insertImage(
                getContentResolver(), bitmap2,
                "Birdaya" + ".png", "drawing");/*(
                getContentResolver(),
                bitmap2,
                "Bird",
                "Image of bird"
        );*/

        // Parse the gallery image url to uri
        Uri savedImageURI = Uri.parse(savedImageURL);

        Toast.makeText(getBaseContext(),"Image saved to gallery.\n" + savedImageURL,Toast.LENGTH_SHORT).show();
    }

    public void saveBitmap(Bitmap bitmap) {
        if(bitmap==null){
            return;
        }
        File imagePath1 = new File(android.os.Environment.DIRECTORY_DCIM,"screenshot.jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath1);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    private static String getGalleryPath() {
        return (Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/");
    }
    /*public Bitmap takeScreenshot() {
        View rootView = getView();
        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache(true);
        Bitmap b1 = Bitmap.createBitmap(rootView.getDrawingCache(true));
        rootView.setDrawingCacheEnabled(false); // clear drawing cache
        return b1;
    }*/
}