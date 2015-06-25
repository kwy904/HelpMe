package com.ywk.helpme.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ywk.helpme.R;


public class ACT_Main extends ActionBarActivity {

    private Context mContext;

    private Toolbar mToolbar;

    private ShareActionProvider mShareActionProvider;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
    }

    private void initView() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mToolbar.setTitle("首页");
        setSupportActionBar(mToolbar);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_settings:
                        Toast.makeText(mContext, "设置", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.action_share:
                        Toast.makeText(mContext, "分享", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                mToolbar.setTitle("首页");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mToolbar.setTitle("救急");
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);


    }

    public void onClick_Find(View v) {
        ACT_FindToilet.Lunch(this);
    }


    public void onClick_Home(View view) {
//        Intent intent = new Intent(this,ACT_Main.class);
//        startActivity(intent);

        mDrawerLayout.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        /* ShareActionProvider配置 */
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu
                .findItem(R.id.action_share));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        mShareActionProvider.setShareIntent(intent);
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
