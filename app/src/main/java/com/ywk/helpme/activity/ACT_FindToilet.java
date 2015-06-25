package com.ywk.helpme.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.navisdk.BNaviEngineManager;
import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams;
import com.ywk.helpme.R;

/**
 * Created by Administrator on 2015/5/31 0031.
 */
public class ACT_FindToilet extends ActionBarActivity {

    private static final String TAG = ACT_FindToilet.class.getSimpleName();

    private Toolbar mToolbar;

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private Context mContext;


    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();


    private boolean isFirst = true;

    //POI相关
    PoiSearch mPoiSearch;

    private int load_Index = 0;

    private boolean mIsEngineInitSuccess = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        mContext = this;
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_find_toilet);
        setupView();


        //初始化导航引擎
        BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
                mNaviEngineInitListener, new LBSAuthManagerListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        String str = null;
                        if (0 == status) {
                            str = "key校验成功!";
                        } else {
                            str = "key校验失败, " + msg;
                        }
                        Toast.makeText(mContext, str,
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private BNaviEngineManager.NaviEngineInitListener mNaviEngineInitListener = new BNaviEngineManager.NaviEngineInitListener() {
        public void engineInitSuccess() {
            mIsEngineInitSuccess = true;
        }

        public void engineInitStart() {
        }

        public void engineInitFail() {
        }
    };


    private void setupView() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mToolbar.setTitle("找厕所");
        setSupportActionBar(mToolbar);

        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));
        mBaiduMap.setOnMapLoadedCallback(onMapLoadedCallback);

        mBaiduMap.setOnMapClickListener(OnMapClickListener);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("找厕所");


        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiSearchResultListener);


        // 开启定位图层
//        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);


    }

    public void onClick_Nav(View view) {
        if (mIsEngineInitSuccess) {
            launchNavigator();
        }
    }

    BNaviPoint startPoint;
    BNaviPoint endPoint;

    /**
     * 指定导航起终点启动GPS导航.起终点可为多种类型坐标系的地理坐标。
     * 前置条件：导航引擎初始化成功
     */
    private void launchNavigator() {
        //这里给出一个起终点示例，实际应用中可以通过POI检索、外部POI来源等方式获取起终点坐标
        /*startPoint = new BNaviPoint(116.307854, 40.055878,
                "百度大厦", BNaviPoint.CoordinateType.BD09_MC);
        endPoint = new BNaviPoint(116.403875, 39.915168,
                "北京天安门", BNaviPoint.CoordinateType.BD09_MC);*/
        BaiduNaviManager.getInstance().launchNavigator(this,
                startPoint,                                      //起点（可指定坐标系）
                endPoint,                                        //终点（可指定坐标系）
                RoutePlanParams.NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME,       //算路方式
                true,                                            //真实导航
                BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //在离线策略
                new BaiduNaviManager.OnStartNavigationListener() {                //跳转监听

                    @Override
                    public void onJumpToNavigator(Bundle configParams) {
                        Intent intent = new Intent(mContext, BNavigatorActivity.class);
                        intent.putExtras(configParams);
                        startActivity(intent);
                    }

                    @Override
                    public void onJumpToDownloader() {
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private BaiduMap.OnMapLoadedCallback onMapLoadedCallback = new BaiduMap.OnMapLoadedCallback() {
        @Override
        public void onMapLoaded() {

        }
    };

    private BaiduMap.OnMapClickListener OnMapClickListener = new BaiduMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            if (mPopupWindow != null) {
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();

                }
            }
        }

        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            return false;
        }
    };

    LinearLayout layout;
    private TextView tvName,
            tvContent;
    PopupWindow mPopupWindow;

    private OnGetPoiSearchResultListener poiSearchResultListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult == null
                    || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                Toast.makeText(ACT_FindToilet.this, "未找到结果", Toast.LENGTH_LONG)
                        .show();
                return;
            }

            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                mBaiduMap.clear();
                PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(poiResult);
                overlay.addToMap();
                overlay.zoomToSpan();
                return;
            }

            if (poiResult.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

                // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
                String strInfo = "在";
                for (CityInfo cityInfo : poiResult.getSuggestCityList()) {
                    strInfo += cityInfo.city;
                    strInfo += ",";
                }
                strInfo += "找到结果";
                Toast.makeText(ACT_FindToilet.this, strInfo, Toast.LENGTH_LONG)
                        .show();

            }

        }


        @Override
        public void onGetPoiDetailResult(PoiDetailResult result) {

            if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(mContext, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                        .show();
            } else {
//                Toast.makeText(mContext, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
//                        .show();


                if (layout == null) {
                    layout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.toilet_info_popwindow, null);
                    tvName = (TextView) layout.findViewById(R.id.tv_name);
                    tvContent = (TextView) layout.findViewById(R.id.tv_content);
                }
                if (mPopupWindow == null || !mPopupWindow.isShowing()) {
                    mPopupWindow = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
                    mPopupWindow.showAtLocation(findViewById(R.id.main), Gravity.RIGHT | Gravity.BOTTOM, 0, 0);
                }
                tvName.setText(result.getName());
                tvContent.setText(result.getAddress());

                endPoint = new BNaviPoint(result.getLocation().longitude, result.getLocation().latitude,
                        result.getAddress());

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mLocationClient.unRegisterLocationListener(myListener);

        mPoiSearch.destroy();
        // 关闭定位图层
//        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationClient.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    public static void Lunch(Context context) {
        Intent intent = new Intent(context, ACT_FindToilet.class);
        context.startActivity(intent);

    }

    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            // }


            return true;
        }
    }

    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);


            if (isFirst) {
                /*StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nradius : ");
                sb.append(location.getRadius());
                if (location.getLocType() == BDLocation.TypeGpsLocation) {
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());
                }*/

//                logMsg(sb.toString());


                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(mapStatusUpdate);
                isFirst = false;

                mPoiSearch.searchNearby(
                        new PoiNearbySearchOption()
                                .keyword("厕所")
                                .location(new LatLng(location.getLatitude(), location.getLongitude()))
                                .radius(1000));
            }

            startPoint = new BNaviPoint(location.getLongitude(), location.getLatitude(), location.getAddrStr());


        }
    }

    private void logMsg(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
