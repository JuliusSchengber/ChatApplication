package webeng.chatapplication;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

import org.bouncycastle.jcajce.provider.symmetric.ARC4;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
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
            success = 98;
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
        byte[] b = Base64.decode(pubkeyString);
        String a = new String(b);
        String c = a.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)","");
        Log.d(TAG, "a nach Replace Anweisung: " +c);
        return c;

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

    public int sendMessage(String name, String recipientName, String nachrichtparam) throws Exception {

        Log.d(TAG, "name: " + name);
        Log.d(TAG, "recipientName: " + recipientName);
        Log.d(TAG, "nachrichtparam: " + nachrichtparam);
        String pubKey = getPubKey(recipientName);


            byte[] nachricht = nachrichtparam.getBytes();
            byte[] pubkey_recipient = Base64.decode(pubKey);
            Log.d(TAG, "user pubkey: " + pubKey);
            Log.d(TAG, "recipient pubkey: " + pubkey_recipient);
            Log.d(TAG, "nachrichtparam: " + nachricht);


                //Symmetrischen Schlüssel bilden
                KeyGenerator kg = null;
                try {
                    kg = KeyGenerator.getInstance("AES");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                SecureRandom securerandom = new SecureRandom();
                byte bytes[] = new byte[20];
                securerandom.nextBytes(bytes);
                kg.init(128, securerandom);
                SecretKey key_recipient_secret = kg.generateKey();
                byte[] key_recipient = key_recipient_secret.getEncoded();

                //Initialisierungsvektor erzeugen
                final Random r = new SecureRandom();
                byte[] iv = new byte[16];
                r.nextBytes(iv);

                //Nachricht verschlüsseln
                byte[] cipher = functions.encryptAESCBC(nachricht, pubkey_recipient, iv, key_recipient_secret);
                if (cipher == null) {
                    return 96;
                }

                //key_recipient mit Public Key verschlüsseln
                byte[] key_recipient_enc = functions.encryptRSAPubKey(pubkey_recipient, key_recipient);
                if (key_recipient_enc == null) {
                    return 97;
                }

                //Bildung von SHA-256 Hash für sig_recipient
                String text = name + Base64.toBase64String(cipher) + Base64.toBase64String(iv) + Base64.toBase64String(key_recipient_enc);
                byte[] textBytes = text.getBytes();

                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e) {
                    return 98;
                }
                md.update(textBytes); // Change this to "UTF-16" if needed
                byte[] sig_recipient = md.digest();
                String sig_recipientHex = Hex.toHexString(sig_recipient).toLowerCase();

                //Verschlüsselung des Hashes mit dem Private Key
                byte[] sig_recipient_enc = functions.encryptRSAPrivKey(myApp.getPrivkey_user(), sig_recipientHex.getBytes());

                if (sig_recipient_enc == null) {
                    return 99;
                }

                //Unix-Zeit
                Long unixTime = System.currentTimeMillis() / 1000L;
                String timestamp = unixTime.toString();

                //Bildung von SHA-256 Hash für sig_service
                String text1 = "{\"Id\":\"" + name + "\",\"Cipher\":\"" + Base64.toBase64String(cipher) + "\",\"Iv\":\"" + Base64.toBase64String(iv) + "\",\"key_recipient_enc\":\"" + Base64.toBase64String(key_recipient_enc) + "\",\"sig_recipient\":\"" + Base64.toBase64String(sig_recipient_enc) + "\"}" + timestamp + recipientName;
                String text3 = text1.replace("/", "\\/");
                byte[] text1Bytes = text3.getBytes();
                MessageDigest md1 = null;
                try {
                    md1 = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                md1.update(text1Bytes); // Change this to "UTF-16" if needed
                byte[] sig_service = md1.digest();
                String sig_serviceHex = Hex.toHexString(sig_service).toLowerCase();

                //Verschlüsselung des Hashes mit dem Private Key
                byte[] sig_service_enc = functions.encryptRSAPrivKey(myApp.getPrivkey_user(), sig_serviceHex.getBytes());
                if (sig_service_enc == null) {
                    return 100;
                }

                //Id_enc bilden
                //Bildung von MD5 Hash für Id_enc
                String text2 = name + timestamp;
                byte[] text2Bytes = text2.getBytes();
                MessageDigest md2 = null;
                try {
                    md2 = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                md2.update(text2Bytes); // Change this to "UTF-16" if needed
                byte[] id = md2.digest();
                String idHex = Hex.toHexString(id).toLowerCase();

                //Verschlüsselung des Hashes mit dem Private Key
                byte[] id_enc = functions.encryptRSAPrivKey(myApp.getPrivkey_user(), idHex.getBytes());
                if (id_enc == null) {
                    return 101;
                }

                String value = "{\"inner_envelope\":{\"cipher\":\""+ cipher + "\",\"iv\":\""+iv+"\",\"key_recipient_enc\":\""+key_recipient_enc+"\",\"sig_recipient\":\""+sig_recipient+"\"},\"receiver\":\""+ recipientName +"\",\"timestamp\":\""+ timestamp +"\",\"sig_service\":\""+ sig_service +"\"}";

                //Verbindung zum Server herstellen
                int success;
                try {
                    success = serverCommunication.sendPost(recipientName + "/message", value);
                } catch (Exception e) {
                    return 101;
                }

                //Logausgabe
                Log.d(TAG, "Übergabestring: " + value);
                Log.d(TAG, "Rückgabestring: " + success);

                return success;
            }


    public String receiveMessage(String name) throws Exception {
        return "";


    }


    }
