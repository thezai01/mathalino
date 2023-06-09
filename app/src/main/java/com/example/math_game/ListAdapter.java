package com.example.math_game;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
public class ListAdapter extends ArrayAdapter<ListData> {
    private ArrayList<ListData> dataList;

    public ListAdapter(@NonNull Context context, ArrayList<ListData> dataArrayList) {
        super(context, R.layout.list_leaderboard, dataArrayList);
        this.dataList = dataArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        ListData listData = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_leaderboard, parent, false);
        }
        TextView user = view.findViewById(R.id.leaderUser);
        TextView score = view.findViewById(R.id.leaderScore);
        TextView standing = view.findViewById(R.id.leaderNo);

        user.setText(listData.name);
        score.setText(String.valueOf(listData.score));
        standing.setText(String.valueOf(listData.standing));
        return view;
    }

    // Method to update the data source of the adapter with new data
    public void updateData(ArrayList<ListData> newDataList) {
        dataList.clear();  // Clear the existing data
        dataList.addAll(newDataList);  // Add the new data
        notifyDataSetChanged();  // Notify the adapter of the data change
    }
}


