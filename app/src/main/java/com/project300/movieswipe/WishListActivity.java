package com.project300.movieswipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WishListActivity extends AppCompatActivity {
    ListView mlistView;
    FirebaseUser currentUser;
    private String currentUserID, currentMovie;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list2);

        //this will allow us to scroll freely through the ListView with no hicups
     //   mlistView.setNestedScrollingEnabled(false);
       // mlistView.setHasFixedSize(true);

        currentUser = FirebaseAuth.getInstance().getCurrentUser() ;
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            currentUserID = user.getUid();
        }

        ArrayList<String> myWishList = (ArrayList<String>) getIntent().getSerializableExtra("myWatchlist");

        mlistView  = (ListView) findViewById(R.id.lvWishlist);

        // String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
        // "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
        // "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
        //  "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
        //"Android", "iPhone", "WindowsMobile" };

        // final ArrayList<String> list = new ArrayList<String>();
        // for (int i = 0; i < values.length; ++i) {
        //   list.add(values[i]);
        // }

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, myWishList);
        mlistView.setAdapter(adapter);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //on item click it remove from the wish list
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() { //remove a movie from watchlist
                                Toast.makeText(WishListActivity.this, "deleting", Toast.LENGTH_SHORT).show();

                                myWishList.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);

                                currentMovie = item;

                                // DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("Users")
                                //   .child("WatchList");
                                //.child(FilmName).setValue(null);

                                //Zahra userID
                                // String userID = "6RgUv98bTGZ3iylhHGQEMoTA0Ni2";

                                String userID = currentUserID;

                                DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(userID).child("connections").child("WatchList");

                                DatabaseReference driverRef = userDb.child(currentMovie);
                                driverRef.removeValue();
                            }
                        });
            }
        });
    }
    public void goHome(View view) {
        Intent intent = new Intent(WishListActivity.this,MainActivity.class);
        startActivity(intent);
        return;
    }
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(WishListActivity context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//public class WishListActivity extends AppCompatActivity {
//    ListView mlistView;
//
//    private RecyclerView mRecyclerView;
//    private RecyclerView.Adapter mMatchesAdapter;
//    private RecyclerView.LayoutManager mMatchesLayoutManager;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wish_list2);
//
//
//        ArrayList<String> myWishList = (ArrayList<String>) getIntent().getSerializableExtra("mylist");
//
//        mlistView  = (ListView) findViewById(R.id.lvWishlist);
//
//        // String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
//        // "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
//        // "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
//        //  "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
//        //"Android", "iPhone", "WindowsMobile" };
//
//
//        // final ArrayList<String> list = new ArrayList<String>();
//        // for (int i = 0; i < values.length; ++i) {
//        //   list.add(values[i]);
//        // }
//
//        final StableArrayAdapter adapter = new StableArrayAdapter(this,
//                android.R.layout.simple_list_item_1, myWishList);
//        mlistView.setAdapter(adapter);
//
//        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            //on item click it remove from the wish list
//            @Override
//            public void onItemClick(AdapterView<?> parent, final View view,
//                                    int position, long id) {
//                final String item = (String) parent.getItemAtPosition(position);
//                view.animate().setDuration(2000).alpha(0)
//                        .withEndAction(new Runnable() {
//                            @Override
//                            public void run() {
//                                myWishList.remove(item);
//                                adapter.notifyDataSetChanged();
//                                view.setAlpha(1);
//                            }
//                        });
//            }
//        });
//    }
//    public void goHome(View view) {
//        Intent intent = new Intent(WishListActivity.this,MainActivity.class);
//        startActivity(intent);
//        return;
//    }
//    private class StableArrayAdapter extends ArrayAdapter<String> {
//
//        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
//
//        public StableArrayAdapter(WishListActivity context, int textViewResourceId,
//                                  List<String> objects) {
//            super(context, textViewResourceId, objects);
//            for (int i = 0; i < objects.size(); ++i) {
//                mIdMap.put(objects.get(i), i);
//            }
//        }
//
//        @Override
//        public long getItemId(int position) {
//            String item = getItem(position);
//            return mIdMap.get(item);
//        }
//
//        @Override
//        public boolean hasStableIds() {
//            return true;
//        }
//
//    }
//}