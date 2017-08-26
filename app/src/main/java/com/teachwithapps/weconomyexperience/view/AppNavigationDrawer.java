package com.teachwithapps.weconomyexperience.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.teachwithapps.weconomyexperience.BuildConfig;
import com.teachwithapps.weconomyexperience.R;
import com.teachwithapps.weconomyexperience.view.util.NavigationDrawer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mint on 26-8-17.
 */

public class AppNavigationDrawer extends NavigationDrawer {

    @BindView(R.id.version)
    protected TextView appVersionInfoTextView;

    private Activity activity;

    public AppNavigationDrawer(AppCompatActivity activity, DrawerLayout drawerLayout) {
        super(activity, drawerLayout);
        this.activity = activity;

        ButterKnife.bind(this, drawerLayout);

        appVersionInfoTextView.setText(activity.getString(R.string.app_version_info, BuildConfig.VERSION_NAME));
    }

    @OnClick(R.id.guts4roses_link)
    protected void guts4rosesLink() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(activity.getString(R.string.guts4roses_link)));
        activity.startActivity(i);
    }

    @OnClick(R.id.teachwithapps_link)
    protected void teachwithappsLink() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(activity.getString(R.string.teachwithapps_link)));
        activity.startActivity(i);
    }
}
