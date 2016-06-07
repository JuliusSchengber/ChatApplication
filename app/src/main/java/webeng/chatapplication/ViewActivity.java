package webeng.chatapplication;

/**
 * Created by JuliusSchengber1 on 07.06.16.
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
    }



    public void openSendMessage(View view){
        Intent i = new Intent(ViewActivity.this, SendMessageActivity.class);
        startActivity(i);
    }

    public void openShowInbox(View view){
        Intent i = new Intent(ViewActivity.this, InboxActivity.class);
        startActivity(i);
    }


}
