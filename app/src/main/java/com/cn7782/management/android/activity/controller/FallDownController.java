package com.cn7782.management.android.activity.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.telephony.TelephonyManager;

public class FallDownController {
	private static final String TAG = "FallDownController";
	private Context mContext;
	private Handler mHandler;
	private static FallDownController fallDownCtl;
	private Vibrator vib;
	private String msgContent = "";

	public FallDownController() {
		super();
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

	public void setHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public synchronized static FallDownController getInstance() {
		if (fallDownCtl == null) {
			fallDownCtl = new FallDownController();
		}
		return fallDownCtl;
	}

	public void Vibrate(long[] pattern, boolean isRepeat) {
		vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, isRepeat ? 1 : -1);
	}

	public void callFallDownWarn(boolean isInBg) {
//		String name = "";
//		String addr = "";
//		String time = DateUtil.formartDate(DateFormatPattern.MD_OBLIQUE_HS, DateUtil.getDateFromMilliseconds(System.currentTimeMillis()));
//		
//		List<Member> list = new MemberDaoImpl(mContext).find();
//		
//		String founderName = SharePreferenceUtil.getMy_User_NAME(mContext);
//		String currentUserId = SharePreferenceUtil.getMyUser_Id(mContext);
//		
//		
//		
//		// String ta = "他";
//		for (Member m : list) {
//			if (m.getUser_id().equals(currentUserId)) {
////				if (Intem.getIsAdmin() == 1) {
////					name = founderName;
////				} else {
////					name = founderName + "的" + m.getUser_type();
////				}
//				String tempAddr = BaseApplication.getInstance().getMyAddress()
//						.getAddress();
//				addr = tempAddr
//						+ "附近("
//						+ DateUtil.formartDate(
//								DateFormatPattern.MD_OBLIQUE_HS, DateUtil
//										.getDateFromMilliseconds(System
//												.currentTimeMillis()))
//
//						+ ")";
//				// ta =
//				// (m.getUser_gender().equals(GlobalConstant.USER_GENDER_MALE))?"他":"她";
//			}
//		}
//		if (name.length() > 0 && addr.length() > 0) {
//			msgContent = name + "的手机疑似摔落(" + time + ")，最后参考位置为" + addr + "。";
//			if (!isInBg) {
//				ProgressDialogUtil
//						.openProgressDialog(mContext, "", "正在发起求助...");
//			}
//			if (null != currentUserId && currentUserId.length() != 0
//					&& list.size() > 0) {
//				new FallDownWarnCalledThread(msgContent).start();
//			}
//		}

	}

	public boolean isCanUseSim() {
		try {
			TelephonyManager mgr = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			return TelephonyManager.SIM_STATE_READY == mgr.getSimState();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isPhoneNumberValid(String phoneNumber) {
		// Pattern p =
		// Pattern.compile("^((13[0-9])|(15[^4,//D])|(18[0,5-9]))//d{8}$");
		Pattern p = Pattern.compile("1[0-9]{10}");
		Matcher m = p.matcher(phoneNumber);
		System.out.println(m.matches() + "---");
		return m.matches();
	}

	public boolean sendMsgInBg(String msg) {
		
		boolean sendMsgSuc = false;
//		List<Member> members = new MemberDaoImpl(mContext).find();
//		String loginId = SharePreferenceUtil.getMyUser_Id(mContext);
//		
//		
//
//		for (Member member : members) {
//			if (!loginId.equals(member.getUser_id())) {
//				SmsManager smsManager = SmsManager.getDefault();
//				if (msg.length() > 70) {
//					List<String> contents = smsManager.divideMessage(msg);
//					for (String sms : contents) {
//						if (member.getUser_mobile() != null
//								&& !member.getUser_mobile().trim().equals("")
//								&& isPhoneNumberValid(member.getUser_mobile())
//								&& isCanUseSim()) {
//							PendingIntent mPI = PendingIntent.getBroadcast(
//									mContext, 0, new Intent(), 0);
//							smsManager.sendTextMessage(member.getUser_mobile(),
//									null, sms, mPI, null);
//							sendMsgSuc = true;
//						}
//					}
//				} else {
//					if (member.getUser_mobile() != null
//							&& !member.getUser_mobile().trim().equals("")
//							&& isPhoneNumberValid(member.getUser_mobile())
//							&& isCanUseSim()) {
//						PendingIntent mPI = PendingIntent.getBroadcast(
//								mContext, 0, new Intent(), 0);
//						smsManager.sendTextMessage(member.getUser_mobile(),
//								null, msg, mPI, null);
//						sendMsgSuc = true;
//					}
//				}
//			}
//		}
		return sendMsgSuc;
	}

	/**
	 * 发起摔落告警
	 * 
	 * @author apple
	 * 
	 */
	class FallDownWarnCalledThread extends Thread {
		private String warnContent = "";

		public FallDownWarnCalledThread(String warnContent) {
			super();
			this.warnContent = warnContent;
		}

		public void run() {
//			Log.i(TAG, "FallDownWarnCalledThread");
//			Message msg = mHandler.obtainMessage();
//			msg.what = GlobalConstant.FALLDOWN_MSG_WHAT;
//			JSONObject jsonObject = new JSONObject();
//			// "sw_fall_down_warn_info":{
//			// “group_account”:”w131ewr1234234232342” //关联家庭ID
//			// “user_id”:”adsfqwer1341234dasfasdf” //用户id
//			// “msg_content”:”跌倒报警内容”
//			// }
//
//			try {
//				jsonObject.put("user_id", SharePreferenceUtil.getMyUser_Id(mContext));
//				jsonObject.put("group_account",
//						SharePreferenceUtil.getGroup_Account(mContext));
//				jsonObject.put("msg_content", warnContent);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//
//			Log.i(TAG, "params:" + jsonObject.toString());
//
//			String response = HttpRequest.request(RequestConstant.FALL_DOWN_WARN_INFO,
//					jsonObject);
//
//			Log.i(TAG, "response:" + response);
//			msg.obj = response;
//			mHandler.sendMessage(msg);
		};
	}

}
