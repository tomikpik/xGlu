package cz.eclub.xglu.xglu;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Log;

import java.util.Arrays;

import cz.eclub.xglu.xglu.Database.MeasuringRecord;
import cz.eclub.xglu.xglu.NfcCardReader;

/**
 * Created by Tom on 22. 11. 2015.
 */
public class GlucoseToolbox {
    private MeasuringActivity activity;

    public GlucoseToolbox(MeasuringActivity activity) {
        this.activity = activity;
    }



    public void analyzeData(byte[] result) {

        byte state = result[1];
        String a = NfcCardReader.ByteArrayToHexString(result);

        Log.d("ahoj", "state: "+state);

        String glucoseValueText = "--";

        int gluc = 0;
        int temp = 0;
        double glucoseValue = 0;
        double temperature = 0;
        if(state==3){
            gluc = Integer.parseInt(a.substring(4,7),16);
            temp = Integer.parseInt(a.substring(7,10),16);

            temperature = ((temp/2.0)-500)/10.0;

            Log.d("ahoj",gluc+" "+temp+" "+temperature);




            if (gluc != 0) {
                glucoseValue = ((gluc - 400) * 0.02) + 5;
                if (glucoseValue < 3) {
                    glucoseValueText = "LO";
                    glucoseValue = 0;
                } else if (glucoseValue > 12) {
                    glucoseValueText = "HI";
                    glucoseValue = 12;
                } else {
                    glucoseValueText = String.format("%.1f", glucoseValue);
                }
            }

        }


        activity.updateConnectedAndStatus(true,state);
        activity.updateGui(gluc, String.format("%.1f",temperature),state, (int) (glucoseValue - 3) * 100, glucoseValueText,a,glucoseValue);

    }
}
