package tk.crackedbunny.www.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class ImageActivity extends AppCompatActivity implements PointCollectorListener {
    private PointCollector pointC = new PointCollector();
    private Database db = new Database(this);
    private  static final String PASSWORD_SET = "PASSWORD_SET";
    private static final int POINT_CLOSENESS = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Boolean resetPasspoints = extras.getBoolean(MainActivity.RESET_PASSPOINTS);
            if(resetPasspoints) {
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit().clear();
                editor.commit();
            }
        }
        setContentView(R.layout.activity_image);

        touchListener();

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Boolean passpointsSet = prefs.getBoolean(PASSWORD_SET, false);

        if(!passpointsSet) {
            showSetPasspointPrompt();
        }
        pointC.setListener(this);
    }

    private void showSetPasspointPrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.dialog_buttontext, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // just remove the dialog
            }
        });

        builder.setTitle(R.string.dialog_title);
        builder.setMessage(R.string.dialog_msg);

        AlertDialog dlg = builder.create();
        dlg.show();
    }

    private void touchListener() {
        ImageView img = (ImageView)findViewById(R.id.touch_image);
        img.setOnTouchListener(pointC);
    }

    public void pointsCollected(final List<Point> points) {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        Boolean passpointsSet = prefs.getBoolean(PASSWORD_SET, false);
        if(!passpointsSet) {
            savePasspoints(points);
        } else {
            verifyPasspoints(points);
        }
    }

    private void savePasspoints(final List<Point> points) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage(R.string.storing_data);

        final AlertDialog dlg = b.create();
        dlg.show();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                db.storePoints(points);
                return null;
            }

            @Override
            protected void onPostExecute(Void r) {
                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(PASSWORD_SET, true);
                editor.commit();

                super.onPostExecute(r);
                dlg.dismiss();
                pointC.clear();
            }
        };

        task.execute();
    }

    private void verifyPasspoints(final List<Point> touchedP) {
        AlertDialog.Builder dB =  new AlertDialog.Builder(this);
        dB.setMessage(R.string.check_message);
        final AlertDialog dlg = dB.create();
        dlg.show();

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                List<Point> savedP = db.getPoints();
                if(savedP.size() != PointCollector.NUM_POINTS ||
                       touchedP.size() != PointCollector.NUM_POINTS ) {
                    return false;
                }

                for(int i = 0; i < PointCollector.NUM_POINTS; i++) {
                    Point savedPoint = savedP.get(i);
                    Point touchedPoint = touchedP.get(i);

                    int xDiff = savedPoint.x - touchedPoint.x;
                    int yDiff = savedPoint.y - touchedPoint.y;
                    int distSquared = xDiff*xDiff + yDiff*yDiff;
                    if(distSquared > POINT_CLOSENESS*POINT_CLOSENESS) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean pass) {
                super.onPostExecute(pass);
                dlg.dismiss();
                pointC.clear();

                if(pass) {
                    Intent i = new Intent(ImageActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(ImageActivity.this,
                            R.string.acc_denied, Toast.LENGTH_LONG).show();
                }
            }
        };
        task.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
