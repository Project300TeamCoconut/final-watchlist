package com.project300.movieswipe;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//this is the function that takes care of populating itemmatches.xml with the specific layout
public class SearchAdapter extends RecyclerView.Adapter<SearchViewHolders>{


    private List<SearchObject> searchList;
    private Context context;
    private int row_index=0;
    private int selectedPos = RecyclerView.NO_POSITION;

    public SearchAdapter(List<SearchObject> searchList, Context context){
        this.searchList = searchList;

        this.context = context;
    }
    @NonNull
    @Override
    public SearchViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //making sure everything fits nicely
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, null, false);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //pass ^^^ these layout params inside the layout view
        layoutView.setLayoutParams(lp);

        SearchViewHolders rcv = new SearchViewHolders((layoutView));

        return rcv;
    }
    //onbindviewholder takes care of populating the view holder with the params we passed into
    //on create view holder

    //ths holder is the one that is the WHOLE layout with the specific layout

    //the postion is the postion the object is within that recycler view
    @Override
    public void onBindViewHolder(@NonNull SearchViewHolders holder, int position) {
        //it will be called upon creation

        //if they are above or belowed they are destroyed
        //we get the postion of the view that is created at that moment

//        row_index = position;
//        if(row_index == position) {
        //and get the position of the movie name we want to show
        holder.mMatchName.setText(searchList.get(position).getMovieName());

        notifyDataSetChanged();
        notifyItemChanged(position);
        notifyAll();

//        holder.mDeleteButton.setText(matchesList(position).changeStatus());
        //  Glide.with(context).load()

        // holder.mMatchID.setText(matchesList.get(position).getMovieName());

    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

}
