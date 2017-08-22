package a3.debianpulse.injen.applivr.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Guillaume on 11/12/2014.
 */
public class LoginRDAO {
    private SQLiteDatabase database;
    private LoginRSQLiteHelper dbHelper;
    private String[] listeColonnes = {LoginRSQLiteHelper.COLONNE_ID, LoginRSQLiteHelper.COLONNE_LOGIN, LoginRSQLiteHelper.COLONNE_DATETIME};

    public LoginRDAO(Context context) {
        dbHelper = new LoginRSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void saveLoginR(int id, String login) {
        ContentValues values = new ContentValues();
        values.put(LoginRSQLiteHelper.COLONNE_ID, id);
        values.put(LoginRSQLiteHelper.COLONNE_LOGIN, login);
        values.put(LoginRSQLiteHelper.COLONNE_DATETIME, getDateTime());

        long insertId = database.insert(LoginRSQLiteHelper.TABLE_LoginR, null, values);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getDateTimeMaxDuration(String dateInput) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateFormat.parse(dateInput));
        cal.add(Calendar.HOUR_OF_DAY, 8);
        return dateFormat.format(cal.getTime());
    }

    private boolean dateTimeCheck(String dateDB) throws ParseException {
        if (getDateTime().compareTo(getDateTimeMaxDuration(dateDB)) < 0)
            return true;
        else
            return false;
    }

    public boolean checkLogin(int id, String login) throws ParseException {
        Cursor c = database.query(LoginRSQLiteHelper.TABLE_LoginR, listeColonnes, LoginRSQLiteHelper.COLONNE_LOGIN+" =?", new String[] {login}, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if (c.getInt(0) == id && dateTimeCheck(c.getString(2))) {
                c.close();
                return true;
            }
            c.moveToNext();
        }
        c.close();
        return false;
    }

    public void empty() {
        database.delete(LoginRSQLiteHelper.TABLE_LoginR,null,null);
    }
}
