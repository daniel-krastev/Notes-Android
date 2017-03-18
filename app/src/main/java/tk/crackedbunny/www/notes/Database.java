package tk.crackedbunny.www.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rico on 24/12/2016.
 */

public class Database extends SQLiteOpenHelper {
    private static final String POINTS_TABLE = "Points";
    private static final String COL_ID = "ID";
    private static final String COL_X = "x";
    private static final String COL_Y = "y";

    public Database(Context context) {
        super(context,"notes.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = String.format("CREATE TABLE %s (%s integer primary key, " +
                 "%s integer not null, %s integer not null);",
                POINTS_TABLE, COL_ID, COL_X, COL_Y);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void storePoints(List<Point> listP) {
        SQLiteDatabase sqlD = getWritableDatabase();
        sqlD.delete(POINTS_TABLE, null, null);
        int i = 0;
        for(Point p : listP) {
            ContentValues values = new ContentValues();
            values.put(COL_ID, i);
            values.put(COL_X, p.x);
            values.put(COL_Y, p.y);
            sqlD.insert(POINTS_TABLE, null, values);
            i++;
        }
        sqlD.close();
    }

    public List<Point> getPoints() {
        List<Point> points = new ArrayList<>();
        SQLiteDatabase sqlD = getReadableDatabase();
        String sql = String.format("SELECT * FROM %s ORDER BY %s", POINTS_TABLE, COL_ID);
        Cursor c = sqlD.rawQuery(sql, null);
        while(c.moveToNext()) {
            int x = c.getInt(1);
            int y = c.getInt(2);

            points.add(new Point(x,y));
        }
        sqlD.close();
        return points;
    }


}
