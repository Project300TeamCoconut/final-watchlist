package com.project300.movieswipe;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieActivity extends AppCompatActivity {

    //    private static String JSON_URL = "https://api.themoviedb.org/3/movie/popular?api_key=8099f5720bad1f61f020fdbc855f73db";
    private static String JSON_URL_TEMPLATE = "https://api.themoviedb.org/3/search/movie?api_key=8099f5720bad1f61f020fdbc855f73db&query=";
    private static String JSON_URL;

    Context context;
    private static String movieName;
    private static String userInput;
//    List<MovieModelClass> movieList;
    RecyclerView recyclerView;
    TextView name_txt;
    private String currentUId;
    private FirebaseAuth mAuth;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String FilmName;
    private RecyclerView.Adapter mSearchAdapter;
    private DatabaseReference usersDb;
    Object dataObject;
    private ArrayList<MovieModelClass> movieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

//        movieName = "you";
        context = this.context;

        name_txt = findViewById(R.id.name_txt);
        userInput = getIntent().getStringExtra("userInput");

        JSON_URL = JSON_URL_TEMPLATE + userInput;

        movieList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        final Context mContext;

        mContext = this.context;
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);

        mSearchAdapter = new SearchAdapter(getDataSetSearch(), MovieActivity.this);

        recyclerView.setAdapter(mSearchAdapter);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null){
            currentUId = user.getUid();
            Log.e("User", currentUId);
        }else{
            Log.e("User", "not found");
        }

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

//        recyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(context, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override public void onItemClick(View view, int position) {
//                    }
//
//                    @Override public void onLongItemClick(View view, int position) {
//                        int itemPosition = recyclerView.getChildLayoutPosition(view);
//                        MovieModelClass item = movieList.get(itemPosition);
//                        Toast.makeText(mContext, item.toString(), Toast.LENGTH_LONG).show();
////                        Toast.makeText(MovieActivity.this, "Movie Name: " + movieName,
////                                Toast.LENGTH_LONG).show();
//
//                    }
//                })
//        );
        MovieActivity.GetData getData = new MovieActivity.GetData();
        getData.execute();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        String currentMovie = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.name_txt)).getText().toString();
                        // Hannah UserID
                        Toast.makeText(MovieActivity.this, "Movie Name: " + currentMovie, Toast.LENGTH_LONG).show();

                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if(user != null){
                            currentUId = user.getUid();
                        }

//                        MovieModelClass items = (MovieModelClass) dataObject;
//                        FilmName = items.getName();
                        usersDb.child(currentUId).child("connections").child("WatchList").child(currentMovie).setValue(true);

                    }

                    @Override public void onLongItemClick(View view, int position) {

                    }
                })
        );

    }

    public void getObject(Object dataObject) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null)
        {
            currentUId = user.getUid();
        }

        MovieModelClass items = (MovieModelClass) dataObject;
        FilmName = items.getName();

//        usersDb.child(currentUId).child("connections").child("WatchList").child(FilmName).setValue(true);
    }

    public class GetData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            String current = "";

            try{
                URL url;
                HttpURLConnection urlConnection = null;

                try{

                    url = new URL(JSON_URL);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream is = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);

                    int data = isr.read();
                    while(data != -1){
                        current +=(char) data;
                        data = isr.read();
                    }
                    return current;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(urlConnection != null){
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return current;
        }
        @Override
        protected void onPostExecute(String s){
            try{
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for(int i = 0; i< jsonArray.length(); i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);


                    MovieModelClass model = new MovieModelClass();
                    model.setId(jsonObject1.getString("vote_average"));
                    model.setName(jsonObject1.getString("title"));
                    model.setImg(jsonObject1.getString("poster_path"));

                    movieName = jsonObject1.getString("title");
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    intent.putExtra("movieName", movieName);

                    movieList.add(model);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PutDataIntoRecyclerView(movieList);
        }

        private void PutDataIntoRecyclerView(List<MovieModelClass> movieList){

            SearchAdapter2 searchAdapter2 = new SearchAdapter2(MovieActivity.this, movieList);
            recyclerView.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.HORIZONTAL));
            recyclerView.setLayoutManager(new LinearLayoutManager(MovieActivity.this, LinearLayoutManager.HORIZONTAL, false));

            recyclerView.setAdapter(searchAdapter2);
        }
    }

    private ArrayList<SearchObject> resultsMatches = new ArrayList<SearchObject>();

    private List<SearchObject> getDataSetSearch() {

        return resultsMatches;

    }

    public void returnSearch(View view) {
        Intent iSearch = new Intent(getApplicationContext(), SearchActivity.class);
        startActivity(iSearch);
    }

    public void returnMain(View view) {
        Intent iMain = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(iMain);
    }



}