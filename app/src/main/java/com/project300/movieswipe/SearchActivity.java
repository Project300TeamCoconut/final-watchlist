package com.project300.movieswipe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    //    private static String JSON_URL = "https://api.themoviedb.org/3/movie/popular?api_key=8099f5720bad1f61f020fdbc855f73db";
    private static String JSON_URL_TEMPLATE = "https://api.themoviedb.org/3/search/movie?api_key=8099f5720bad1f61f020fdbc855f73db&query=";
    private static String JSON_URL, userInput, test;

    //EditText etMovieName;
    TextView tvURLTest;
    List<MovieModelClass> movieList;
    RecyclerView recyclerView;
    SearchView etMovieName, svMovieName2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        // etMovieName = findViewById(R.id.etMovieName);
//        tvURLTest = findViewById(R.id.tvURLTest);

        etMovieName = findViewById(R.id.svMovieName);


        // perform set on query text listener event
        etMovieName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // do something on text submit

                //Intent MoviePage = new Intent(view.getContext(), MovieActivity.class);
                // userInput = etMovieName.getText().toString();
                //  Intent MoviePage = new Intent(view.getContext(), MovieActivity.class);

                userInput = etMovieName.getQuery().toString();
                userInput = userInput.replace(" ", "%20");
                Intent intent = new Intent(getApplicationContext(), MovieActivity.class);
                // MoviePage.putExtra("userInput", userInput);
                intent.putExtra("userInput", userInput);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // do something when text changes
                return false;
            }
        });

    }

    public void goHome(View view) {
        Intent iHome = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(iHome);
    }



/*
    //searchView.getQuery()
    public void searchMovie(View view) {
        Intent MoviePage = new Intent(view.getContext(), MovieActivity.class);
       // userInput = etMovieName.getText().toString();
        userInput = etMovieName.getQuery().toString();
        MoviePage.putExtra("userInput", userInput);
//        tvURLTest.setText(JSON_URL_TEMPLATE + userInput);
        startActivity(MoviePage);
    }

 */
}