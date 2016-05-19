package cz.eclub.xglu.xglu.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import cz.eclub.xglu.xglu.Database.MeasuringRecord;
import cz.eclub.xglu.xglu.Misc.CalibrationData;
import cz.eclub.xglu.xglu.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class DebugFragment extends Fragment {
    CalibrationData c;
    SharedPreferences sharedPref;
    Switch s;

    EditText glucose1,glucose2,xgluvalue1,xgluvalue2;

    Button btnSaveCalibration;

    public DebugFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        c = new CalibrationData(getActivity());
        glucose1.setText(c.glucose1+"");
        glucose2.setText(c.glucose2+"");
        xgluvalue1.setText(c.xgluvalue1+"");
        xgluvalue2.setText(c.xgluvalue2+"");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_debug, container, false);

        setDeleteDB(v);
        setSwitch(v);
        setCalibration(v);

        return v;
    }

    private void setDeleteDB(View v){
        ((Button)v.findViewById(R.id.btDeleteDB)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeasuringRecord.clearDatabase(getContext());
            }
        });
    }

    private void setSwitch(View v){
        s = ((Switch)v.findViewById(R.id.switch1));
        s.setChecked(sharedPref.getBoolean("debugMode",false));
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("debugMode",isChecked);
                Log.d("tmp", "debug "+isChecked);
                editor.commit();
                Toast.makeText(getContext(), "Debug mode: "+isChecked, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCalibration(View v){
        glucose1 = (EditText)v.findViewById(R.id.etGlucose1);
        glucose2 = (EditText)v.findViewById(R.id.etGlucose2);
        xgluvalue1 = (EditText)v.findViewById(R.id.etXgluValue1);
        xgluvalue2 = (EditText)v.findViewById(R.id.etXgluValue2);

        btnSaveCalibration = (Button) v.findViewById(R.id.btnSaveCalibration);

        btnSaveCalibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                c.glucose1=Float.valueOf(glucose1.getText().toString());
                c.glucose2=Float.valueOf(glucose2.getText().toString());
                c.xgluvalue1=Integer.valueOf(xgluvalue1.getText().toString());
                c.xgluvalue2=Integer.valueOf(xgluvalue2.getText().toString());

                Toast.makeText(getContext(), "Calibration data saved", Toast.LENGTH_SHORT).show();
                c.saveData();
            }
        });

    }


}
