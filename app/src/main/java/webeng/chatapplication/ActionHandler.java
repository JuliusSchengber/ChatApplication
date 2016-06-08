package webeng.chatapplication;

/**
 * Created by JuliusSchengber1 on 07.06.16.
 */

public class ActionHandler {

    MessengerApplication myApp;
    private Functions security = new Functions();
    private JsonHandler jsonHandler = new JsonHandler();

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

}
/*
    public int login(String name, String password) {
        myApp.setName(name);

        //Verbindung zum Server herstellen
        String value = "{\"id\":\"" + name + "\"}";
        String success = "";
        try {
            success = httpConnection.sendGetWithBody("/User", value);
        } catch (Exception e) {
            return 99;
        }

        //Logausgabe
        Log.d(TAG, "Übergabestring: " + value);
        Log.d(TAG, "Rückgabestring: " + success);

        //String in JSON umwandeln und Daten extrahieren
        JsonHandler jHandler = new JsonHandler();
        String salt_masterkeyString = jHandler.extractString(jHandler.convert(success), "salt_masterkey");
        String privkey_user_encString = jHandler.extractString(jHandler.convert(success), "privkey_user_enc");
        byte[] salt_masterkey = Hex.decode(salt_masterkeyString);
        byte[] privkey_user_enc = Hex.decode(privkey_user_encString);

        //Masterkey bilden
        byte[] masterkey = security.pbkf2(password, salt_masterkey);
        if(masterkey == null) {
            return 98;
        }

        //PrivateKey entschlüsseln
        byte[] privkey_user = security.decryptAESECB(masterkey, privkey_user_enc);
        myApp.setPrivkey_user(privkey_user);
        if(privkey_user == null) {
            return 98;
        }

        //Rückgabe eines Statuscodes
        if(!success.equals("")) {
            return jsonHandler.extractInt(jsonHandler.convert(success), "fehlercode");
        }
        else return 98;}
        */
