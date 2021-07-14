// TODO fix ting legger seg på stack

package oslomet.bachelorprosjekt.molendoansatt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import oslomet.bachelorprosjekt.molendoansatt.fragments.AlertDialogFragment;
import oslomet.bachelorprosjekt.molendoansatt.fragments.LogInCodeFragment;
import oslomet.bachelorprosjekt.molendoansatt.fragments.LogInLoadingFragment;
import oslomet.bachelorprosjekt.molendoansatt.fragments.LogInPhoneFragment;
import oslomet.bachelorprosjekt.molendoansatt.objects.Registration;
import oslomet.bachelorprosjekt.molendoansatt.objects.RegistrationHandler;
import oslomet.bachelorprosjekt.molendoansatt.requests.GetRequestTask;
import oslomet.bachelorprosjekt.molendoansatt.requests.PostRequestTask;
import oslomet.bachelorprosjekt.molendoansatt.requests.RequestManager;

public class MainActivity extends AppCompatActivity implements GetRequestTask.GetRequestListener, PostRequestTask.PostRequestListener {
    LogInPhoneFragment phoneFragment;
    LogInLoadingFragment loadingFragment;

    ImageView ivIcon;

    static RegistrationHandler registrationHandler;
    public static RegistrationHandler getRegistrationHandler() { return registrationHandler; }

    static Jwt jwt;
    public static Jwt getJwt() { return jwt; }

    static String URL = "http://18.191.107.144:88/molendo/";
    public static String getURL() { return URL; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLanguage();

        setContentView(R.layout.activity_main);
        initializeVariables();

        if(jwt.loadAuth()) runGetRequestTask("0");
        else showFragment(phoneFragment);
    }

    private void initializeVariables() {
        /* --- Fragments --- */
        phoneFragment = new LogInPhoneFragment();
        loadingFragment = new LogInLoadingFragment();
        registrationHandler = new RegistrationHandler();

        ivIcon = this.findViewById(R.id.iv_information);
        ivIcon.setOnClickListener(v -> openInformation());

        SharedPreferences sharedPrefs = this.getPreferences(Context.MODE_PRIVATE);
        jwt = new Jwt(sharedPrefs);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_login_fragment, fragment).commit();
    }

    public void sendSMS(String phonenumber) {
        showFragment(loadingFragment);

        try {
            RequestManager.phone = phonenumber;
            JSONObject jo = new JSONObject()
                    .put("phone", phonenumber)
                    .put("Service", "Magic Circle");

            runPostRequestTask("1", jo.toString());
        } catch (Exception e) {
            // TODO error handling
        }
    }

    public void logIn(String code) {
        showFragment(loadingFragment);
        try {
            JSONObject jo = new JSONObject()
                    .put("phone", RequestManager.phone)
                    .put("code", code);

            runPostRequestTask("2", jo.toString());
        } catch (Exception e) {
            // TODO error handling
        }
    }

    public void runPostRequestTask(String... request) {
        PostRequestTask task = new PostRequestTask(this, this, request);
        RequestManager.getManagerInstance().runTask(task);
    }

    public void runGetRequestTask(String... request) {
        GetRequestTask task = new GetRequestTask(this, this, request);
        RequestManager.getManagerInstance().runTask(task);
    }

    public void onSendPhoneResult(boolean result, LogInCodeFragment codeFragment) {
        if(result) showFragment(codeFragment);
        else {
            showFragment(phoneFragment);
            AlertDialogFragment errorDialog = new AlertDialogFragment(getString(R.string.error_phone),
                    getString(R.string.error_phone_text));
            errorDialog.show(getSupportFragmentManager(), "PhoneError");
        }
    }


    public void onGetRostersResult(boolean result, ArrayList<Registration> registrations) {
        if(result) {
            registrations.forEach(r -> registrationHandler.add(r));
            Intent i = new Intent(this, ApplicationActivity.class);
            startActivity(i);
        }
        else {
            // Handle error
        }
    }

    public void onSendCodeResult(boolean result, String id, String phone, String token) {
        if(result) {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("id", id);
            editor.putString("jwt", token);
            editor.apply();

            RequestManager.userID = id;
            RequestManager.jwt = token;

            runGetRequestTask("0");
        } else {
            // TODO show code dialog instead
            showFragment(phoneFragment);
            AlertDialogFragment errorDialog = new AlertDialogFragment(getString(R.string.error_code),
                    getString(R.string.error_code_text));
            errorDialog.show(getSupportFragmentManager(), "CodeError");
        }
    }

    @Override
    public void onSaveResult(int resultCode) { }

    private void setLocale(String locale_code) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration cf = resources.getConfiguration();
        Locale locale = new Locale(locale_code);
        cf.setLocale(locale);
        Locale.setDefault(locale);
        resources.updateConfiguration(cf, dm);
    }

    private void setLanguage() {
        Locale current = getResources().getConfiguration().locale;
        switch (current.toString()) {
            case "nb_NO":
                setLocale("no");
                break;
            default:
                setLocale("en");
        }
    }

    private void openInformation() {
        Intent i = new Intent(this, InformationActivity.class);
        startActivity(i);
    }
}