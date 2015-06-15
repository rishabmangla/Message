package com.assignment1mg.rishabmangla.messagefor1mg.Loader;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.assignment1mg.rishabmangla.messagefor1mg.Data.ConversationThread;
import com.assignment1mg.rishabmangla.messagefor1mg.Data.Message;
import com.assignment1mg.rishabmangla.messagefor1mg.Data.Message.MESSAGE_TYPE;
import com.assignment1mg.rishabmangla.messagefor1mg.MessageListFragment;

//Backround thread to load the rest of the messages

public class SmsLoaderTask extends AsyncTask<Integer, Void, Void>{

	private Activity mContext;
	private Hashtable<String, String> mContactList;
	private Hashtable<String, ConversationThread> conversationsList;
	private MessageListFragment fragment;
	private ArrayList<ConversationThread> msgList = new ArrayList<ConversationThread>();
	
	public SmsLoaderTask(Activity context, MessageListFragment fragment, Hashtable<String, ConversationThread> conversationList) {
		mContext = context;
		this.fragment = fragment;
		mContactList = fragment.contactList;
		this.conversationsList = (Hashtable<String, ConversationThread>) conversationList.clone();
//		conversationsList = new Hashtable<String, ConversationThread>();
//		if(list != null)
//			msgList = (ArrayList<ConversationThread>) list.clone();
	}
	
	@Override
	protected Void doInBackground(Integer... params) {
		long time = System.currentTimeMillis();
		int startIndex = params[0];
		
		String[] projection = {"address", "body", "date", "read", "type"};
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor msgCursor = contentResolver.query(Uri.parse("content://sms/"), projection, null, null, null);
        int addressIndex = msgCursor.getColumnIndex("address");
        int bodyIndex = msgCursor.getColumnIndex("body");
        int timeIndex = msgCursor.getColumnIndex("date");
        int readIndex = msgCursor.getColumnIndex("read");
        int folderIndex = msgCursor.getColumnIndex("type");
        String address;

        if (!msgCursor.moveToPosition(startIndex)) {
            return null;
        }
        int index = 0;
        do {
        	address = msgCursor.getString(addressIndex);
        	if(address == null) { //Ignoring draft messages
        		Log.d("rishab", "" + msgCursor.getString(bodyIndex));
        		continue;
        	}
        	address = getTrimmedAddress(address);
        	if(!conversationsList.containsKey(address)) {
        		MESSAGE_TYPE type;
        		if(msgCursor.getString(folderIndex).contains("1")) {
        			type = Message.MESSAGE_TYPE.INBOX;
        		} else {
        			type = Message.MESSAGE_TYPE.SENT;
        		}
        		ConversationThread conversation = new ConversationThread(address, msgCursor.getString(bodyIndex), 
        				msgCursor.getLong(timeIndex), type, msgCursor.getString(readIndex));
        		conversationsList.put(address, conversation);
        		msgList.add(conversation);
        		
        		if(conversation.getDisplayName() == null) {
        			conversation.setDisplayName(getContactName(address));
        		}

//        		conversation.addMessage(msgCursor.getString(bodyIndex), msgCursor.getLong(timeIndex), type);
        	} else {
        		ConversationThread conversation = conversationsList.get(address);
        		MESSAGE_TYPE type;
        		if(msgCursor.getString(folderIndex).contains("1")) {
        			type = Message.MESSAGE_TYPE.INBOX;
        		} else {
        			type = Message.MESSAGE_TYPE.SENT;
        		}
        		conversation.addMessage(msgCursor.getString(bodyIndex), msgCursor.getLong(timeIndex), type);
        	}
        	index++;
        } while (msgCursor.moveToNext());
        msgCursor.close();
        Log.d("rishab", "index: " + index);
        Log.d("rishab", "thread time: " + (System.currentTimeMillis()-time) + "ms, " + conversationsList.size() + " conversations");
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		fragment.conversationsList = conversationsList;
		fragment.updateList(msgList);
		conversationsList = null;
		msgList = null;
	}
	
	public String getContactName(String number) {
		if(mContactList.containsKey(number)) {
			return mContactList.get(number);
		} else {
			return "";
		}
	}
	
	private String getTrimmedAddress(String address) {
		StringBuilder result = new StringBuilder(address);
		int i=0;
		while(i<result.length()) {
			if(result.charAt(i) == 32) {
				result.deleteCharAt(i);
			} else {
				i++;
			}
		}
		
		return result.toString();
	}

}
