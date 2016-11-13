package com.richer.color.erp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.richer.color.erp.R;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WebViewActivity extends Activity {
	
	private final String TAG = "UnityActivity";
	ImageView imageView = null;
	Button btnSubmit = null;
	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;//����
	public static final int PHOTOZOOM = 2;//����
	public static final int PHOTORESOULT = 3;//���
	
	public static final String PATH_IMAGE_ROOT = "/mnt/sdcard/Android/data/com.richer.erp/files/Image";
	public static final String IMAGE_UNSPECIFIED = "image/*";
	public final static String DATA_URL = "/data/data/";
	
	public String FILE_NAME = "image.png";
	@Override
	protected void onCreate(Bundle saveInstanceState){
		super.onCreate(saveInstanceState);
		setContentView(R.layout.view_takephoto);
		imageView = (ImageView)this.findViewById(R.id.imageID);
		btnSubmit = (Button) this.findViewById(R.id.button0);
		btnSubmit.setOnClickListener(new Button.OnClickListener(){//��������
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SubmitPhoto();
			}
        });
		String type = this.getIntent().getStringExtra("type");
		FILE_NAME = this.getIntent().getStringExtra("name");
		//�������ж��Ǵ򿪱�����ỹ��ֱ������
		if(type.equals("takephoto")){
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg")));
			startActivityForResult(intent, PHOTOHRAPH);
		} else {
			Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
            startActivityForResult(intent, PHOTOZOOM);
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE)
            return;
        // ����
        if (requestCode == PHOTOHRAPH) {
            //�����ļ�����·��������ڸ�Ŀ¼��
            File picture = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
            Log.d(TAG, TAG + "take photo end, go to show" + Environment.getExternalStorageDirectory() + "/temp.jpg");
            startPhotoZoom(Uri.fromFile(picture));
        }  
 
        if (data == null)
            return;  
 
        // ��ȡ�������ͼƬ
        if (requestCode == PHOTOZOOM) {
        	Log.d(TAG, TAG + "select photo end, go to show" + data.getData().toString());
            
            startPhotoZoom(data.getData());
        }
        // ������
        if (requestCode == PHOTORESOULT) {
            Bundle extras = data.getExtras();
            if (extras != null) {  
                Bitmap photo = extras.getParcelable("data");
		        imageView.setImageBitmap(photo);  
		        Log.d(TAG, TAG + "show photo end and go to save");
            	try {
            		SaveBitmap(photo);
				} catch (IOException e) {
					// TODO Auto-generated catch block
			        Log.d(TAG, TAG + "save error : " + e.toString());
					e.printStackTrace();
				}
            }  
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
	
	public void startPhotoZoom(Uri uri) {
        Log.d(TAG, TAG + "show photo : " + uri.toString());
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
        intent.putExtra("crop", "true");
        // aspectX aspectY �ǿ�ߵı���
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY �ǲü�ͼƬ���
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTORESOULT);
    }  
 
	public void SaveBitmap(Bitmap bitmap) throws IOException {

        Log.d(TAG, TAG + "save photo");
		FileOutputStream fOut = null;
 
		//ע��1
		try {
			  //�鿴���·���Ƿ���ڣ�
			  //���û�����·����
			  //�������·��
			  File destDir = new File(PATH_IMAGE_ROOT);
			  if (!destDir.exists())
			  {
				  Log.d(TAG, TAG + "save photo, get dirs:" + PATH_IMAGE_ROOT);
				  destDir.mkdirs();
			  }
			fOut = new FileOutputStream(PATH_IMAGE_ROOT + "/" + FILE_NAME) ;
			Log.d(TAG, TAG + "save photo, open file: " + PATH_IMAGE_ROOT + "/" + FILE_NAME);
			
		} catch (FileNotFoundException e) {
			Log.d(TAG, TAG + "save photo, open file error : " + e.toString());
			
			e.printStackTrace();
		}
		//��Bitmap����д�뱾��·���У�Unity��ȥ��ͬ��·������ȡ����ļ�
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d(TAG, TAG + "save photo end");
	}
	
	private void SubmitPhoto(){
		JSONObject jo = new JSONObject();
		try {
			jo.put("method", "on_get_photo");
			jo.put("path", FILE_NAME);

			UnityPlayer.UnitySendMessage("SDKMgr", "OnSDKMessage", jo.toString());
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			//���û�������ؼ��� ֪ͨUnity��ʼ��"/mnt/sdcard/Android/data/com.richer.erp/files";·���ж�ȡͼƬ��Դ������������Unity��
			SubmitPhoto();
		}
		return super.onKeyDown(keyCode, event);
	}
}
