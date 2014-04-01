package il.ac.huji.todolist;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

public class MyApplication extends Application {
	public static String mDeviceId = null;

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "SZYxv80CIz7WdgtKrGZ91Z0zXfmtRSOkWISpkWdX",
				"4dM88RiSouLABFm3Bu8a8hredLbuY1ZhY97edClG");

		ParseUser.enableAutomaticUser();
		
		ParseACL defaultACL = new ParseACL();
		defaultACL.setPublicReadAccess(true);
		defaultACL.setPublicWriteAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);
	}
	public String getDeviceId(Activity a){
		if(mDeviceId == null){
			mDeviceId = getUniqueID(a);
		}
		return mDeviceId;
	}
	public static String getUniqueID(Activity a) {
		TelephonyManager tm = (TelephonyManager) a
				.getSystemService(Context.TELEPHONY_SERVICE);
		return (tm.getDeviceId() + Secure.getString(a.getContentResolver(),
				Secure.ANDROID_ID));
	}
}
