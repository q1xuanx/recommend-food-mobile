package com.example.food_ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChoseFoodActivity extends AppCompatActivity {

    private List<Spinner> spinners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_chose_food);

        LinearLayout spinnerContainer = findViewById(R.id.display_chose);
        Button submitButton = findViewById(R.id.submitButton);

        // Nhận dữ liệu từ Intent
        String dataString = getIntent().getStringExtra("data");
        try {
            JSONObject data = new JSONObject(dataString);
            Iterator<String> keys = data.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                JSONArray foodArray = data.getJSONArray(key);

                // Tạo TextView cho từng loại thực phẩm
                TextView textView = new TextView(this);
                textView.setText(key);
                textView.setTextSize(18);
                textView.setPadding(0, 20, 0, 10);
                spinnerContainer.addView(textView);

                // Tạo Spinner cho từng loại thực phẩm
                Spinner spinner = new Spinner(this);
                String[] foodItems = new String[foodArray.length()];
                for (int i = 0; i < foodArray.length(); i++) {
                    JSONObject foodItem = foodArray.getJSONObject(i);
                    foodItems[i] = foodItem.getString("nameOfFood");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, foodItems);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinnerContainer.addView(spinner);
                spinners.add(spinner);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Có lỗi xảy ra khi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện click cho nút gửi
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thu thập dữ liệu từ các Spinner
                ArrayList<String> selectedItems = new ArrayList<>();
                for (Spinner spinner : spinners) {
                    selectedItems.add(spinner.getSelectedItem().toString());
                }
                // Gọi API và điều hướng đến RecommendationActivity
                new RecommendFoodTask(selectedItems).execute();
            }
        });
    }

    private class RecommendFoodTask extends AsyncTask<Void, Void, String> {
        private ArrayList<String> selectedItems;

        public RecommendFoodTask(ArrayList<String> selectedItems) {
            this.selectedItems = selectedItems;
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("https://055b-118-70-31-97.ngrok-free.app/food/recommend-food");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                String requestBody = new JSONArray(selectedItems).toString();
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                out.write(requestBody.getBytes());
                out.flush();
                out.close();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Điều hướng đến RecommendationActivity và truyền kết quả
            Intent intent = new Intent(ChoseFoodActivity.this, RecommendActivity.class);
            intent.putExtra("recommendationResult", result);
            startActivity(intent);
        }
    }
}
