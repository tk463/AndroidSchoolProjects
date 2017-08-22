package a3.debianpulse.injen.applivr.Data;

import java.util.StringTokenizer;

/**
 * Created by Guillaume on 04/12/2014.
 */
public class Livraison {
    private int numLivraison;
    private String adresse;

    public Livraison() {
    }

    public Livraison(int numLivraison, String adresse) {
        this.numLivraison = numLivraison;
        this.adresse = adresse;
    }

    public Livraison(String input) {
        StringTokenizer st = new StringTokenizer(input.replace("]"," "), "|");
        this.numLivraison = Integer.parseInt(st.nextToken().trim());
        this.adresse = st.nextToken().trim();
    }

    public int getNumLivraison() {
        return numLivraison;
    }

    public void setNumLivraison(int numLivraison) {
        this.numLivraison = numLivraison;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @Override
    public String toString() {
        return numLivraison + " / " + adresse;
    }
}
