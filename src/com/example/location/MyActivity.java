package com.example.location;

import java.util.ArrayList;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyActivity extends Activity implements MKOfflineMapListener, OnGetGeoCoderResultListener, OnMarkerClickListener{
	
	MapView mMapView = null;
	BaiduMap mBaiduMap = null;
	GeoCoder mSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	
	private MKOfflineMap mOffline = null;
	
	Button search1 = null;	
	Button search2 = null;
	Button location = null;
	EditText city = null;
	EditText address = null;
	
	 /** 
     * ��λSDK�ĺ����� 
     */  
    private LocationClient mLocClient;  
    /** 
     * �û�λ����Ϣ  
     */  
    /** 
     * �ҵ�λ��ͼ�� 
     */  
    //private LocationOverlay myLocationOverlay = null;  
    /** 
     * ��������ͼ�� 
     */  
    //private PopupOverlay mPopupOverlay  = null;  
      
    private boolean isRequest = false;//�Ƿ��ֶ���������λ  
    private boolean isFirstLoc = true;//�Ƿ��״ζ�λ  
      
    /** 
     * ��������ͼ���View 
     */  
	
	public MyLocationListenner myListener = new MyLocationListenner();
	
	private LocationMode mCurrentMode;
	
	BitmapDescriptor mCurrentMarker;
	
	boolean choice = true;
	private ArrayList<MKOLUpdateElement> localMapList = null;
	
	private LocalMapAdapter lAdapter = null;
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);   
        //��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext  
        //ע��÷���Ҫ��setContentView����֮ǰʵ��  
        setContentView(R.layout.main); 
        
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        
        
        mBaiduMap.setOnMarkerClickListener(this);
        
        // ������λͼ��
     	mBaiduMap.setMyLocationEnabled(true);
     	LatLng xian = new LatLng(34.27, 108.93);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().target(xian).build()));
     	// ��λ��ʼ��
     	mLocClient = new LocationClient(this);
     	mLocClient.registerLocationListener(myListener);
     	
     	LocationClientOption option = new LocationClientOption();
     	option.setOpenGps(true);// ��gps
     	option.setCoorType("bd09ll"); // ������������
     	option.setScanSpan(5000);
     	option.setNeedDeviceDirect(true);
     	option.setAddrType("all");
     	option.SetIgnoreCacheException(false);//��ֹ���û��涨λ
     	mLocClient.setLocOption(option);
     	mLocClient.start();
        
        CharSequence titleLable = "������빦��";
		setTitle(titleLable);
		
		// ��ʼ������ģ�飬ע���¼�����
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		
        mOffline = new MKOfflineMap();
		mOffline.init(this);
		
		search1 = (Button) findViewById(R.id.search);
		search2 = (Button) findViewById(R.id.searchWithNewActivy);
		location = (Button) findViewById(R.id.location);
		location.getBackground().setAlpha(255);
		city = (EditText) findViewById(R.id.city);
		address = (EditText) findViewById(R.id.address);
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		
		final int screenHeight = dm.heightPixels;
		final int screenWidth = dm.widthPixels;
		
		search1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(city.getText().toString() == null || address.getText().toString().equals(""))
				{
					Toast.makeText(getApplicationContext(), "��ַ����Ϊ��", Toast.LENGTH_LONG).show();
				}
				if(address.getText().toString() == null || city.getText().toString().equals(""))
				{
					Toast.makeText(getApplicationContext(), "����������Ϊ��", Toast.LENGTH_LONG).show();
					return;
				}
				mSearch.geocode(new GeoCodeOption().city(
						city.getText().toString()).address(
						address.getText().toString()));
				choice = true;
			}
			
		});
		search2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(city.getText().toString() != null && address.getText().toString() != null)
				{
				
					mSearch.geocode(new GeoCodeOption().city(
							city.getText().toString()).address(
							address.getText().toString()));
					choice = false;
				}
				else
				{
					Toast.makeText(getApplicationContext(), "��ַ����Ϊ��", Toast.LENGTH_LONG).show();
					return;
				}
			}
			
		});
		
		location.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				isRequest = true;
				if(mLocClient != null && mLocClient.isStarted())
				{
					Toast.makeText(getApplicationContext(), "���ڶ�λ...", Toast.LENGTH_LONG).show();
					mLocClient.requestLocation();
				}
				else
				{
					Log.d("LocSDK5", "locClient is null or not started");
					Toast.makeText(getApplicationContext(), "locClient is null or not started", Toast.LENGTH_LONG).show();
				}
			}
			
		});
		location.setLongClickable(true);
		location.setOnTouchListener(new OnTouchListener(){
			
			int lastX,lastY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				int ea = event.getAction();
				Log.i("TAG", "Touch" + ea);
				switch(ea)
				{
				case MotionEvent.ACTION_DOWN:
					lastX = (int)event.getRawX();
					lastY = (int)event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int dx = (int)event.getRawX() - lastX;
					int dy = (int)event.getRawY() - lastY;
					int l = v.getLeft() + dx;
					int b = v.getBottom() + dy;
					int r = v.getRight() + dx;
					int t = v.getTop() + dy;
					if(l < 0)
					{
						l = 0;
						r = l + v.getWidth();
					}
					if(t < 0)
					{
						t = 0;
						b = t + v.getHeight();
					}
					if(r > screenWidth )
					{
						r = screenWidth;
						l = r - v.getWidth();
					}
					if(b > screenHeight)
					{
						b = screenHeight;
						t = b - v.getHeight();
					}
					v.layout(l, t, r, b);
					lastX = (int)event.getRawX();
					lastY = (int)event.getRawY();
					v.postInvalidate();
					break;
				case MotionEvent.ACTION_UP:
					break;
				
				}
				return false;
			}
			
		});
		init();
    }
	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;
			
			MyLocationData locData = new MyLocationData.Builder()
			.accuracy(location.getRadius())
			// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
			.direction(location.getDirection()).latitude(location.getLatitude())
			.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			
			// ���ö�λͼ������ã���λģʽ���Ƿ���������Ϣ���û��Զ��嶨λͼ�꣩  
			/*mCurrentMarker = BitmapDescriptorFactory  
			    .fromResource(R.drawable.icon_lation);  
			MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);  
			mBaiduMap.setMyLocationConfigeration(config); */
			if (isFirstLoc || isRequest) {
				isRequest = false;
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
				//mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().target(ll).build()));
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	
	/**
	 * ����״̬��ʾ
	 */
	public void updateView() {
		localMapList = mOffline.getAllUpdateInfo();
		if (localMapList == null) {
			localMapList = new ArrayList<MKOLUpdateElement>();
		}
		lAdapter.notifyDataSetChanged();
	}
	
	void init()
	{
		// ��ȡ���¹������ߵ�ͼ��Ϣ
				localMapList = mOffline.getAllUpdateInfo();
				if (localMapList == null) {
					localMapList = new ArrayList<MKOLUpdateElement>();
				}

				//ListView localMapListView = (ListView) findViewById(R.id.localmaplist);
				//lAdapter = new LocalMapAdapter();
				//localMapListView.setAdapter(lAdapter);
	}
	/**
	 * ���ߵ�ͼ�����б�������
	 */
	public class LocalMapAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return localMapList.size();
		}

		@Override
		public Object getItem(int index) {
			return localMapList.get(index);
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int index, View view, ViewGroup arg2) {
			MKOLUpdateElement e = (MKOLUpdateElement) getItem(index);
			view = View.inflate(MyActivity.this,
					R.layout.offline_localmap_list, null);
			initViewItem(view, e);
			return view;
		}

		void initViewItem(View view, final MKOLUpdateElement e) {
			Button display = (Button) view.findViewById(R.id.display);
			Button remove = (Button) view.findViewById(R.id.remove);
			TextView title = (TextView) view.findViewById(R.id.title);
			TextView update = (TextView) view.findViewById(R.id.update);
			TextView ratio = (TextView) view.findViewById(R.id.ratio);
			ratio.setText(e.ratio + "%");
			title.setText(e.cityName);
			if (e.update) {
				update.setText("�ɸ���");
			} else {
				update.setText("����");
			}
			if (e.ratio != 100) {
				display.setEnabled(false);
			} else {
				display.setEnabled(true);
			}
			remove.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mOffline.remove(e.cityID);
					updateView();
				}
			});
			display.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("x", e.geoPt.longitude);
					intent.putExtra("y", e.geoPt.latitude);
					intent.setClass(MyActivity.this, BaseMapDemo.class);
					startActivity(intent);
				}
			});
		}

	}
	/**
	 * ��SD���������ߵ�ͼ��װ��
	 * 
	 * @param view
	 */
	public void importFromSDCard(View view) {
		int num = mOffline.importOfflineData();
		String msg = "";
		if (num == 0) {
			msg = "û�е������߰�������������߰�����λ�ò���ȷ�������߰��Ѿ������";
		} else {
			msg = String.format("�ɹ����� %d �����߰������������ع���鿴", num);
		}
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		updateView();
	}
	
	public void onGetOfflineMapState(int type, int state) {
		switch (type) {
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
			MKOLUpdateElement update = mOffline.getUpdateInfo(state);
			// �������ؽ��ȸ�����ʾ
			if (update != null) {
				Toast.makeText(this,String.format("%s : %d%%", update.cityName,
						update.ratio),Toast.LENGTH_LONG);
				//updateView();
			}
		}
			break;
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			// �������ߵ�ͼ��װ
			Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			// �汾������ʾ
			// MKOLUpdateElement e = mOffline.getUpdateInfo(state);

			break;
		}

	}
	 @Override  
	    protected void onDestroy() {  
	        super.onDestroy();  
	        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
	     // �˳�ʱ���ٶ�λ
			mLocClient.stop();
			// �رն�λͼ��
			mBaiduMap.setMyLocationEnabled(false);
			mLocClient.unRegisterLocationListener(myListener);
			mMapView.onDestroy();
			mMapView = null;
	        mOffline.destroy();
	    }  
	    @Override  
	    protected void onResume() {  
	        super.onResume();  
	        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
	        mMapView.onResume();  
	        }  
	    @Override  
	    protected void onPause() {  
	        super.onPause();  
	        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
	        mMapView.onPause();  
	        }

		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
			// TODO Auto-generated method stub
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(MyActivity.this, "��Ǹ��δ���ҵ����", Toast.LENGTH_LONG)
						.show();
				return;
			}
			if(choice)
			{
				mBaiduMap.clear();
				mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_marka)));
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
						.getLocation()));
				String strInfo = String.format("γ�ȣ�%f ���ȣ�%f",
						result.getLocation().latitude, result.getLocation().longitude);
				Toast.makeText(MyActivity.this, strInfo, Toast.LENGTH_LONG).show();
			}
			else
			{
			Intent intent = new Intent();
			intent.putExtra("y", result.getLocation().latitude);
			intent.putExtra("x", result.getLocation().longitude);
			intent.setClass(MyActivity.this, BaseMapDemo.class);
			startActivity(intent);
			}
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onMarkerClick(Marker marker) {
			// TODO Auto-generated method stub
			
			Toast.makeText(
					MyActivity.this,
					"λ�ã�" + marker.getPosition().latitude + ", "
							+ marker.getPosition().longitude + "title: " + marker.getTitle(),
					Toast.LENGTH_LONG).show();
			
			return true;
		}  
}
