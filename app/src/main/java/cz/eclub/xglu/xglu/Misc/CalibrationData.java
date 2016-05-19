package cz.eclub.xglu.xglu.Misc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Tom on 15. 5. 2016.
 */
public class CalibrationData {
    public float glucose1=5;
    public float glucose2=7;

    public int xgluvalue1=1100;
    public int xgluvalue2=1200;

    SharedPreferences sharedPref;

    public CalibrationData(Activity a){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(a);
        loadData();
    }

    public void loadData(){
        glucose1=sharedPref.getFloat("glucose1",5);
        glucose2=sharedPref.getFloat("glucose2",7);

        xgluvalue1=sharedPref.getInt("xglu1",1100);
        xgluvalue2=sharedPref.getInt("xglu2",1200);
    }

    public void saveData(){
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putFloat("glucose1",glucose1);
        editor.putFloat("glucose2",glucose2);

        editor.putInt("xglu1",xgluvalue1);
        editor.putInt("xglu2",xgluvalue2);

        editor.commit();
    }

}
