package com.assignment1mg.rishabmangla.messagefor1mg;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.assignment1mg.rishabmangla.messagefor1mg.Data.ConversationThread;


public class MessageThread extends FragmentActivity {

    private static final String TAG = "MessageThread";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent() != null){
            ConversationThread smsThread = getIntent().getParcelableExtra("sms");

            // Checks to see if fragment has already been added, otherwise adds a new
            // ContactDetailFragment with the Uri provided in the intent
            if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                // Adds a newly created MessageThreadFragment that is instantiated with the
                // data ConversationThread
                ft.add(android.R.id.content, MessageThreadFragment.newInstance("asd", smsThread), "asdfgb");
                ft.commit();
            }
        }else{
            finish();
        }
    }


    //    @Override
    //    public boolean onCreateOptionsMenu(Menu menu) {
    //        // Inflate the menu; this adds items to the action bar if it is present.
    //        getMenuInflater().inflate(R.menu.menu_message_thread, menu);
    //        return true;
    //    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
