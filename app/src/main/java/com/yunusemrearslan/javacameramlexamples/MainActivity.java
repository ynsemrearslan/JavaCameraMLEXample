package com.yunusemrearslan.javacameramlexamples;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 2;
    ImageView imageView;
    public Bitmap bitmapImage;
    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=(ImageView) findViewById(R.id.imageView);
        textView=(TextView) findViewById(R.id.textView);
    }

    public void openCamera(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        }
        else{
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==2){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==2&&data!=null&&resultCode==RESULT_OK){
            try {
                bitmapImage=(Bitmap)data.getExtras().get("data");
                imageView.setImageBitmap(bitmapImage);

            }
            catch (Exception e){
                Toast.makeText(this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                Log.v("Hata",e.getLocalizedMessage());
            }
        }
    }
    public void imageTextRecognized(View view){

        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(bitmapImage);
        FirebaseVisionTextRecognizer detector= FirebaseVision.getInstance().getCloudTextRecognizer();
        Task<FirebaseVisionText> result=detector.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                processTxt(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

    }
    private void processTxt(FirebaseVisionText visionText){
        textView.setText("Tıklandı");
        List<FirebaseVisionText.TextBlock> blocks=visionText.getTextBlocks();
        if (blocks==null){
            Toast.makeText(getApplicationContext(),"No Text",Toast.LENGTH_LONG).show();
            return;
        }
        for (FirebaseVisionText.TextBlock block: visionText.getTextBlocks()){
            String text=block.getText();
            Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
            textView.setText(text);
        }

    }
}