package com.assignment1mg.rishabmangla.messagefor1mg.Data;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.assignment1mg.rishabmangla.messagefor1mg.Data.Message.MESSAGE_TYPE;

/**
 * This class represents the conversation with particular person.
 */
public class ConversationThread implements Parcelable {

	private String number;
	private String displayName;
    private String read;
//	private String body;
//	private long time;
//	String date;
//	private static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
	
    private ArrayList<Message> messages = new ArrayList<Message>();

    private int count = 0;
    private int receivedCount = 0;
    private int sentCount = 0;
    
    public ConversationThread(String number, String body, long time, MESSAGE_TYPE type, String read) {
    	this.number = number;
    	messages.add(new Message(body, time, type));
    	this.read = read;
//    	this.time = time;
//    	this.body = body;
//    	this.date = format.format(time);
    }
    
    public ConversationThread(Parcel src) {
    	this.number = src.readString();
    	src.readList(messages, getClass().getClassLoader());
	}

	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}

	
	public void addMessage(String message, long time, MESSAGE_TYPE type){
        messages.add(new Message(message, time, type));
        count++;
        if(type == MESSAGE_TYPE.INBOX) {
        	receivedCount++;
        } else {
        	sentCount++;
        }
	}

	public void newMessage(String message, long time, MESSAGE_TYPE type){
		this.messages.add(0, new Message(message, time, type));
	}
	
    public ArrayList<Message> getMessages(){
        return messages;
    }

    public String getBody(){
    	if(messages != null)
    		return messages.get(0).text;
    	else
    		return null;
    }

	public long getTime() {
    	if(messages != null)
    		return messages.get(0).time;
    	else
    		return 0;
	}

//	public void setTime(long time) {
//    	if(messages != null)
//    		messages.get(0).time;
//    	else
//    		return 0;
//	}

	public int getCount() {
		return count;
	}

	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Boolean getRead() {
		if(read.equals("1"))
			return true;
		return false;
	}

	public void setRead() {
		this.read = "1";
	}

	public int getReceivedCount() {
		return receivedCount;
	}

	public int getSentCount() {
		return sentCount;
	}

    public boolean isNumberSaved(){
        if(displayName.equals(""))
            return false;
        return true;
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(number);
		dest.writeList(messages);
//		dest.writeStringList(messages);
	}
	
	public static final Parcelable.Creator<ConversationThread> CREATOR = new Creator<ConversationThread>() {
		
		@Override
		public ConversationThread[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ConversationThread[size];
		}
		
		@Override
		public ConversationThread createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new ConversationThread(source);
		}
	};
}
