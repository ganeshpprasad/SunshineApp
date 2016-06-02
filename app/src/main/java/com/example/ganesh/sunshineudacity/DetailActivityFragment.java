package com.example.ganesh.sunshineudacity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
//        Log.v("STRING" , text);

        TextView textView = (TextView) view.findViewById(R.id.detail_tv);
        textView.setText(text);

        return view;
    }
}
