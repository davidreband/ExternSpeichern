package com.example.administrator.externspeichern;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private  static  final  int PERMISSIONS_REGUEST_WRITE_EXTERNAL_STORAGE  = 1;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textview);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
                    requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REGUEST_WRITE_EXTERNAL_STORAGE);
            }else{
                doIt();
            }
        }else{
           doIt();
        }

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == PERMISSIONS_REGUEST_WRITE_EXTERNAL_STORAGE &&
                (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){

        }
    }

    private void doIt() {
        textView.setText(String.format("Medium kann%s entfernt werden \n",
                Environment.isExternalStorageRemovable()
                        ? "" : " nicht"));
        final String state = Environment.getExternalStorageState();
        final boolean canRead;
        final boolean canWrite;

        switch (state) {
            case Environment.MEDIA_MOUNTED:
                canRead = true;
                canWrite = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                canRead = true;
                canWrite = false;
                break;
            default:
                canRead = false;
                canWrite = false;
        }
        textView.append(String.format("Lesen ist%s möglich\n\n",
                canRead ? "" : " nicht", canWrite ? "" : " nicht"));

        File dirBase = Environment.getExternalStorageDirectory();

        textView.append(String.format("Path: %s\n",
                dirBase.getAbsolutePath()));

        File dirAppBase = new File(dirBase.getAbsolutePath()
                + File.separator
                + "Android" + File.separator + "data" + File.separator
                + getClass().getPackage().getName() + File.separator
                + "files");

        if (!dirAppBase.mkdirs()) {
            textView.append(String.format("alle Unterverzeichnisse " +
                            "von %s schon vorhanden\n\n",
                    dirAppBase.getAbsolutePath()));
        }

        File f1 = getExternalFilesDir(null);

        if (f1 != null) {
            textView.append(String.format("getExternalFilesDir(null): %s\n\n",
                    f1.getAbsolutePath()));
        }


        // app-spezifisches Verzeichnis für Bilder erfragen
        File f2 = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (f2 != null) {
            textView.append(String.format("getExternalFilesDir(Environment" +
                            ".DIRECTORY_PICTURES): %s\n\n",
                    f2.getAbsolutePath()));
        }

        File dirPublicPictures = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);


        if (!dirPublicPictures.mkdirs()) {
            textView.append(String.format(
                    "alle Unterverzeichnisse von %s schon vorhanden\n\n",
                    dirPublicPictures.getAbsolutePath()));
        }
// Grafik erzeugen und speichern
        File file = new File(dirPublicPictures, "grafik.png");
        try (FileOutputStream fos =  new FileOutputStream(file)) {
            saveBitmap(fos);
        } catch (IOException e) {
            Log.e(TAG, "new FileOutputStream()", e);
        }

    }

    private void saveBitmap(OutputStream out){
        int w = 100;
        int h = 100;
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bm);
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);
        c.drawRect(0, 0, w - 1, h - 1, paint);
        paint.setColor(Color.BLUE);
        c.drawLine(0, 0, w - 1, h - 1, paint);
        c.drawLine(0, h - 1, w - 1, 0, paint);
        paint.setColor(Color.BLACK);
        c.drawText("Hallo Android!", w / 2, h / 2, paint);
// und speichern
        bm.compress(Bitmap.CompressFormat.PNG, 100, out);
    }
}
