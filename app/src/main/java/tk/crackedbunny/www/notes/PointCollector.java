package tk.crackedbunny.www.notes;

import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rico on 22/12/2016.
 */

public class PointCollector implements View.OnTouchListener {
    private List<Point> listP;
    private PointCollectorListener listener;
    public static final int NUM_POINTS = 4;

    public PointCollector() {
        listP = new ArrayList<>();
    }

    public void setListener(PointCollectorListener listener) {
        this.listener = listener;
    }

    public boolean onTouch(View v, MotionEvent event) {
        int x = Math.round(event.getX());
        int y = Math.round(event.getY());

        Log.d(MainActivity.DEBUGTAG, String.format("Coordinates x,y: %d, %d", x,y));
        listP.add(new Point(x,y));
        if(listP.size() == NUM_POINTS) {
            if(listener != null) {
                listener.pointsCollected(listP);
            }
        }
        return false;
    }

    public void clear() {
        listP.clear();
    }
}
