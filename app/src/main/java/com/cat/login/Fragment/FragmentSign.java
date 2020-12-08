package com.cat.login.Fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cat.login.MainActivity;
import com.cat.login.Utils.ToMD5;
import com.github.ybq.android.spinkit.SpinKitView;
import com.cat.login.R;
import com.cat.login.Utils.CompatUtils;
import com.cat.login.Utils.LogUtils;
import com.cat.login.Utils.SpecialChar;

import java.util.Objects;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSign#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSign extends Fragment {
    private ImageView back;
    private LinearLayout sign;
    private TextView send_sign;
    private boolean pw_ph_flag = true;
    private ImageView card_ph_pw;
    private EditText password;
    private EditText username;
    private EditText password_two;
    private TextView sign_text;
    private SpinKitView spinKitView;
    private EditText sign_email;

    public FragmentSign() {}

    public static FragmentSign newInstance(String param1, String param2) {
        return new FragmentSign();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView(){
        back = getActivity().findViewById(R.id.back_login);
        sign = getActivity().findViewById(R.id.sign);
        send_sign = getActivity().findViewById(R.id.send_sign);
        card_ph_pw = getActivity().findViewById(R.id.card_ph_pw_sign);
        password = getActivity().findViewById(R.id.password_sign);
        password_two = getActivity().findViewById(R.id.password_sign_two);
        username = getActivity().findViewById(R.id.username_sign);
        sign_text = getActivity().findViewById(R.id.sign_text);
        spinKitView = getActivity().findViewById(R.id.spin_kit_sign);
        sign_email = getActivity().findViewById(R.id.sign_email);

        back.setOnClickListener(v->{
            ((MainActivity)getActivity()).setViewPager(1);
        });
        sign.setOnClickListener(v->{
            showBottomLayout();
            sign_text.setVisibility(View.GONE);
            spinKitView.setVisibility(View.VISIBLE);
            new Thread(() -> {
                try {
                    //验证
                    String username_str = username.getText().toString();
                    String password_str = password.getText().toString();
                    String email = sign_email.getText().toString();
                    String password_str_two = password_two.getText().toString();
                    if ("".equals(username_str) || "".equals(password_str) || "".equals(password_str_two)) {
                        getActivity().runOnUiThread(() -> {
                            Toasty.warning(getActivity(), "账号密码不能为空", Toast.LENGTH_SHORT, true).show();
                            showBottomLayout_back();
                            spinKitView.setVisibility(View.GONE);
                        });
                        Thread.sleep(250);
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                            sign_text.setVisibility(View.VISIBLE);
                        });
                    } else if (SpecialChar.isSpecialChar(username_str) || SpecialChar.isSpecialChar(password_str) || SpecialChar.isSpecialChar(password_str_two)) {
                        getActivity().runOnUiThread(() -> {
                            Toasty.warning(getActivity(), "账号密码不能包含特殊字符[ _`~!@#$%^&*()+=|{}':;',[].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|", Toast.LENGTH_SHORT, true).show();
                            showBottomLayout_back();
                            spinKitView.setVisibility(View.GONE);
                        });
                        Thread.sleep(250);
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                            sign_text.setVisibility(View.VISIBLE);
                        });
                    } else if (username_str.length() < 2 || username_str.length() > 15) {
                        getActivity().runOnUiThread(() -> {
                            Toasty.warning(getActivity(), "用户名长度不能小于2，大于15", Toast.LENGTH_SHORT, true).show();
                            showBottomLayout_back();
                            spinKitView.setVisibility(View.GONE);
                        });
                        Thread.sleep(250);
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                            sign_text.setVisibility(View.VISIBLE);
                        });
                    } else if (password_str.length() < 6 || password_str.length() > 30) {
                        getActivity().runOnUiThread(() -> {
                            Toasty.warning(getActivity(), "密码长度不能小于6，大于30", Toast.LENGTH_SHORT, true).show();
                            showBottomLayout_back();
                            spinKitView.setVisibility(View.GONE);
                        });
                        Thread.sleep(250);
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                            sign_text.setVisibility(View.VISIBLE);
                        });
                    } else {
                        if(password_str.equals(password_str_two)){
                            sendRequestWithOkHttp(username_str, ToMD5.md5(password_str));
                        }else{
                            getActivity().runOnUiThread(() -> {
                                Toasty.error(getActivity(), "两次密码不相等", Toast.LENGTH_SHORT, true).show();
                                showBottomLayout_back();
                                spinKitView.setVisibility(View.GONE);
                            });
                            Thread.sleep(250);
                            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                                sign_text.setVisibility(View.VISIBLE);
                            });
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
        send_sign.setOnClickListener(v->{
            String email = sign_email.getText().toString();
            sendYZM(email);
        });
    }

    private void sendYZM(String email) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("http://localhost:8080/ks_server/toDo?cmd=3&email=" + email)
                            .build();//创建一个Request对象
                    Response response = client.newCall(request).execute();//发送请求获取返回数据
                    String responseData = response.body().string();//处理返回的数据
                    LogUtils.d(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendRequestWithOkHttp(String username_str, String password_str) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("http://localhost:8080/ks_server/toDo?cmd=1&username=" + username_str + "&password=" + password_str)
                            .build();//创建一个Request对象
                    Response response = client.newCall(request).execute();//发送请求获取返回数据
                    String responseData = response.body().string();//处理返回的数据
                    LogUtils.d(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void showBottomLayout() {
        TypeEvaluator<ViewGroup.LayoutParams> evaluator = new HeightEvaluator();

        ViewGroup.LayoutParams start = new ViewGroup.LayoutParams(CompatUtils.dp2px(Objects.requireNonNull(getActivity()), 200), CompatUtils.dp2px(getActivity(), 50));
        ViewGroup.LayoutParams end = new ViewGroup.LayoutParams(CompatUtils.dp2px(getActivity(), 50), CompatUtils.dp2px(getActivity(), 50));
        ValueAnimator animator = ObjectAnimator.ofObject(sign, "layoutParams", evaluator, start, end);

        AnimatorSet set = new AnimatorSet();
        set.play(animator);
        set.setDuration(400);
        set.start();
    }

    private void showBottomLayout_back() {
        TypeEvaluator<ViewGroup.LayoutParams> evaluator = new HeightEvaluator();

        ViewGroup.LayoutParams start = new ViewGroup.LayoutParams(CompatUtils.dp2px(Objects.requireNonNull(getActivity()), 50), CompatUtils.dp2px(getActivity(), 50));
        ViewGroup.LayoutParams end = new ViewGroup.LayoutParams(CompatUtils.dp2px(getActivity(), 200), CompatUtils.dp2px(getActivity(), 50));
        ValueAnimator animator = ObjectAnimator.ofObject(sign, "layoutParams", evaluator, start, end);

        AnimatorSet set = new AnimatorSet();
        set.play(animator);
        set.setDuration(400);
        set.start();
    }

    class HeightEvaluator implements TypeEvaluator<ViewGroup.LayoutParams> {

        @Override
        public ViewGroup.LayoutParams evaluate(float fraction, ViewGroup.LayoutParams startValue, ViewGroup.LayoutParams endValue) {
            ViewGroup.LayoutParams params = sign.getLayoutParams();
            params.width = (int) (startValue.width + fraction * (endValue.width - startValue.width));
            return params;
        }
    }
}