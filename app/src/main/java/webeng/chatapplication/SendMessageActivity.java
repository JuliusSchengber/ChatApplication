package webeng.chatapplication;

/**
 * Created by JuliusSchengber1 on 07.06.16.
 */
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendMessageActivity extends AppCompatActivity {

    private EditText rcpt;
    private EditText msg;
    private String rcptString;
    private String msgString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        rcpt = (EditText) findViewById(R.id.recipient_editText);
        msg = (EditText) findViewById(R.id.message_editText);
        setupButtons();
    }

    public void setupButtons() {
        final Button send = (android.widget.Button) findViewById(R.id.send_button);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                rcptString = rcpt.getText().toString();
                msgString = msg.getText().toString();
                new SendMessageAction().execute(rcptString, msgString);
            }
        });
    }

    class SendMessageAction extends AsyncTask<String, String, Integer> {

        private MessengerApplication myApp = (MessengerApplication) getApplication();

        @Override
        protected Integer doInBackground(String... params) {
            ActionHandler a = new ActionHandler(myApp);
            Integer response = a.sendMessage(myApp.getName(), params[0], params[1]);
            return response;
        }

        @Override
        protected void onPostExecute (Integer response) {

            if(response==0) {
                Intent i = new Intent(SendMessageActivity.this, ViewActivity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(), "Nachricht erfolgreich an " + rcptString +"versendet.", Toast.LENGTH_SHORT).show();            }
            else {
                Toast.makeText(getApplicationContext(), "Es ist ein Fehler aufgetreten, die Nachricht wurde nicht an " + rcptString + " zugestellt. Fehlercode: " + response +" Bitte erneut versuchen.", Toast.LENGTH_SHORT).show();
            }
        }

        }

    }


