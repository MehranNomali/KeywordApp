package com.example.keywordapp;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keywordapp.Adapetr.WordsItemsAdapter;
import com.example.keywordapp.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> words;
    private Set<String> wordSet;
    private int currentWordIndex = 0;
    private int correct = 0;
    private int incorrect = 0;
    private double skill = 0.0;

    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        AssetManager assetManager = getAssets();
        wordSet = new HashSet<>();

        // Read the dictionary file and store the words in the wordSet
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

        binding.imgState.setImageResource(R.drawable.terrible);

        // Set up the RecyclerView
        RecyclerView wordsRecview = binding.wordsRecview;
        wordsRecview.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));

        // Manage the click event on the "Check" button
        binding.btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.wordsRecview.setVisibility(View.GONE);
                binding.txtChehk.setVisibility(View.GONE);

                if (currentWordIndex == 0) {
                    // Get the words from the EditText
                    String text = binding.edText.getText().toString();
                    text = text.replaceAll(",", " ,");
                    text = text.replaceAll("\\.", " .");
                    String[] wordArray = text.split(" ");
                    words = new ArrayList<>(Arrays.asList(wordArray));
                    binding.btnCheck.setText("Next Word");
                }

                if (currentWordIndex < words.size()) {
                    String word = words.get(currentWordIndex);
                    checkWord(word, binding);
                    currentWordIndex++;
                    updateScoreDisplay(binding);

                    if (currentWordIndex == words.size()) {
                        currentWordIndex = 0;
                    }
                }
            }
        });
    }

    private void checkWord(String word, ActivityMainBinding binding) {
        ArrayList<String> wordsSug = new ArrayList<>();

        if (Character.isUpperCase(word.charAt(0))) {
            word = word.substring(0, 1).toLowerCase() + word.substring(1);
        }

        if (word.trim().isEmpty() || word.contains(",") || word.contains(".")) {
            if (word.contains(",")){
                stringBuilder.append(", ");
                //binding.txtChehk.setText("The word \" , \" is correct in terms of spelling");
            }else if (word.contains(".")){
                stringBuilder.append(", ");
                //binding.txtChehk.setText("The word \" . \" is correct in terms of spelling");
            }
        } else {
            if (wordSet.contains(word)) {
                correct++;
                binding.txtChehk.setText("The word \" " + word + " \" is correct in terms of spelling");
                stringBuilder.append(word + " ");
                binding.txtChehk.setVisibility(View.VISIBLE);
            } else {
                incorrect++;
                for (String wordIncorrect : wordSet) {
                    if (editDistance(word, wordIncorrect) < 2) {
                        wordsSug.add(wordIncorrect);
                        Log.e("main", "yes edit-distance " + word + " : " + wordIncorrect);
                    }
                }
                binding.wordsRecview.setVisibility(View.VISIBLE);
                binding.wordsRecview.setAdapter(new WordsItemsAdapter(wordsSug, item -> {
                    Toast.makeText(MainActivity.this, "Selected: " + item, Toast.LENGTH_SHORT).show();
                    stringBuilder.append(item + " ");
                }));
            }
        }
    }

    private void updateScoreDisplay(ActivityMainBinding binding) {
       /* binding.imgState.setVisibility(View.VISIBLE);
        binding.txtState.setVisibility(View.VISIBLE);*/
        binding.txtShow.setText(stringBuilder);
        binding.cdState.setVisibility(View.VISIBLE);
        double correctDouble = (double) correct;
        double incorrectDouble = (double) incorrect;
        skill = (correctDouble * 100) / (correctDouble + incorrectDouble);
        binding.txtCorrectNum.setText(String.valueOf(correct));
        binding.txtIncorrectNum.setText(String.valueOf(incorrect));
        String formattedSkill = String.format("%.1f", skill);
        binding.txtMasterySpellingNum.setText(formattedSkill + " %");
        //binding.txtShow.setText("");
        if (skill >= 85) {
            binding.imgState.setImageResource(R.drawable.very_good);
            binding.txtState.setText(R.string.veryGood_text);
        } else if (skill >= 70) {
            binding.imgState.setImageResource(R.drawable.good);
            binding.txtState.setText(R.string.good_text);
        } else if (skill >= 55) {
            binding.imgState.setImageResource(R.drawable.normal);
            binding.txtState.setText(R.string.normal_text);
        } else if (skill >= 40) {
            binding.imgState.setImageResource(R.drawable.little_bad);
            binding.txtState.setText(R.string.littleBad_text);
        } else if (skill >= 25) {
            binding.imgState.setImageResource(R.drawable.bad);
            binding.txtState.setText(R.string.bad_text);
        } else {
            binding.imgState.setImageResource(R.drawable.terrible);
            binding.txtState.setText(R.string.terrible_text);
        }
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