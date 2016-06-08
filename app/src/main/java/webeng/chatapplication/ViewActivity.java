package webeng.chatapplication;

/**
 * Created by JuliusSchengber1 on 07.06.16.
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        setupButtons();
    }
    public void setupButtons(){
        final Button sendMessage = (android.widget.Button) findViewById(R.id.sendMessage_button);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                Intent i = new Intent(ViewActivity.this, SendMessageActivity.class);
                startActivity(i);
            }
        });
        final Button showInbox = (android.widget.Button) findViewById(R.id.showInbox_button);
        showInbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View w) {
                Intent i = new Intent(ViewActivity.this, InboxActivity.class);
                startActivity(i);
            }
        });
    }
}
