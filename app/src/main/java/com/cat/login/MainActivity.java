package com.cat.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import com.cat.login.Fragment.FragmentForget;
import com.cat.login.Fragment.FragmentLogin;
import com.cat.login.Fragment.FragmentSign;
import com.cat.login.Utils.FixedSpeedScroller;
import com.cat.login.Utils.NoScrollViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private NoScrollViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        List<Fragment> list = new ArrayList<>();
        list.add(new FragmentForget());
        list.add(new FragmentLogin());
        list.add(new FragmentSign());
        viewPager = findViewById(R.id.login_vp);
        MyAdapter adapter = new MyAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setScrollable(false);
        viewPager.setCurrentItem(1);
        controlViewPagerSpeed(this,viewPager);
    }

    @Override //双击退出
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            goTOMainActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void successLogin(){
        Toasty.success(this, "登录成功!", Toast.LENGTH_SHORT, true).show();
        goTOMainActivity();
    }

    public void goTOMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("page", 2);
        startActivity(intent);
        finish();
    }

    public void setViewPager(int page){
        viewPager.setCurrentItem(page);
    }

    private void controlViewPagerSpeed(Context context, ViewPager viewpager) {
        try {
            Field mField;
            mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            FixedSpeedScroller mScroller =
                    new FixedSpeedScroller(context, new AccelerateInterpolator());
            mScroller.setmDuration(300);
            mField.set(viewpager, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        ArgbEvaluator evaluator = new ArgbEvaluator(); // ARGB求值器
        int evaluate = 0x00FFFFFF; // 初始默认颜色（透明白）
        if(position == 0){
            evaluate = (Integer) evaluator.evaluate(positionOffset, 0XFF6698cb, 0XFF50c7a2); // 根据positionOffset和第0页~第1页的颜色转换范围取颜色值
        }else if (position == 1) {
            evaluate = (Integer) evaluator.evaluate(positionOffset, 0XFF50c7a2, 0XFF7fccde); // 根据positionOffset和第0页~第1页的颜色转换范围取颜色值
        }else if(position == 2){
            evaluate = (Integer) evaluator.evaluate(positionOffset, 0XFF7fccde, 0XFF50c7a2); // 根据positionOffset和第1页~第2页的颜色转换范围取颜色值
        }
        ((View)viewPager.getParent()).setBackgroundColor(evaluate); // 为ViewPager的父容器设置背景色
    }

    @Override
    public void onPageSelected(int position) {}

    @Override
    public void onPageScrollStateChanged(int state) {}

    public static class MyAdapter extends FragmentPagerAdapter {
        private List<Fragment> mfragmentList;
        public MyAdapter(FragmentManager fm, List<Fragment>fragmentList) {
            super(fm); this.mfragmentList=fragmentList;
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mfragmentList.get(position);
        }
        @Override
        public int getCount() {
            return mfragmentList.size();
        }
    }
}