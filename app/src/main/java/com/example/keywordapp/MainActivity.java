package com.example.keywordapp;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keywordapp.Adapetr.WordsItemsAdapter;
import com.example.keywordapp.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> dictionary;
    private Set<String> wordSet;
    static String sug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        AssetManager assetManager = getAssets();
        //List<String> fileLines = new ArrayList<>();
        wordSet = new HashSet<>();

        //read file dictionary in mobile in directory documents and save all words in HashSet
        try {
            InputStream inputStream = assetManager.open("dictionarys.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                wordSet.add(line);
            }
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        ArrayList<String> wordsSug = new ArrayList<>();
        binding.imgState.setImageResource(R.drawable.terrible);

        RecyclerView wordsRecview = binding.wordsRecview;
        wordsRecview.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));


        //manege events click in button Check in layout main activity. all words in edit text save to string array
        binding.btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int correct = 0;
                int incorrect = 0;
                double skill = 0.0;
                StringBuilder stringBuilder = new StringBuilder();
                binding.txtShow.setText(binding.edText.getText());
                String text = binding.txtShow.getText().toString();
                text = text.replaceAll(",", " ,");
                text = text.replaceAll("\\.", " .");
                String[] words = text.split(" ");

                for (String word : words) {

                    if (Character.isUpperCase(word.charAt(0))) {
                        word = word.substring(0, 1).toLowerCase() + word.substring(1);
                    }

                    if (word.trim().isEmpty() || word.contains(",") || word.contains(".")) {
                        continue;
                    } else {
                        if (wordSet.contains(word)) {
                            correct++;
                        } else {
                            incorrect++;
                            for (String wordIncorrect : wordSet) {
                                if (editDistance(word, wordIncorrect) < 2) {
                                    wordsSug.add(wordIncorrect);
                                    Log.e("main", "yes edit-distance " + word + " : " + wordIncorrect);
                                }
                            }
                            binding.wordsRecview.setAdapter(new WordsItemsAdapter(wordsSug , item -> {
                                Toast.makeText(MainActivity.this, "Selected: " + item, Toast.LENGTH_SHORT).show();
                            }));

                        }
                    }

                    stringBuilder.append(word + " ");
                }
                double correctDouble = (double) correct;
                double incorrectDouble = (double) incorrect;
                skill = (correctDouble * 100) / (correctDouble + incorrectDouble);
                binding.txtCorrectNum.setText(String.valueOf(correct));
                binding.txtIncorrectNum.setText(String.valueOf(incorrect));
                String formattedSkill = String.format("%.1f", skill);
                binding.txtMasterySpellingNum.setText(formattedSkill + " %");
                binding.txtShow.setText(stringBuilder);
            }
        });
    }


    public static int editDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();

        int[][] dp = new int[m + 1][n + 1];

        // Initialize the first row and column
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        // Compute the edit distance
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
                }
            }
        }

        return dp[m][n];
    }


}