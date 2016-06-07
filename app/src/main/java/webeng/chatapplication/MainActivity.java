package webeng.chatapplication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private EditText name;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (EditText)findViewById(R.id.name_editText);
        password = (EditText)findViewById(R.id.password_editText);
    }

    public void clickedLogin(View view){

        new LoginTask().execute(name.getText().toString(), password.getText().toString());

    }

    class LoginTask extends AsyncTask<String, String, Integer> {

        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
        private MessengerApplication myApp = (MessengerApplication) getApplication();
        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Login...");
            Dialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {

            UseCases uc = new UseCases(myApp);
            Integer response = uc.login(params[0], params [1]);
            return response;

        }

        @Override
        protected void onPostExecute (Integer response) {
            Dialog.dismiss();
            Log.d(TAG, "Rückgabe: " + response.toString());

            if(response == 0) {
                Intent i = new Intent(MainActivity.this, ViewActivity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(), "Erfolgreich eingeloggt", Toast.LENGTH_LONG).show();
            }

            else {
                Toast.makeText(getApplicationContext(), "Fehler" + response, Toast.LENGTH_LONG).show();
            }
        }

    }
}
