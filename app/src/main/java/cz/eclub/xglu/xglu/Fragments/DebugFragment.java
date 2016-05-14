package cz.eclub.xglu.xglu.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

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

        Switch s = ((Switch)v.findViewById(R.id.switch1));

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        s.setChecked(sharedPref.getBoolean("debug",false));

        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setDebug(isChecked);
            }
        });

        return v;
    }

    public void deleteDatabase(View v){
        MeasuringRecord.clearDatabase(getContext());
    }

    public void setDebug(Boolean b){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("debug",b);
        editor.commit();


        Toast.makeText(getContext(), "Debug mode: "+b, Toast.LENGTH_SHORT).show();
    }


}
