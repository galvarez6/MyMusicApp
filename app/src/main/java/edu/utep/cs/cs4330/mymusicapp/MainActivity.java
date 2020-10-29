package edu.utep.cs.cs4330.mymusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer player;
    private EditText urlEdit;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlEdit = findViewById(R.id.urlEdit);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        Button button = findViewById(R.id.playButton);
        button.setOnClickListener(this::playClicked);
        button = findViewById(R.id.stopButton);
        button.setOnClickListener(this::stopClicked);
    }

    /** Called when the play button is clicked. */
    public void playClicked(View view) {
        if (player == null || !player.isPlaying()) {
            player = MediaPlayer.create(this, Uri.parse(urlEdit.getText().toString()));
            if (player != null) {
                player.start();
                toast("Playing.");
                //--
                //-- TODO: WRITE YOUR CODE HERE
                //--
                //showProgressAsyncTask(player);
                showProgressThread(player);
                //showProgressHandler(player);
                //--
            } else {
                toast("Failed!");
            }
        }
    }

    private void showProgressThread(MediaPlayer player) {
        progressBar.setMax(player.getDuration());
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        new Thread (new Runnable(){
            public void run(){
                while(player.isPlaying()){
                    progressBar.setProgress(player.getCurrentPosition());
                    Log.d("SECONDS",":"+ player.getCurrentPosition());
                }//end of while
                progressBar.setProgress(0);
                progressBar.setVisibility(View.INVISIBLE);
                runOnUiThread(()->{
                    toast("Finished Playing");
                });
            }
        }).start();
    }

    private void showProgressAsyncTask(MediaPlayer player) {
        MyAsyncTask task = new MyAsyncTask(this);
        task.execute();
    }

    private static class MyAsyncTask extends AsyncTask<Integer,Integer, String>{
        private WeakReference<MainActivity> activityWeakReference;
        MyAsyncTask(MainActivity activity){
            activityWeakReference = new WeakReference<MainActivity>(activity);
        }
        @Override
        protected String doInBackground(Integer... integers) {
            //return null;
            MainActivity activity = activityWeakReference.get();
            Log.d("TOTAL", "seconds are "+activity.player.getDuration());
            while (activity.player.isPlaying()){
                for(int i = 0; i < activity.player.getDuration(); i+=1000){
                    Log.d("TOTAL", "seconds are "+ i);
                    publishProgress(activity.player.getCurrentPosition());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return "Finished Playing";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MainActivity activity = activityWeakReference.get();
            activity.progressBar.setMax(activity.player.getDuration());
            activity.progressBar.setProgress(0);
            activity.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MainActivity activity = activityWeakReference.get();

            Toast.makeText(activity,s,Toast.LENGTH_SHORT).show();
            activity.progressBar.setProgress(0);
            activity.progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            MainActivity activity = activityWeakReference.get();
            activity.progressBar.setProgress(values[0]);
        }
    }


    /** Called when the stop button is clicked. */
    public void stopClicked(View view) {
        if (player != null && player.isPlaying()) {
            player.stop();
            //player.release(); // or reset?
            player.reset();
            toast("Stopped.");
        }
    }

    /** Shows a toast message. */
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //--
    //-- WRITE YOUR CODE HERE
    //--

    //--
}