package com.htmessage.cola_marketing.activity.chat.location;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.htmessage.cola_marketing.R;
import com.htmessage.cola_marketing.activity.BaseActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class GdMapActivity extends BaseActivity implements AMapLocationListener, LocationSource {
    private MapView amapView;
    private AMap aMap;//地图对象
    static AMapLocation lastLocation = null;
    private Marker marker;//图钉

    //定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private OnLocationChangedListener mListener = null;//定位监听器

    private String fromLat, fromLng;
    private String fromAddress;
    private String type;
    private ProgressDialog progressDialog;
    private double latitude = 0;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_gd_map);
        getData();
        initView(arg0);
        initData();
        setListener();
    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
        //启动后台定位，第一个参数为通知栏ID，建议整个APP使用一个
        mLocationClient.enableBackgroundLocation(2001, buildNotification());
    }

    private void getData() {
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
    }

    private void initView(Bundle arg0) {
        //显示地图
        amapView = findViewById(R.id.amapView);
        //必须要写
        amapView.onCreate(arg0);
        //sendButton = findViewById(R.id.btn_location_send);
    }

    private void initData() {
        //获取地图对象
        aMap = amapView.getMap();
        //设置显示定位按钮 并且可以点击

        if (latitude != 0){
            stopLocation();
            final double longtitude = getIntent().getDoubleExtra("longitude", 0);
            aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(latitude, longtitude)));
            MarkerOptions options = getMarkerOptions(new LatLng(latitude,longtitude), "他的位置");
            marker = aMap.addMarker(options);
        }else {
            UiSettings settings = aMap.getUiSettings();
            settings.setZoomControlsEnabled(false);
            //设置定位监听
            aMap.setLocationSource(this);
            // 是否显示定位按钮
            settings.setMyLocationButtonEnabled(false);
            // 是否可触发定位并显示定位层
            aMap.setMyLocationEnabled(true);
            //定位的小图标 默认是蓝点
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.showMyLocation(true);
            aMap.setMyLocationStyle(myLocationStyle);
            //地图点击，设置marker
//            aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
//                @Override
//                public void onMapClick(LatLng latLng) {
//                    if (marker != null){marker.setPosition(latLng);}
//                    else{
//                        MarkerOptions options = getMarkerOptions(latLng, "实际位置");
//                        marker = aMap.addMarker(options);
//                    }
//                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
//                }
//            });
            aMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    if (marker != null){marker.setPosition(latLng);}
                    else{
                        MarkerOptions options = getMarkerOptions(latLng, "实际位置");
                        marker = aMap.addMarker(options);
                    }
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
                }
            });
        }
    }

    private void setListener() {}

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            dismissDialog();
            lastLocation = amapLocation;
            //sendButton.setEnabled(true);
            showRightButton(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendLocation(v);
                }
            });
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                amapLocation.getAccuracy();//获取精度信息
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码

                //设置缩放级别
                //aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                //将地图移动到定位点
                //aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                //点击定位按钮 能够将地图的中心移动到定位点
                mListener.onLocationChanged(amapLocation);

                //stopLocation();
            } else {
                dismissDialog();
                Toast.makeText(GdMapActivity.this, R.string.location_failed, Toast.LENGTH_SHORT).show();
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        //初始化定位
        mLocationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));

        showProgross();
        startLocation();
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.disableBackgroundLocation(true);
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    /**
     * copy from amap api
     */
    private static final String NOTIFICATION_CHANNEL_NAME = "BackgroundLocation";
    private NotificationManager notificationManager = null;
    boolean isCreateChannel = false;
    @SuppressLint("NewApi")
    private Notification buildNotification() {

        Notification.Builder builder = null;
        Notification notification = null;
        if(android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            String channelId = getPackageName();
            if(!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
                notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(getApplicationContext(), channelId);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        builder.setSmallIcon(R.mipmap.app_logo)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("正在后台运行")
                .setWhen(System.currentTimeMillis());

        notification = builder.build();
        return notification;
    }

    private void showProgross() {
        String str1 = getResources().getString(R.string.Making_sure_your_location);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(str1);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            public void onCancel(DialogInterface arg0) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Log.d("map", "cancel retrieve type_location");
                finish();
            }
        });
        progressDialog.show();
    }

    private void dismissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        amapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        amapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        amapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocation();
        destroyLocation();
        dismissDialog();
        amapView.onDestroy();
    }

    public void sendLocation(View view) {
        final ProgressDialog progressDialog = new ProgressDialog(GdMapActivity.this);
        progressDialog.setMessage(getString(R.string.are_doing));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {

            @Override
            public void onMapScreenShot(Bitmap bitmap) {
                bitmap= cropBitmap(bitmap);
                File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp/");
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                File file = new File(file1.getAbsolutePath().toString() + "/" + System.currentTimeMillis() + ".png");
                FileOutputStream out;
                try {
                    out = new FileOutputStream(file);
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                        out.flush();
                        out.close();
                    }
                    GdMapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                    Intent intent = GdMapActivity.this.getIntent();
                    if (marker == null){
                        intent.putExtra("latitude", lastLocation.getLatitude());
                        intent.putExtra("longitude", lastLocation.getLongitude());
                        intent.putExtra("address", lastLocation.getAddress());
                    }else{
                        intent.putExtra("latitude", marker.getPosition().latitude);
                        intent.putExtra("longitude", marker.getPosition().longitude);
                        intent.putExtra("address", "我的位置");
                    }
                    intent.putExtra("thumbnailPath", file.getAbsolutePath());
                    GdMapActivity.this.setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                } catch (FileNotFoundException e) {
                    GdMapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                    e.printStackTrace();
                } catch (IOException e) {
                    GdMapActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 按长方形裁切图片
     *
     * @param bitmap
     * @return
     */
    public static Bitmap cropBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int nw, nh, retX, retY;

            nw = w ;
            nh = w/2;
            retX = 0;
            retY = (h/2)-(w/4);


         Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
                false);
        if (bitmap != null && !bitmap.equals(bmp) && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return bmp;// Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
        // false);
    }

    //自定义一个图钉，并且设置图标，当我们点击图钉时，显示设置的信息
    private MarkerOptions getMarkerOptions(LatLng latLng, String address) {
        //设置图钉选项
        MarkerOptions options = new MarkerOptions();
        //图标
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka_xhdpi));
        //位置
        options.position(latLng);
        //拖动
        options.draggable(true);
        StringBuffer buffer = new StringBuffer();
        buffer.append(address);
        //标题
        options.title(buffer.toString());
        //子标题
//        options.snippet("这里好火");
        //设置多少帧刷新一次图片资源
        options.period(60);
        return options;
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        if (mLocationClient != null) {
            if (mLocationClient.isStarted()) {
                // 停止定位
                mLocationClient.stopLocation();
                //关闭后台定位，参数为true时会移除通知栏，为false时不会移除通知栏，但是可以手动移除
                mLocationClient.disableBackgroundLocation(true);
            }
        }
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != mLocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mLocationClient.onDestroy();
            mLocationClient = null;
            mLocationOption = null;
        }
    }

//    private void showCityPup(final String fromLat, final String fromLng, final String fromAddress, final String activityLat, final String activityLng, final String address) {
//        HTAlertDialog dialog = new HTAlertDialog(GdMapActivity.this, getString(R.string.navigation), new String[]{getString(R.string.bd_map), getString(R.string.gd_map), getString(R.string.gg_map), getString(R.string.tc_map), getString(R.string.cancel)});
//        dialog.init(new HTAlertDialog.OnItemClickListner() {
//            @Override
//            public void onClick(int position) {
//                switch (position) {
//                    case 0:
//                        MapUtils.openBaiduMap(GdMapActivity.this, activityLat, activityLng, address);
//                        break;
//                    case 1:
//                        MapUtils.openGDMap(GdMapActivity.this, activityLat, activityLng, address);
//                        break;
//                    case 2:
//                        MapUtils.openGoogleMap(GdMapActivity.this, fromLat, fromLng, activityLat, activityLng);
//                        break;
//                    case 3:
//                        MapUtils.openTencentMap(GdMapActivity.this, fromAddress, fromLat, fromLng, activityLat, activityLng, address);
//                        break;
//                    case 4:
//
//                        break;
//                }
//            }
//        });
//    }


}
