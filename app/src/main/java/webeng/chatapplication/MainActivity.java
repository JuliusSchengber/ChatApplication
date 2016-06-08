package webeng.chatapplication;

import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText name;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (EditText)findViewById(R.id.name_editText);
        password = (EditText)findViewById(R.id.password_conf_reg);
        setupButtons();
    }

    public void setupButtons(){
        final Button login = (android.widget.Button) findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            //login
            public void onClick(View w) {
                new LoginAction().execute(name.getText().toString(), password.getText().toString());
            }
        });
        final Button register = (Button) findViewById(R.id.register_reg_button);
        register.setOnClickListener(new View.OnClickListener() {
            //register
            public void onClick(View w) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
    }

    class LoginAction extends AsyncTask<String, String, Integer> {
        private MessengerApplication myApp = (MessengerApplication) getApplication();

        @Override
        protected Integer doInBackground(String... params) {
            ActionHandler a = new ActionHandler(myApp);
            Integer response = a.login(params[0], params [1]);
            return response;
        }

        @Override
        protected void onPostExecute (Integer response) {
            if(response == 0) {
                startActivity(new Intent(MainActivity.this, ViewActivity.class));
                Toast.makeText(getApplicationContext(), "Login erfolgreich", Toast.LENGTH_SHORT).show();
            }

            else {
                Toast.makeText(getApplicationContext(), "Login fehlgeschlagen" + response, Toast.LENGTH_SHORT).show();
            }
        }

    }
}
