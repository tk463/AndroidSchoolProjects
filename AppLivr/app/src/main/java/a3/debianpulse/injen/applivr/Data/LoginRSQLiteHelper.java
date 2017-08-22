package a3.debianpulse.injen.applivr.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Guillaume on 11/12/2014.
 */
public class LoginRSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_LoginR = "LoginRTable";
    public static final String COLONNE_ID = "_id";
    public static final String COLONNE_LOGIN = "login";
    public static final String COLONNE_DATETIME = "date_time";

    private static final String NOM_BDD = "loginr.db";
    private static final int VERSION_BDD = 1;

    public LoginRSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public LoginRSQLiteHelper(Context context) {
        super(context, NOM_BDD, null, VERSION_BDD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_LoginR +
                "("+ COLONNE_ID +" integer primary key, " +
                COLONNE_LOGIN +" text not null, " +
                COLONNE_DATETIME +" text not null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LivraisonsSQLiteHelper.class.getName(),
                "Mise à jour de la version " + oldVersion + " vers " +
                        newVersion + " - les tables précédentes seront effacées");
        db.execSQL("drop table if exists "+ TABLE_LoginR);
        onCreate(db);
    }
}
