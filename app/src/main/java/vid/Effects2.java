package vid;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import com.javacodegeeks.androidvideocaptureexample.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Arman on 2/3/15.
 */
public class Effects2 {
    public enum EFFECT{FADE,SlIDE,ROTATE}

    private static Effects2 efectInstance;
    private  EFFECT effect;
    private  DisplayMetrics dm;
    static int counter;
    String source1path = Environment.getExternalStorageDirectory().getPath()+"/picsartVideo/sourcefirst/frame_";
    String source2path = Environment.getExternalStorageDirectory().getPath()+"/picsartVideo/sourcesecond/frame_";



    private Effects2(){

    }
    private Effects2(EFFECT eftype){
    this.effect = eftype;
    }

    public static Effects2 builder(EFFECT effectType){

        synchronized (Effects2.class){

                efectInstance = new Effects2(effectType);

        }
        return efectInstance;
    }

    public Effects2 setParams(DisplayMetrics dm){
        efectInstance.dm=dm;
        return  efectInstance;
    }

    public static void initConfig(double time){
        counter = (int)time*25;

    }



   public void generateFrames(Context ctx) {

       File outptfls = new File(Environment.getExternalStorageDirectory().getPath() + "/picsartVideo/readyframes/");

        if(!outptfls.exists()){
            outptfls.mkdirs();
        }

       Bitmap transBitmapimmut = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.picsintframe1);
       Bitmap transBitmap = transBitmapimmut.copy(Bitmap.Config.ARGB_8888,true);
       transBitmap = Bitmap.createScaledBitmap(transBitmap, 720, 720, true);
       Log.d("Width Height", String.valueOf(transBitmap.getWidth()) + " x " +transBitmap.getHeight());
       FileOutputStream out;
       ProgressDialog d = ProgressDialog.show(ctx,"Working..","Processing frames") ;
       d.show();
       for (int i = 1; i <= counter; i++) {
           d.setProgress(i);

           Canvas canvas = new Canvas(transBitmap);
           try {
               Bitmap source = BitmapFactory.decodeFile(source1path + String.format("%05d", i) + ".jpg");
               Bitmap source2 = BitmapFactory.decodeFile(source2path + String.format("%05d", i) + ".jpg");

               canvas.drawBitmap(source, 11, 468, null);
               canvas.drawBitmap(source2, 380, 12, null);

               out = null;
               try {
                   File filename = new File(outptfls.getPath() + "/frame_" + String.format("%05d", i) + ".jpg");
                   out = new FileOutputStream(filename);
                   transBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

               } catch (Exception e) {
                   e.printStackTrace();
               } finally {
                   try {
                       if (out != null) {
                           out.close();
                       }
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           }catch (Exception e){
               //no more frames
           }

           Log.d("frame gen ", ((i / (double) counter) * 100) + " %");
       }
       Log.d("frame gen ","successful frame generation");
       new MergeVidsWorker2(ctx,"","").execute();
       d.dismiss();
   }






}
