package vid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
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
    public static int currentframe=0;

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



   public boolean generateFrames(Context ctx) {

       File outptfls = new File(Environment.getExternalStorageDirectory().getPath() + "/picsartVideo/readyframes/");

        if(!outptfls.exists()){
            outptfls.mkdirs();
        }
       Bitmap transBitmapimmut=null;

       switch (currentframe){

           case R.drawable.picsintframe1:
               transBitmapimmut = BitmapFactory.decodeResource(ctx.getResources(), currentframe);
               break;

           case R.drawable.frame3:
               transBitmapimmut = BitmapFactory.decodeResource(ctx.getResources(), currentframe);
               break;
           case R.drawable.frame8:
               transBitmapimmut = BitmapFactory.decodeResource(ctx.getResources(), currentframe);
               break;

       }


       Bitmap transBitmap = transBitmapimmut.copy(Bitmap.Config.ARGB_8888,true);
       transBitmap = Bitmap.createScaledBitmap(transBitmap, 720, 720, true);
       Log.d("Width Height", String.valueOf(transBitmap.getWidth()) + " x " +transBitmap.getHeight());
       FileOutputStream out;

       for (int i = 1; i <= counter; i++) {


           Canvas canvas = new Canvas(transBitmap);
           try {
               Bitmap source = BitmapFactory.decodeFile(source1path + String.format("%05d", i) + ".jpg");
               Bitmap source2 = BitmapFactory.decodeFile(source2path + String.format("%05d", i) + ".jpg");

              if(currentframe == R.drawable.picsintframe1) {
                  canvas.drawBitmap(source, 11, 468, null);
                  canvas.drawBitmap(source2, 380, 12, null);
              }
               else if(currentframe== R.drawable.frame3){
                  Matrix matrix = new Matrix();
                  matrix.postRotate(90);
                  Bitmap rotatedsource = Bitmap.createBitmap(source, 0, 0,
                          source.getWidth(), source.getHeight(),
                          matrix, true);

                  Bitmap rotatedsource2 = Bitmap.createBitmap(source2, 0, 0,
                          source2.getWidth(), source2.getHeight(),
                          matrix, true);
                  canvas.drawBitmap(rotatedsource, 41, 342, new Paint());
                  canvas.drawBitmap(rotatedsource2,450,340,new Paint());

              }

               else if(currentframe== R.drawable.frame8){
                  Matrix matrix = new Matrix();
                  matrix.postRotate(90);
                  Bitmap rotatedsource = Bitmap.createBitmap(source, 0, 0,
                          source.getWidth(), source.getHeight(),
                          matrix, true);

                  Bitmap rotatedsource2 = Bitmap.createBitmap(source2, 0, 0,
                          source2.getWidth(), source2.getHeight(),
                          matrix, true);
                  canvas.drawBitmap(rotatedsource, 15, 13, new Paint());
                  canvas.drawBitmap(rotatedsource2,461,385,new Paint());

              }



               out = null;
               try {
                   File filename = new File(outptfls.getPath() + "/frame_" + String.format("%05d", i) + ".jpg");
                   out = new FileOutputStream(filename);
                   transBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

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
       Log.d("frame gen ", "successful frame generation");

       return true;


   }






}
