package cz.eclub.xglu.xglu.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by Tom on 2. 2. 2016.
 */
public final class MeasuringRecord {

    public MeasuringRecord(){}

    public static abstract class MeasuringRecordEntry implements BaseColumns{
        public static final String TABLE_NAME = "MeasuringRecords";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_GLUCOSE_VALUE = "glucoseValue";


    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    static public final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MeasuringRecordEntry.TABLE_NAME + " (" +
                    MeasuringRecordEntry._ID + " INTEGER PRIMARY KEY," +
                    MeasuringRecordEntry.COLUMN_NAME_TIMESTAMP + TEXT_TYPE + COMMA_SEP +
                    MeasuringRecordEntry.COLUMN_NAME_GLUCOSE_VALUE + " REAL " +
            " )";

    static public final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MeasuringRecordEntry.TABLE_NAME;

    public static void addRecord(Context context,Double value){

        MeasuringRecordDbHelper mDbHelper = new MeasuringRecordDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues valuess = new ContentValues();
        valuess.put(MeasuringRecord.MeasuringRecordEntry.COLUMN_NAME_GLUCOSE_VALUE,value);
        valuess.put(MeasuringRecord.MeasuringRecordEntry.COLUMN_NAME_TIMESTAMP, MeasuringRecordDbHelper.getDateTime());

        long newRowId;
        newRowId = db.insert(
                MeasuringRecord.MeasuringRecordEntry.TABLE_NAME,
                null,
                valuess);

    }

    public static void clearDatabase(Context context){
        MeasuringRecordDbHelper mDbHelper = new MeasuringRecordDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(MeasuringRecord.SQL_DELETE_ENTRIES);
        mDbHelper.onCreate(db);
    }

}
