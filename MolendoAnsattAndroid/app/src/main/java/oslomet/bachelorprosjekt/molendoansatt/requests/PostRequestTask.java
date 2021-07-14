package oslomet.bachelorprosjekt.molendoansatt.requests;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import oslomet.bachelorprosjekt.molendoansatt.ApplicationActivity;
import oslomet.bachelorprosjekt.molendoansatt.MainActivity;
import oslomet.bachelorprosjekt.molendoansatt.fragments.LogInCodeFragment;
import oslomet.bachelorprosjekt.molendoansatt.objects.Registration;

public class PostRequestTask implements Runnable {
    public static final String TAG = "RUNNABLE";

    private Context context;
    private String[] request;
    private DateTimeFormatter formatter;
    PostRequestListener listener;

    public interface PostRequestListener {
        void onSaveResult(int resultCode);
    }

    public PostRequestTask(PostRequestListener listener, Context context, String... request) {
        this.listener = listener;
        this.context = context;
        this.request = request;

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public void run() {
        String url = MainActivity.getURL();
        switch (request[0]) {
            case "0":
                // Post Roster
                postRoster(url);
                break;
            case "1":
                //Post phone number
                postPhone(url);
                break;
            case "2":
                // Post sms code
                postCode(url);
                break;
        }
    }

    private void postRoster(String url) {
        url += "roster/";
        Log.d("request", request[1]);
        String return_code = getJson(url, request[1])[0];
        listener.onSaveResult(Integer.parseInt(return_code));
    }

    private void postPhone(String url) {
        url += "auth/send-sms-code/";
        String[] jsonResult = getJson(url, request[1]);
        String json = jsonResult[1];

        String phone = "";
        try {
            JSONObject input = new JSONObject(request[1]);
            phone = input.isNull("phone") ? "" : decodeUtf8(input.getString("phone"));
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(json == null) ((MainActivity) context).onSendPhoneResult(false,
                new LogInCodeFragment(phone));
        else ((MainActivity) context).onSendPhoneResult(true,
                new LogInCodeFragment(phone));
    }

    private void postCode(String url) {
        url += "auth/check-sms-code/";
        String[] jsonResult = getJson(url, request[1]);
        String json = jsonResult[1];

        new Handler(Looper.getMainLooper()).post(() -> {
            if(json == null) ((MainActivity) context).onSendCodeResult(false, "", "", "");
            else {
                String[] parsedArray = parseCodeResult(json);
                if(parsedArray == null) ((MainActivity) context).onSendCodeResult(false, "", "", "");
                else {
                    String id = parsedArray[0];
                    String phone = parsedArray[1];
                    String token = parsedArray[2];

                    ((MainActivity) context).onSendCodeResult(true, id, phone, token);
                }
            }
        });
    }

    private String[] getJson(String url, String body) {
        StringBuilder output = new StringBuilder();
        int return_code = 408;
        try {
            URL full_url = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) full_url.openConnection();
            //byte[] postData = request[1].getBytes(StandardCharsets.UTF_8);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            if(RequestManager.jwt != null) conn.setRequestProperty("Authorization", "Bearer "
                    + RequestManager.jwt);

            if(!body.equals("")) {
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(request[1]);
                os.flush();
                os.close();
            }

            if(conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {
                Log.d("error", Integer.toString(conn.getResponseCode()));

                String[] errorResult = new String[2];
                errorResult[0] = Integer.toString(return_code);
                errorResult[1] = null;
                return errorResult;
            }

            return_code = conn.getResponseCode();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String s = "";
            while((s = br.readLine()) != null) {
                output.append(s);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] result = new String[2];
        result[0] = Integer.toString(return_code);
        result[1] = output.toString();

        return result;
    }

    private String[] parseCodeResult(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            String[] returnString = new String[3];

            returnString[0] = obj.isNull("id") ? "" : decodeUtf8(obj.getString("id"));
            returnString[1] = obj.isNull("phone") ? "" : decodeUtf8(obj.getString("phone"));
            returnString[2] = obj.isNull("token") ? "" : decodeUtf8(obj.getString("token"));

            return returnString;
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String decodeUtf8(String encoded) throws UnsupportedEncodingException {
        return URLDecoder.decode(encoded, "UTF-8");
    }
}
