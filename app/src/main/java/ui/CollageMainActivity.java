package ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.javacodegeeks.androidvideocaptureexample.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import vid.Effects2;
import vid.FFGraber;

public class CollageMainActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private Button capture, switchCamera, renderBtn;
    ImageButton vidLeft;
    ImageButton vidRight;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    private int firstSecond = 1;
    private TextView textView;
    Thread myThread = null;
    int count = 0;
    int currentCapturedTime;
    int capturedTime;
    boolean isCaptured = false;
    boolean isplaying=false;
    private RecyclerView recyclerView;
    MediaPlayer mediaPlayer;

    private SlideShowAdapter slideShowAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RecyclerView.ItemAnimator itemAnimator;

    private LinearLayout cameraPreview2;

    private ArrayList<String> selectedImagesPathList = new ArrayList();
    private static final String root = Environment.getExternalStorageDirectory().toString();
    private File myDir = new File(root + "/picsartVideo");
    static int timer =0;

    public static int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    MediaController media_Controller;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;


        myDir.mkdirs();
        initialize();
        //loadFFMpegBinary();

        //getInit();
    }

   /* public void getInit() {
        video_player_view = (VideoView) findViewById(R.id.video_player_view);
        media_Controller = new MediaController(this);
        dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        video_player_view.setMinimumWidth(width);
        video_player_view.setMinimumHeight(height);
        video_player_view.setMediaController(media_Controller);
        media_Controller.setVisibility(View.GONE);
        video_player_view.setVideoPath(myDir + "/myvideo1.mp4");
    }*/


    private void loadFFMpegBinary() {
        try {

            new FFmpeg(CollageMainActivity.this).loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }
            });
        } catch (FFmpegNotSupportedException e) {

        }
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {

                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchCamera.setVisibility(View.GONE);
            }
            mCamera = Camera.open(findBackFacingCamera());
            mPreview.refreshCamera(mCamera);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        releaseCamera();
    }

    public void initialize() {


        capturedTime = 0;
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();  // deprecated
        int height = display.getHeight();

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width / 2, (int) (1.3 * width / 2));

        renderBtn = (Button) findViewById(R.id.rendersavebtn);
        cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);
        cameraPreview2 = (LinearLayout) findViewById(R.id.camera_preview2);
        textView = (TextView) findViewById(R.id.text_view);
        vidLeft = (ImageButton) findViewById(R.id.vid_left);
        vidRight = (ImageButton) findViewById(R.id.vid_right);
        //cameraPreview.setLayoutParams(layoutParams);
        //cameraPreview2.setLayoutParams(layoutParams);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mPreview = new CameraPreview(myContext, mCamera);

        capture = (Button) findViewById(R.id.button_capture);
        capture.setOnClickListener(captrureListener);

        switchCamera = (Button) findViewById(R.id.button_ChangeCamera);
        switchCamera.setOnClickListener(switchCameraListener);

        cameraPreview.addView(mPreview);
        renderBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new FFGraber(CollageMainActivity.this).execute();
            }

        });

        vidLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                cameraPreview2.removeView(mPreview);
                cameraPreview.addView(mPreview);
                firstSecond = 1;
                vidRight.setVisibility(View.VISIBLE);
            }
        });

        final VideoView video=new VideoView(getApplicationContext());
        media_Controller = new MediaController(this);
        video.setMinimumWidth(50);
        video.setMinimumHeight(50);
        video.setMediaController(media_Controller);
        media_Controller.setVisibility(View.GONE);
        video.setVideoPath(myDir + "/myvideo1.mp4");

        vidRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                cameraPreview.removeView(mPreview);
                //cameraPreview.addView(video);
                //video.start();
                cameraPreview2.addView(mPreview);
                firstSecond = 2;
                vidLeft.setVisibility(View.VISIBLE);
            }
        });
//        cameraPreview.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (firstSecond != 1) {
//                    cameraPreview2.removeView(mPreview);
//                    cameraPreview.addView(mPreview);
//                    firstSecond = 1;
//                }
//            }
//        });
//
//        cameraPreview2.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (firstSecond == 1) {
//                    cameraPreview.removeView(mPreview);
//                    cameraPreview2.addView(mPreview);
//                    firstSecond = 2;
//                }
//
//            }
//        });

        ArrayList<Bitmap> bitmaps=new ArrayList<>();
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.frame2));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.frame3));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.frame6));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.frame7));
        for (int i = 0; i < 4; i++) {

            selectedImagesPathList.add("gag");
        }

        slideShowAdapter = new SlideShowAdapter(selectedImagesPathList, this, bitmaps);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        itemAnimator = new DefaultItemAnimator();

        recyclerView.setAdapter(slideShowAdapter);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.addItemDecoration(new SpacesItemDecoration(2));

    }


    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }


    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_QVGA));

        mediaRecorder.setOrientationHint(90);

        File file = new File(myDir, "myvideo" + firstSecond + ".mp4");
        mediaRecorder.setOutputFile(file.getAbsolutePath());

        mediaRecorder.setMaxDuration(90000); // Set max duration 90 sec.

        mediaRecorder.setMaxFileSize(50000000); // Set max file size 50M
        count++;

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }


    private boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public void chooseCamera() {
        // if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


    OnClickListener switchCameraListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // get the number of cameras
            if (!recording) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    // release the old camera instance
                    // switch camera, from the front and the back and vice versa

                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        }
    };


    boolean recording = false;
    OnClickListener captrureListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recording) {
                myThread.interrupt();
                capturedTime = currentCapturedTime;

                vidLeft.setClickable(true);
                vidRight.setClickable(true);
                Effects2.initConfig(currentCapturedTime);
                // stop recording and release camera
                mediaRecorder.stop(); // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                Toast.makeText(CollageMainActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
                ((TextView) v).setText("Capture");
                recording = false;
            } else {
                if (!prepareMediaRecorder()) {
                    Toast.makeText(CollageMainActivity.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                    finish();
                }

                currentCapturedTime = 0;

                vidLeft.setClickable(false);
                vidRight.setClickable(false);

                Runnable myRunnableThread = new CountDownRunner();
                myThread = new Thread(myRunnableThread);
                myThread.start();

                // work on UiThread for better performance
                runOnUiThread(new Runnable() {
                    public void run() {
                        // If there are stories, add them to the table

                        try {
                            mediaRecorder.start();
                            isCaptured = true;


                        } catch (final Exception ex) {
                            // Log.i("---","Exception in thread");
                        }
                    }
                });
                ((TextView) v).setText("Stop");

                recording = true;
            }
        }
    };

    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }


    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    textView.setText("Time  " + capturedTime/10.0 +"/"+ currentCapturedTime/10.0);

                    currentCapturedTime++;
                    if (currentCapturedTime == capturedTime) {
                        captrureListener.onClick(capture);
                        Effects2.initConfig(capturedTime/10.0);
                    }

                }catch (Exception e) {}
            }
        });
    }


    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(100); // Pause of 1 Second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

}
