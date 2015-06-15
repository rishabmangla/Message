package com.assignment1mg.rishabmangla.messagefor1mg;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.assignment1mg.rishabmangla.messagefor1mg.Data.ConversationThread;
import com.assignment1mg.rishabmangla.messagefor1mg.Data.Message;
import com.assignment1mg.rishabmangla.messagefor1mg.Data.Message.MESSAGE_TYPE;
import com.assignment1mg.rishabmangla.messagefor1mg.dummy.DummyContent;

/**
 * Fragment represting the conversation with particular person.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class MessageThreadFragment extends ListFragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ConversationThread smsThread;
    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
    private EditText mRecipient;
    private Button mSendMessage;
    private EditText mMessage;
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static MessageThreadFragment newInstance(String compose, ConversationThread smsThread) {
        MessageThreadFragment fragment = new MessageThreadFragment();
        Bundle args = new Bundle();
        if(smsThread != null)
            args.putParcelable(ARG_PARAM1,smsThread);
//        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, compose);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessageThreadFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            smsThread = getArguments().getParcelable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
        if(smsThread != null){
//        	mAdapter = new ArrayAdapter<String>(getActivity(),
//        			android.R.layout.simple_list_item_1, android.R.id.text1, smsThread.getMessages());
            Log.i("rishab","num " + smsThread.getNumber() + "msg count " + smsThread.getMessages().size());
            mAdapter = new ListAdapter(getActivity(), smsThread.getMessages());
            setListAdapter(mAdapter);
        }
    }

    public class ListAdapter extends ArrayAdapter<Message> {
        // List context
        private final Context context;
        private ArrayList<Message> messages = new ArrayList<Message>();

        @Override
        public int getCount() {
            return messages.size();
//    		return super.getCount();
        }
        public ListAdapter(Context context, ArrayList<Message> messages) {
            super(context, android.R.layout.simple_list_item_1);
            this.context = context;
            this.messages = messages;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
//			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//			convertView = inflater.inflate(android.R.id.text1, parent, false);
            TextView sms = new TextView(context);
            sms.setText(messages.get(position).getText());
            sms.setPadding(15,15,15,15);
            sms.setTextSize(15);
            if(messages.get(position).getType() == MESSAGE_TYPE.INBOX)
                sms.setGravity(Gravity.LEFT);
            else
                sms.setGravity(Gravity.RIGHT);
//            Log.i("rishab","text " + messages.get(position).getText());
            return sms;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messagethread_list, container, false);
        mRecipient = (EditText) view.findViewById(R.id.recipient);
        mSendMessage = (Button) view.findViewById(R.id.sendMessage);
        mMessage = (EditText) view.findViewById(R.id.message);
        if(smsThread == null)
            mRecipient.setVisibility(View.VISIBLE);
        else
            getActivity().setTitle(smsThread.getNumber());
        mSendMessage.setOnClickListener(new View.OnClickListener()   {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
//        // Set the  Set OnItemClickListener so we can be notified on item cladapter
//        mListView = (AbsListView) view.findViewById(android.R.id.list);
//        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
//
//        //icks
//        mListView.setOnItemClickListener(this);

        return view;
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    protected void sendMessage() {
        String toPhoneNumber;
        if(smsThread == null)
            toPhoneNumber = mRecipient.getText().toString();
        else
            toPhoneNumber = smsThread.getNumber();
        String smsMessage = mMessage.getText().toString();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(toPhoneNumber, null, smsMessage, null, null);
            Toast.makeText(getActivity(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    "Sending SMS failed.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
