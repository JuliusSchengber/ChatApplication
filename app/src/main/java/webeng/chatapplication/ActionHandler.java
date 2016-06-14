package webeng.chatapplication;

import android.util.Log;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

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
        String value = "{\"salt_masterkey\":\"" + Hex.toHexString(salt_masterkey) + "\",\"pubkey\":\"" + publickey64 + "\",\"privkey_enc\":\"" + Hex.toHexString(privkey_user_enc) + "\"}";

        //Verbindung zum Server herstellen
        int success;
        try {
            success = serverCommunication.sendPost(name, value);
        } catch (Exception e) {
            return 99;
        }
        Log.d(TAG, "Übergabestring(register): " + value);
        Log.d(TAG, "Rückgabestring(register): " + success);

        //Rückgabe eines Statuscodes
        return success;
    }

    public int login(String name, String password) {
        myApp.setName(name);

        //Verbindung zum Server herstellen
        String value = "{\"user\":\"" + name + "\"}";
        String success;
        try {
            success = serverCommunication.sendGet(name);
        } catch (Exception e) {
            return 99;
        }


        //Logausgabe
        Log.d(TAG, "Übergabestring(login): " + value);
        Log.d(TAG, "Rückgabestring(login): " + success);


        if(success.equals("not found")){
            return 404;
        }

        JSONObject jObj = null;
        try {
            jObj = new JSONObject(success);
        } catch (JSONException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }


        //String in JSON umwandeln und Daten extrahieren
        JsonAction jHandler = new JsonAction();
        String pubkeyString = jHandler.getString(jObj, "pubkey");
        Log.d(TAG, "Pubkey " + pubkeyString);
        String salt_masterkeyString = jHandler.getString(jObj, "salt_masterkey");
        Log.d(TAG, "Salt Masterkey " + salt_masterkeyString);
        String privkey_user_encString = jHandler.getString(jObj, "privkey_enc");
        Log.d(TAG, "privkey enc " + privkey_user_encString);
        byte[] salt_masterkey = Hex.decode(salt_masterkeyString);
        byte[] privkey_user_enc = Hex.decode(privkey_user_encString);

        if(salt_masterkey == null){
            return 98;
        }

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
        if(!success.equals("x")) {
            return 200;
        }
        else {
            return 98;
        }
    }

    public String getPubKey(String username)throws Exception{
        String success = serverCommunication.sendGet(username);
        JSONObject jObj = null;
        try {
            jObj = new JSONObject(success);
        } catch (JSONException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        JsonAction jHandler = new JsonAction();
        String pubkeyString = jHandler.getString(jObj, "pubkey");
        Log.d(TAG, "Pubkey " + pubkeyString);
        return pubkeyString;
    }

    public int deleteUser()throws Exception{

        String username;
        try {
             username = myApp.getName();
            Log.d(TAG, "username: " + username);
        }
        catch (Exception e){
             username = "";
            Log.d(TAG, "username: " + username);
        }
        int success = serverCommunication.delete(username);
        Log.d(TAG, "success: " + success);
        return success;
    }

}
