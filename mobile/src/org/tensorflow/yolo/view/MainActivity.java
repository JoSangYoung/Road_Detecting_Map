package org.tensorflow.yolo.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.tensorflow.yolo.R;
import org.tensorflow.yolo.ThresholdController;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button autoBtn = (Button)findViewById(R.id.autoBtn);
        autoBtn.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, ClassifierActivity.class);
            startActivity(intent);
        });
        Button manualBtn = (Button)findViewById(R.id.manualBtn);
        manualBtn.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, ManualActivity.class);
            startActivity(intent);
        });
        Button exitBtn = (Button)findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener((View v) -> {
            finish();
        });
        Button settingBtn = (Button)findViewById(R.id.setButton);
        settingBtn.setOnClickListener((View v)->{
            EditText thresholdTxt = (EditText)findViewById(R.id.thresholdTxt);
            ThresholdController.THRESHOLD = Float.valueOf(thresholdTxt.getText().toString());
            Toast.makeText(MainActivity.this, "임계값이 " + ThresholdController.THRESHOLD + "로 변경되었습니다.", Toast.LENGTH_SHORT).show();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            }
            else{
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

}
