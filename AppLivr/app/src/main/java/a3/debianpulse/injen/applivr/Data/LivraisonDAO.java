package a3.debianpulse.injen.applivr.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guillaume on 04/12/2014.
 */
public class LivraisonDAO {
    private SQLiteDatabase database;
    private LivraisonsSQLiteHelper dbHelper;
    private String[] listeColonnes = {LivraisonsSQLiteHelper.COLONNE_ID, LivraisonsSQLiteHelper.COLONNE_ADRESSE};

    public LivraisonDAO(Context context) {
        dbHelper = new LivraisonsSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Livraison creerLivraison(int numLivraison, String adresse) {
        ContentValues values = new ContentValues();
        values.put(LivraisonsSQLiteHelper.COLONNE_ID, numLivraison);
        values.put(LivraisonsSQLiteHelper.COLONNE_ADRESSE, adresse);

        long insertId = database.insert(LivraisonsSQLiteHelper.TABLE_LIVRAISONS, null, values);
        Cursor c = database.rawQuery("select * from "+
                LivraisonsSQLiteHelper.TABLE_LIVRAISONS + " where " +
                LivraisonsSQLiteHelper.COLONNE_ID + " = " + Long.toString(insertId), null);
        c.moveToFirst();
        Livraison tempLivraison = cursorToLivraison(c);
        c.close();
        return tempLivraison;
    }

    public void retirerLivraison(Livraison livraison) {
        int id = livraison.getNumLivraison();
        database.delete(LivraisonsSQLiteHelper.TABLE_LIVRAISONS,
                LivraisonsSQLiteHelper.COLONNE_ID + " = " + Integer.toString(id), null);
    }

    public void viderLivraison() {
        database.delete(LivraisonsSQLiteHelper.TABLE_LIVRAISONS,null,null);
    }

    public List<Livraison> getListeLivraison() {
        List<Livraison> listLivraisons = new ArrayList<>();

        Cursor c = database.query(LivraisonsSQLiteHelper.TABLE_LIVRAISONS, listeColonnes, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Livraison livraison = cursorToLivraison(c);
            listLivraisons.add(livraison);
            c.moveToNext();
        }
        c.close();
        return listLivraisons;
    }

    public Livraison insererLivraison(Livraison livraison) {
        ContentValues values = new ContentValues();
        values.put(LivraisonsSQLiteHelper.COLONNE_ID, livraison.getNumLivraison());
        values.put(LivraisonsSQLiteHelper.COLONNE_ADRESSE, livraison.getAdresse());

        long insertId = database.insert(LivraisonsSQLiteHelper.TABLE_LIVRAISONS, null, values);
//        Cursor c = database.rawQuery("select * from "+
//                LivraisonsSQLiteHelper.TABLE_LIVRAISONS + " where " +
//                LivraisonsSQLiteHelper.COLONNE_ID + " = " + Long.toString(insertId), null);
//        c.moveToFirst();
//        Livraison tempLivraison = cursorToLivraison(c);
//        c.close();
        return livraison;
    }

    private Livraison cursorToLivraison(Cursor c) {
        Livraison livraison = new Livraison();
        livraison.setNumLivraison(c.getInt(0));
        livraison.setAdresse(c.getString(1));
        return livraison;
    }
}
