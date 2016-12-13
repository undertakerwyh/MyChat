package com.wyh.mychat.util;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class SystemUtils {
	private static SystemUtils systemUtils;
	private Context context;
	private TelephonyManager telManager;
	private ConnectivityManager connManager;
	private LocationManager locationManager;
	private String position;

	private SystemUtils(Context context) {
		this.context = context;
		telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		locationManager=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}

	public static SystemUtils getInstance(Context context) {
		if (systemUtils == null) {
			systemUtils = new SystemUtils(context);
		}
		return systemUtils;
	}
	/**判断网络是否连接*/
	public boolean isNetConn() {
		NetworkInfo info = connManager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		}
		return false;
	}
	/**获取sim卡的类型*/
	public String simType(){
		String simOperator = telManager.getSimOperator();
		String type = "";
		if ("46000".equals(simOperator)) {
			type = "移动";
		} else if ("46002".equals(simOperator)) {
			type = "移动";
		} else if ("46001".equals(simOperator)) {
			type = "联通";
		} else if ("46003".equals(simOperator)) {
			type = "电信";
		}
		return type;
	}
	public void hideSoftKeyboard(Activity activity){
		if (activity == null) return;

		View view = activity.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * 获取手机IMEI 号码
	 * @return IMEI
	 */
	public String getIMEI(){
		return telManager.getDeviceId();
	}

	public String getPosition(){
		return this.position;
	}

//	public void locatPosition(){
//		 locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,new_pop LocationListener(){
//			public void onLocationChanged(Location location) {
//				double longitude=location.getLongitude();
//				double latitude=location.getLatitude();
//				AsyncHttpClient client=new_pop AsyncHttpClient();
//				client.get("http://maps.googleapis.com/maps/api/geocode/json?latlng="+longitude+","+latitude+"&sensor=false", handler);
//			}
//			public void onStatusChanged(String provider, int status,Bundle extras) {}
//			public void onProviderEnabled(String provider) {}
//			public void onProviderDisabled(String provider) {}
//		 });
//	}
	
//	private JsonHttpResponseHandler handler=new_pop JsonHttpResponseHandler(){
//
//		@Override
//		public void onSuccess(int statusCode, PreferenceActivity.Header[] headers, JSONObject response) {
//			if(statusCode==200){
//				System.out.println(response.toString());
//			}
//		}
//
//		@Override
//		public void onSuccess(int statusCode, PreferenceActivity.Header[] headers, JSONArray response) {
//			if(statusCode==200){
//				System.out.println(response.toString());
//			}
//		}
//
//		@Override
//		public void onFailure(int statusCode, PreferenceActivity.Header[] headers, String responseString, Throwable throwable) {
//			super.onFailure(statusCode, headers, responseString, throwable);
//			System.out.println("onFailure");
//		}
//
//	};
	
	/**
	 * 获取手机的IMEI值ֵ
	 */
	
	public static String getIMEI(Context context){
		TelephonyManager telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}
}
