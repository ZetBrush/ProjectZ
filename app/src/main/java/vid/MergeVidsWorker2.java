package vid;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

/**
 * Created by Arman on 5/6/15.
 */
public class MergeVidsWorker2 extends AsyncTask<Integer, Integer, Integer> implements ICommandProvider {

    Context ctx;
    String inputDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/picsArtvideo/readyframes/";
    String outputDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
    String outputVidName=Environment.getDownloadCacheDirectory().getAbsolutePath()+"/PicsArtVideo_collage";

    IThreadCompleteListener lis;


    public MergeVidsWorker2(Context ctx){
        this.ctx = ctx;

    }

    public void setListener(IThreadCompleteListener listener){
        this.lis=listener;
    }


    @Override
    protected Integer doInBackground(final Integer... params) {

        final FFmpeg mmpg = new FFmpeg(ctx);
        try {
            final boolean[] check = {true};
            mmpg.execute(getCommand(inputDir, outputDir), new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    lis.notifyOfThreadComplete(2);
                    Log.d("Merging.....Success", message);

                }


                @Override
                public void onProgress(String message) {

                    Log.d("Merging.....",message);
                }

                @Override
                public void onFailure(String message)
                {

                    Log.d("Merging....Failure",message);
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    Log.d("Merging.....", "Finished!");

                        Toast.makeText(ctx, "Video is ready!", Toast.LENGTH_SHORT).show();
                    }

            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    @Override
    public String getCommand(String... param) {
        //Log.d("Merge Command","-i " + "concat:" +videosPathBuilder(Integer.valueOf(param[0])) + " -preset ultrafast "+ "-c copy "+param[1]+".mp4");
        return "-y -i "+param[0] +"frame_%05d.jpg -r 25 -preset ultrafast "+param[1]+"/PicsArtVideo_collage.mp4";
    }

}
