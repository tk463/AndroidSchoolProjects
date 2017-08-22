package a3.debianpulse.injen.applivr.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Guillaume on 04/12/2014.
 */
public class LivraisonsSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_LIVRAISONS = "livraionsToDo";
    public static final String COLONNE_ID = "_id";
    public static final String COLONNE_ADRESSE = "adresse";

    private static final String NOM_BDD = "livraisons.db";
    private static final int VERSION_BDD = 1;

    public LivraisonsSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public LivraisonsSQLiteHelper(Context context) {
        super(context, NOM_BDD, null, VERSION_BDD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_LIVRAISONS +
            "("+ COLONNE_ID +" integer primary key, " +
            COLONNE_ADRESSE +" text not null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LivraisonsSQLiteHelper.class.getName(),
                "Mise à jour de la version "+ oldVersion + " vers " +
                newVersion + " - les tables précédentes seront effacées");
        db.execSQL("drop table if exists "+ TABLE_LIVRAISONS);
        onCreate(db);
    }
}
