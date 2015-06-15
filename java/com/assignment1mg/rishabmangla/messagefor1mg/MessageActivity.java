package com.assignment1mg.rishabmangla.messagefor1mg;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.assignment1mg.rishabmangla.messagefor1mg.Data.ConversationThread;


public class MessageActivity extends FragmentActivity implements MessageListFragment.OnMessageInteractionListener {

	private static final String TAG = "MessageActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		Log.i("rishab", "onCreate activity");
		//        MessageListFragment mMessageListFragment = (MessageListFragment)
		//                getSupportFragmentManager().findFragmentById(R.id.message_list);

	}

	@Override
	public void onMessageSelected(ConversationThread smsThread) {
		Intent intent = new Intent(this, MessageThread.class);
		intent.putExtra("sms", smsThread);
		startActivity(intent);

	}
}
