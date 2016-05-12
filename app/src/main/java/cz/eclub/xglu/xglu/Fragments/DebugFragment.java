package cz.eclub.xglu.xglu.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cz.eclub.xglu.xglu.Database.MeasuringRecord;
import cz.eclub.xglu.xglu.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class DebugFragment extends Fragment {

    public DebugFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_debug, container, false);
        ((Button)v.findViewById(R.id.btDeleteDB)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    deleteDatabase(v);
            }
        });
        return v;
    }

    public void deleteDatabase(View v){
        MeasuringRecord.clearDatabase(getContext());
    }


}
