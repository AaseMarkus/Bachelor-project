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
import android.widget.RelativeLayout;

import oslomet.bachelorprosjekt.molendoansatt.MainActivity;
import oslomet.bachelorprosjekt.molendoansatt.R;

public class LogInPhoneFragment extends Fragment {
    Button sendSMS;
    EditText etPhoneInput;

    public LogInPhoneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_in_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeVariables();
    }

    private void initializeVariables() {
        /* --- Buttons --- */
        sendSMS = getActivity().findViewById(R.id.btn_phone_button);
        etPhoneInput = getActivity().findViewById(R.id.et_phone_input);

        /* --- OnClickListeners --- */
        sendSMS.setOnClickListener((View v) -> ((MainActivity) getActivity()).
                sendSMS(etPhoneInput.getText().toString()));
    }
}