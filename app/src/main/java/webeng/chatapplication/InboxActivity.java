package webeng.chatapplication;

/**
 * Created by JuliusSchengber1 on 07.06.16.
 */
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class InboxActivity extends AppCompatActivity {
    private TextView nachricht;
    private MessengerApplication myApp;
    private static final String TAG = MessengerApplication.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetMessageTask().execute();
    }

    class GetMessageTask extends AsyncTask<String, String, String> {
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
        protected String doInBackground(String... params) {
            Log.d(TAG, "start doInBackground");
            ActionHandler actionHandler = new ActionHandler(myApp);
            String response = null;
            try {
                response = actionHandler.receiveMessage(myApp.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute (final String response) {
            Log.d(TAG, "start onPostExecute");
            Dialog.dismiss();
            if(response != null) {
                Toast.makeText(getApplicationContext(), "Nachrichten erfolgreich abgerufen", Toast.LENGTH_LONG).show();
               nachricht = (TextView) findViewById(R.id.text_textView);
                Log.d(TAG, "Übergabestring: " + response);
                nachricht.setText("Absender" + response);

            }
            else {
                Toast.makeText(getApplicationContext(), "Keine Nachrichten vorhanden", Toast.LENGTH_LONG).show();
            }
        }


}}