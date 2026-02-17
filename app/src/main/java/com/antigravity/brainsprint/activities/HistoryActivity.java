package com.antigravity.brainsprint.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.antigravity.brainsprint.R;
import com.antigravity.brainsprint.managers.ScoreManager;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ScoreManager scoreManager = new ScoreManager(this);
        LinearLayout container = findViewById(R.id.historyContainer);
        List<Integer> scores = scoreManager.getScoreHistory();

        if (scores.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No runs recorded yet.");
            empty.setTextColor(getResources().getColor(R.color.white_muted));
            empty.setTextSize(18);
            empty.setGravity(Gravity.CENTER);
            container.addView(empty);
        } else {
            for (int i = 0; i < scores.size(); i++) {
                TextView item = new TextView(this);
                item.setText((i + 1) + ".    " + scores.get(i) + " PTS");
                item.setTextColor(getResources().getColor(R.color.white_pure));
                item.setTextSize(20);
                item.setPadding(32, 32, 32, 32);
                item.setBackgroundResource(R.drawable.bg_history_item);
                
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 
                    LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 16);
                item.setLayoutParams(lp);
                
                container.addView(item);
            }
        }

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
}
