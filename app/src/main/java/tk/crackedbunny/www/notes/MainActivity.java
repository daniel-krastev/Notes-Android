package tk.crackedbunny.www.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    public static final String DEBUGTAG = "DKK";
    public static final String TEXTFILE = "textFile.txt";
    public static final String FILESAVED = "FileSaved";
    public static final String RESET_PASSPOINTS = "RessetPasspoints";
    private File imageFile;
    private static final int PHOTO_TAKEN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addSaveButtonListener();
        addLockButtonListener();
        if (getPreferences(MODE_PRIVATE).getBoolean(FILESAVED, false)) {
            loadSavedFile();
        } else {
            Toast.makeText(MainActivity.this, R.string.no_previous_file, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_passpoints_reset:
                Intent i = new Intent(this, ImageActivity.class);
                i.putExtra(RESET_PASSPOINTS, true);
                startActivity(i);
                finish();
                return true;
//            case R.id.item_upload_from_camera:
//                uploadFromCamera();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    private void uploadFromCamera() {
//        File picturesDirectory;
//        if(isExternalStorageWritable()) {
//            picturesDirectory = Environment.
//                    getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        } else {
//            picturesDirectory = getFilesDir();
//        }
//        imageFile = new File(picturesDirectory, "passspoints_image");
//        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
//        startActivityForResult(i, PHOTO_TAKEN);
//    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch(requestCode) {
//            case PHOTO_TAKEN:
//                Bitmap photo = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//                try {
//                    photo = rotateImageIfRequired(photo, Uri.fromFile(imageFile));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if(photo != null) {
//                    ImageView imageView = (ImageView)findViewById(R.id.touch_image);
//                    imageView.setImageBitmap(photo);
//                    Intent i = new Intent(this, ImageActivity.class);
//                    i.putExtra(RESET_PASSPOINTS, true);
//                    startActivity(i);
//                    finish();
//                } else {
//                    Toast.makeText(this, "Unable to read photo file", Toast.LENGTH_LONG).
//                            show();
//                }
//                break;
//        }
//    }

//    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
//        ExifInterface ei = new ExifInterface(selectedImage.getPath());
//        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//        switch (orientation) {
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                return rotateImage(img, 90);
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                return rotateImage(img, 180);
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                return rotateImage(img, 270);
//            default:
//                return img;
//        }
//    }

//

    private void loadSavedFile() {
        try {
            FileInputStream fis = openFileInput(TEXTFILE);
            BufferedReader bR = new BufferedReader(new InputStreamReader(new DataInputStream(fis)));
            EditText text = (EditText) findViewById(R.id.text);
            String line;
            while ((line = bR.readLine()) != null) {
                text.append(line);
                text.append("\n");
            }
            fis.close();
        } catch (Exception e) {

        }
    }

    private void addSaveButtonListener() {
        Button saveBtn = (Button) findViewById(R.id.save);
        saveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String txt = ((EditText) findViewById(R.id.text)).getText().toString();
                try {
                    FileOutputStream fos = openFileOutput(TEXTFILE, MODE_PRIVATE);
                    fos.write(txt.getBytes());
                    fos.close();
                    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(FILESAVED, true);
                    editor.commit();
                } catch (Exception e) {
                }
            }
        });
    }

    private void addLockButtonListener() {
        Button lB = (Button) findViewById(R.id.lock);
        lB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, ImageActivity.class);
                    startActivity(i);
                    finish();
                }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
