package com.example.location;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

/**
 * ��ʾMapView�Ļ����÷�
 */
public class BaseMapDemo extends Activity {
	@SuppressWarnings("unused")
	private static final String LTAG = BaseMapDemo.class.getSimpleName();
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (intent.hasExtra("x") && intent.hasExtra("y")) {
			// ����intent����ʱ���������ĵ�Ϊָ����
			Bundle b = intent.getExtras();
			LatLng p = new LatLng(b.getDouble("y"), b.getDouble("x"));
			mMapView = new MapView(this,
					new BaiduMapOptions().mapStatus(new MapStatus.Builder()
							.target(p).build()));
			mBaiduMap = mMapView.getMap();
			mBaiduMap.clear();
			mBaiduMap.addOverlay(new MarkerOptions().position(p)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.icon_marka)));
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(p));
			String strInfo = String.format("γ�ȣ�%f ���ȣ�%f",
					p.latitude, p.longitude);
			Toast.makeText(BaseMapDemo.this, strInfo, Toast.LENGTH_LONG).show();
			
		} else {
			mMapView = new MapView(this, new BaiduMapOptions());
		}
		setContentView(mMapView);
		mBaiduMap = mMapView.getMap();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// activity ��ͣʱͬʱ��ͣ��ͼ�ؼ�
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity �ָ�ʱͬʱ�ָ���ͼ�ؼ�
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// activity ����ʱͬʱ���ٵ�ͼ�ؼ�
		mMapView.onDestroy();
	}

}

