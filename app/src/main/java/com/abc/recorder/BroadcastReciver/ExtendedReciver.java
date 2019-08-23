package com.abc.recorder.BroadcastReciver;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.abc.recorder.SqliteDatabase.DatabaseHelper;
import com.abc.recorder.contacts.ContactProvider;
import com.abc.recorder.pojo_classes.Contacts;
import com.abc.recorder.utils.StringUtils;

import java.util.Date;

public class ExtendedReciver extends com.abc.recorder.BroadcastReciver.MyReceiver {
    String formated_number;
    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
       //incoming call ringing
    }
    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        //out going call started
        formated_number= StringUtils.prepareContacts(ctx,number);
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean b=SP.getBoolean("STATE",true);
        if (b&&ContactProvider.checkContactToRecord(ctx,number)){
            startRecord(formated_number+"__"+ ContactProvider.getCurrentTimeStamp()+"__"+"OUT__2");
            addtoDatabase(ctx,formated_number);
            ContactProvider.sendnotification(ctx);
        }
    }
    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        //incoming call answered
        formated_number= StringUtils.prepareContacts(ctx,number);
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean b=SP.getBoolean("STATE",true);
        if(b&&ContactProvider.checkContactToRecord(ctx,number)){
            startRecord(formated_number+"__"+ContactProvider.getCurrentTimeStamp()+"__"+"IN__2");
            addtoDatabase(ctx,formated_number);
            ContactProvider.sendnotification(ctx);
        }
    }
    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        //incoming call ended
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean b=SP.getBoolean("STATE",true);
        if(b){
            stopRecording();
        }
        NotificationManager notificationManager=(NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        ContactProvider.sendnotificationOnEnd(ctx);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        //outgoing call ended
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean b=SP.getBoolean("STATE",true);
        if(b){
            stopRecording();
        }
        NotificationManager notificationManager=(NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        ContactProvider.sendnotificationOnEnd(ctx);
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        //miss call
    }

    public void addtoDatabase(Context ctx,String number){
        DatabaseHelper db=new DatabaseHelper(ctx);
        if(db.isContact(number).getNumber()!=null){

        }else{
            Contacts contacts=new Contacts();
            contacts.setFav(0);
            contacts.setState(0);
            contacts.setNumber(number);
            db.addContact(contacts);
        }
    }
}

