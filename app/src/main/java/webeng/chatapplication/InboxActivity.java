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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class InboxActivity extends AppCompatActivity {
    private MessengerApplication myApp;
    private static final String TAG = MessengerApplication.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetMessageTask().execute();
        setContentView(R.layout.activity_inbox);
    }


    class GetMessageTask extends AsyncTask<String, String[], String[]> {
        private ProgressDialog Dialog = new ProgressDialog(InboxActivity.this);
        private MessengerApplication myApp = (MessengerApplication) getApplication();

        @Override
        protected void onPreExecute()
        {
            Log.d(TAG, "start onPreExecute: ");
            Dialog.setMessage("Nachrichten abrufen...");
            Dialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            Log.d(TAG, "start doInBackground");
            ActionHandler actionHandler = new ActionHandler(myApp);
            String[] response = null;
            try {
                response = actionHandler.receiveMessage(myApp.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute (String[] response) {
            Log.d(TAG, "start onPostExecute");
            Dialog.dismiss();
            if(response != null) {

                Toast.makeText(getApplicationContext(), "Nachrichten erfolgreich abgerufen", Toast.LENGTH_LONG).show();
               TextView nachricht = (TextView) findViewById(R.id.nachricht);
                TextView sender = (TextView) findViewById(R.id.sender);
                final Button gelesen = (android.widget.Button) findViewById(R.id.gelesen_button);
                gelesen.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View w) {
                        Intent i = new Intent(InboxActivity.this, ViewActivity.class);
                        startActivity(i);
                    }
                });
                Log.d(TAG, "Übergabestring: " + response);
                try{
                    sender.setText(response[0]);
                    nachricht.setText(response[1]);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Keine Nachrichten vorhanden", Toast.LENGTH_LONG).show();
                Intent i = new Intent(InboxActivity.this, ViewActivity.class);
                startActivity(i);
            }
        }


}}