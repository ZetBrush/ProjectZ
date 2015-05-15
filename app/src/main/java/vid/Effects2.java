package vid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.util.DisplayMetrics;

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



   public void generateFrames(Bitmap theme,File sourcedir1, Bitmap sourcedir2, int counter) {

       File outptfls = new File(Environment.getExternalStorageDirectory().getPath() + "/picsartVideo/readyframes");
       String source1path = Environment.getExternalStorageDirectory().getPath()+"/picsartVideo/sourcefirst/frame_";
       String source2path = Environment.getExternalStorageDirectory().getPath()+"/picsartVideo/sourcesecond/frame_";

        if(!outptfls.exists()){
            outptfls.mkdirs();
        }

       Bitmap transBitmap = Bitmap.createBitmap(theme);
       FileOutputStream out = null;

       for (int i = 0; i < counter; i++) {
           Canvas canvas = new Canvas(transBitmap);
           canvas.drawRGB(0, 0, 0);
           Bitmap source = BitmapFactory.decodeFile(source1path+String.format("%05d", i) + ".jpg");
           Bitmap source2 = BitmapFactory.decodeFile(source2path+String.format("%05d", i) + ".jpg");
           final Paint paint = new Paint();

               canvas.drawBitmap(source, 11, 468, paint);
               canvas.drawBitmap(source2,377,10, paint);

           out = null;
           try {
               File filename = new File(outptfls.getPath()+"frame_" + String.format("%05d", i) + ".jpg");
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


       }
   }






}
