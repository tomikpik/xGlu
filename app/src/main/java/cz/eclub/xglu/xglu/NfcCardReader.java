package cz.eclub.xglu.xglu;

/**
 * Created by Tom on 11. 5. 2016.
 */
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * Created by Tom on 05.05.2016.
 */
public class NfcCardReader implements NfcAdapter.ReaderCallback {
    private MeasuringActivity mainActivity;
    private NfcV nfcV;
    private ScheduledExecutorService scheduledExecutorService;
    private Thread t;
    private final Object lock = new Object();
    private ScheduledFuture<?> future;
    private Handler mHandler;
    private static int counter=0;
    private GlucoseToolbox glucoseToolbox;

    public NfcCardReader(MeasuringActivity mainActivity){


        this.mainActivity=mainActivity;
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        mHandler = new Handler();
        glucoseToolbox=new GlucoseToolbox(mainActivity);


        t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Log.d("X.THERM","READING");
                    byte[] a = {0x02,0x23,0x05,0x00};
                    byte[] result;
                    synchronized (lock) {
                        result=nfcV.transceive(a);
                    }
                    processResult(result);
                } catch (IOException e) {
                    Log.e("X.THERM",e.getMessage());
                }
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {

            try {
                Log.d("X.THERM","READING");
                byte[] a = {0x02,0x23,0x05,0x00};
                byte[] result;
                synchronized (lock) {
                    result=nfcV.transceive(a);
                }
                processResult(result);

                counter=0;
            } catch (Exception e){
                counter++;
                Log.e("error",e.getMessage()+" "+counter);

            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                if(counter<3) {
                    mHandler.postDelayed(mStatusChecker, 900);
                } else {
                    counter=0;
                    mainActivity.updateConnectedAndStatus(false,0);
                }
            }
        }
    };

    @Override
    public void onTagDiscovered(Tag tag) {


        try {
            nfcV = NfcV.get(tag);
            nfcV.close();
            nfcV.connect();
            mHandler.removeCallbacks(mStatusChecker);
            mHandler.postDelayed(mStatusChecker,200);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /*@Override
    public void onTagDiscovered(Tag tag) {
        Log.d("X.GLU","discovered");
        synchronized (lock) {
            if (future != null) {
                future.cancel(true);
                future = null;
            }
            nfcV = NfcV.get(tag);
            try {
                nfcV.connect();
                future = scheduledExecutorService.scheduleAtFixedRate(t, 0, 750, TimeUnit.MILLISECONDS);
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }*/

    private void processResult(byte[] result){
        glucoseToolbox.analyzeData(result);
    }


    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static int byteToInt(byte first, byte second) {
        int value = (second & 0xFF) << (Byte.SIZE );
        value |= (first & 0xFF);
        return value;
    }

    public static int byteArrayToLeInt(byte first, byte second) {
        int value = (second & 0xFF) << (Byte.SIZE);
        value |= (first & 0xFF);
        return value;
    }

    public void stop() {
        synchronized (lock) {
            if (future != null) {
                future.cancel(true);
            }
            scheduledExecutorService.shutdown();
        }
    }

}
