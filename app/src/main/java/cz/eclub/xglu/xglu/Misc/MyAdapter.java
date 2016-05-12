package cz.eclub.xglu.xglu.Misc;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cz.eclub.xglu.xglu.Database.MeasuringRecord;
import cz.eclub.xglu.xglu.R;

/**
 * Created by Tom on 2. 2. 2016.
 */
public class MyAdapter extends CursorAdapter {
    private final LayoutInflater mInflater;

    public MyAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String time = cursor.getString(cursor.getColumnIndex(MeasuringRecord.MeasuringRecordEntry.COLUMN_NAME_TIMESTAMP));
        Double glucose = cursor.getDouble(cursor.getColumnIndex(MeasuringRecord.MeasuringRecordEntry.COLUMN_NAME_GLUCOSE_VALUE));


        ((TextView) view.findViewById(R.id.tvListItemDate)).setText(time);

        String glucoseText = String.format("%.1f", glucose)+" mmol/l";
        if(glucose<0) glucoseText = "Error (LO)";
        if(glucose<-10) glucoseText = "Error (HI)";

        ((TextView) view.findViewById(R.id.tvListItemGlucose)).setText(glucoseText);
    }
}