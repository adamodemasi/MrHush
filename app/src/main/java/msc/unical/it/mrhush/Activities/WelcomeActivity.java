package msc.unical.it.mrhush.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import msc.unical.it.mrhush.Handlers.PrefManager;
import msc.unical.it.mrhush.R;

import static android.provider.Settings.*;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button buttonNext;
    private PrefManager prefManager;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        buttonNext = (Button) findViewById(R.id.btn_next);

        // layouts of all welcome sliders
        layouts = new int[]{R.layout.welcome_slide1, R.layout.welcome_slide2,
                R.layout.welcome_slide3, R.layout.welcome_slide4};

        addBottomDots(0);

        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int current = getItem(+1);

                if (current == 2)
                    grantPermissions();
                if (current == 3 && !flag)
                    dndRequest();
                if (current == layouts.length - 1)
                    addRuntimePermission();

                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }

            }
        });
    }

    private void dndRequest() {
        flag = true;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;")); //html code for the dots
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == 2)
                grantPermissions();
            if (position == 3 && !flag)
                dndRequest();
            if (position == layouts.length - 1)
                addRuntimePermission();

            // changing the next button text
            if (position == layouts.length - 1) {
                // last page
                buttonNext.setText(getString(R.string.start));

            } else {
                // still pages are left
                buttonNext.setText(getString(R.string.next));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) { }

        @Override
        public void onPageScrollStateChanged(int arg0) { }
    };

    /* Making notification bar transparent */
    private void changeStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() { }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void grantPermissions() {
        if (!(ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION))) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void addRuntimePermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                + (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                != PackageManager.PERMISSION_GRANTED) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION))) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "Please, grant permissions!",
                        Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.ACCESS_COARSE_LOCATION},
                                        1);
                            }
                        }).show();
            } else {

                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

}