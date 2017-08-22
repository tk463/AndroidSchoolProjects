package a3.debianpulse.injen.apptech;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import a3.debianpulse.injen.apptech.ProtoOERP.AnswerOERP;


public class TechActivity extends Activity {
    private ProgressBar bar;
    private EditText ETCarNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech);

        bar = (ProgressBar) findViewById(R.id.progressBar2);
        TextView user = (TextView) findViewById(R.id.textViewUser);
        user.setText(getIntent().getExtras().get("login").toString());

        Button BRepair = (Button) findViewById(R.id.buttonRepair);
        BRepair.setOnClickListener(onRepair);
        Button BBreak = (Button) findViewById(R.id.buttonBreak);
        BBreak.setOnClickListener(onBreak);

        ETCarNumber = (EditText) findViewById(R.id.editText);
    }

    View.OnClickListener onRepair = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NotifyCom notifyCom = new NotifyCom(AnswerOERP.REPAIRS);
            notifyCom.execute();
        }
    };

    View.OnClickListener onBreak = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NotifyCom notifyCom = new NotifyCom(AnswerOERP.BREAKDOWN);
            notifyCom.execute();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tech, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private class NotifyCom extends AsyncTask {
        private int RequestCode;

        private NotifyCom(int requestCode) {
            RequestCode = requestCode;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            doRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            publishProgress(90);
            switch (RequestCode) {
                case AnswerOERP.BREAKDOWN :
                    Toast.makeText(getApplicationContext(), "Breakdown reported", Toast.LENGTH_LONG).show();
                    break;
                case AnswerOERP.REPAIRS :
                    Toast.makeText(getApplicationContext(), "Repairs reported", Toast.LENGTH_LONG).show();
                    break;
            }
            publishProgress(100);
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            bar.setProgress((Integer) values[0]);
        }

        private void doRequest() {
            try {
                publishProgress(0);
                Socket socket = new Socket("172.19.50.58",32036);
                String data = RequestCode + "$" + ETCarNumber.getText().toString() + "\n";
                publishProgress(30);

                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.write(data);
                pw.flush();
                publishProgress(60);

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
