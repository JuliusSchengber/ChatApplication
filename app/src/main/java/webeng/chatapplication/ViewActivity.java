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

public class ViewActivity extends AppCompatActivity {

    public static final String TAG = ViewActivity.class.getName();
    public InboxActivity n;

    private MessengerApplication myApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        try{
            setupButtons();
        }
        catch (Exception e){

        }
    }
    public void setupButtons()throws Exception {
        final Button sendMessage = (android.widget.Button) findViewById(R.id.sendMessage_button);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                Intent i = new Intent(ViewActivity.this, SendMessageActivity.class);
                startActivity(i);
            }
        });
        Button showInbox = (android.widget.Button) findViewById(R.id.showInbox_button);
        showInbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                Intent i = new Intent(ViewActivity.this, InboxActivity.class);
                startActivity(i);
            }
        });
        final Button logout = (android.widget.Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                MessengerApplication myApp = (MessengerApplication) getApplication();
                myApp.setMessage(null);
                myApp.setName(null);
                myApp.setPrivkey_user(null);
                Intent i = new Intent(ViewActivity.this, MainActivity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(), "Erfolgreich ausgelggt", Toast.LENGTH_SHORT).show();
            }
        });
        final Button delete = (android.widget.Button) findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                new DeleteAction().execute();
            }
        });
        final Button gelesen = (android.widget.Button) findViewById(R.id.gelesen_button);
        gelesen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                setContentView(R.layout.activity_view);

            }
        });
    }

        class DeleteAction extends AsyncTask<String, String, Integer>{
        private MessengerApplication myApp = (MessengerApplication) getApplication();

        @Override
        protected Integer doInBackground(String... params) {
            ActionHandler ac = new ActionHandler(myApp);
            Integer response;
            try{
                response = ac.deleteUser();
            }
            catch (Exception e){
                response = 0;
            }
            return response;
        }

        @Override
        protected void onPostExecute(Integer response) {
            if (response == 200) {
                Toast.makeText(getApplicationContext(), "Account gelöscht", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewActivity.this, MainActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), "Löschen fehlgeschlagen "+response, Toast.LENGTH_SHORT).show();
            }

        }
    }


}
