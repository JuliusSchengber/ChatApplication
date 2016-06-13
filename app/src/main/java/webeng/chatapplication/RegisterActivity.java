package webeng.chatapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by JuliusSchengber1 on 08.06.16.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText name;
    private EditText passw;
    private EditText passw_conf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = (EditText) findViewById(R.id.name_reg);
        passw = (EditText) findViewById(R.id.password_reg);
        passw_conf = (EditText) findViewById(R.id.password_conf_reg);
        setupButton();
    }

    public void setupButton() {
        final Button register = (android.widget.Button) findViewById(R.id.register_reg_button);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                if (passw.getText().toString().equals(passw_conf.getText().toString())) {
                    new RegisterAction().execute(name.getText().toString(), passw.getText().toString());
                }
            }
        });
    }

    class RegisterAction extends AsyncTask<String, String, Integer> {
        private MessengerApplication myApp = (MessengerApplication) getApplication();

        @Override
        protected Integer doInBackground(String... params) {
            ActionHandler ac = new ActionHandler(myApp);
            Integer response = ac.register(params[0], params[1]);
            return response;
        }

        @Override
        protected void onPostExecute(Integer response) {
            if (response == 200) {
                Toast.makeText(getApplicationContext(), "Erfolgreich registriert.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            } else {
                if (response == 400) {
                    Toast.makeText(getApplicationContext(), "Benutzer bereits vorhanden. ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Es ist ein Fehler aufgetreten. " + response, Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}



