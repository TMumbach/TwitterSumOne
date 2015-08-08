package br.com.sumone.sumonetwitter.home;

import android.content.res.Configuration;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import br.com.sumone.sumonetwitter.R;

/**
 * Created by Tiago on 06/08/2015.
 *
 * Clase usada para controlar o menu lateral da aplicação
 *
 * @see NavigationView
 * @see DrawerLayout
 */
public class NavigationDrawer implements NavigationView.OnNavigationItemSelectedListener{

    private static final long DRAWER_CLOSE_DELAY_MS = 350;

    private final Handler mDrawerActionHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private int mNavItemId;

    private NavigationView navigationView;

    private OnNavigateItemSelected mOnNavigateItemSelected;

    public NavigationDrawer(HomeActivity homeActivity, int mNavItemId) {

        this.mNavItemId = mNavItemId;

        mDrawerLayout = (DrawerLayout) homeActivity.findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) homeActivity.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(mNavItemId).setChecked(true);

        mDrawerToggle = new ActionBarDrawerToggle(homeActivity, mDrawerLayout, homeActivity.getToolbarView(), R.string.open, R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


    }

    public boolean isDrawerpen() {
        return mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item);
    }

    public void setConfiguration(Configuration newConfig) {
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public int getNavItemId() {
        return mNavItemId;
    }

    public void setOnNavigateItemSelected(OnNavigateItemSelected onNavigateItemSelected) {
        this.mOnNavigateItemSelected = onNavigateItemSelected;
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        menuItem.setChecked(true);
        mNavItemId = menuItem.getItemId();

        mDrawerLayout.closeDrawer(GravityCompat.START);

        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mOnNavigateItemSelected.itemSelected(menuItem);
            }
        }, DRAWER_CLOSE_DELAY_MS);

        return true;
    }

    private void navigate(final int itemId) {
    }

    public interface OnNavigateItemSelected {
        void itemSelected(MenuItem item);
    }
}
