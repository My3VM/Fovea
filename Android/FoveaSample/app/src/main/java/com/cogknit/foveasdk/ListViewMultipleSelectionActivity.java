package com.cogknit.foveasdk;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cogknit.fovea.Fovea;
import com.cogknit.fovea.providers.FoveaAppInvitee;

import java.util.ArrayList;
import java.util.List;
/**
 * Class for sending out the App invitations to the Fovea recommended contacts of the user
 */
public class ListViewMultipleSelectionActivity extends AppCompatActivity implements
        View.OnClickListener {
    Button button, cancelButton;
    EditText msgBox;
    ListView listView;
    FoveaInviteAdapter adapter;
    ArrayList<FoveaAppInvitee> invitees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_multiple_selection);
        findViewsById();

        //Get the recommended invitees from Fovea
        invitees = new ArrayList<>(getAppInvitees());
        if (invitees.isEmpty())
        {
            Log.v("Sample", getResources().getString(R.string.noRecommendationsMsg));
            finish();
        }

        adapter = new FoveaInviteAdapter(this,
                android.R.layout.simple_list_item_multiple_choice, invitees);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);

        button.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    /**
     * Initializes the control resources
     */
    private void findViewsById() {
        listView = (ListView) findViewById(R.id.list);
        button =  (Button) findViewById(R.id.sendbutton);
        cancelButton = (Button) findViewById(R.id.cancelbutton);
        msgBox = (EditText)findViewById(R.id.invitemsgbox);
    }

    /**
     * Gets the recommended target invitees by Fovea
     */
    public List<FoveaAppInvitee> getAppInvitees(){
        //1. Get the App invitees
        List<FoveaAppInvitee> invites = null;
        try
        {
            invites = Fovea.getAppInvitees(this);
            Log.v("Sample", "Recommended invitees" + invites);
        }
        catch (Fovea.UninitializedException exception) {
            Log.v("Sample", exception.getLocalizedMessage());
        }
        return invites;
    }

    public void onClick(View v) {
        switch(v.getId())  //get the id of the view clicked. (in this case button)
        {
            case R.id.sendbutton :
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                ArrayList<FoveaAppInvitee> approvedList = new ArrayList<FoveaAppInvitee>();
                ArrayList<FoveaAppInvitee> declinedList = new ArrayList<FoveaAppInvitee>(invitees);
                //get the approved & declined contacts from the list
                for (int i = 0; i < checked.size(); i++) {
                    int position = checked.keyAt(i);
                    if (checked.valueAt(i))
                    {
                        approvedList.add(adapter.getItem(position));
                        declinedList.remove(adapter.getItem(position));
                    }
                }
                //proceed with sending out the invitations with the user provided by message
                try {
                    Fovea.proceedWithAppInvites(this, approvedList, declinedList, msgBox.getText().toString());
                }
                catch (Fovea.UninitializedException exception) {
                    Log.v("Sample", exception.getLocalizedMessage());
                }
                catch (Fovea.AppInviteException exception) {
                    Log.v("Sample", exception.getLocalizedMessage());
                }
                finish();
                break;
            case R.id.cancelbutton :
                //do something
                finish();
                break;
        }
    }
}

/**
 * Class for the Custom FoveaInvite List adapter
 */
class FoveaInviteAdapter extends ArrayAdapter<FoveaAppInvitee> {

    public FoveaInviteAdapter(Context context, int textViewResourceId, ArrayList<FoveaAppInvitee> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        FoveaAppInvitee item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
        }

        TextView inviteeName = (TextView) convertView.findViewById(android.R.id.text1);
        inviteeName.setText(item.getInviteeName());

        return convertView;
    }
}

