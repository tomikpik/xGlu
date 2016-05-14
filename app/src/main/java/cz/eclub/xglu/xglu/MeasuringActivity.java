package cz.eclub.xglu.xglu;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Field;

import cz.eclub.xglu.xglu.Database.MeasuringRecord;
import cz.eclub.xglu.xglu.NfcCardReader;

public class MeasuringActivity extends AppCompatActivity {

    private static int READER_FLAGS = NfcAdapter.FLAG_READER_NFC_V;


    int previousState = 0;

    private TextView pageState;
    private TextView pageGlucosetv;
    private TextView pageTemperaturetv;
    private TextView pagePayload;
    private TextView pageSessionReg;
    private ProgressBar progressBar;
    private TextView glucoseValueTv;
    private ImageView xGluLogo;

    private NfcCardReader nfcCardReader;
    private NfcAdapter nfcAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        //overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

        setContentView(R.layout.activity_measuring);

        pageState=(TextView)findViewById(R.id.tvState);
        pageGlucosetv=(TextView)findViewById(R.id.tvGlucose);
        pageTemperaturetv=(TextView)findViewById(R.id.tvTemp);
        pagePayload=(TextView)findViewById(R.id.tvPayload);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        glucoseValueTv=(TextView)findViewById(R.id.glucoseValueTv);
        xGluLogo=(ImageView)findViewById(R.id.xGluLogo);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("debug",false)){
            findViewById(R.id.debugInfo1).setVisibility(View.VISIBLE);
            findViewById(R.id.debugInfo2).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.debugInfo1).setVisibility(View.INVISIBLE);
            findViewById(R.id.debugInfo2).setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        nfcCardReader = new NfcCardReader(this);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableReaderMode(this,nfcCardReader,READER_FLAGS,null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableReaderMode(this);
        nfcCardReader.stop();
        nfcAdapter=null;
        nfcCardReader=null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d("tmp", "settings opened");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void readingSkipped(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animation = ObjectAnimator.ofInt(xGluLogo, "alpha",255,0);
                animation.setDuration(50);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.start();

                animation = ObjectAnimator.ofInt(xGluLogo, "alpha",0,255);
                animation.setDuration(100);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.start();

            }
        });
    }

    public void updateConnectedAndStatus(final boolean connected, final int status){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView connectedTv=(TextView)findViewById(R.id.tvConnected);
                String text = "";
                String statusText = "";
                int color;

                TextView statusTv =(TextView)findViewById(R.id.tvStatus);



                if(connected){
                    text="CONNECTED";
                    color=Color.GREEN;

                    switch(status){
                        case 0:
                            statusText+="Initializing";
                            break;

                        case 1:
                            statusText+="Place blood sample";
                            break;
                        case 2:
                            statusText+="Measuring";
                            break;
                        case 3:
                            statusText+="Completed";
                            break;
                        case -130:
                            statusText+="Used blood strip inserted";
                            break;
                        case -129:
                            statusText+="No strip detected ";
                            break;
                        case -128:
                            statusText+="Error";
                            break;
                    }

                    if(status<0){
                        statusTv.setTextColor(Color.RED);
                    } else {
                        statusTv.setTextColor(Color.BLACK);
                    }

                } else {
                    text="DISCONNECTED";
                    color=Color.RED;
                }

                connectedTv.setText(text);
                connectedTv.setTextColor(color);

                statusTv.setText(statusText);


            }
        });
    }

    public void updateGui(final int glucose, final String temperature, final byte state, final int glucoseValue, final String glucoseValueText, final String payload, double glucoseValueDouble) {
        Log.d("ahojState",previousState+" "+state);
        if(previousState!=3&&state==3){
            if(glucoseValueDouble==0)glucoseValueDouble=-2;
            if(glucoseValueDouble==12)glucoseValueDouble = -12;

            Intent i = new Intent();
            i.putExtra("glucose",glucoseValueDouble);
            i.putExtra("OGTTID",getIntent().getIntExtra("OGTTID",-1));

            Log.d("ahoj","setting result "+glucoseValueDouble);
            this.setResult(1,i);
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.d("ahoj", glucoseValueText + " " + temperature + " " + state);

                pageState.setText("" + state);
                pageGlucosetv.setText("" + glucose);
                pageTemperaturetv.setText("" + temperature);
                pagePayload.setText(payload);

                if (previousState != 3 && state == 3) {
                    glucoseValueTv.setText(glucoseValueText);
                    ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, glucoseValue); // see this max value coming back here, we animale towards that value
                    animation.setDuration(1500); //in milliseconds
                    animation.setInterpolator(new AccelerateDecelerateInterpolator());
                    animation.start();


                }
                previousState = state;

            }
        });



    }


    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    public void manualEntry(View v){
        Log.d("ahoj", "manual");
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter blood glucose level");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        alert.setView(input);
        alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {



                Float glucoseValue = Float.parseFloat(input.getText().toString());
                String glucoseValueText = String.format("%.1f", glucoseValue);
                if (glucoseValue < 3) {
                    glucoseValue = (float) 0;
                    glucoseValueText = "LO";
                } else if (glucoseValue > 12) {
                    glucoseValue = (float) 12;
                    glucoseValueText = "HI";
                }

                updateGui(0, "0", (byte) 3, (int) (glucoseValue - 3) * 100, glucoseValueText,"", glucoseValue);


            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Put actions for CANCEL button here, or leave in blank
            }
        });
        alert.show();

    }

}
