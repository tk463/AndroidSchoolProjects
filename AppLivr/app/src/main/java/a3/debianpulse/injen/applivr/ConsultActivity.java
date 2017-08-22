package a3.debianpulse.injen.applivr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import a3.debianpulse.injen.applivr.Data.Livraison;
import a3.debianpulse.injen.applivr.Data.LivraisonDAO;
import a3.debianpulse.injen.applivr.Data.LoginRDAO;


public class ConsultActivity extends Activity {
    private String login;
    private boolean afterCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consult);
        login = (String) getIntent().getExtras().get("login");

        LoginRDAO loginRDAO = new LoginRDAO(getApplicationContext());
        try {

            loginRDAO.open();
            if (!loginRDAO.checkLogin(getIntent().getExtras().getInt("id"),login)) {
                loginRDAO.close();
                Intent toLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(toLogin);
            }
            else
                loginRDAO.close();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        afterCreate = true;

        List<Livraison> modeleLivraison = null;
        LivraisonDAO datasource = new LivraisonDAO(getApplicationContext());
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        modeleLivraison = datasource.getListeLivraison();
        Toast.makeText(getApplicationContext(), modeleLivraison.toString(),Toast.LENGTH_SHORT).show();
        datasource.close();

        ListView vueLivraison = (ListView) findViewById(R.id.listViewConsult);
        ArrayAdapter<Livraison> controleurLivraison = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, modeleLivraison);
        vueLivraison.setAdapter(controleurLivraison);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!afterCreate) {
            LoginRDAO loginRDAO = new LoginRDAO(getApplicationContext());
            try {
                loginRDAO.open();
                if (!loginRDAO.checkLogin(getIntent().getExtras().getInt("id"), login)) {
                    System.out.println("-> to login please");
                    loginRDAO.close();
                    Intent toLogin = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(toLogin);
                } else
                    loginRDAO.close();
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
            }
        }
        else
            afterCreate = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_consult, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_choose) {
            Intent toMain = new Intent(getApplicationContext(), MainActivity.class);
            toMain.putExtra("login",login);
            toMain.putExtra("id", getIntent().getExtras().getInt("id"));
            startActivity(toMain);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
