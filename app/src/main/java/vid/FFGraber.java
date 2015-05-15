package vid;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;

/**
 * Created by Arman on 5/14/15.
 */
public class FFGraber extends AsyncTask<String, Integer, Integer> implements ICommandProvider {
    static Context ctx;
    String[] input = new String[2];
    String[] output = new String[2];


    public FFGraber(Context ctx){
        this.ctx=ctx;
        input[0]= Environment.getExternalStorageDirectory().getPath()+"/picsartVideo/myvideo1.mp4";
        input[1]= Environment.getExternalStorageDirectory().getPath()+"/picsartVideo/myvideo2.mp4";
        output[0]= Environment.getExternalStorageDirectory().getPath()+"/picsartVideo/sourcefirst/";
        output[1] = Environment.getExternalStorageDirectory().getPath()+"/picsartVideo/sourcesecond/";
    }

    @Override
    protected Integer doInBackground(String... pats) {
        final boolean[] checker2pass = {true};
        final boolean[] checker2frame = {false};
        final FFmpeg ffmpg = new FFmpeg(ctx);
        File f1 = new File(output[0]);
        if(!f1.exists()){
            f1.mkdirs();
        }
        f1 = new File(output[1]);
        if(!f1.exists()){
            f1.mkdirs();
        }


        try {
            ffmpg.execute(getCommand(input[0],output[0]), new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {

                    if(checker2pass[0]){
                        checker2pass[0] =false;
                        checker2frame[0] = true;
                        try {
                            ffmpg.execute(getCommand(input[1], output[1]), new FFmpegExecuteResponseHandler() {
                                @Override
                                public void onSuccess(String message) {
                                    Log.d("Graber sucess current",message);

                                        if(checker2frame[0]) {
                                            Log.d("final frame processing", " ");
                                            Effects2.builder(Effects2.EFFECT.FADE)
                                                    .generateFrames(ctx);
                                            checker2frame[0]=false;
                                        }
                                }

                                @Override
                                public void onProgress(String message) {

                                }

                                @Override
                                public void onFailure(String message) {

                                }

                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onFinish() {

                                }
                            });
                        } catch (FFmpegCommandAlreadyRunningException e) {
                            e.printStackTrace();
                        }

                     } else {




                    }


                }

                @Override
                public void onProgress(String message) {
                    Log.d("Graber ",message);
                }

                @Override
                public void onFailure(String message) {

                    Log.d("Graber Failed",message);
                }

                @Override
                public void onStart() {



                }

                @Override
                public void onFinish() {

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public String getCommand(String... param) {

        return "-i "+ param[0]+" -r 25 -an -f image2 "+param[1]+"frame_%05d.jpg";
    }
}
