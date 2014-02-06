package com.appkefu.appkehu_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.util.StringUtils;

import com.appkefu.lib.interfaces.KFInterfaces;
import com.appkefu.lib.service.KFMainService;
import com.appkefu.lib.service.KFSettingsManager;
import com.appkefu.lib.service.KFXmppManager;
import com.appkefu.lib.utils.KFSLog;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
	
	/*
	 提示：如果已经运行过旧版的Demo，请先在手机上删除原先的App再重新运行此工程
	 更多使用帮助参见：http://appkefu.com/AppKeFu/tutorial-android.html
	
	 简要使用说明：
	 第1步：到http://appkefu.com/AppKeFu/admin/，注册/创建应用/分配客服，并将获取的appkey填入AnroidManifest.xml
	 		中的com.appkefu.lib.appkey
	 第2步：用真实的客服名初始化mKefuUsername
	 第3步：调用 KFInterfaces.visitorLogin(this); 函数登录
	 第4步：调用chatWithKeFu(mKefuUsername);与客服会话，其中mKefuUsername需要替换为真实客服名
	 第5步：(可选)
     	//设置昵称，否则在客服客户端 看到的会是一串字符串(必须在登录成功之后才能调用，才有效)
     	KFInterfaces.setVisitorNickname("访客1", this);
	 */
	
	int[] image = {
			R.drawable.hos_case, R.drawable.hos_chat, R.drawable.hos_eye, R.drawable.hos_eye_2,
			R.drawable.hos_instro, R.drawable.hos_nav, R.drawable.hos_other, R.drawable.hos_pro,
			R.drawable.hos_promotion 
		};
	
	private MyAdapter adapter = null;
	private ArrayList<Map<String, Object>> array;
	
	//客服用户名，需要填写为真实的客服用户名，需要到管理后台(http://appkefu.com/AppKeFu/admin/),分配
	private String 			  mKefuUsername;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//与admin会话,实际应用中需要将admin替换为真实的客服用户名			
		mKefuUsername = "admin";
				
		GridViewInterceptor gv = (GridViewInterceptor) findViewById(R.id.gride);
		array = getData();
		adapter = new MyAdapter();
		gv.setDropListener(onDrop);
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(new ItemClickEvent());
		
		//设置开发者调试模式，默认为true，如要关闭开发者模式，请设置为false
		KFSettingsManager.getSettingsManager(this).setDebugMode(true);
		//第一步：登录
		KFInterfaces.visitorLogin(this);
		
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
				Intent intent = new Intent(HospitalOnlineActivity.this, HosIntroActivity.class);
				startActivity(intent);
			}
			else if(textView.getText().equals("专家团队"))
			{
				Intent intent = new Intent(HospitalOnlineActivity.this, HosProActivity.class);
				startActivity(intent);
			}
			else if(textView.getText().equals("活动专题"))
			{
				Intent intent = new Intent(HospitalOnlineActivity.this, HosSubjectActivity.class);
				startActivity(intent);
			}
			else if(textView.getText().equals("美丽导航"))
			{
				Intent intent = new Intent(HospitalOnlineActivity.this, HosNavActivity.class);
				startActivity(intent);
			}
			else if(textView.getText().equals("在线预约"))
			{
				Intent intent = new Intent(HospitalOnlineActivity.this, HosYuyueActivity.class);
				startActivity(intent);
			}
			else if(textView.getText().equals("在线咨询"))
			{
				
				chatWithKeFu(mKefuUsername);
			}
			else if(textView.getText().equals("双眼皮"))
			{
				Intent intent = new Intent(HospitalOnlineActivity.this, HosShuangActivity.class);
				startActivity(intent);
			}
			else if(textView.getText().equals("开眼角"))
			{
				Intent intent = new Intent(HospitalOnlineActivity.this, HosKaiyanjiaoActivity.class);
				startActivity(intent);
			}
			else if(textView.getText().equals("微整形案例"))
			{
				Intent intent = new Intent(HospitalOnlineActivity.this, HosCaseActivity.class);
				startActivity(intent);
			}
			
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		KFSLog.d("onStart");
		
		IntentFilter intentFilter = new IntentFilter();
		//监听网络连接变化情况
        intentFilter.addAction(KFMainService.ACTION_XMPP_CONNECTION_CHANGED);
        //监听消息
        intentFilter.addAction(KFMainService.ACTION_XMPP_MESSAGE_RECEIVED);

        registerReceiver(mXmppreceiver, intentFilter); 
	}


	@Override
	protected void onStop() {
		super.onStop();

		KFSLog.d("onStop");
		
        unregisterReceiver(mXmppreceiver);
	}
	

	//启动咨询对话框
	private void chatWithKeFu(String kefuUsername)
	{
		KFInterfaces.startChatWithKeFu(this,
				kefuUsername, //客服用户名
				"您好，我是微客服小秘书，请问有什么可以帮您的?",  //问候语
				"咨询客服");//会话窗口标题
	}
	
	//监听：连接状态、即时通讯消息、客服在线状态
	private BroadcastReceiver mXmppreceiver = new BroadcastReceiver() 
	{
        public void onReceive(Context context, Intent intent) 
        {
            String action = intent.getAction();
            //监听：连接状态
            if (action.equals(KFMainService.ACTION_XMPP_CONNECTION_CHANGED))//监听链接状态
            {
                updateStatus(intent.getIntExtra("new_state", 0));        
            }
            //监听：即时通讯消息
            else if(action.equals(KFMainService.ACTION_XMPP_MESSAGE_RECEIVED))//监听消息
            {
            	String body = intent.getStringExtra("body");
            	String from = StringUtils.parseName(intent.getStringExtra("from"));
            	
            	KFSLog.d("body:"+body+" from:"+from);
            }

        }
    };


  //根据监听到的连接变化情况更新界面显示
    private void updateStatus(int status) {

    	switch (status) {
            case KFXmppManager.CONNECTED:
            	KFSLog.d("connected");
            	//mTitle.setText("微客服(客服Demo)");

        		//设置昵称，否则在客服客户端 看到的会是一串字符串(必须在登录成功之后才能调用，才有效)
        		//KFInterfaces.setVisitorNickname("访客1", this);

                break;
            case KFXmppManager.DISCONNECTED:
            	KFSLog.d("disconnected");
            	//mTitle.setText("微客服(客服Demo)(未连接)");
                break;
            case KFXmppManager.CONNECTING:
            	KFSLog.d("connecting");
            	//mTitle.setText("微客服(客服Demo)(登录中...)");
            	break;
            case KFXmppManager.DISCONNECTING:
            	KFSLog.d("connecting");
            	//mTitle.setText("微客服(客服Demo)(登出中...)");
                break;
            case KFXmppManager.WAITING_TO_CONNECT:
            case KFXmppManager.WAITING_FOR_NETWORK:
            	KFSLog.d("waiting to connect");
            	//mTitle.setText("微客服(客服Demo)(等待中)");
                break;
            default:
                throw new IllegalStateException();
        }
    }
    
}
















