package com.tbuonomo.jawgmapsample;

/**
 * Created by leo on 26/10/17.
 */

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CameraFragment extends Fragment implements LocationListener{

    private static final String TAG = "AndroidCameraApi";
    private android.support.design.widget.FloatingActionButton takePictureButton;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    private static final int REAR_CAMERA = 0;
    private static final int FRONT_CAMERA = 1;
    private int CURRENT_CAMERA = REAR_CAMERA;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private final File fileRoot = new File(Environment.getExternalStorageDirectory() + "/IPicMap");
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    private LocationManager locationManager;
    private double latitude;
    private double longitude;

    View rootView;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView");

        rootView = inflater.inflate(R.layout.camera_layout, container, false);

        fileRoot.mkdir();

        //Init texture
        textureView = rootView.findViewById(R.id.texture);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);

        //Init location
        locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                this.getActivity().finish();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        //Handle taking pictures
        takePictureButton = rootView.findViewById(R.id.btn_takepicture);
        assert takePictureButton != null;
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "takePictureButton.setOnClickListener");
                takePicture();
            }
        });

        //Handle Camera Change
        ImageView rotateCameraButton = rootView.findViewById(R.id.btn_rotateCamera);
        rotateCameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Log.e(TAG, "rotateCameraButton.setOnClickListener");
                closeCamera();
                if(CURRENT_CAMERA == REAR_CAMERA)
                    CURRENT_CAMERA = FRONT_CAMERA;
                else
                    CURRENT_CAMERA = REAR_CAMERA;
                openCamera();

            }
        });

        return rootView;
    }


    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();
            Log.e(TAG, "onSurfaceTextureAvailable");
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
            Log.e(TAG, "onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Log.e(TAG, "onSurfaceTextureDestroyed");
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            //Log.e(TAG, "onSurfaceTextureUpdated");
        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.e(TAG, "onDisconnected");
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.e(TAG, "onError");
            cameraDevice.close();
            cameraDevice = null;
        }
    };
    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            Log.e(TAG, "onCaptureCompleted");
            super.onCaptureCompleted(session, request, result);
            createCameraPreview();
        }
    };

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Save method
    private void save(byte[] pictureBytes, JSONObject info) throws IOException {

        Log.e(TAG, "save");

        //Create the new files to save data
        File tmpFile = new File(fileRoot + "/" + UUID.randomUUID().toString());
        tmpFile.mkdir();
        File picFile = new File(tmpFile + "/pic.jpg");
        File dataFile = new File(tmpFile + "/data.json");

        OutputStream outputPic = null;
        OutputStream outputData = null;
        try {
            //Save picture
            outputPic = new FileOutputStream(picFile);
            outputPic.write(pictureBytes);

            //Save JSON
            outputData = new FileOutputStream(dataFile);
            outputData.write(info.toString().getBytes());

        } finally {
            if (null != outputPic) outputPic.close();
            if (null != outputData) outputData.close();
            Toast.makeText(this.getContext(), "Saved:" + tmpFile, Toast.LENGTH_LONG).show();
        }
    }

    protected void takePicture() {

        Log.e(TAG, "takePicture");
        if (null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) this.getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {

            //Get the camera capture size
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }

            //Create the target image and set its size
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);


            //Define targeted surfaces for the preview
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));

            //Create the capture request
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            // Orientation
            int rotation = this.getActivity().getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            //Define the callback to save the image when ready
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    //try {
                    image = reader.acquireLatestImage();
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    final byte[] bytes = new byte[buffer.capacity()];
                    buffer.get(bytes);
                    openPicPreview(bytes);
                    //}
                    //catch (FileNotFoundException e) {
                    //    e.printStackTrace();
                    //} catch (IOException e) {
                    //    e.printStackTrace();
                    //} finally {
                    if (image != null) {
                        image.close();
                    }
                    //}
                }
            };

            //Bind the image to the previous listener
            reader.setOnImageAvailableListener(readerListener, null); //TODO mBackgroundHandler

            //Create the capture session callback, called when the capture occured
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    createCameraPreview();
                }
            };

            //Create the capture session before launching the actual capture
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, null); //TODO mBackgroundHandler
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, null); //TODO mBackgroundHandler
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //Show the preview panel
    protected void openPicPreview(final byte[] bytes){

        Log.e(TAG, "openPicPreview");
        final CardView card = rootView.findViewById(R.id.cardPreview);
        takePictureButton = rootView.findViewById(R.id.btn_takepicture);

        card.setVisibility(View.VISIBLE);
        takePictureButton.setVisibility(View.INVISIBLE);


        ImageView imagePreview = rootView.findViewById(R.id.imagePreview);
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        imagePreview.setImageBitmap(imageBitmap);

        textureView.setAlpha((float).3);

        Button cancelBtn = rootView.findViewById(R.id.cancelSnap);
        cancelBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Log.e(TAG, "cancelBtn.setOnClickListener");
                card.setVisibility(View.INVISIBLE);
                textureView.setAlpha(1);
                takePictureButton.setVisibility(View.VISIBLE);

            }
        });
        Button saveBtn = rootView.findViewById(R.id.saveSnap);
        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Log.e(TAG, "saveBtn.setOnClickListener");
                EditText title = rootView.findViewById(R.id.contentTitle);
                EditText description = rootView.findViewById(R.id.contentDescription);
                JSONObject state = new JSONObject();
                try {
                    state.put("title",title.getText());
                    state.put("description",description.getText());
                    state.put("latitude",latitude);
                    state.put("longitude",longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    save(bytes,state);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                card.setVisibility(View.INVISIBLE);
                textureView.setAlpha(1);
                takePictureButton.setVisibility(View.VISIBLE);

            }
        });
    }


    protected void createCameraPreview() {
        try {
            Log.e(TAG, "createCameraPreview");
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());

            //Apply 4/3 transformation on the texture to obtain visually attractive preview
            Matrix matrix = new Matrix();
            matrix.setScale((float)4/3, 1 );
            textureView.setTransform(matrix);

            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                    Log.e(TAG, "PreviewUpdated");
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(getContext(), "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) this.getActivity().getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            cameraId = manager.getCameraIdList()[CURRENT_CAMERA];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }
    protected void updatePreview() {
        Log.e(TAG, "updatePreview");
        if(null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
            return;
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            Log.e(TAG, "setRepeatingRequest");
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler); //TODO mBackgroundHandler
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error preview");
            e.printStackTrace();
        }
    }
    private void closeCamera() {
        Log.e(TAG, "closeCamera");
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult");
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(this.getContext(), "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                this.getActivity().finish();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        closeCamera();
        stopBackgroundThread();
    }


    @Override
    public void onStop(){
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
    }



    @Override
    public void onLocationChanged(Location location) {
        //Log.e(TAG, "onLocationChanged");
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }
}
