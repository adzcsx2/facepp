package com.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.facerecognition.api.ApiHandle.TRAIN_TYPE;
import com.facerecognition.api.Face;
import com.facerecognition.api.Group;
import com.facerecognition.api.Person;
import com.facerecognition.data.DetectReturn;
import com.facerecognition.data.FacePlus;
import com.facerecognition.data.RecognizeReturn;
import com.facerecognition.data.Result;
import com.facerecognition.data.SubInfoFP.SessionInfoReturn;
import com.minterface.CallBack;

/**
 * 该封装为个人封装，详细API请查看face++官网:http://www.faceplusplus.com.cn/trainidentify/
 * 使用说明:需要先在Configure类中声明api_key和api_secret
 * 在使用缓存查询数据时，groupname前面需要加FaceppRecognize.GROUP
 * personname前面需要加FaceppRecognize.PERSON
 * 例:createGroup(FaceppRecognize.GROUP+"groupname",new Callback());
 * 
 * @author 浩然
 * 
 */

public class FaceppRecognize {
	public static final String TAG = "FaceppRecognize";
	public static final String GROUP = "group_";
	public static final String PERSON = "person_";
	public static final String Smile50 = "看起来气色不错哦";
	public static final String Smile30 = "心情很好哦";
	public static final String Smile10 = "天天好心情";
	public static final String Smile0 = "祝你有个好心情";

	private FacePlus mFacePlus = FacePlus.getInstance();
	private SharedPreferences sp;
	private Context context;

	public SharedPreferences getSharedPreferences(Context context) {
		sp = context.getSharedPreferences("face", Context.MODE_PRIVATE);
		return sp;
	}

	public FaceppRecognize(Context context) {
		super();
		this.context = context;
	}

	/**
	 * 初始化 缓存groupname的所有信息，假如groupname不存在，以该groupname创建该group。
	 * 
	 * 缓存的格式 组 key:group_groupname value:groupid 成员 key:person_personname
	 * value:personid
	 * 
	 * @param groupname
	 */
	public void init(final String groupname) {
		sp = getSharedPreferences(context);
		if (!sp.contains(GROUP + groupname)) {
			getGroupId(groupname, new CallBack() {

				@Override
				public void result(String groupid) {
					// TODO Auto-generated method stub
					if (!groupid.equals("false")) {
						Editor edit = sp.edit();
						edit.putString("group_" + groupname, groupid);
						edit.commit();
						getGroupInfo(groupid, new CallBack() {

							@Override
							public void result(String result) {
								// TODO Auto-generated method stub
								if ("".equals(result)) {
									return;
								}
								if ("false".equals(result)) {

									create_group(groupname, new CallBack() {

										@Override
										public void result(String result) {
											// TODO Auto-generated method stub
											Log.e(TAG, result);
										}
									});
								} else {
									String[] split = result.split(",");// personname:personid
									for (int i = 0; i < split.length; i++) {
										String[] person = split[i].split(":");

										String personname = person[0];
										String personid = person[1];
										if (!groupname.equals(personname)) {
											Editor editor = sp.edit();
											editor.putString("person_"
													+ personname, personid);
											editor.commit();
										}
									}
								}
							}
						});
					} else {
						create_group(groupname, new CallBack() {

							@Override
							public void result(String result) {
								// TODO Auto-generated method stub
								Log.e(TAG, result);
							}
						});
					}
				}
			});

		}
	}

	/**
	 * 重新初始化，将缓存sp里的数据清空，重新加入groupname中的数据
	 * 同init，假如不存在该groupname的group,将创建一个以该groupname命名的group
	 * 
	 * @param groupname
	 */
	public void reInit(String groupname) {
		if (sp == null) {
			sp = getSharedPreferences(context);
		}
		// if (!sp.contains("group_" + groupname)) {
		Editor edit = sp.edit();
		edit.clear();
		edit.commit();
		init(groupname);
		// }
	}

	/**
	 * 创建组，成功true,失败返回false;默认缓存
	 * 
	 * @param groupname
	 * @param callBack
	 */
	public void create_group(final String groupname, final CallBack callBack) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result createGroup = mFacePlus.createGroup(groupname,
						groupname, null);
				if (createGroup.type == Result.TYPE.FAILED) {
					Log.e(TAG, createGroup.data + "");
					callBack.result("false");
					return;
				}
				Group group = (Group) createGroup.data;
				// LOG.e("groupid = "+group.getId());
				sp.edit().putString(GROUP + groupname, group.getId()).commit();
				callBack.result("true");
			}
		}).start();
	}

	/**
	 * 人脸检测(从SD卡)，成功返回face_id : smile(图片上所有的faceid),失败返回失败信息
	 * 例:xxxxx:3.33
	 * @param imgfile
	 * @param callback
	 */

	public void detect(final String imgfile, final CallBack callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result detect = mFacePlus.detect(imgfile);
				// Result detect =
				// mFacePlus.detectByPicUrl("http://a4.att.hudong.com/86/42/300000876508131216423466864_950.jpg");
				if (detect.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + detect.data);
					callback.result("false");
					return;
				}
				DetectReturn data = (DetectReturn) detect.data;
				int size = data.faceList.size();
				if (size == 0) {
					callback.result("false");
					return;
				}
				String allFace = "";
				for (int i = 0; i < size; i++) {
					Face face = data.faceList.get(i);
					if (i == size - 1) {
						allFace += face.getId() + ":" + face.getSmiling();
					} else {
						allFace += face.getId() + ":" + face.getSmiling() + ",";
					}
				}
				callback.result(allFace);
			}
		}).start();
	}

	/**
	 * 人脸检测(从网络),成功返回face_id,失败返回失败信息
	 * 
	 * @param imgurl
	 * @param callback
	 */
	public void detectFromUrl(final String imgurl, final CallBack callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result detect = mFacePlus.detectByPicUrl(imgurl);
				if (detect.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + detect.data);
					callback.result("false");
					return;
				}
				DetectReturn data = (DetectReturn) detect.data;
				int size = data.faceList.size();
				for (int i = 0; i < size; i++) {
					Face face = data.faceList.get(i);
					String faceid = face.getId();
					callback.result(faceid + "");
				}
			}
		}).start();
	}

	/**
	 * 创建一个人,成功返回true,失败返回false 人名不可重复 默认缓存
	 * 
	 * @param personname
	 *            人名
	 * @param groupid
	 *            注册的group id
	 * @param callBack
	 *            回调接口 true or false
	 */

	public void createPerson(final String personname, final CallBack callBack) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<String> faceIds = new ArrayList<String>();
				// TODO Auto-generated method stub
				Result result = mFacePlus.createPerson(personname, faceIds,
						personname, null);
				Debug.debug(TAG, "result=" + result.type.name());
				if (result.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + result.data);
					callBack.result("false");
					return;
				}
				final Person person = (Person) result.data;
				// 将该数据加入缓存
				sp.edit().putString(PERSON + personname, person.getId())
						.commit();
				callBack.result("true");

			}
		}).start();
	}

	/**
	 * 创建一个人,并将其添加进group中，成功返回person_id,失败返回失败信息 人名不可重复 (人不存在)
	 * 
	 * @param personname
	 *            人名
	 * @param groupid
	 *            注册的group id
	 * @param callBack
	 *            回调接口 返回personid
	 */

	public void createPerson2Group(final String personname,
			final String groupid, final CallBack callBack) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<String> groupids = new ArrayList<String>();
				groupids.add(groupid);
				Result result = mFacePlus.createPerson(personname, null,
						personname, groupids);
				Debug.debug(TAG, "result=" + result.type.name());
				if (result.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + result.data);
					callBack.result("false");
					return;
				}
				final Person person = (Person) result.data;
				// 将该数据加入缓存
				if (sp != null && !sp.contains("person_" + personname)) {
					sp.edit().putString("person_" + personname, person.getId())
							.commit();
				}
				callBack.result(person.getId() + "");

			}
		}).start();
	}

	/**
	 * 给指定人添加人脸(用于训练提高识别度),没训练。
	 * 
	 * @param personid
	 * @param faceid
	 * @param callBack
	 */

	public void addface2person(final String personid, final String faceid,
			final CallBack callBack) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				List<String> faceIds = new ArrayList<String>();
				faceIds.add(faceid);
				Result result = mFacePlus.addFace2Person(personid, faceIds);
				if (result.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + result.data);
					callBack.result("false");
					return;
				}
				// LOG.e(result.data + "");
				callBack.result("true");
			}
		}).start();
	}

	/**
	 * 给指定组的人添加人脸(提高识别度),并且训练
	 * 
	 * @param personid
	 * @param faceid
	 * @param groupid
	 * @param callBack
	 */
	public void addface2person(final String personid, final String faceid,
			final String groupid, final CallBack callBack) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				List<String> faceIds = new ArrayList<String>();
				faceIds.add(faceid);
				Result result = mFacePlus.addFace2Person(personid, faceIds);
				if (result.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + result.data);
					callBack.result("false");
					return;
				}
				// LOG.e(result.data + "");
				callBack.result("true");
				tain_identify(groupid, null);
			}
		}).start();
	}

	/**
	 * (人已经存在，通过personid添加进group)给指定组添加人
	 * 
	 * @param groupid
	 * @param personid
	 * @param callback
	 */

	public void addperson2group(final String groupid, final String personid,
			final CallBack callback) {
		new Thread() {
			public void run() {

				List<String> personIds = new ArrayList<String>();
				personIds.add(personid); // 安以轩personId
				Result result = mFacePlus.addPerson2Group(groupid, personIds);
				if (result.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + result.data);
					callback.result("false");
					return;
				}
				Log.e(TAG, result.data + "");
				callback.result("true");
				// 添加成功就训练
				tain_identify(groupid, null);
			}
		}.start();
	}

	/**
	 * 对指定组训练 ps:某个组添加了人，或该组某个人添加了脸，该组都需要重新训练
	 * 该训练所花费的时间比较长，可通过返回的sessionId查询训练是否完毕。 训练时需要保证group内的所有person均非空
	 * 
	 * @param groupid
	 * @param callback
	 *            成功返回sessionId,失败返回false
	 */
	public void tain_identify(final String groupid, final CallBack callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result result = mFacePlus.train(groupid, TRAIN_TYPE.all);
				if (result.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + result.data);
					callback.result("false");
					return;
				}
				if (callback != null) {
					callback.result(result.data + "");
				}
			}
		}).start();
	}

	/**
	 * !!!此方法内部json解析有问题，只能通过log日志查看是否训练完毕。 如果修改内部的json解析可能会导致其他问题
	 * 通过sessionId查询是否训练完毕
	 * 
	 * 
	 * @param sessionId
	 * @param callback
	 */
	public void getTainResult(final String sessionId, final CallBack callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result result = mFacePlus.getSessionInfo(sessionId);
				if (result.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + result.data);
					callback.result("false");
					return;
				}
				SessionInfoReturn data = (SessionInfoReturn) result.data;
				Log.e(TAG, data.toString());
				callback.result("true");
			}
		}).start();
	}

	/**
	 * 人脸识别 返回图片中所有脸分别在group中最像的人的名字和笑容
	 * 
	 * @param groupid
	 * @param imgFile
	 * @param callback
	 *            返回一张图片每张脸对应group中最像的脸 返回格式personname:confidence
	 */

	public void indentify(final String groupid, final String imgFile,
			final CallBack callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result recognize = mFacePlus.recognize(groupid, imgFile);
				if (recognize.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + recognize.data);
					Log.e(TAG, recognize.data + "");
					callback.result("false");
					return;
				}
				RecognizeReturn data = (RecognizeReturn) recognize.data;
				// 一张图片里有几张脸
				int size = data.faceList.size();
				if (size == 0) {
					// LOG.e("图片没能识别出脸");
					callback.result("no face!");
					return;
				}
				// LOG.e("识别出"+size+"张脸");
				String similarName = "";
				for (int i = 0; i < size; i++) {
					// 第i张脸在group中的置信度 ,第0个置信度最高
					List<Person> personList = data.faceList.get(i)
							.getCandidatePersonList();
					if (personList.size() == 0) {
						callback.result("组内没有相似的人");
						return;
					}
					Person person = personList.get(0);
					// LOG.e("该图片第"+i+"张脸最有可能是"+person.getName());

					if (i == size - 1) {
						similarName += person.getName();
					} else {
						similarName += person.getName() + ",";
					}
				}
				// LOG.e(data.toString());
				callback.result(similarName);
			}
		}).start();
	}

	/**
	 * 根据groupid,得到该group中所有person的name和id
	 * 
	 * @param groupid
	 * @param callback
	 *            返回格式 personname:personid,personname:personid
	 *            (每个以','号隔开，末尾没有','号)
	 */
	public void getGroupInfo(final String groupid, final CallBack callback) {
		new Thread() {
			public void run() {
				Result result = mFacePlus.getGroupInfo(groupid);
				if (result.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + result.data);
					callback.result("false");
					return;
				}
				Group group = (Group) result.data;
				Debug.debug(TAG, group.toString());
				String allname = "";
				List<Person> personList = group.getPersonList();

				for (int i = 0; i < personList.size(); i++) {
					if (i == personList.size() - 1) {
						allname += personList.get(i).getName() + ":"
								+ personList.get(i).getId();
					} else {
						allname += personList.get(i).getName() + ":"
								+ personList.get(i).getId() + ",";
					}
				}
				callback.result(allname);
			}
		}.start();
	}

	/**
	 * 得到face++应用所有的person name+id;
	 * 
	 * @param callback
	 *            返回格式:name:id,name:id(每个以','隔开，末尾没有',')
	 */

	public void getPersonList(final CallBack callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result result = mFacePlus.getAllPersonListInfo();
				if (result.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + result.data);
					callback.result("false");
					return;
				}
				List<Person> personlist = (List<Person>) result.data;
				String persons = "";
				for (int i = 0; i < personlist.size(); i++) {
					Person person = personlist.get(i);
					if (i == personlist.size() - 1) {
						persons += person.getName() + ":" + person.getId();
					} else {
						persons += person.getName() + ":" + person.getId()
								+ ",";
					}
				}
				callback.result(persons);
			}
		}).start();
	}

	/**
	 * 通过名字找到id(从face++应用所有名字中查找，效率不高)
	 * 
	 * @param personname
	 * @param callback
	 *            返回personid
	 */

	public void getPersonId(final String personname, final CallBack callback) {
		getPersonList(new CallBack() {

			@Override
			public void result(String result) {
				// TODO Auto-generated method stub
				if (result.equals("false")) {
					callback.result("false");
					return;
				}
				String[] split = result.split(",");
				String id = "没有此name";
				for (int i = 0; i < split.length; i++) {
					String[] name_id = split[i].split(":");
					for (int j = 0; j < name_id.length; j++) {
						if (personname.equals(name_id[0])) {
							id = name_id[1];
						}
					}
				}
				callback.result(id);
			}
		});
	}

	/**
	 * 通过personname找到在该group中的personid(从group中查找id,效率高)
	 * 
	 * @param groupid
	 * @param personname
	 * @param callback
	 *            返回该personname对应的personid
	 */
	public void getPersonIdByGroup(final String groupid,
			final String personname, final CallBack callback) {
		getGroupInfo(groupid, new CallBack() {

			@Override
			public void result(String result) {
				// 切成对应的personname:personid格式
				if (result.equals("false")) {
					return;
				}
				String[] split = result.split(",");
				for (int i = 0; i < split.length; i++) {
					String[] name_id = split[i].split(":");
					if (name_id[0].equals(personname)) {
						callback.result(name_id[1]);
					}
				}
			}
		});
	}

	/**
	 * 通过组名得到groupid
	 * 
	 * @param groupname
	 * @param callback
	 *            返回groupid
	 */

	public void getGroupId(final String groupname, final CallBack callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result result = mFacePlus.getAllGroupListInfo();
				if (result.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + result.data);
					callback.result("false");
					return;
				}
				List<Group> groupList = (List<Group>) result.data;
				String id = "false";
				for (int i = 0; i < groupList.size(); i++) {
					if (groupname.equals(groupList.get(i).getName())) {
						id = groupList.get(i).getId();
					}
				}
				callback.result(id);
			}
		}).start();
	}

	/**
	 * 删除组
	 * 
	 * @param group_id
	 * @param callback
	 *            true or false;
	 */
	public void deleteGroup(final String group_id, final CallBack callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<String> groupIds = new ArrayList<String>();
				groupIds.add(group_id);
				// TODO Auto-generated method stub
				Result result = mFacePlus.deleteGroup(groupIds);
				if (result.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + result.data);
					callback.result("false");
					return;
				}
				String groupname = getSPKeyByValue(group_id);
				sp.edit().remove(groupname).commit();
				callback.result("true");
			}
		}).start();
	}

	/**
	 * 通过person_id删除人
	 * 
	 * @param person_id
	 * @param callback
	 *            true or false
	 */
	public void deletePerson(final String person_id, final CallBack callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result result = mFacePlus.deletePerson(person_id);
				if (result.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + result.data);
					callback.result("false");
					return;
				}
				sp.edit().remove(getSPKeyByValue(person_id)).commit();
				callback.result("true");
			}
		}).start();
	}

	public void getGroupList(final CallBack callback) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Result result = mFacePlus.getAllGroupListInfo();
				if (result.type == Result.TYPE.FAILED) {
					Debug.debug(TAG, "err msg = " + result.data);
					return;
				}
				List<Group> groupList = (List<Group>) result.data;
				String str_group = "";
				for (int i = 0; i < groupList.size(); i++) {
					Group group = groupList.get(i);
					if (i == groupList.size() - 1) {
						str_group += group.getName() + ":" + group.getId();
					} else {
						str_group += group.getName() + ":" + group.getId()
								+ ",";
					}
				}
				callback.result(str_group);
			}
		}).start();
	}
	
	/**通过sharepreference查找id*/
	public String getPersonId(String name) {
		String personId = sp.getString(PERSON + name, "false");
		return personId;
	}
	public String getGroupId(String name) {
		String groupId = sp.getString(GROUP + name, "false");
		return groupId;
	}

	private String getSPKeyByValue(String value) {
		Map<String, String> all = (Map<String, String>) sp.getAll();
		Set<Entry<String, String>> entrySet = all.entrySet();
		for (Entry<String, String> entry : entrySet) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return "";
	}

	/**
	 * 将笑容数值转为语言
	 * 
	 * @param smileValue
	 * @return
	 */
	public String getSmile(float smileValue) {
		if (smileValue > 50) {
			return Smile50;
		} else if (smileValue > 30) {
			return Smile30;
		} else if (smileValue > 10) {
			return Smile10;
		}
		return Smile0;
	}
}
