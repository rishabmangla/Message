package com.assignment1mg.rishabmangla.messagefor1mg.Data;

import android.os.Parcel;
import android.os.Parcelable;

//This is the class for particular message

public class Message implements Parcelable {

	public enum MESSAGE_TYPE {INBOX, SENT};
	public String text;
	public long time;
	public MESSAGE_TYPE type;
	
	public Message(String text, long time, MESSAGE_TYPE type) {
		this.text = text;
		this.time = time;
		this.type = type;
	}
	
	public Message (Parcel src){
		this.text = src.readString();
		this.time = src.readLong();
		this.type = (MESSAGE_TYPE) src.readSerializable();
	}
	
	public String getText(){
		return text;
	}
	
	public MESSAGE_TYPE getType(){
		return type;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(text);
		dest.writeLong(time);
		dest.writeSerializable(type);
	}
	
	public static final Parcelable.Creator<Message> CREATOR = new Creator<Message>() {
		
		@Override
		public Message[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Message[size];
		}
		
		@Override
		public Message createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Message(source);
		}
	};
}
