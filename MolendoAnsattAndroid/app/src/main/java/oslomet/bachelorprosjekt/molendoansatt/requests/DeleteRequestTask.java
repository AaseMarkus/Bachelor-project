package oslomet.bachelorprosjekt.molendoansatt.requests;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import oslomet.bachelorprosjekt.molendoansatt.ApplicationActivity;
import oslomet.bachelorprosjekt.molendoansatt.MainActivity;
import oslomet.bachelorprosjekt.molendoansatt.fragments.LogInCodeFragment;
import oslomet.bachelorprosjekt.molendoansatt.objects.Registration;

public class DeleteRequestTask implements Runnable {
    public static final String TAG = "RUNNABLE";
    DeleteRequestListener listener;

    public interface DeleteRequestListener {
        void onSaveResult(int responseCode);
    }

    private Context context;
    private String[] request;

    public DeleteRequestTask(DeleteRequestListener listener, Context context, String... request) {
        this.listener = listener;
        this.context = context;
        this.request = request;
    }

    @Override
    public void run() {
        String url = MainActivity.getURL();

        switch (request[0]) {
            case "0":
                url += "roster/" + RequestManager.userID;
                break;
            default:
                break;
        }

        int response_code = 408;
        try {
            URL full_url = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) full_url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Accept", "text/plain");
            if(RequestManager.jwt != null) conn.setRequestProperty("Authorization", "Bearer "
                    + RequestManager.jwt);
            response_code = conn.getResponseCode();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        listener.onSaveResult((int) response_code);

    }
}
