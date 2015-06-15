package com.assignment1mg.rishabmangla.messagefor1mg;

import java.util.ArrayList;
import java.util.Hashtable;

import android.R.anim;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.assignment1mg.rishabmangla.messagefor1mg.Data.ConversationThread;
import com.assignment1mg.rishabmangla.messagefor1mg.Data.Message;
import com.assignment1mg.rishabmangla.messagefor1mg.Data.Message.MESSAGE_TYPE;
import com.assignment1mg.rishabmangla.messagefor1mg.Loader.SmsLoaderTask;

/**
 * A fragment showing the list of all the first message categorized as per unique users.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link com.assignment1mg.rishabmangla.messagefor1mg.MessageListFragment.OnMessageInteractionListener}
 * interface.
 */
public class MessageListFragment extends ListFragment {

    private static final String TAG = "MessageListFragment";

    private int INITIAL_MESSAGES_LOAD_COUNT = 20;

    public Hashtable<String, ConversationThread> conversationsList = new Hashtable<String, ConversationThread>();
    public Hashtable<String, String> contactList = new Hashtable<String, String>();
    
    private ListAdapter mAdapter;
    private OnMessageInteractionListener mListener;

    SwipeDetector swipeDetector;

    // TODO: Rename and change types of parameters
    public static MessageListFragment newInstance(String param1, String param2) {
        MessageListFragment fragment = new MessageListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessageListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        Log.i(TAG, "onCreate fragment");

        mAdapter = new ListAdapter(getActivity());

        setListAdapter(mAdapter);
        createMessageList();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the list fragment layout
        return inflater.inflate(R.layout.message_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeDetector = new SwipeDetector();
        getListView().setOnTouchListener(swipeDetector);
//            convertView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    if(swipeDetector.swipeDetected()
//                            && swipeDetector.getAction() == SwipeDetector.Action.RL) {
//                        Log.i("rishab","swipe leftttttttty");
//                    }
//                }
//            });
    }

    // this message is called at the time of activity creation i.e. when user launcher the application
    // so the loading time should be minimum.
    // Thus at this time only few messages are loaded
    // since the user can see maximum ~20 messages at one time
    // Rest of the loading takes place in background thread.
    public void createMessageList() {
    	long time = System.currentTimeMillis();
    	
    	String[] projection = {"address", "body", "date", "read", "type"};
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor msgCursor = contentResolver.query(Uri.parse("content://sms/"), projection, null, null, null);
        int addressIndex = msgCursor.getColumnIndex("address");
        int bodyIndex = msgCursor.getColumnIndex("body");
        int timeIndex = msgCursor.getColumnIndex("date");
        int readIndex = msgCursor.getColumnIndex("read");
        int folderIndex = msgCursor.getColumnIndex("type");
        String address;

        if (!msgCursor.moveToFirst()) {
            return;
        }
        mAdapter.clear();
        populateContactList(getActivity());
        Log.d("rishab", msgCursor.getCount() +"");
        int index = 0;
        do {
        	address = msgCursor.getString(addressIndex);
        	if(address == null) { //Ignoring draft messages
        		Log.d("rishab", "" + msgCursor.getString(bodyIndex));
        		continue;
        	}
//        	address = getTrimmedAddress(address);
        	if(!conversationsList.containsKey(address)) {
        		MESSAGE_TYPE type;
        		if(msgCursor.getString(folderIndex).contains("1")) {
        			type = Message.MESSAGE_TYPE.INBOX;
        		} else {
        			type = Message.MESSAGE_TYPE.SENT;
        		}
        		ConversationThread conversation = new ConversationThread(address, msgCursor.getString(bodyIndex), 
        				msgCursor.getLong(timeIndex),type, msgCursor.getString(readIndex));
        		conversationsList.put(address, conversation);
        		mAdapter.add(conversation);
        		
        		if(conversation.getDisplayName() == null) {
        			conversation.setDisplayName(getContactName(address));
        		}

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
        	if(conversationsList.size() == INITIAL_MESSAGES_LOAD_COUNT) break;
        } while (msgCursor.moveToNext());
        msgCursor.close();
        Log.d("rishab", "index: " + index);
        Log.d("rishab", "time: " + (System.currentTimeMillis()-time) + "ms, " + conversationsList.size() + " conversations");
        SmsLoaderTask task = new SmsLoaderTask(getActivity(), this, conversationsList);
        task.execute(msgCursor.getPosition());
    }
    
    private void populateContactList(Context context) {
    	String number;
    	String name;
    	String[] mPhoneNumberProjection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
    	ContentResolver contentResolver = getActivity().getContentResolver();
    	Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, mPhoneNumberProjection, ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ?", new String[]{"1"}, null);
    	
		try {
			if (!cursor.moveToFirst()) {
				return;
			}
			do {
				number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//				number = getTrimmedAddress(number);
				name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				contactList.put(number, name);
			} while(cursor.moveToNext());
		} finally {
			if (cursor != null)
				cursor.close();
		}
    }
	public String getContactName(String number) {
		if(contactList.containsKey(number)) {
			return contactList.get(number);
		} else {
			return "";
		}
	}
	
//	private String getTrimmedAddress(String address) {
//		StringBuilder result = new StringBuilder(address);
//		int i=0;
//		while(i<result.length()) {
//			if(result.charAt(i) == 32) {
//				result.deleteCharAt(i);
//			} else {
//				i++;
//			}
//		}
//		return result.toString();
////		StringBuilder result = new StringBuilder();
////		StringBuilder sb = new StringBuilder(address);
////		int length = sb.length();
////		for(int i=0; i<length; i++) {
////			if(sb.charAt(i) != 32) {
////				result.append(sb.charAt(i));
////			}
////		}
////		
////		return result.toString();
//	}
	
//	public void addItem(ConversationThread conv) {
//		mAdapter.add(conv);
//	}
	
	public void updateList(ArrayList<ConversationThread> list) {
		mAdapter.addAll(list);
	}
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMessageInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);

    	if (null != mListener) {
    		final ConversationThread t = (ConversationThread) l.getAdapter().getItem(position);

    		if(swipeDetector.swipeDetected()){
    			if(t.isNumberSaved())
    				markMessageAsRead(getActivity(), t);
    			else{
    				int dest = 0;
    				if (swipeDetector.getAction() == SwipeDetector.Action.RL )
    					dest = -v.getWidth();
    				if(swipeDetector.getAction() == SwipeDetector.Action.LR)
    					dest = v.getWidth();
    				Log.i("rishab", "swipeDetected");
    				TranslateAnimation swipe = new TranslateAnimation(0, dest, 0 ,0);
    				swipe.setDuration(400);
    				v.startAnimation(swipe);
    				swipe.setAnimationListener(new Animation.AnimationListener() {

    					@Override
    					public void onAnimationStart(Animation animation) {

    					}

    					@Override
    					public void onAnimationRepeat(Animation animation) {

    					}

    					@Override
    					public void onAnimationEnd(Animation animation) {
    						deleteMessage(getActivity(), t);
    					}
    				});
    			}

    		}else{
    			mListener.onMessageSelected(t);
    			markMessageAsRead(getActivity(), t);
    		}
    	}
    }

    private void deleteMessage(Context context, ConversationThread t){
        Log.i("rishab","deleteMessage");
        conversationsList.remove(t.getNumber());
        mAdapter.remove(t);

        String from = t.getNumber();

        Uri uri = Uri.parse("content://sms/inbox");
        String selection = "address=? ";
        String[] selectionArgs = {from};

//        ContentValues values = new ContentValues();
//        values.put("read", "1");

        int val = context.getContentResolver().delete(uri, selection, selectionArgs);
        Log.i("rishab","Val delete " + val + " add " + from);
    }
    private void markMessageAsRead(Context context, ConversationThread t) {
        Log.i("rishab","markMessageAsRead");
        t.setRead();
        mAdapter.notifyDataSetChanged();

        String from = t.getNumber();

    	Uri uri = Uri.parse("content://sms/inbox");
    	String selection = "address = ? AND read = ?";
    	String[] selectionArgs = {from, "0"};

    	ContentValues values = new ContentValues();
    	values.put("read", "1");

    	int val = context.getContentResolver().update(uri, values, selection, selectionArgs);
    	Log.d("rishab", "val:" + val);
    }
    public class ListAdapter extends ArrayAdapter<ConversationThread> {

        // List context
        private final Context context;
        // List values
//        private ArrayList<ConversationThread> conversationList;

        public ListAdapter(Context context) {
            super(context, R.layout.message_list_item);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	ViewHolder viewHolder;
        	ConversationThread conversation;
			if (convertView == null) {
				LayoutInflater inflater = ((Activity) context).getLayoutInflater();
				convertView = inflater.inflate(R.layout.message_list_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.senderInfo = (TextView) convertView.findViewById(R.id.phoneNum);
				viewHolder.body = (TextView) convertView.findViewById(R.id.body);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
//			conversation = conversationList.get(position);
			conversation = getItem(position);
            String displayName = conversation.getDisplayName().equals("") ? conversation.getNumber() : conversation.getDisplayName();
            if(conversation.getDisplayName().equals(""))
                convertView.setBackgroundColor(Color.LTGRAY);
            else
                convertView.setBackgroundColor(Color.WHITE);
            viewHolder.senderInfo.setText(displayName/* + "  " + conversation.getCount() + ", sent:" + conversation.getSentCount() + ", received:" + conversation.getReceivedCount()*/);
            viewHolder.body.setText(conversation.getBody());
            if(conversation.getRead()) {
            	viewHolder.body.setTypeface(Typeface.DEFAULT);
            } else {
            	viewHolder.body.setTypeface(Typeface.DEFAULT_BOLD);
            }

            return convertView;
        }
        
    }

    private static class ViewHolder {
    	TextView senderInfo;
    	TextView body;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        Log.i("rishab","onCreateOptionsMenu");
        // Inflate the menu items
        inflater.inflate(R.menu.menu_message, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("rishab", "onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_compose_message) {
            Intent intent = new Intent(getActivity(), MessageThread.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMessageInteractionListener {
        // TODO: Update argument type and name
        public void onMessageSelected(ConversationThread smsThread);
    }

}
