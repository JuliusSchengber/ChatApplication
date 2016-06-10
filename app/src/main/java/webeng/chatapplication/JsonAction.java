package webeng.chatapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JuliusSchengber1 on 10.06.16.
 */
public class JsonAction {

    public JSONObject convertToJSON (String input) {

        JSONObject jObject = null;
        try {
            jObject = new JSONObject(input);
        } catch (JSONException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        return jObject;

    }

    public String getString (JSONObject input, String key) {

        String output = "";
        if (input.has(key)) {
            try {
                output = input.getString(key);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return output;
    }

    public int getInt (JSONObject input, String key) {

        int output = 98;
        if (input.has(key)) {
            try {
                output = input.getInt(key);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return output;
    }

    public JSONArray getArray (JSONObject input, String key) {

        JSONArray output = null;
        if (input.has(key)) {
            try {
                output = input.getJSONArray(key);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return output;
    }
}
