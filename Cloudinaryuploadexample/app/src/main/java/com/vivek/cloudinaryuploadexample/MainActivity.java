package com.vivek.cloudinaryuploadexample;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "## Upload ##";

    private Button button;
    private ImageView imageview;
    private Uri imagepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.button);
        imageview=findViewById(R.id.imageView);

        initconfig();

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestpermission();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaManager.get().upload(imagepath).callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG,"onstart : " + "started");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d(TAG,"onProgress: " + "uploading...");
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Log.d(TAG,"-----UPLOAD SUCCESS-----");
                        Log.d(TAG,"Version: " + "v"+resultData.get("version"));
                        Log.d(TAG,"File name: " + resultData.get("public_id")+"."+resultData.get("format"));
                        Log.d(TAG,"Image Link for API: " + "v"+resultData.get("version")+"/"+resultData.get("public_id")+"."+resultData.get("format"));
                        Log.d(TAG,"URL: " + resultData.get("secure_url"));
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.d(TAG,"onError: " + error);
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.d(TAG, " onReschedule: " + error);
                    }
                }).dispatch();
            }
        });
    }

    public void initconfig(){
        Map config = new HashMap();
        config.put("cloud_name", "CloudName");
        config.put("api_key","ApiKey");
        config.put("api_secret","ApiSecret");
        config.put("secure", true);
        MediaManager.init(this, config);
    }

    private void requestpermission(){
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED)
        {
            selectImage();
        }else{
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            },1);
        }
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launchsomeactivity.launch(intent);
    }

    ActivityResultLauncher<Intent> launchsomeactivity
            = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
            result -> {
                        if(result.getResultCode() == Activity.RESULT_OK)
                        {
                            Intent data = result.getData();
                            if(data!=null && data.getData()!= null){
                                imagepath = data.getData();
                                Bitmap selectedimagebitmap = null;
                                try{
                                    selectedimagebitmap = MediaStore.Images.Media.getBitmap(
                                            this.getContentResolver(),imagepath
                                    );
                                }catch(IOException e){
                                    e.printStackTrace();
                                }
                                imageview.setImageBitmap(selectedimagebitmap);
                            }
                        }
            }
    );
}