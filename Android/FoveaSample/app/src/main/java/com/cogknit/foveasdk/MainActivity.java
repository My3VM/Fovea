package com.cogknit.foveasdk;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.cogknit.fovea.Fovea;
import com.cogknit.fovea.FoveaCallback;
import com.cogknit.fovea.FoveaConstants;
import com.cogknit.fovea.FoveaUserProfile;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListViewMultipleSelectionActivity.class);
                startActivity(intent);
            }
        });

        //Create Array of FoveaConstants.Services to specify the services to be activated
        FoveaConstants.Services miningContexts[] = {
                FoveaConstants.Services.APP_VIRALITY,
                FoveaConstants.Services.APP_USAGE_DASHBOARD,
                FoveaConstants.Services.APP_USERLOCATION_DASHBOARD,
                FoveaConstants.Services.DISCOVERY,
                FoveaConstants.Services.FB
        };
        //1. Call Fovea.initialize() with context, API_KEY, an array of FoveaConstants.Services and a handler callback
        Fovea.initialize(getApplicationContext(), "f0258b66-8568-3c11-bbad-94d91b8fa02a1445939104995", "90fb5ae6-6d33-3777-8736-a6c4cb9185961445939282435", miningContexts, new FoveaCallback() {
            @Override
            public void onCompletion() {
                //Authorization successful. Set user profile and provide Facebook token if needed.
                Log.v("Sample", "Successfully authorized");

                //Build user profile
                FoveaUserProfile profile1 = new FoveaUserProfile("123");
                profile1.setUserName("John");
                profile1.setEmailID("John@gmail.com");
                profile1.setDateOfBirth("13/05/2000");
                profile1.setPhoneNumber("324-443-4444");

                //2. Optional though important - Call Fovea.setCustomerProfileDetails() with the User object and a handler callback
                Fovea.setCustomerProfileDetails(profile1, new FoveaCallback() {
                    @Override
                    public void onCompletion() {
                        //Customer profile set successfully
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        //Error setting user profile
                        Log.v("Sample", exception.getLocalizedMessage());
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                //Error auth
                Log.v("Sample", exception.getLocalizedMessage());
            }
        });


        //3. SetFacebook Access token, re-initialize upon every FB login refresh
        try
        {
            Fovea.setFacebookAccessToken(this, null);
        }
        catch (Fovea.UninitializedException exception)
        {
            Log.v("Sample", exception.getLocalizedMessage());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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
