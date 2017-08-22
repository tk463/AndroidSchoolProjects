package a3.debianpulse.injen.applivr;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import a3.debianpulse.injen.applivr.Data.Livraison;
import a3.debianpulse.injen.applivr.Data.LivraisonDAO;
import a3.debianpulse.injen.applivr.Data.LoginRDAO;
import a3.debianpulse.injen.applivr.ProtoOERP.AnswerOERP;


public class MainActivity extends Activity {
    private ProgressBar bar;
    private String login;
    List<Livraison> modeleLivraison = new ArrayList<>();
    ArrayAdapter<Livraison> controleurLivraison;
    private boolean afterCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button BGetDelivery = (Button) findViewById(R.id.buttonDeliv);
        BGetDelivery.setOnClickListener(onGetDelivery);
        bar = (ProgressBar) findViewById(R.id.progressBarMain);
        login = getIntent().getExtras().getString("login");

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

        ListView vueLivraison = (ListView) findViewById(R.id.listView);
        controleurLivraison = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, modeleLivraison);
        vueLivraison.setOnItemClickListener(onChooseDelivery);
        vueLivraison.setAdapter(controleurLivraison);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (!afterCreate) {
//            LoginRDAO loginRDAO = new LoginRDAO(getApplicationContext());
//            try {
//                loginRDAO.open();
//                if (!loginRDAO.checkLogin(getIntent().getExtras().getInt("id"), login)) {
//                    System.out.println("-> to login please");
//                    loginRDAO.close();
//                    Intent toLogin = new Intent(getApplicationContext(), LoginActivity.class);
//                    startActivity(toLogin);
//                } else
//                    loginRDAO.close();
//            } catch (SQLException | ParseException e) {
//                e.printStackTrace();
//            }
//        }
//        else
//            afterCreate = false;
    }

    View.OnClickListener onGetDelivery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText ETCriteria = (EditText) findViewById(R.id.criteria);
            if (!ETCriteria.getText().toString().isEmpty()) {
                GetDeliveryCom getDeliveryCom = new GetDeliveryCom(ETCriteria.getText().toString());
                getDeliveryCom.execute();
            }
        }
    };

    AdapterView.OnItemClickListener onChooseDelivery = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ChooseDeliveryCom chooseDeliveryCom = new ChooseDeliveryCom(modeleLivraison.get(position).getNumLivraison());
            chooseDeliveryCom.execute();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_close:
                EditText ETVehi = (EditText) findViewById(R.id.carNumber);
                if (!ETVehi.getText().toString().isEmpty()) {
                    CloseCom closeCom = new CloseCom(Integer.parseInt(ETVehi.getText().toString()));
                    closeCom.execute();
                }
                return true;
            case R.id.action_consult:
                Intent toConsult = new Intent(getApplicationContext(), ConsultActivity.class);
                toConsult.putExtra("login",login);
                toConsult.putExtra("id", getIntent().getExtras().getInt("id"));
                startActivity(toConsult);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetDeliveryCom extends AsyncTask {
        private String criteria;
        private AnswerOERP answer;

        private GetDeliveryCom(String criteria) {
            criteria.replace(" ","$");
            this.criteria = criteria;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            doRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (answer.getAnswer()) {
                controleurLivraison.clear();
                for (int i = 1; i < answer.getCharge().size(); i++) {
                    controleurLivraison.add(new Livraison(answer.getCharge().get(i)));
                }
//                Toast.makeText(getApplicationContext(), answer.getCharge().toString() + " " + answer.getCharge().size(), Toast.LENGTH_LONG).show();
                controleurLivraison.notifyDataSetChanged();
            }
            else
                Toast.makeText(getApplicationContext(), "Pas de livraison ou requete incorrecte", Toast.LENGTH_LONG).show();
            publishProgress(100);
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            bar.setProgress((Integer) values[0]);
        }

        private void doRequest() {
            try {
                Socket socket = new Socket("172.19.50.58",32036);
                String data = AnswerOERP.GETDELIVERY + "$" + criteria + "\n";
                publishProgress(25);

                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.write(data);
                pw.flush();
                publishProgress(50);

                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                data = br.readLine();
                answer = new AnswerOERP(data);
                publishProgress(75);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ChooseDeliveryCom extends AsyncTask {
        private int id;

        private ChooseDeliveryCom(int id) {
            this.id = id;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            bar.setProgress((Integer) values[0]);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            doRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            publishProgress(100);
            Toast.makeText(getApplicationContext(), "Delivery chosen " + id, Toast.LENGTH_SHORT).show();
        }

        private void doRequest() {
            try {
                Socket socket = new Socket("172.19.50.58",32036);
                String data = AnswerOERP.CHOOSEDELIV + "$" + id + "$"+ login +"\n";
                publishProgress(33);

                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.write(data);
                pw.flush();
                publishProgress(67);

//                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                data = br.readLine();
//                answer = new AnswerOERP(data);
//                publishProgress(75);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class CloseCom extends AsyncTask {
        private int vehicule;
        private AnswerOERP answer;

        private CloseCom(int vehicule) {
            this.vehicule = vehicule;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (answer.getAnswer()) {
                LivraisonDAO livraisonDAO = new LivraisonDAO(getApplicationContext());
                try {
                    livraisonDAO.open();
                    livraisonDAO.viderLivraison();
                    for (int i = 1; i < answer.getCharge().size(); i++) {
                        System.out.println(answer.getCharge().get(i));
                        livraisonDAO.insererLivraison(new Livraison(answer.getCharge().get(i)));
                    }
                    livraisonDAO.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Deliveries saved", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getApplicationContext(), "Close Failed !", Toast.LENGTH_LONG).show();
            publishProgress(100);
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            bar.setProgress((Integer) values[0]);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            doRequest();
            return null;
        }

        private void doRequest() {
            try {
                Socket socket = new Socket("172.19.50.58",32036);
                String data = AnswerOERP.CLOSE + "$" + login + "$" + vehicule + "\n";
                publishProgress(25);

                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.write(data);
                pw.flush();
                publishProgress(50);

                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                data = br.readLine();
                answer = new AnswerOERP(data);
                publishProgress(75);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
