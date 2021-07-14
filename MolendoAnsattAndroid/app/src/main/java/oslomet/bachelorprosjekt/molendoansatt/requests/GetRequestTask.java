package oslomet.bachelorprosjekt.molendoansatt.requests;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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

public class GetRequestTask implements Runnable {
    public static final String TAG = "RUNNABLE";

    private Context context;
    private String[] request;
    private DateTimeFormatter formatter;
    private GetRequestListener listener;

    public interface GetRequestListener {
        void onGetRostersResult(boolean result, ArrayList<Registration> registrations);
    }

    public GetRequestTask(GetRequestListener listener, Context context, String... request) {
        this.listener = listener;
        this.context = context;
        this.request = request;

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public void run() {
        String url = MainActivity.getURL();
        String json;



        switch (request[0]) {
            case "0":
                getRosters(url);
                break;
            case "2":
                url += "servertime";
                json = getJson(url).replace("\"", "");
                ((ApplicationActivity) context).onServerTimeResult(LocalDateTime.parse(json, formatter));
                break;
            default:

        }
    }

    private void getRosters(String url) {
        url += "roster/" + RequestManager.userID;
        String json = getJson(url);

        new Handler(Looper.getMainLooper()).post(() -> {
            if(json == null) listener.onGetRostersResult(false, null);
            else listener.onGetRostersResult(true, parseGetRosterResult(json));
        });

        /*
        if(json == null) ((MainActivity) context).onGetRostersResult(false, null);
        else ((MainActivity) context).onGetRostersResult(true, parseGetRosterResult(json)); */
    }

    private ArrayList<Registration> parseGetRosterResult(String json) {
        ArrayList<Registration> returnList = new ArrayList<>();
        try {
            JSONArray mat = new JSONArray(json);
            for (int i = 0; i < mat.length(); i++) {
                JSONObject obj = mat.getJSONObject(i);

                LocalDate start_date = convertToLocalDate(obj.getString("fldOpRStartDate"));
                LocalTime start_time = convertToLocalTime(obj.getString("fldOpRStartTime"));
                LocalDate end_date = convertToLocalDate(obj.getString("fldOpREndDate"));
                LocalTime end_time = obj.isNull("fldOpREndTime") ? null :
                        convertToLocalTime(obj.getString("fldOpREndTime"));

                returnList.add(new Registration(obj.getString("fldOpRID"),
                        obj.getString("fldOpServID"), start_date, end_date, start_time, end_time,
                        false, obj.getBoolean("fldOpRLogOn"),
                        obj.getBoolean("fldOpRLogOut")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnList;
    }

    private String getJson(String url) {
        StringBuilder output = new StringBuilder();
        try {
            URL pointerUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) pointerUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("Accept", "application/json");
            if(RequestManager.jwt != null) conn.setRequestProperty("Authorization", "Bearer "
                    + RequestManager.jwt);
            if (conn.getResponseCode() != 200) {
                return null;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String s = "";
            while((s = br.readLine()) != null) {
                output.append(s);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    private LocalTime convertToLocalTime(String timeString) {
        StringBuilder sb = new StringBuilder(timeString);
        while(sb.length() > 19) sb.deleteCharAt(sb.length() - 1);

        return LocalTime.parse(sb.toString().replace('T', ' '), formatter);
    }

    private LocalDate convertToLocalDate(String dateString) {
        StringBuilder sb = new StringBuilder(dateString);
        while(sb.length() > 19) sb.deleteCharAt(sb.length() - 1);

        return LocalDate.parse(sb.toString().replace('T', ' '), formatter);
    }
}
