package cz.eclub.xglu.xglu.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import cz.eclub.xglu.xglu.Database.MeasuringRecord;
import cz.eclub.xglu.xglu.Database.MeasuringRecordDbHelper;
import cz.eclub.xglu.xglu.MeasuringActivity;
import cz.eclub.xglu.xglu.Misc.MyAdapter;
import cz.eclub.xglu.xglu.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MeasurementHistoryFragment extends Fragment {
    private ListView listView;
    private FloatingActionButton startMeasurement;

    public MeasurementHistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_measurement_history, container, false);

        listView = (ListView) v.findViewById(R.id.listView);
        startMeasurement = (FloatingActionButton) v.findViewById(R.id.btnStartMeasurement);

        startMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MeasuringActivity.class);
                startActivityForResult(intent, 64);
            }
        });


        refreshHistory();
        return v;
    }

    public void refreshHistory(){
        MeasuringRecordDbHelper mDbHelper = new MeasuringRecordDbHelper(getContext());
        SQLiteDatabase dbr = mDbHelper.getReadableDatabase();

        String[] projection = {
                MeasuringRecord.MeasuringRecordEntry.COLUMN_NAME_TIMESTAMP,
                MeasuringRecord.MeasuringRecordEntry.COLUMN_NAME_GLUCOSE_VALUE,
                MeasuringRecord.MeasuringRecordEntry._ID
        };

        String sortOrder =
                MeasuringRecord.MeasuringRecordEntry.COLUMN_NAME_TIMESTAMP + " DESC";

        Cursor c = dbr.query(
                MeasuringRecord.MeasuringRecordEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        MyAdapter ma = new MyAdapter(getContext(),c);
        listView.setAdapter(ma);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==64&&resultCode==1){
            Log.d("OGTT", "save result normal " + requestCode + " " + resultCode);
            double glucose = data.getDoubleExtra("glucose",0);
            if(glucose!=0){
                MeasuringRecord.addRecord(getContext(), glucose);
                refreshHistory();
            }

        }
    }

}
