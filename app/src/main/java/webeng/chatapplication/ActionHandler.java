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
    private Functions security = new Functions();
    private JsonAction jsonHandler = new JsonAction();


    public ActionHandler(MessengerApplication myapp) {
        this.myApp = myapp;
    }


    public int login(String name, String password){

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
        byte[] masterkey = security.pbkf2(password, salt_masterkey);
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
        String publickey = security.writePublicKey(publicKeyByte);
        String publickey64 = Base64.toBase64String(publickey.getBytes());

        //privkey_user zu privkey_user_enc verschlüsseln
        byte[] privkey_user_enc = security.encryptAESECB(masterkey, privateKeyByte);
        if(privkey_user_enc == null) {
            return 98;
        }

        //Übergabestring erstellen
        String value = "{\"user\":\"" + name + "\",\"salt_masterkey\":\"" + Hex.toHexString(salt_masterkey) + "\",\"pubkey\":\"" + publickey64 + "\",\"privkey_enc\":\"" + Hex.toHexString(privkey_user_enc) + "\"}";

        //Verbindung zum Server herstellen
        String success = "";
        try {
            success = httpConnection.sendPost("/User", value);
        } catch (Exception e) {
            return 99;
        }

        //Rückgabe eines Statuscodes
        if(!success.equals("")) {
            return jsonHandler.extractInt(jsonHandler.convert(success), "fehlercode");
        }
        else return 98;
    }

}
