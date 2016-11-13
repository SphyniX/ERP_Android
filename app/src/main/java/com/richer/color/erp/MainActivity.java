package com.richer.color.erp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.rongygame.sdk.ISDKApi;
import com.rongygame.sdk.SDKApi;
import com.shanggame.dema.UnityPlayerNativeActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends UnityPlayerNativeActivity implements ISDKApi {
	private final String TAG = "UnityActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		SDKApi.sdkApi = this;
		Log.d(TAG, "OnCreate");
	}
	
	@Override
	public String doSubmitData(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "doSubmitData");
		JSONObject joParam;
		try {
			joParam = new JSONObject(arg1);
			JSONObject joOrder= joParam.getJSONObject("param");
			String tag = joOrder.getString("tag");
			switch (tag) {
				case "doTakePhoto":
					String param = joOrder.getString("type");
					String name = joOrder.getString("name");
					Intent intent = new Intent(arg0,WebViewActivity.class);
			        intent.putExtra("type", param);
			        intent.putExtra("name", name);
			        this.startActivity(intent);
					break;
				default:
					break;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("#game run test#: doSubmitData error");
		}
		return "doSubmitData";
	}

	@Override
	public String doTakePhoto(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.d(TAG, "doTakePhoto");
		JSONObject joParam;
		try {
			joParam = new JSONObject(arg1);
			JSONObject joOrder = joParam.getJSONObject("param");
			String name = joOrder.getString("name");
			String type = joOrder.getString("type");
			Intent intent = new Intent(arg0,WebViewActivity.class);
	        intent.putExtra("type", type);
	        intent.putExtra("name", name);
	        this.startActivity(intent);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("#game run test#: doSubmitData error");
		}
		
		return "doTakePhoto";
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
	}
}
