package com.project300.movieswipe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

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
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyAdapter adapter;

    private ArrayList<MovieModelClass> movieList = new ArrayList<>();

    SwipeFlingAdapterView flingContainer;

    private static String JSON_URL = "https://api.themoviedb.org/3/movie/popular?api_key=8099f5720bad1f61f020fdbc855f73db";

    public  ArrayList<String> myWishList = new ArrayList<String>();

    private int i;

    private FirebaseAuth mAuth;

    EditText GetUserID;

    private String currentUId;

    private DatabaseReference usersDb;

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String FilmName;

    FirebaseUser currentFirebaseUser;

    String MatchFriendID ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        GetUserID = findViewById(R.id.UserIDInput);

        GetData getData = new GetData();
        getData.execute();


        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null){
            currentUId = user.getUid();
            Log.e("User", currentUId);
        }else{
            Log.e("User", "not found");
        }


        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                movieList.remove(0);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null){
                    currentUId = user.getUid();
                }

                MovieModelClass items = (MovieModelClass) dataObject;
                FilmName = items.getName();
                //male
                usersDb.child(currentUId).child("connections").child("nope").child(FilmName).setValue(true);



                Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null){
                    currentUId = user.getUid();
                }

                MovieModelClass items = (MovieModelClass) dataObject;
                FilmName = items.getName();

                //male
                //you have to be logged in as this for it to work
                usersDb.child(currentUId).child("connections").child("yep").child(FilmName).setValue(true);

                isConnectionMatch(currentUId, dataObject);


                Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        //on click item added to wishlist
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
               // Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null){
                    currentUId = user.getUid();
                }

                MovieModelClass items = (MovieModelClass) dataObject;
                FilmName = items.getName();

                //save movie name to firebase in watchlist
                usersDb.child(currentUId).child("connections").child("WatchList").child(FilmName).setValue(true);

                isConnectionMatch(currentUId, dataObject);

                //add movie name to the list
                // myList.add(FilmName);

                if (myWishList.contains(FilmName)) { // <- look for item!
                    // ... item already in list
                    Toast.makeText(MainActivity.this, "this movie is alrready saved in the watchlist", Toast.LENGTH_SHORT).show();
                } else {

                    myWishList.addAll(Arrays.asList(FilmName));
                    Toast.makeText(MainActivity.this, " saved to the watchlist", Toast.LENGTH_SHORT).show();
                }

              //  myWishList.addAll(Arrays.asList(FilmName));


            }
        });

    }

    public void GettingUserID(View view) {

        MatchFriendID = GetUserID.getText().toString();
         GetUserID.setText("");

    }

    public void FindUserID(View view) {

    }


        private void isConnectionMatch(String userIDm, Object dataObject) {

        MovieModelClass items = (MovieModelClass) dataObject;
        FilmName = items.getName();
        String userID = MatchFriendID;

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            currentUId = user.getUid();
        }

        //female////

        DatabaseReference currentUserConnectionsDB = usersDb.child(userID).child("connections").child("yep").child(FilmName);

        currentUserConnectionsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //to know if this child exists // if it exists then create childs // so if movie name exists
                if(snapshot.exists()){
                    Toast.makeText(MainActivity.this, "new connection", Toast.LENGTH_LONG).show();
                    //so this is a male swiping when a female has already swiped

                    //usersDb.child(oppositeUserSex).child(snapshot.getKey()).child("connections").child("matches").child(currentUId).setValue(true);

                    //female
                    usersDb.child(userID).child("connections").child("matches").child(snapshot.getKey()).setValue(true);

                    //male
                    usersDb.child(currentUId).child("connections").child("matches").child(snapshot.getKey()).setValue(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
        return;
    }
    // go to wish list activity
    public void goToWishList(View view) {
        Intent intent = new Intent(getApplicationContext(), WishListActivity.class);

        //pass the myWishList to the wish list activity
        intent.putExtra("myWatchlist", myWishList);
        startActivity(intent);
    }

    public void clearMovies(View view) {
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userDbMatches = FirebaseDatabase.getInstance().getReference().child("Users").child(currentFirebaseUser.getUid()).child("connections").child("matches");

        DatabaseReference userDbYep = FirebaseDatabase.getInstance().getReference().child("Users").child(currentFirebaseUser.getUid()).child("connections").child("yep");

        DatabaseReference userDbNope = FirebaseDatabase.getInstance().getReference().child("Users").child(currentFirebaseUser.getUid()).child("connections").child("nope");

        DatabaseReference userDbWatchList = FirebaseDatabase.getInstance().getReference().child("Users").child(currentFirebaseUser.getUid()).child("connections").child("WatchList");

        userDbMatches.removeValue();
        userDbNope.removeValue();
        userDbYep.removeValue();
        userDbWatchList.removeValue();
    }

    public void goSearch(View view) {
        Intent iSearch = new Intent(getApplicationContext(), SearchActivity.class);
        startActivity(iSearch);
    }

    public class GetData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            String current = "";

            try {
                URL url;
                HttpURLConnection urlConnection = null;

                try {

                    url = new URL(JSON_URL);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream is = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);

                    int data = isr.read();
                    while (data != -1) {
                        current += (char) data;
                        data = isr.read();
                    }

                    return current;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    ;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        //  urlConnection.disconnect();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return current;
        }

        @Override
        protected void onPostExecute(String s) {


            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("results");


                movieList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    MovieModelClass model = new MovieModelClass();

                    //  model.setId(jsonObject1.getString("vote_average"));
                    model.setName(jsonObject1.getString("title"));
                      model.setImg(jsonObject1.getString("poster_path"));


                    //  moviename = jsonObject1.getString("title");
                    //   model.setName(moviename);

                    movieList.add(model);
                }

                // arrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.item, R.id.helloText, movieList);

                adapter = new MyAdapter(MainActivity.this, R.layout.item, movieList);
                flingContainer.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    public void goToMatches(View view){

        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
        intent.putExtra("FRIENDID", MatchFriendID);
        startActivity(intent);
        return;
    }



    public void logoutUser(View view) {

        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
        startActivity(intent);
        finish();
        return;

    }



}



