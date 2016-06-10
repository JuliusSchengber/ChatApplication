package webeng.chatapplication;

import android.util.Log;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by JuliusSchengber1 on 07.06.16.
 */

public class ActionHandler {

    MessengerApplication myApp;
    private Functions functions = new Functions();
    private JsonAction jsonHandler = new JsonAction();
    private ServerCommunication serverCommunication = new ServerCommunication();
    private static final String TAG = ActionHandler.class.getName();

    public ActionHandler(MessengerApplication myapp) {
        this.myApp = myapp;
    }


    public int loginn(String name, String password){

        //Test-Mock-Objekte

        String nameTest = "tim";
        String passwordTest = "test";
        int response = 5;
        //Success
        if(name.equals(nameTest) && password.equals(passwordTest)){
            response = 0;
            return response;
        }
        //Fail
        else{
            response = 1;
            return response;
        }

    }
    public int sendMessage(String name, String recipientName, String nachrichtparam){

        return 1;
    }

    public int registerr(String name, String password){
        String nameTest = "tim";
        String passwordTest = "test";
        int response = 5;
        //Success
        if(name.equals(nameTest) && password.equals(passwordTest)){
            response = 0;
            return response;
        }
        //Fail
        else{
            response = 1;
            return response;
        }

    }

    public int register(String name, String password) {

        //Bildung des Salt MAsterkeys
        final Random r = new SecureRandom();
        byte[] salt_masterkey = new byte[64];
        r.nextBytes(salt_masterkey);

        //Aufruf PBKDF2 Funktion um masterkey aus password und salt_masterkey zu bilden
        byte[] masterkey = functions.pbkf2(password, salt_masterkey);
        if(masterkey == null) {
            return 98;
        }

        //RSA Schlüsselpaar erzeugen
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            return 98;
        }
        SecureRandom securerandom = new SecureRandom();
        byte bytes[] = new byte[20];
        securerandom.nextBytes(bytes);
        kpg.initialize(2048, securerandom);
        KeyPair kp = kpg.genKeyPair();

        //publickey und privatekey in Variablen speichern
        Key pubkey_user = kp.getPublic();
        Key privkey_user = kp.getPrivate();
        byte[] privateKeyByte = privkey_user.getEncoded();
        byte[] publicKeyByte = pubkey_user.getEncoded();
        String publickey = functions.writePublicKey(publicKeyByte);
        String publickey64 = Base64.toBase64String(publickey.getBytes());

        //privkey_user zu privkey_user_enc verschlüsseln
        byte[] privkey_user_enc = functions.encryptAESECB(masterkey, privateKeyByte);
        if(privkey_user_enc == null) {
            return 98;
        }

        //Übergabestring erstellen
        String value = "{\"user\":\"" + name + "\",\"salt_masterkey\":\"" + Hex.toHexString(salt_masterkey) + "\",\"pubkey\":\"" + publickey64 + "\",\"privkey_enc\":\"" + Hex.toHexString(privkey_user_enc) + "\"}";

        //Verbindung zum Server herstellen
        String success;
        try {
            success = serverCommunication.sendPost("/user", value);
        } catch (Exception e) {
            return 99;
        }
        Log.d(TAG, "Übergabestring: " + value);
        Log.d(TAG, "Rückgabestring: " + success);

        //Rückgabe eines Statuscodes
        if(!success.equals("")) {
             return jsonHandler.getInt(jsonHandler.convertToJSON(success), "fehlercode");
        }
        else return 111;
    }

    public int login(String name, String password) {
        myApp.setName(name);

        //Verbindung zum Server herstellen
        String value = "{\"user\":\"" + name + "\"}";
        String success = "";
        try {
            success = serverCommunication.sendGetWithBody("/"+name, value);
        } catch (Exception e) {
            return 99;
        }

        //Logausgabe
        Log.d(TAG, "Übergabestring: " + value);
        Log.d(TAG, "Rückgabestring: " + success);

        //String in JSON umwandeln und Daten extrahieren
        JsonAction jHandler = new JsonAction();
        String salt_masterkeyString = jHandler.getString(jHandler.convertToJSON(success), "salt_masterkey");
        String privkey_user_encString = jHandler.getString(jHandler.convertToJSON(success), "privkey_enc");
        byte[] salt_masterkey = Hex.decode(salt_masterkeyString);
        byte[] privkey_user_enc = Hex.decode(privkey_user_encString);

        //Masterkey bilden
        byte[] masterkey = functions.pbkf2(password, salt_masterkey);
        if(masterkey == null) {
            return 98;
        }

        //PrivateKey entschlüsseln
        byte[] privkey_user = functions.decryptAESECB(masterkey, privkey_user_enc);
        myApp.setPrivkey_user(privkey_user);
        if(privkey_user == null) {
            return 98;
        }

        //Rückgabe eines Statuscodes
        if(!success.equals("")) {
            return jsonHandler.getInt(jsonHandler.convertToJSON(success), "fehlercode");
        }
        else return 98;
    }

}
