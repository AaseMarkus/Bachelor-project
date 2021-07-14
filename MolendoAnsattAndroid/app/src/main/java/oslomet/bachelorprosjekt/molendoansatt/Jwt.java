package oslomet.bachelorprosjekt.molendoansatt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import oslomet.bachelorprosjekt.molendoansatt.objects.RegistrationHandler;
import oslomet.bachelorprosjekt.molendoansatt.requests.RequestManager;

public class Jwt {
    SharedPreferences sharedPrefs;

    public Jwt(SharedPreferences sharedPrefs) {
        this.sharedPrefs = sharedPrefs;
    }

    public boolean loadAuth() {
        String id = sharedPrefs.getString("id", null);
        String jwt = sharedPrefs.getString("jwt", null);

        if(id == null || jwt == null) return false;

        String body = decoded(jwt);

        try {
            JSONObject jo = new JSONObject(body);
            long expiration = (Long.parseLong(jo.getString("exp")) * 1000) - 86400000;
            if(expiration < System.currentTimeMillis()) {
                sharedPrefs.edit().remove("id").remove("jwt").apply();
                return false;
            }

            RequestManager.jwt = jwt;
            RequestManager.userID = jo.getString("opservid");
            return true;
        } catch (JSONException e) {

        }

        return false;
    }

    public String decoded(String JWTEncoded) {
        try {
            String[] split = JWTEncoded.split("\\.");
            return getJson(split[1]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }

    public void signOut() {
        sharedPrefs.edit().remove("jwt").remove("id").apply();

        RequestManager.userID = null;
        RequestManager.jwt = null;
        RequestManager.phone = null;
    }
}
