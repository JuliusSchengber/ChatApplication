package webeng.chatapplication;

/**
 * Created by JuliusSchengber1 on 07.06.16.
 */
import android.app.Application;

public class MessengerApplication extends Application {
    private byte[] privkey_user;
    private String name;
    private String[] message;

    public byte[] getPrivkey_user() {
        return privkey_user;
    }

    public void setPrivkey_user(byte[] privkey_user) {
        this.privkey_user = privkey_user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getMessage() {
        return message;
    }

    public void setMessage(String[] message) {
        this.message = message;
    }

}
