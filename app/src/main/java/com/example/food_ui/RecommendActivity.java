package com.example.food_ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_ui.adapter.RecommendAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecommendActivity extends AppCompatActivity {

    private RecyclerView recommendationRecyclerView;
    private RecommendAdapter recommendationAdapter;
    private List<String> recommendedFoods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_recommend);
        recommendationRecyclerView = findViewById(R.id.recommendationRecyclerView);
        recommendationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        String recommendationResult = getIntent().getStringExtra("recommendationResult");
        try {
            JSONObject jsonObject = new JSONObject(recommendationResult);
            int code = jsonObject.getInt("code");
            String message = jsonObject.getString("message");

            if (code == 200) {
                JSONObject data = jsonObject.getJSONObject("data");
                Iterator<String> keys = data.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    recommendedFoods.add(key);
                }

                recommendationAdapter = new RecommendAdapter(RecommendActivity.this, recommendedFoods);
                recommendationRecyclerView.setAdapter(recommendationAdapter);
            } else {
                Toast.makeText(RecommendActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RecommendActivity.this, "Có lỗi xảy ra khi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
        }
        Button back = findViewById(R.id.btnBackToMain);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(RecommendActivity.this, MainActivity.class);
                startActivity(main);
                finish();
            }
        });
    }
}
