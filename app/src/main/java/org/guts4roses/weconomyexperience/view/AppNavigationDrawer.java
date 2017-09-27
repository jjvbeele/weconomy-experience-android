package org.guts4roses.weconomyexperience.view;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.guts4roses.weconomyexperience.BuildConfig;
import org.guts4roses.weconomyexperience.R;
import org.guts4roses.weconomyexperience.view.util.NavigationDrawer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mint on 26-8-17.
 */

public class AppNavigationDrawer extends NavigationDrawer {

    @BindView(R.id.version)
    protected TextView appVersionInfoTextView;

    private NavigationInterface navigationInterface;

    public AppNavigationDrawer(AppCompatActivity activity, NavigationInterface navigationInterface, DrawerLayout drawerLayout) {
        super(activity, drawerLayout);

        this.navigationInterface = navigationInterface;

        ButterKnife.bind(this, drawerLayout);

        appVersionInfoTextView.setText(activity.getString(R.string.app_version_info, BuildConfig.VERSION_NAME));
    }

    @OnClick(R.id.info_text)
    protected void onClickInfoText() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(navigationInterface.getResources().getString(R.string.explanation_link)));
        navigationInterface.startActivity(i);
    }

    @OnClick(R.id.guts4roses_link)
    protected void onClickGuts4rosesLink() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(navigationInterface.getResources().getString(R.string.guts4roses_link)));
        navigationInterface.startActivity(i);
    }

    @OnClick(R.id.teachwithapps_link)
    protected void onClickTeachwithappsLink() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(navigationInterface.getResources().getString(R.string.teachwithapps_link)));
        navigationInterface.startActivity(i);
    }

    @OnClick(R.id.icons_link)
    protected void onClickIconsLink() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(navigationInterface.getResources().getString(R.string.icons_link)));
        navigationInterface.startActivity(i);
    }

    @OnClick(R.id.sign_out_link)
    protected void onClickSignOut() {
        navigationInterface.signOut();
    }

    @OnClick(R.id.enable_admin)
    protected void onClickEnableAdmin() {
        navigationInterface.toggleAdminMode();
    }

    public interface NavigationInterface {
        void toggleAdminMode();

        void startActivity(Intent intent);

        void signOut();

        Resources getResources();
    }
}
