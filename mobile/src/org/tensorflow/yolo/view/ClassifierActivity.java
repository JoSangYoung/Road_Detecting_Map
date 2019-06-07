package org.tensorflow.yolo.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.widget.Toast;

import org.tensorflow.yolo.R;
import org.tensorflow.yolo.TensorFlowImageRecognizer;
import org.tensorflow.yolo.ThresholdController;
import org.tensorflow.yolo.model.JsonObject;
import org.tensorflow.yolo.model.NetworkServicer;
import org.tensorflow.yolo.model.Recognition;
import org.tensorflow.yolo.util.ClassAttrProvider;
import org.tensorflow.yolo.util.ImageUtils;
import org.tensorflow.yolo.view.components.BorderedText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import static org.tensorflow.yolo.Config.INPUT_SIZE;
import static org.tensorflow.yolo.Config.LOGGING_TAG;

/**
 * Classifier activity class
 * Modified by Zoltan Szabo
 */
public class ClassifierActivity extends TextToSpeechActivity implements OnImageAvailableListener {
    private boolean MAINTAIN_ASPECT = true;
    private float TEXT_SIZE_DIP = 10;

    private TensorFlowImageRecognizer recognizer;
    private Integer sensorOrientation;
    private int previewWidth = 0;
    private int previewHeight = 0;
    private Bitmap croppedBitmap = null;
    private boolean computing = false;
    private Matrix frameToCropTransform;

    private OverlayView overlayView;
    private BorderedText borderedText;
    private long lastProcessingTimeMs;

    NetworkServicer jsonSender;
    double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //GPS가 켜져있는지 체크
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //GPS 설정화면으로 이동
            Toast.makeText(getApplicationContext(), "GPS 기능이 필요합니다 GPS를 켜주십시오.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
        }

        // 수동으로 위치 구하기
        String locationProvider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "GPS 기능에 권한 문제가 있어서 이전 화면으로 돌아갑니다.", Toast.LENGTH_LONG).show();
            finish();
        }

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                lng = location.getLongitude();
                lat = location.getLatitude();
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);

        jsonSender = new NetworkServicer();
    }

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        recognizer = TensorFlowImageRecognizer.create(getAssets());

        overlayView = (OverlayView) findViewById(R.id.overlay);
        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        final int screenOrientation = getWindowManager().getDefaultDisplay().getRotation();

        Log.i(LOGGING_TAG, String.format("Sensor orientation: %d, Screen orientation: %d",
                rotation, screenOrientation));

        Log.i(LOGGING_TAG, String.format("Initializing at size %dx%d", previewWidth, previewHeight));

        croppedBitmap = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Config.ARGB_8888);

        frameToCropTransform = ImageUtils.getTransformationMatrix(previewWidth, previewHeight,
                INPUT_SIZE, INPUT_SIZE, 0, MAINTAIN_ASPECT);
        frameToCropTransform.invert(new Matrix());

        addCallback((final Canvas canvas) -> renderAdditionalInformation(canvas));
    }

    @Override
    public void onImageAvailable(final ImageReader reader) {
        Image image = null;
        try {
            image = reader.acquireLatestImage();

            if (image == null) {
                return;
            }

            if (computing) {
                image.close();
                return;
            }

            computing = true;
            fillCroppedBitmap(image);
            image.close();
        } catch (final Exception ex) {
            if (image != null) {
                image.close();
            }
            Log.e(LOGGING_TAG, ex.getMessage());
        }

        runInBackground(() -> {
            final long startTime = SystemClock.uptimeMillis();
            final List<Recognition> results = recognizer.recognizeImage(croppedBitmap);
            lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
            overlayView.setResults(results);
            speak(results);
            requestRender();
            if(!results.isEmpty()){
                //create a file to write bitmap data
                File f = new File(getApplicationContext().getCacheDir(), "tempCrackImg");
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Convert bitmap to byte array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                croppedBitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close(); // hmmmm...
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    //fos.close();
                }
                JsonObject jsonObject = new JsonObject("detect", "gps", lng, lat, f);
                String result = jsonSender.requestJsonObject(jsonObject);
                Log.i("Test"," " + lat+"    " + lng);


                Toast.makeText(getApplicationContext(), "서버에 크랙을 전송하였습니다.", Toast.LENGTH_LONG).show();
            }
            computing = false;
        });
    }

    private void fillCroppedBitmap(final Image image) {
            Bitmap rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
            rgbFrameBitmap.setPixels(ImageUtils.convertYUVToARGB(image, previewWidth, previewHeight),
                    0, previewWidth, 0, 0, previewWidth, previewHeight);
            new Canvas(croppedBitmap).drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recognizer != null) {
            recognizer.close();
        }
    }

    private void renderAdditionalInformation(final Canvas canvas) {
        final Vector<String> lines = new Vector<String>();
        if (recognizer != null) {
            for (String line : recognizer.getStatString().split("\n")) {
                lines.add(line);
            }
        }

        lines.add("자동으로 크랙을 검출하여 확인합니다.");
        lines.add("프레임: " + previewWidth + "x" + previewHeight);
        lines.add("뷰크기: " + canvas.getWidth() + "x" + canvas.getHeight());
        if (lat ==0. && lng == 0.)
            lines.add("LatLng: Error");
        else
            lines.add("LatLng: " + lat + ", " + lng);
        lines.add("검증소요: " + lastProcessingTimeMs + "ms");

        borderedText.drawLines(canvas, 10, 10, lines);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        //imageFilePath = image.getAbsolutePath();
        return image;
    }
}
