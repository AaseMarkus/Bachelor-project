package oslomet.bachelorprosjekt.molendoansatt.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import oslomet.bachelorprosjekt.molendoansatt.MainActivity;
import oslomet.bachelorprosjekt.molendoansatt.R;
import oslomet.bachelorprosjekt.molendoansatt.requests.RequestManager;

public class LogInCodeFragment extends Fragment {
    TextView tvCodeSent;
    Button sendCode, logIn;
    EditText etCodeInput;
    String phone;

    public LogInCodeFragment(String phone) {
        this.phone = phone;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_in_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeVariables();
    }

    private void initializeVariables() {
        /* --- Buttons --- */
        sendCode = getActivity().findViewById(R.id.btn_new_code);
        logIn = getActivity().findViewById(R.id.btn_login);

        /* --- EditText --- */
        etCodeInput = getActivity().findViewById(R.id.et_code_input);

        /* --- TextView --- */
        tvCodeSent = getActivity().findViewById(R.id.tv_code_sent);
        String text = getString(R.string.code_sent) + " " + phone;
        tvCodeSent.setText(text);

        /* --- OnClickListeners --- */
        sendCode.setOnClickListener((View v) -> ((MainActivity) getActivity()).sendSMS(RequestManager.phone));
        logIn.setOnClickListener((View v) -> {
            logIn.setEnabled(false);
            ((MainActivity) getActivity()).logIn(etCodeInput.getText().toString());
        });
    }
}