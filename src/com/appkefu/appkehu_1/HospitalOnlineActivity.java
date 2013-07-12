package com.appkefu.appkehu_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.appkefu.lib.ChatViewActivity;
import com.appkefu.lib.service.UsernameAndKefu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HospitalOnlineActivity extends Activity {
	
	int[] image = {
			R.drawable.hos_case, R.drawable.hos_chat, R.drawable.hos_eye, R.drawable.hos_eye_2,
			R.drawable.hos_instro, R.drawable.hos_nav, R.drawable.hos_other, R.drawable.hos_pro,
			R.drawable.hos_promotion 
		};
	
	private MyAdapter adapter = null;
	private ArrayList<Map<String, Object>> array;
	
	//Begin From AppKeFuDemoAdvanced
	private static final String TAG = HospitalOnlineActivity.class.getSimpleName();
	private static final int LOGIN_REQUEST_CODE = 1;	
	private static final String SERIAL_KEY = "com.appkefu.lib.username.serialize";
	private AppApplication app;
	//End

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Begin From AppKeFuDemoAdvanced
		app = (AppApplication)getApplication();
		//End
		
		GridViewInterceptor gv = (GridViewInterceptor) findViewById(R.id.gride);
		array = getData();
		adapter = new MyAdapter();
		gv.setDropListener(onDrop);
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(new ItemClickEvent());
	}

	private GridViewInterceptor.DropListener onDrop = new GridViewInterceptor.DropListener() {
		public void drop(int from, int to) {
			Map item = adapter.getItem(from);

			adapter.remove(item);
			adapter.insert(item, to);
		}
	};

	public class ImageList extends BaseAdapter {
		Activity activity;

		// construct
		public ImageList(Activity a) {
			activity = a;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return image.length;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return image[position];
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView iv = new ImageView(activity);
			iv.setImageResource(image[position]);
			return iv;
		}
	}

	class MyAdapter extends ArrayAdapter<Map<String, Object>> {

		MyAdapter() {
			super(HospitalOnlineActivity.this, R.layout.gridview_item, array);
		}

		public ArrayList<Map<String, Object>> getList() {
			return array;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.gridview_item, parent, false);
			}
			ImageView imageView = (ImageView) row.findViewById(R.id.img);
			imageView.setImageResource(Integer.valueOf(array.get(position).get("img").toString()));
			TextView textView = (TextView) row.findViewById(R.id.text);

			switch(position)
			{
			case 0:
				textView.setText(R.string.hos_intro);
				break;
			case 1:
				textView.setText(R.string.hos_pro);
				break;
			case 2:
				textView.setText(R.string.hos_subject);
				break;
			case 3:
				textView.setText(R.string.hos_nav);
				break;
			case 4:
				textView.setText(R.string.hos_yuyue);
				break;
			case 5:
				textView.setText(R.string.hos_chat);
				break;
			case 6:
				textView.setText(R.string.hos_shuang);
				break;
			case 7:
				textView.setText(R.string.hos_kaiyanjiao);
				break;
			case 8:
				textView.setText(R.string.hos_case);
				break;
			}
			//医院简介、专家团队、活动专题、微整形案例、美丽导航、在线预约、在线咨询
			//双眼皮、开眼角、其他美容项目
			
			return (row);
		}
	}

	private ArrayList<Map<String, Object>> getData() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < image.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("img", image[i]);
			list.add(map);

		}
		return list;
	}

	class ItemClickEvent implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			
			TextView textView =  (TextView)view.findViewById(R.id.text);
			
			Toast.makeText(HospitalOnlineActivity.this, textView.getText(), Toast.LENGTH_SHORT).show();
			view.setPressed(false);
			view.setSelected(false);
			
			if(textView.getText().equals("医院简介"))
			{
				Log.d(TAG, "hos_intro");
				Intent intent = new Intent(HospitalOnlineActivity.this, HosIntroActivity.class);
				startActivity(intent);
			}
			else if(textView.getText().equals("专家团队"))
			{
				Log.d(TAG, "hos_pro");
				Intent intent = new Intent(HospitalOnlineActivity.this, HosProActivity.class);
				startActivity(intent);
			}
			else if(textView.getText().equals("活动专题"))
			{
				Log.d(TAG, "hos_subject");
				Intent intent = new Intent(HospitalOnlineActivity.this, HosSubjectActivity.class);
				startActivity(intent);
			}
			else if(textView.getText().equals("美丽导航"))
			{
				Log.d(TAG, "hos_nav");
				Intent intent = new Intent(HospitalOnlineActivity.this, HosNavActivity.class);
				startActivity(intent);
			}
			else if(textView.getText().equals("在线预约"))
			{
				Log.d(TAG, "hos_yuyue");
				Intent intent = new Intent(HospitalOnlineActivity.this, HosYuyueActivity.class);
				startActivity(intent);
			}
			else if(textView.getText().equals("在线咨询"))
			{
				Log.d(TAG, "hos_chat");
				startChat("testusername","admin");
			}
			else if(textView.getText().equals("双眼皮"))
			{
				Log.d(TAG, "hos_shuang");
				Intent intent = new Intent(HospitalOnlineActivity.this, HosShuangActivity.class);
				startActivity(intent);
			}
			else if(textView.getText().equals("开眼角"))
			{
				Log.d(TAG, "hos_kaiyanjiao");
				Intent intent = new Intent(HospitalOnlineActivity.this, HosKaiyanjiaoActivity.class);
				startActivity(intent);
			}
			else if(textView.getText().equals("微整形案例"))
			{
				Log.d(TAG, "hos_case");
				Intent intent = new Intent(HospitalOnlineActivity.this, HosCaseActivity.class);
				startActivity(intent);
			}
			
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
		
		if(!app.isConnected())
		{
			Log.d(TAG, "start login");
			Intent login = new Intent(this, LoginActivity.class);
			startActivityForResult(login, LOGIN_REQUEST_CODE);
		}
		else
		{
			Log.d(TAG, "already logged in");
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LOGIN_REQUEST_CODE) {
			
			if (resultCode == Activity.RESULT_OK) 
			{
				Log.d(TAG, "Activity.RESULT_OK");
				app.setConnected(true);

			}
			else if (resultCode == Activity.RESULT_CANCELED) 
			{
				Log.d(TAG, "Activity.RESULT_CANCELED");
				app.setConnected(false);
				
				//请检查网络链接、appkey是否填写正确
				Toast.makeText(this, "链接服务器失败", Toast.LENGTH_LONG).show();
			}
		} 
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "onRestart");
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();

		Log.d(TAG, "onStop");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		
	}
	
	private void startChat(String username, String kefuName) {
		
		String jid = kefuName + "@appkefu.com";
		Intent intent = new Intent(this, ChatViewActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		UsernameAndKefu usernameAndKefu = new UsernameAndKefu();
		usernameAndKefu.setUsername(username);
		usernameAndKefu.setKefuJID(jid);
		
		Bundle mbundle = new Bundle();
		mbundle.putSerializable(SERIAL_KEY, usernameAndKefu);
		intent.putExtras(mbundle);
			
		startActivity(intent);	
    }
}
















