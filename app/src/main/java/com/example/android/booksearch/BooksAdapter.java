package com.example.android.booksearch;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;



public class BooksAdapter extends ArrayAdapter {

    public BooksAdapter(Activity c, ArrayList<Book> b) {
        super(c, 0, b);
    }

    public BooksAdapter(Activity c) {
        super(c, 0);
    }

    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Book b = (Book) getItem(position);
        if (b != null) {
            TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(b.getTitle());
            TextView author = (TextView) convertView.findViewById(R.id.author);
            author.setText("By: " + b.getAuthors());

        }
        return convertView;
    }
}
