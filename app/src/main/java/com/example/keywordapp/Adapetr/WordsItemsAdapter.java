package com.example.keywordapp.Adapetr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keywordapp.R;

import java.util.ArrayList;
import java.util.List;

public class WordsItemsAdapter extends RecyclerView.Adapter<WordsItemsAdapter.ViewHolder> {
    List<String> wordsSug = new ArrayList<>();

    //private List<String> data;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String item);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.txtWord);
        }
    }
    public WordsItemsAdapter(List<String> words , OnItemClickListener listener){
        wordsSug = words;
        this.listener = listener;

    }

    @NonNull
    @Override
    public WordsItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recview_wrods , parent , false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull WordsItemsAdapter.ViewHolder holder, int position) {
        //holder.text.setText(wordsSug.get(position));
        String item = wordsSug.get(position);
        holder.text.setText(item);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return wordsSug.size();
    }

}
