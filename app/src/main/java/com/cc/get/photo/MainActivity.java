package com.cc.get.photo;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends Activity {

    private String tag = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int options = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(options);

        requestPermissions(new String[]{
                Manifest.permission.CAMERA
        }, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initView();
    }

    private void initView(){
        setContentView(R.layout.main);
        final CameraView cameraView = findViewById(R.id.cameraView);
        findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Camera camera = cameraView.getCamera();
                if (camera != null) {
                    camera.takePicture(null, null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(final byte[] data, Camera camera) {
                            if (data == null) {
                                return;
                            }
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    Bitmap fullBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    if (fullBitmap == null) {
                                        return;
                                    }
                                    FileOutputStream fileOutputStream = null;
                                    try {
                                        fileOutputStream = new FileOutputStream(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "takePicture.jpg");
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                    }
                                    if (fileOutputStream != null) {
                                        fullBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                        try {
                                            fileOutputStream.close();
                                        } catch (Throwable e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    Log.v(tag, "saved");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            cameraView.startPreview();
                                        }
                                    });
                                }
                            }.start();
                        }
                    });
                }
            }
        });
    }

}
