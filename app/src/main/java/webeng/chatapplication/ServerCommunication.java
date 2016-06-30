package webeng.chatapplication;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

/**
 * Created by JuliusSchengber1 on 10.06.16.
 */
public class ServerCommunication {

    private final String ipAdress = "10.60.70.15";
    private final String httpIP = "http://10.60.70.15/";
    private static final String TAG = ActionHandler.class.getName();

    public int sendPost(String param_url, String param_body) throws Exception {

        String body = param_body;
        String url = httpIP + param_url;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //request header
        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(false);
        con.setUseCaches(false);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Content-Length", String.valueOf(body.length()));

        Log.d(TAG, "Send POST to: " + url);
        // Send post request
        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
        writer.write(body);
        writer.flush();

        int responseCode = con.getResponseCode();
        String r = con.getContentEncoding();

        Log.d(TAG, "ResponseCode (POST): " + responseCode);

        return responseCode;
    }

    public String sendGet(String param_url) throws Exception {

        String url = httpIP + param_url;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        //con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();

        if (responseCode == 404) {
            return "not found";
        }
        if (responseCode != 200) {

            return "" + responseCode;
        }

        Log.d(TAG, "Responsecode(Login): " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public int delete(String param_url) throws Exception {

        URL url = new URL(httpIP + param_url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("charset", "utf-8");
        connection.setUseCaches(false);
        int responseCode = connection.getResponseCode();
        return responseCode;
    }


    public String sendPatch(String param_url, String value) throws Exception {

        String body = value;
        String url = httpIP + param_url;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("PATCH");
        con.setDoInput(true);
        con.setDoOutput(false);
        con.setUseCaches(false);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Content-Length", String.valueOf(body.length()));

        Log.d(TAG, "Send PATCH to: " + url);
        // Send post request
        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
        writer.write(body);
        writer.flush();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        Log.d(TAG, "Response (Patch): " + response.toString());
        return response.toString();
    }
}
