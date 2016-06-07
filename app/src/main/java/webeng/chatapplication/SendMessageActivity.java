package webeng.chatapplication;

/**
 * Created by JuliusSchengber1 on 07.06.16.
 */
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SendMessageActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getName();
    private EditText recipient;
    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        recipient = (EditText)findViewById(R.id.recipient_editText);
        message = (EditText)findViewById(R.id.message_editText);
    }

    public void sendMessage(View view){

        new SendMessageTask().execute(recipient.getText().toString(), message.getText().toString());
    }

    class SendMessageTask extends AsyncTask<String, String, Integer> {

        private ProgressDialog Dialog = new ProgressDialog(SendMessageActivity.this);
        private MessengerApplication myApp = (MessengerApplication) getApplication();

        @Override
        protected void onPreExecute() {
            Dialog.setMessage("Send...");
            Dialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {

            UseCases uc = new UseCases(myApp);
            Integer response = uc.sendMessage(myApp.getName(), params[0], params[1]);
            return response;

        }

        @Override
        protected void onPostExecute (Integer response) {
            Dialog.dismiss();
            Log.d(TAG, "Rückgabe: " + response.toString());

            if(response == 0) {
                Intent i = new Intent(SendMessageActivity.this, MainActivity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(), "Sending successful!", Toast.LENGTH_LONG).show();
            }

            else {
                Toast.makeText(getApplicationContext(), "Error" + response, Toast.LENGTH_LONG).show();
            }
        }

    }

}
