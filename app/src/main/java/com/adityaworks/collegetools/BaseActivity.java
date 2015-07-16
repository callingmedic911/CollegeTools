package com.adityaworks.collegetools;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public abstract class BaseActivity extends AppCompatActivity {

    public static Context appContext;
    protected Toolbar mActionBarToolbar;
    private DrawerLayout mDrawerLayout;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();

        setContentView(getLayoutResourceId());

        setUpToolbar();
        setupNavDrawer();

    }

    protected abstract int getLayoutResourceId();

    private void setUpToolbar() {

        ActionBarDrawerToggle actionBarDrawerToggle;

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        setSupportActionBar(mActionBarToolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mActionBarToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        actionBarDrawerToggle.syncState();

    }

    private void setupNavDrawer() {
        String[] mNavigationDrawerItems = getResources().getStringArray(R.array.navigation_drawer_items);
        mListView = (ListView) findViewById(R.id.left_drawer);

        mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mNavigationDrawerItems));
        mListView.setOnItemClickListener(new DrawerItemClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
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

    private void selectItem(int position) {
        Intent intent;
        mDrawerLayout.closeDrawer(mListView);
        switch (position) {
            default:
                break;
            case 1:
                intent = new Intent(this, BunkCalculator.class);
                startActivityWithDelay(intent);
                break;
            case 2:
                intent = new Intent(this, DayGrid.class);
                startActivityWithDelay(intent);
                break;
        }
        overridePendingTransition(0, 0);

        // update selected item and title, then close the drawer
        mListView.setItemChecked(position, true);
    }

    private void startActivityWithDelay(final Intent intent) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        }, 200);
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
