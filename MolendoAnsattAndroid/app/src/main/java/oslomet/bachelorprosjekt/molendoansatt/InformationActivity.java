package oslomet.bachelorprosjekt.molendoansatt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class InformationActivity extends AppCompatActivity {
    String[] creditsArray;

    ImageView ivBack;
    TextView tvCredits, tvInformationTitle, tvCreditsTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        initializeVariables();
        setCreditsText();
    }

    private void initializeVariables() {
        ivBack = this.findViewById(R.id.iv_information_back);
        ivBack.setOnClickListener(v -> finish());

        tvCredits = this.findViewById(R.id.tv_credits);

        tvCreditsTitle = this.findViewById(R.id.tv_credits_title);
        tvCreditsTitle.setText(R.string.credits_title);

        tvInformationTitle = this.findViewById(R.id.tv_information_title);
        tvInformationTitle.setText(R.string.information_title);

        creditsArray = getResources().getStringArray(R.array.credits);
    }

    private void setCreditsText() {
        StringBuilder credits = new StringBuilder();

        for (String s : creditsArray) { credits.append(s).append("\n\n"); }
        tvCredits.setText(credits.toString());
    }
}