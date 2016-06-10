package webeng.chatapplication;

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

    public int register(String name, String password){
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

}
