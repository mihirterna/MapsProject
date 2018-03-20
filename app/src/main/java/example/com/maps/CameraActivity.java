package example.com.maps;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import example.com.maps.Utils.GPSTracker;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    private Button upload;
    private ImageView mImageView;
    File mediaFile;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Location");
    StorageReference  storageReference = FirebaseStorage.getInstance().getReference("Location");
    Button getLL;
    EditText latitudeLL,longitudeLL,describe,city;
    String lati,longi;
    // GPSTracker class
    GPSTracker gps;
    Context mContext;
    public double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

            latitudeLL = findViewById(R.id.latitude);
        longitudeLL = findViewById(R.id.longitude);
        mImageView = findViewById(R.id.imageView1);
        describe = findViewById(R.id.etdescription);
        upload=findViewById(R.id.tvUpload);
        mContext = this;
        city=findViewById(R.id.city);

        ImageView closecamera = findViewById(R.id.ivClose);

        closecamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Log.i(TAG, "IOException" + ex.getMessage());
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });

        uploadToFB();
    }

    private void uploadToFB() {
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((FirebaseAuth.getInstance().getCurrentUser() != null) || ((lati != null) && (longi!=null)))
                {
                    //storageReference=storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    //databaseReference=databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Map<String,String> map = new HashMap<>();
                    map.put("lati",lati);
                    map.put("longi",longi);
                    map.put("describe",describe.getText().toString());
                    map.put("city",city.getText().toString());
                    Log.e(TAG,FirebaseAuth.getInstance().getCurrentUser().getUid());
                    map.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    String key =databaseReference.child(city.getText().toString()).push().getKey();
                    databaseReference.child(city.getText().toString()).child(key).setValue(map);
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
                    db.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Queries").child(key).setValue(map);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    final ProgressDialog progressDialog = new ProgressDialog(mContext);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    // TODO: 20-03-2018 Progress dialouge should not disappear after touching screen
                    storageReference.child((city.getText().toString())).child(key).putBytes(data)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        progressDialog.dismiss();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setProgress((int) progress);
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
                }
                else{
                    Toast.makeText(mContext, "Please Login Properly", Toast.LENGTH_SHORT).show();
                }
                }
        });

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_pirates:
                if (checked){
                    if (ContextCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION)

                            != PackageManager.PERMISSION_GRANTED

                            && ActivityCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION)

                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CameraActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                    } else {
                        Toast.makeText(mContext, "You need have granted permission", Toast.LENGTH_SHORT).show();
                        gps = new GPSTracker(mContext, CameraActivity.this);

                        // Check if GPS enabled

                        if (gps.canGetLocation()) {

                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();

                            // \n is for new line

                            Toast.makeText(getApplicationContext(),
                                    "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                            lati = Double.toString(latitude);
                            longi = Double.toString(longitude);
                            latitudeLL.setText(lati);
                            longitudeLL.setText(longi);
                        } else {
                            // Can't get location.

                            // GPS or network is not enabled.

                            // Ask user to enable GPS/network in settings.

                            gps.showSettingsAlert();
                        }
                    }
                }
                break;
            case R.id.radio_ninjas:
                if (checked) {
                    Intent i = new Intent(CameraActivity.this, MapsActivity.class);
                    startActivityForResult(i, 2);
                }
                break;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                mImageView.setImageBitmap(mImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void getPhotoFromCamera() {
        MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);
        if (!marshMallowPermission.checkPermissionForCamera()) {
            marshMallowPermission.requestPermissionForCamera();
        } else {
            if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                marshMallowPermission.requestPermissionForExternalStorage();
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
                try {
                    mediaFile = File.createTempFile(
                            "IMG_" + timeStamp,
                            ".jpg");
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mediaFile));
                //    startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
