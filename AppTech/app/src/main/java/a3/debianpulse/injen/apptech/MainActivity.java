package a3.debianpulse.injen.apptech;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import a3.debianpulse.injen.apptech.ProtoOERP.AnswerOERP;


public class MainActivity extends Activity {
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button BLogin = (Button) findViewById(R.id.log_in_button);
        BLogin.setOnClickListener(onLogin);
        bar = (ProgressBar) findViewById(R.id.progressBar);
    }

    View.OnClickListener onLogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText ETLogin = (EditText) findViewById(R.id.login);
            EditText ETPass = (EditText) findViewById(R.id.password);
            if (!ETLogin.getText().toString().isEmpty() && !ETPass.getText().toString().isEmpty()) {
                LoginTask lt = new LoginTask(ETLogin.getText().toString(), ETPass.getText().toString());
                lt.execute();
                ETPass.setText("");
            }
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
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private class LoginTask extends AsyncTask {
        private String login;
        private String password;
        private AnswerOERP answer;

        public LoginTask(String login, String password) {
            this.login = login;
            this.password = password;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            doLogin();
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            bar.setProgress((Integer) values[0]);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            publishProgress(100);
            if (answer.getAnswer()) {
                Intent main = new Intent(getApplicationContext(), TechActivity.class);
                main.putExtra("login", login);
                startActivity(main);
            }
            else {
                Toast.makeText(getApplicationContext(), R.string.error_invalid, Toast.LENGTH_LONG).show();
                publishProgress(0);
            }
        }

        private void doLogin() {
            try {
                Socket socket = new Socket("172.19.50.58",32036);
                String data = AnswerOERP.LOGIN + "$" + login + "$" + password + "\n";
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
