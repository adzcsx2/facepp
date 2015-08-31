package com.facepp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.DropBoxManager.Entry;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.mfaceppdemo2.R;
import com.facepp.FaceppRecognize.CallBack;
import com.facerecognition.api.ApiHandle.TRAIN_TYPE;
import com.facerecognition.api.Face;
import com.facerecognition.api.Group;
import com.facerecognition.api.Person;
import com.facerecognition.data.DetectReturn;
import com.facerecognition.data.FacePlus;
import com.facerecognition.data.RecognizeReturn;
import com.facerecognition.data.Result;
import com.facerecognition.data.SubInfoFP.SessionInfoReturn;


public class MainActivity extends Activity implements OnClickListener {
	final private static String TAG = "MainActivity";
	final private int PICTURE_CHOOSE = 1;
	
	public static String groupid_long = "ad4e3bfbdbdbafbc5fd96f7372abfbfc";
	public static String fan_personid = "9ef4cf138446de161878a0a66a853bf6";
	public static String li_personid = "a755e7cb641b732a7f5712971c35f61a";
	public static String an_personid = "650c280f6c9369347fdb28975ae5749a";
	public static String liu_personid = "63cca61f69c58e19a4b195337520fe99";
	public static String fan3_personid = "018022675afbe77f05936c04410d0fdd";
	
	public static String faceid = "6a1881d966d331f48b4b425f4917970f";
	public static String personid = "b12dc19f92301d92c1621d7eb1f2490d";
	public static String sessionId = "";
	
	private FacePlus mFacePlus = FacePlus.getInstance();
	
	private ImageView btn_chooseimg;
	private Bitmap img;
	private String fileSrc;
	private EditText groupname,personname;
	private FaceppRecognize facepp;
	private SharedPreferences sp;
	
	/**
	 * 使用face++的时候需要在Configure类里配置api_key,api_secret。
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	
		facepp = new FaceppRecognize(this);
		facepp.reInit("ccc");
		sp = facepp.getSharedPreferences(this);
		btn_chooseimg = (ImageView) findViewById(R.id.btn_chooseimg);
		btn_chooseimg.setOnClickListener(this);
	
		groupname = (EditText) findViewById(R.id.edit_groupname);
		personname = (EditText) findViewById(R.id.edit_personname);
		findViewById(R.id.btn_create_group).setOnClickListener(this);
		findViewById(R.id.btn_detect).setOnClickListener(this);
		findViewById(R.id.btn_createPerson).setOnClickListener(this);
		findViewById(R.id.btn_addperson2group).setOnClickListener(this);
		findViewById(R.id.btn_identify).setOnClickListener(this);
		findViewById(R.id.btn_tain).setOnClickListener(this);
		findViewById(R.id.btn_queryResult).setOnClickListener(this);
		findViewById(R.id.btn_addface2person).setOnClickListener(this);
		findViewById(R.id.btn_findAllgroup).setOnClickListener(this);
		findViewById(R.id.btn_findpersonByGroup).setOnClickListener(this);
		findViewById(R.id.btn_deletegroup).setOnClickListener(this);
		findViewById(R.id.btn_deleteperson).setOnClickListener(this);
		findViewById(R.id.btn_findallperson).setOnClickListener(this);
		findViewById(R.id.btn_getsp).setOnClickListener(this);
		
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_getsp:
			Map<String, String> all = (Map<String, String>) sp.getAll();
//			Set<Entry<String,String>> entrySet = all.entrySet();
//			for (Entry<String, String> entry : entrySet) {
//				Log.e(TAG,entry.getKey()+"  "+entry.getValue());
//			}
			break;
		case R.id.btn_chooseimg:
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, PICTURE_CHOOSE);
			break;
		case R.id.btn_create_group:
//			create_group();
			facepp.create_group("ccc", new CallBack() {
				
				@Override
				public void result(String result) {
					// TODO Auto-generated method stub
					Log.e(TAG,result);
				}
			});
			break;
		case R.id.btn_detect:
//			detect();
			facepp.detect(fileSrc, new CallBack() {
				
				@Override
				public void result(String result) {
					// TODO Auto-generated method stub
					Log.e(TAG,result);
				}
			});
			break;
		case R.id.btn_createPerson:
//			register();
			facepp.createPerson("ddd", new CallBack() {
				
				@Override
				public void result(String result) {
					// TODO Auto-generated method stub
					Log.e(TAG,result);
				}
			});
			break;
		case R.id.btn_addperson2group:
//			addperson2group();
			facepp.addperson2group(sp.getString(FaceppRecognize.GROUP+"ccc",null),sp.getString(FaceppRecognize.PERSON+"ddd", null), new CallBack() {
				
				@Override
				public void result(String result) {
					// TODO Auto-generated method stub
					Log.e(TAG,result);
				}
			});
			break;
		case R.id.btn_tain:
//			tain();
			//返回sessionid,可以用来查询训练结果
			facepp.tain_identify(sp.getString(facepp.GROUP+"ccc",""), new CallBack() {
				
				@Override
				public void result(String result) {
					// TODO Auto-generated method stub
//				Log.e(TAG,result);	
					sessionId = result;
				}
			});
			break;	
		case R.id.btn_queryResult:
			//通过sessionid查询训练结果
//			queryTainResult();
			facepp.getTainResult(sessionId, new CallBack() {
				
				@Override
				public void result(String result) {
					// TODO Auto-generated method stub
					Log.e(TAG,result);
				}
			});
			break;
		case R.id.btn_identify:
			//没训练---人名不合法
//			indentify();
			facepp.indentify(sp.getString(facepp.GROUP+"ccc",""), fileSrc, new CallBack() {
				
				@Override
				public void result(String result) {
					// TODO Auto-generated method stub
				Log.e(TAG,result);	
				}
			});
			break;
		case R.id.btn_addface2person:
//			addface2person();
			facepp.detect(fileSrc, new CallBack() {
				
				@Override
				public void result(String result) {
					// TODO Auto-generated method stub
					if("false".equals(result)){
						Log.e(TAG,"图片没识别出脸");
								return;
					}
					facepp.addface2person(sp.getString(facepp.PERSON+"ddd", ""), result, new CallBack() {
						
						@Override
						public void result(String result) {
							// TODO Auto-generated method stub
							Log.e(TAG,result);
						}
					});
				}
			});
			break;
		case R.id.btn_findpersonByGroup:
//			getGroupInfoAsync();
			facepp.getGroupInfo(sp.getString("ccc", ""), new CallBack() {
				
				@Override
				public void result(String result) {
					// TODO Auto-generated method stub
				Log.e(TAG,result);	
				}
			});
			break;
		case R.id.btn_findAllgroup:
//			findAllGroup();
			facepp.getGroupList(new CallBack() {
				
				@Override
				public void result(String result) {
					// TODO Auto-generated method stub
					Log.e(TAG,result);
				}
			});
			break;
		case R.id.btn_deletegroup:
//			deleteGroup();
			facepp.deleteGroup(sp.getString("group_"+"ccc",null), new CallBack() {
				
				@Override
				public void result(String result) {
					// TODO Auto-generated method stub
					Log.e(TAG,result);
				}
			});
			break;
		case R.id.btn_deleteperson:
//			deletePerson();
			facepp.getPersonId("ddd", new CallBack() {
				
				@Override
				public void result(String result) {
					// TODO Auto-generated method stub
					facepp.deletePerson(result, new CallBack() {
						
						@Override
						public void result(String result) {
							// TODO Auto-generated method stub
							Log.e(TAG,result);
						}
					});
				}
			});
			break;
		case R.id.btn_findallperson:
//			findAllPerson();
			facepp.getPersonList(new CallBack() {
				
				@Override
				public void result(String result) {
					// TODO Auto-generated method stub
					Log.e(TAG,result);
				}
			});
			break;
		}
	}


	private void findAllPerson() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result result = mFacePlus.getAllPersonListInfo();
				if(result.type == Result.TYPE.FAILED){
					Debug.debug(TAG, "err msg = " + result.data);
					Log.e(TAG,"false");
					return;
				}
				List<Person> personlist = (List<Person>) result.data;
				for (int i = 0; i < personlist.size(); i++) {
					Person person = personlist.get(i);
					Log.e(TAG,person.getName());
					Log.e(TAG,person.getId());
				}
			}
		}).start();
	}


	private void deletePerson() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
			String faceid= 	"63cca61f69c58e19a4b195337520fe99";
			Result result = mFacePlus.deletePerson(faceid);
			if(result.type == Result.TYPE.FAILED){
				Debug.debug(TAG, "err msg = " + result.data);
				Log.e(TAG,"false");
				return;
			}
			Log.e(TAG,"true");
			}
		}).start();
	}


	private void deleteGroup() {
		new  Thread(new Runnable() {
			@Override
			public void run() {
				List<String> groupIds = new ArrayList<String>();
				groupIds.add( "944849f70c22d0b8fffcd8cabb5b0876");
				// TODO Auto-generated method stub
				Result result = mFacePlus.deleteGroup(groupIds);
				if(result.type == Result.TYPE.FAILED){
					Debug.debug(TAG, "err msg = " + result.data);
					Log.e(TAG,"false");
					return;
				}
				Log.e(TAG,"true");
			}
		}).start();
	}


	private void findAllGroup() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result result = mFacePlus.getAllGroupListInfo();
				if(result.type == Result.TYPE.FAILED){
					Debug.debug(TAG, "err msg = " + result.data);
					return;
				}
				List<Group> groupList = (List<Group>) result.data;
				String str_groupname = groupname.getText().toString();
				for (int i = 0; i < groupList.size(); i++) {
					Log.e(TAG,groupList.get(i).getName()+"");
						Log.e(TAG,groupList.get(i).getId()+"");
					
				}
			}
		}).start();
	}

	private void getPersonInfoAsync(){
		new Thread(){
			public void run(){
				//安以轩person id
				String personId = an_personid;
				Result result = mFacePlus.getPersonInfo(personId);
				Debug.debug(TAG, "result=" + result.type.name());
				if(result.type == Result.TYPE.FAILED){
					Debug.debug(TAG, "err msg = " + result.data);
					return;
				}
				Person person = (Person)result.data;
				Debug.debug(TAG, person.toString());
			}
		}.start();
	}
	
	private void getGroupInfoAsync(){
		new Thread(){
			public void run(){
				String id = sp.getString(facepp.GROUP+"ccc", "");
				Log.e(TAG,id);
				Result result = mFacePlus.getGroupInfo(id);
				if(result.type == Result.TYPE.FAILED){
					Debug.debug(TAG, "err msg = " + result.data);
					return;
				}
				Group group = (Group)result.data;
				Debug.debug(TAG, group.toString());
				List<Person> personList = group.getPersonList();
				for (int i = 0; i < personList.size(); i++) {
					Log.e(TAG,personList.get(i).getName());
					Log.e(TAG,personList.get(i).getId());
				}
			}
		}.start();
	}
	private void create_group() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result createGroup = mFacePlus.createGroup(groupname.getText().toString(), groupname.getText().toString(), null);
				if(createGroup.type == Result.TYPE.FAILED){
					Debug.debug(TAG, "err msg = " + createGroup.data);
					return;
				}
				Group group = (Group) createGroup.data;
				Log.e(TAG,"groupid = "+group.getId());
				Log.e(TAG,group.toString());
			}
		}).start();
	}


	private void addperson2group() {
		new Thread(){
			public void run(){
				
				String personid = personname.getText().toString();
				if(personid.equals("fan")){
					personid = fan_personid;
				}
				else if (personid.equals("li")){
					personid = li_personid;
				}
				else if (personid.equals("an")){
					personid = an_personid;
				}
				else if (personid.equals("liu")){
					personid = liu_personid;
				}
				
				String groupId = groupid_long;	//konka group id
				List<String> personIds = new ArrayList<String>();
				personIds.add(personid);		//安以轩personId
				Result result = mFacePlus.addPerson2Group(groupId, personIds);
				if(result.type == Result.TYPE.FAILED){
					Debug.debug(TAG, "err msg = " + result.data);
					return;
				}
				Log.e(TAG,result.data+"");
			}
		}.start();
	}


	private void tain() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result result = mFacePlus.train(groupid_long, TRAIN_TYPE.all );
				if(result.type == Result.TYPE.FAILED){
					Debug.debug(TAG, "err msg = " + result.data);
					return;
				}
				sessionId = result.data+"";
				 Log.e(TAG,sessionId);
			}
		}).start();
	}


	private void queryTainResult() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result result = mFacePlus.getSessionInfo(sessionId);
				if(result.type == Result.TYPE.FAILED){
					Debug.debug(TAG, "err msg = " + result.data);
					return;
				}
				SessionInfoReturn data = (SessionInfoReturn) result.data;
				Log.e(TAG,data.toString());
			}
		}).start();
	}


	private void addface2person() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String personid = personname.getText().toString();
				if(personid.equals("fan")){
					personid = fan_personid;
				}
				else if (personid.equals("li")){
					personid = li_personid;
				}
				else if (personid.equals("an")){
					personid = an_personid;
				}
				else if (personid.equals("liu")){
					personid = liu_personid;
				}
				
				List<String> faceIds = new ArrayList<String>();
				faceIds.add(faceid);
				Result result = mFacePlus.addFace2Person(personid, faceIds );
				if(result.type == Result.TYPE.FAILED){
					Debug.debug(TAG, "err msg = " + result.data);
					return;
				}
				Debug.debug(TAG, "执行" + result.data);
			}
		}).start();
	}


	private void indentify() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result recognize = mFacePlus.recognize(groupid_long, fileSrc);
				Log.e(TAG,fileSrc);
//					RecognizeReturn result = (RecognizeReturn) recognize.data;
				if(recognize.type == Result.TYPE.FAILED){
					Debug.debug(TAG, "err msg = " + recognize.data);
					return;
				}
				RecognizeReturn data = (RecognizeReturn) recognize.data;
				//一张图片里有几张脸
				int size = data.faceList.size();
				if(size==0){
					Log.e(TAG,"图片没能识别出脸");
					return;
				}
				Log.e(TAG,"识别出"+size+"张脸");
				for (int i = 0; i < size; i++) {
					//第i张脸在group中的置信度 ,第0个置信度最高
					List<Person> personList = data.faceList.get(i).getCandidatePersonList();
					Person person = personList.get(0);
					Log.e(TAG,"该图片第"+i+"张脸最有可能是"+person.getName());
				}
				Log.e(TAG,data.toString());
			}
		}).start();
	}


	private void register() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String str_personname = personname.getText().toString();
				List<String> faceIds = new ArrayList<String>();
				faceIds.add(faceid);
				List<String> groupid = new ArrayList<String>();
				groupid.add(groupid_long);
				Result result = mFacePlus.createPerson(str_personname, faceIds, str_personname, groupid );
				Debug.debug(TAG, "result=" + result.type.name());
				if(result.type == Result.TYPE.FAILED){
					Debug.debug(TAG, "err msg = " + result.data);
					return;
				}
				final Person person = (Person)result.data;
				personid = person.getId();
				Log.e(TAG,personid);
				Debug.debug(TAG, person.toString());
				
			}
		}).start();
	}


	private void detect() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result detect = mFacePlus.detect(fileSrc);
//					Result detect = mFacePlus.detectByPicUrl("http://a4.att.hudong.com/86/42/300000876508131216423466864_950.jpg");
				if(detect.type == Result.TYPE.FAILED){
					Debug.debug(TAG, "err msg = " + detect.data);
					return;
				}
				DetectReturn data = (DetectReturn) detect.data;
				int size = data.faceList.size();
				for (int i = 0; i < size; i++) {
					Face face = data.faceList.get(i);
					faceid = face.getId();
					Log.e(TAG,faceid);
				}
			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		// the image picker callback
		if (requestCode == PICTURE_CHOOSE) {
			if (intent != null) {
				// The Android api ~~~
				// Log.d(TAG, "idButSelPic Photopicker: " +
				// intent.getDataString());
				Cursor cursor = getContentResolver().query(intent.getData(),
						null, null, null, null);
				cursor.moveToFirst();
				int idx = cursor.getColumnIndex(ImageColumns.DATA);
				fileSrc = cursor.getString(idx);
				 Log.e(TAG, "Picture:" + fileSrc);

				// just read size
				Options options = new Options();
				options.inJustDecodeBounds = true;
				img = BitmapFactory.decodeFile(fileSrc, options);

				// scale size to read
				options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
						(double) options.outWidth / 1024f,
						(double) options.outHeight / 1024f)));
				options.inJustDecodeBounds = false;
				img = BitmapFactory.decodeFile(fileSrc, options);

				btn_chooseimg.setImageBitmap(img);
			} else {
				Log.d(TAG, "idButSelPic Photopicker canceled");
			}
			detect();
		}
	}

	
}
