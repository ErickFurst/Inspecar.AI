package com.coresynesis.inspecarai.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.coresynesis.inspecarai.R;
import com.coresynesis.inspecarai.database.IviDatabaseHandler;
import com.coresynesis.inspecarai.models.PredicaoYoloModel;
import com.google.firebase.firestore.FirebaseFirestore;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG="MainActivity";
    private Mat mRgba;
    private Mat mGrey;
    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    boolean startYolo = false;
    boolean firstTimeYolo = false;
    Net tinyYolo;


    // Cria ou abre um bd local
    IviDatabaseHandler iviDB = new IviDatabaseHandler(this);

    // Cria o Camera View
    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface
                        .SUCCESS:{
                    Log.i(TAG, "OpenCv está carregado");
                    mOpenCvCameraView.enableView();
                }
                default:{
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    // Início Yolo
    public void YOLO(View Button){
        if (startYolo == false){
            startYolo = true;
            if (firstTimeYolo == false){
                firstTimeYolo = true;
                String tinyYoloCfg = getPath("yolov4-tiny-custom.cfg", this);
                String tinyYoloWeights = getPath("yolov4-tiny-custom_final.weights", this);
                tinyYolo = Dnn.readNetFromDarknet(tinyYoloCfg, tinyYoloWeights);
            }
        }
        else{
            startYolo = false;
        }

    }
    // Fim Yolo



    public CameraActivity(){
        Log.i(TAG,"Nova instância" + this.getClass());

    }


    // ON CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int MY_PERMISSIONS_REQUEST_CAMERA=0;
        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(CameraActivity.this, new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }

        setContentView(R.layout.activity_camera);

        mOpenCvCameraView= (CameraBridgeViewBase) findViewById(R.id.CameraView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCameraPermissionGranted();
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableFpsMeter();

    } // Fim On Create



    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV Inicializado");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else {
            Log.d(TAG,"Opencv não carregado.Tente novamente.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null){
            mOpenCvCameraView.disableView();
        }
    }



    public void onDestroy(){
        super.onDestroy();
        if (mOpenCvCameraView != null){
            mOpenCvCameraView.disableView();
        }
    }



    public void onCameraViewStarted(int width, int height){
        mRgba= new Mat(height,width, CvType.CV_8UC4);
        mGrey= new Mat(height,width, CvType.CV_8UC1);

        // Início Yolo
        if (startYolo == true){
            String tinyYoloCfg = getPath("yolov4-tiny-custom.cfg", this);
            String tinyYoloWeights = getPath("yolov4-tiny-custom_final.weights", this);
            tinyYolo = Dnn.readNetFromDarknet(tinyYoloCfg, tinyYoloWeights);
        }
        // Fim Yolo

    }



    public void onCameraViewStopped(){
        mRgba.release();
    }


    // Carrega o arquivo no storage e retorna um caminho.
    private static String getPath(String file, Context context) {
        AssetManager assetManager = context.getAssets();
        BufferedInputStream inputStream = null;
        try {
            // Lê dados de  assets.
            inputStream = new BufferedInputStream(assetManager.open(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            // Cria arquivo de copia no storage.
            File outFile = new File(context.getFilesDir(), file);
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(data);
            os.close();
            // Caminho para o arquivo
            return outFile.getAbsolutePath();
        } catch (IOException ex) {
            Log.i(TAG, "Falha ao carregar o arquivo");
        }
        return "";
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){

        // Início Yolo
        Mat frame = inputFrame.rgba();

        if (startYolo == true) {

            // Converte o padrão de cores
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2RGB);

            Mat imageBlob = Dnn.blobFromImage(frame, 0.00392, new Size(416, 416), new Scalar(0, 0, 0),/*swapRB*/false, /*crop*/false);

            tinyYolo.setInput(imageBlob);
            List<String> ln = getOutputNames(tinyYolo);
            List<Mat> result = new ArrayList<Mat>();

            // Executa o modelo na imagem
            tinyYolo.forward(result,ln);

            int cols = frame.cols();
            int rows = frame.rows();

            // Threshold, Nível de confiança
            // Recebe da Activity anterior AddCar o valor retornado do Firebase
            Bundle extras = getIntent().getExtras();
            String valThrhold = extras.getString("valThreshold");
            valThrhold = valThrhold.replace(",",".");
            float confThreshold = Float.parseFloat(valThrhold) / 100;

            //float confThreshold = 0.001f;

            List<Integer> clsIds = new ArrayList<>();
            List<Float> confs = new ArrayList<>();

            for (int i = 0; i < result.size(); ++i)
            {
                Mat level = result.get(i);
                for (int j = 0; j < level.rows(); ++j)
                {
                    Mat row = level.row(j);
                    Mat scores = row.colRange(5, level.cols());
                    Core.MinMaxLocResult mm = Core.minMaxLoc(scores);
                    float confidence = (float)mm.maxVal;
                    Point classIdPoint = mm.maxLoc;

                    if (confidence > confThreshold)
                    {
                        int centerX = (int)(row.get(0,0)[0] * frame.cols());
                        int centerY = (int)(row.get(0,1)[0] * frame.rows());
                        int width   = (int)(row.get(0,2)[0] * frame.cols());
                        int height  = (int)(row.get(0,3)[0] * frame.rows());

                        clsIds.add((int)classIdPoint.x);
                        confs.add((float)confidence);

                        // Pegar posições
                        //https://www.programcreek.com/java-api-examples/?class=org.opencv.imgproc.Imgproc&method=rectangle
                        int left = (int) (centerX - width * 0.5);
                        int top =(int)(centerY - height * 0.5);
                        int right =(int)(centerX + width * 0.5);
                        int bottom =(int)(centerY + height * 0.5);

                        // Desenha o retangulo
                        //https://docs.opencv.org/4.x/d0/d6c/tutorial_dnn_android.html
                        List<String> iviNames = Arrays.asList("Batida leve", "Batida grave", "Vidro quebrado", "Dano retrovisor", "Arranhao", "Desgaste pintura");
                        Imgproc.putText(frame,iviNames.get((int)classIdPoint.x) + " " + String.format("%.1f", (confidence * 100)) + "%",new Point(left, top), 2, 2, new Scalar(255,255,0),2);
                        Imgproc.rectangle(frame, new Point(left, top), new Point(right, bottom), new Scalar(0, 255, 0), 2);

                        //Gravar imagem/ Ler Imagem
                        //String filename = "temp" + j + ".jpg";
                        //File file = new File(path, filename);
                        //filename = file.toString();
                        //Log.i(TAG,"Erick filename: " + filename);
                        //Imgcodecs.imwrite(filename, frame);


                        // Converte Mat para byte e grava
                        byte[] buffer = new byte[(int) (frame.total() * frame.elemSize())];
                        frame.get(0, 0, buffer);

                        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                        Bitmap bmp2 = Bitmap.createBitmap(cols, rows, conf);
                        Utils.matToBitmap(frame, bmp2);

                        iviDB.addInspIvi(new PredicaoYoloModel(left,top,right,bottom,
                                iviNames.get((int)classIdPoint.x) ,
                                String.format("%.1f", (confidence * 100)),
                                getBytesFromBitmap(bmp2), "Concordo", "Sem dano"));

                    }
                }
            }

        } // Fim If Yolo

        return frame;

        // fim Yolo

    } // Fim Camera frame


    //https://answers.opencv.org/question/214676/android-java-dnnforward-multiple-output-layers-segmentation-fault/
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static List<String> getOutputNames(Net net) {
        List<String> names = new ArrayList<>();

        List<Integer> outLayers = net.getUnconnectedOutLayers().toList();
        List<String> layersNames = net.getLayerNames();

        outLayers.forEach((item) -> names.add(layersNames.get(item - 1)));
        return names;
    }


    // convert from bitmap to byte array
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }




}