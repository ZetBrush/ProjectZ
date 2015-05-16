package com.generatingmain;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.javacodegeeks.androidvideocaptureexample.R;

import java.io.File;

import ui.CollageMainActivity;
import vid.FFGraber;

/**
 * Created by Arman on 1/29/15.
 */
public class VideoRenderService extends IntentService {
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    public static final int STATUS_RUNNING = 001;
    public static int totWorkCount=0;
    private int progress =0;
    String outputVidName=Environment.getExternalStorageDirectory().getPath()+"/PicsArtVideo_collage";



    public VideoRenderService() {
        super(VideoRenderService.class.getName());

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        final String musPth = CollageMainActivity.getMusicPath();
        final int start = CollageMainActivity.secondsfromstarting;


        final Bundle bundle = new Bundle();

        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("VidsArt")
                .setContentText("processing")
                .setSmallIcon(R.drawable.ic_launcher);
        mNotifyManager.notify(STATUS_RUNNING, mBuilder.build());

        final FFGraber ffg = new FFGraber(VideoRenderService.this);

        if(CollageMainActivity.getMusicPath()!=null && CollageMainActivity.getMusicPath()!= "") {

            totWorkCount=5;
        }else totWorkCount=4;

        ffg.setListener(new vid.IThreadCompleteListener() {
            @Override
            public void notifyOfThreadComplete(int id) {
                receiver.send(id, bundle);
                Toast.makeText(VideoRenderService.this, "processing stages " + id + " of "+totWorkCount, Toast.LENGTH_SHORT).show();
                mBuilder.setProgress(totWorkCount, id, false);
                mNotifyManager.notify(STATUS_RUNNING, mBuilder.build());

                if(id==4 && totWorkCount==4){

                    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/picsartVideo");
                    if (f.exists()) {
                        if(f.isDirectory()){
                            for(File ff :f.listFiles()){
                                ff.delete();
                            }

                        }
                    }
                }

                if(id==4 && totWorkCount == 5){
                FFmpeg ffgg = new FFmpeg(VideoRenderService.this);

                    String cmd = "-y -i " + outputVidName + ".mp4"+" -ss "+start+" -i " + musPth + " -acodec copy -vcodec copy -shortest " + outputVidName + "_m.mp4";
                    Log.d("musicCommand", cmd);
                    try {
                        ffgg.execute(cmd, new FFmpegExecuteResponseHandler() {
                            @Override
                            public void onSuccess(String message) {
                                Log.d("musicMerge success", message);
                                File f = new File(outputVidName + ".mp4");
                                if (f.exists()) f.delete();
                                f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/picsartVideo");
                                if (f.exists()) {
                                    f.delete();
                                }
                            }

                            @Override
                            public void onProgress(String message) {
                                Log.d("musicMerge", message);

                            }

                            @Override
                            public void onFailure(String message) {
                                Log.d("musicMerge failure", message);
                            }

                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onFinish() {
                                VideoRenderService.this.stopSelf();
                            }
                        });


                    } catch (FFmpegCommandAlreadyRunningException e1) {
                        e1.printStackTrace();
                    }


                }




            }
        });

        ffg.execute();






    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent,flags,startId);


    }
}
