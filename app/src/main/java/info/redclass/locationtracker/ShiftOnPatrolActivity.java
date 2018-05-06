package info.redclass.locationtracker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class ShiftOnPatrolActivity extends AppCompatActivity implements LocationAssistant.Listener {

    private static final String TAG = "ShiftOnPatrolActivity";

    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    private LocationAssistant assistant;
    Location mCurrentLocation;
    String mLastUpdateTime;
    Date mLastUpdateTimeObj;

    private String mCurrentShiftGuardCode = "";
    private Boolean bLocationAccuracyHasGoneBelow20Once = false;
    private PowerManager.WakeLock mOnPatrolWakeLock;

    TextView tvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_on_patrol);

        tvLocation = (TextView) findViewById(R.id.tvLocation);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (assistant == null)
        {
            assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 1000, false);
            assistant.start();
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            mOnPatrolWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyWakelockTag");
            mOnPatrolWakeLock.acquire();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //assistant.stop();
    }

    @Override
    protected void onDestroy() {
        assistant.stop();
        mOnPatrolWakeLock.release();

        super.onDestroy();
    }

    @Override
    public void onNeedLocationPermission() {
        assistant.requestLocationPermission();

        assistant.stop();
        assistant.start();
    }

    @Override
    public void onExplainLocationPermission() {

    }

    @Override
    public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNeedLocationSettingsChange() {

    }

    @Override
    public void onFallBackToSystemSettings(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNewLocationAvailable(Location location) {
        mCurrentLocation = location;
        try {
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            mLastUpdateTimeObj = new Date();

            if (location.getAccuracy() <= 20)
            {
                bLocationAccuracyHasGoneBelow20Once = true;
            }

            updateUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {

    }



    private void updateUI() throws IOException {
        Log.d(TAG, "UI update initiated .............");

        if (null != mCurrentLocation) {

            String deviceID = Build.SERIAL;
            String guardCode = getIntent().getStringExtra("GUARDCODE");
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());
            String accuracy = String.valueOf(mCurrentLocation.getAccuracy());
            String eventType = "LOCATION";

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            String formattedDate = df.format(c.getTime());


            String urlLocation = "http://www.redclass.info/Event/SubmitEventData/" + formattedDate + "/" + deviceID + "/" + guardCode + "/" + lng + "/" + lat + "/" + accuracy + "/" + eventType;


            //String urlLocation = "http://redclass.info/DeviceData/SubmitDeviceLocationData/" + deviceID + "/" + String.valueOf(mCurrentLocation.getLatitude()) + "/" + String.valueOf(mCurrentLocation.getLongitude()) + "/" + String.valueOf(mCurrentLocation.getBearing()) + "/" + accuracy + "/" + formattedDate;
            urlLocation = urlLocation.replace(" ", "%20");
            urlLocation = urlLocation.replace(":", "!");
            urlLocation = urlLocation.replace("http!", "http:");

            //URLEncoder.encode(urlLocation, urlLocation);

            if (bLocationAccuracyHasGoneBelow20Once == true || (mCurrentLocation.hasAccuracy() && mCurrentLocation.getAccuracy() <= 20)) {
                new SendLocationDataToServerTask().execute(urlLocation);


                if (tvLocation != null) {

                    //String lat = String.valueOf(mCurrentLocation.getLatitude());
                    //String lng = String.valueOf(mCurrentLocation.getLongitude());
                    tvLocation.setText("At Time: " + mLastUpdateTime + "\n" +
                            "Serial: " + deviceID + "\n" +
                            "Latitude: " + lat + "\n" +
                            "Longitude: " + lng + "\n" +
                            "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                            "Provider: " + mCurrentLocation.getProvider());
                }
            }else
            {
                tvLocation.setText("Not sending location due to insufficient accuracy.");
            }
        } else {
            Log.d(TAG, "location is null ...............");
        }

    }

    public void OnFinishPatrolButtonClicked(View view)
    {
        finish();
    }

    public void OnTakePhotoButtonClicked(View view)
    {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "info.redclass.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);
            try {
            File f = new File(mCurrentPhotoPath);
            byte[] fileData = new byte[(int) f.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(f));
            dis.readFully(fileData);
            dis.close();


            // Resize the image
                File fsmall = null;
                Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath());
// original measurements
                int origWidth = b.getWidth();
                int origHeight = b.getHeight();

                final int destWidth = 400;//or the width you need

                byte[] imageBytes = null;

                if(origWidth > destWidth){
                    // picture is wider than we want it, we calculate its target height
                    int destHeight = origHeight/( origWidth / destWidth ) ;
                    // we create an scaled bitmap so it reduces the image, not just trim it
                    Bitmap b2 = Bitmap.createScaledBitmap(b, destWidth, destHeight, false);
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    // compress to the format you want, JPEG, PNG...
                    // 70 is the 0-100 quality percentage
                    b2.compress(Bitmap.CompressFormat.JPEG,70 , outStream);
                    // we save the file, at least until we have made use of it
                    //fsmall = new File(Environment.getExternalStorageDirectory() + File.separator + "upload.jpg");
                    fsmall = new File(mCurrentPhotoPath);

                    fsmall.createNewFile();
                    //write the bytes in file
                    FileOutputStream fo = new FileOutputStream(fsmall);

                    imageBytes = outStream.toByteArray();

                    fo.write(imageBytes);
                    // remember close de FileOutput
                    fo.close();
                }



            // Send fileData!

                String deviceID = Build.SERIAL;
                String guardCode = getIntent().getStringExtra("GUARDCODE");
                String lat = String.valueOf(mCurrentLocation.getLatitude());
                String lng = String.valueOf(mCurrentLocation.getLongitude());
                String accuracy = String.valueOf(mCurrentLocation.getAccuracy());
                String eventType = "PHOTO";

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar c = Calendar.getInstance();
                String formattedDate = df.format(c.getTime());


                String urlLocation = "http://www.redclass.info/Event/SubmitEventData/" + formattedDate + "/" + deviceID + "/" + guardCode + "/" + lng + "/" + lat + "/" + accuracy + "/" + eventType;

                //String urlLocation = "http://redclass.info/ShiftData/SubmitShiftPhotoData/" + deviceID + "/" + formattedDate;
                urlLocation = urlLocation.replace(" ", "%20");
                urlLocation = urlLocation.replace(":", "!");
                urlLocation = urlLocation.replace("http!", "http:");
                urlLocation = urlLocation.replace("localhost!", "localhost:");

                try {

                    //String filedataString = new String(fileData);
                    //String returned = new SendLocationDataToServerTask().execute(urlLocation, filedataString).get();

                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();

                    FileInputStream fi = new FileInputStream(fsmall);
                    FileBody bin = new FileBody(fsmall, "image/jpeg");
                    //params.put("photo", fsmall, "image/jpg");


                    //ByteArrayEntity entity = new ByteArrayEntity(imageBytes);
                    //entity.setContentType("image/jpeg");

                    String asBase64 = Base64.encodeToString(imageBytes, 0, imageBytes.length, Base64.DEFAULT);
                    asBase64 = asBase64.replace("\r\n", ""); //This fixes everything

                    StringEntity entity = new StringEntity(asBase64);


                    //client.post(urlLocation, params, new AsyncHttpResponseHandler() {
                    client.post(null, urlLocation, entity, "image/jpeg", new AsyncHttpResponseHandler() {

                        @Override
                        public void onStart() {
                            // called before request is started
                            Toast.makeText(getApplicationContext(), "Uploading Photo...", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getApplicationContext(), "Photo Upload Failure: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onRetry(int retryNo) {
                            // called when request is retried
                            Toast.makeText(getApplicationContext(), "Photo Upload Retry", Toast.LENGTH_LONG).show();
                        }
                    });


                    //Toast.makeText(this, returned, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void SendFile()
    {
        //AsyncHttpClient
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
