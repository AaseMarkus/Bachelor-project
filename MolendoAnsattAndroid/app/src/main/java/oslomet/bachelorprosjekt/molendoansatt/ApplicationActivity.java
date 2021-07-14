package oslomet.bachelorprosjekt.molendoansatt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

import oslomet.bachelorprosjekt.molendoansatt.fragments.AlertDialogFragment;
import oslomet.bachelorprosjekt.molendoansatt.fragments.HomeFragment;
import oslomet.bachelorprosjekt.molendoansatt.fragments.WeeklyPlanFragment;
import oslomet.bachelorprosjekt.molendoansatt.objects.Registration;
import oslomet.bachelorprosjekt.molendoansatt.objects.RegistrationHandler;
import oslomet.bachelorprosjekt.molendoansatt.requests.GetRequestTask;
import oslomet.bachelorprosjekt.molendoansatt.requests.RequestManager;

public class ApplicationActivity extends AppCompatActivity implements GetRequestTask.GetRequestListener,
ConfirmationDialog.DialogClickListener {
    /*
    LogInPhoneFragment phoneFragment;
    LogInCodeFragment codeFragment; */
    WeeklyPlanFragment weeklyPlanFragment;
    HomeFragment homeFragment;
    RegistrationHandler registrationHandler;
    LocalDateTime servertime;
    TextView toolbarTitle;

    ImageView ivHome, ivCalendar, ivSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        runGetRequestTask("2");

        initializeVariables();
    }


    private void initializeVariables() {
        registrationHandler = MainActivity.getRegistrationHandler();

        //initializeTestObjects();

        /* --- Fragments --- */
        weeklyPlanFragment = new WeeklyPlanFragment();
        homeFragment = new HomeFragment();

        ivHome = this.findViewById(R.id.iv_home);
        ivCalendar = this.findViewById(R.id.iv_calendar);
        ivSignOut = this.findViewById(R.id.iv_sign_out);
        toolbarTitle = this.findViewById(R.id.tv_toolbar_title);
        ivHome.setOnClickListener(v -> showFragment(homeFragment, "home"));
        ivCalendar.setOnClickListener(v -> showFragment(weeklyPlanFragment, "weeklyPlan"));
        ivSignOut.setOnClickListener(v -> showSignOutDialog());
    }

    public void runGetRequestTask(String... request) {
        GetRequestTask task = new GetRequestTask(this, this, request);
        RequestManager.getManagerInstance().runTask(task);
    }

    public void showFragment(Fragment fragment, String fragmentTag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content_fragment, fragment).
                addToBackStack(fragmentTag).commit();
    }

    public void onServerTimeResult(LocalDateTime servertime) {
        this.servertime = servertime;
        registrationHandler.setCurrent_date(servertime);
        showFragment(homeFragment, "home");
    }

    @Override
    public void onGetRostersResult(boolean result, ArrayList<Registration> registrations) { }

    private void showSignOutDialog() {
        ConfirmationDialog dialog = new ConfirmationDialog(getString(R.string.log_out_dialog_title),
                getString(R.string.log_out_dialog_description), 0);
        dialog.show(getSupportFragmentManager(),
                "New confirmation dialog");
    }

    private void signOut() {
        MainActivity.getJwt().signOut();

        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public void onOkClick(int result) { signOut(); }

    // Updates text element in toolbar
    public void setToolbarTitle(String text) { toolbarTitle.setText(text); }
}