package com.cat.login.Fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.util.Log;
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
import com.cat.login.R;
import com.cat.login.Utils.CompatUtils;
import com.cat.login.Utils.LogUtils;
import com.cat.login.Utils.SpecialChar;
import com.cat.login.View.TickView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.zhihu.matisse.internal.utils.Platform;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentLogin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLogin extends Fragment  {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView login_text;
    private SpinKitView spinKitView;
    private LinearLayout login;
    private TickView tickView;
    private LinearLayout forget;
    private TextView sign_up;
    private ImageView back_main;
    private LinearLayout pw_ph;
    private boolean pw_ph_flag = true;
    private ImageView card_ph_pw;
    private ImageView pw_ph_img;
    private EditText password;
    private EditText username;
    private TextView send;
    private Handler mHandler_fr_login = new Handler(msg -> {
        switch (msg.what) {
            case 0:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getActivity().runOnUiThread(() -> { //对号
                                spinKitView.setVisibility(View.GONE);
                                tickView.setVisibility(View.VISIBLE);
                                tickView.start();
                            });
                            Thread.sleep(1000);
                            getActivity().runOnUiThread(() -> {//结束对号
                                tickView.stop();
                                ((MainActivity) getActivity()).successLogin();
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case 1:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getActivity().runOnUiThread(() -> {
                                Toasty.error(getActivity(), "密码错误", Toast.LENGTH_SHORT, true).show();
                                showBottomLayout_back();
                                spinKitView.setVisibility(View.GONE);
                            });
                            Thread.sleep(250);
                            getActivity().runOnUiThread(() -> {
                                login_text.setVisibility(View.VISIBLE);
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case 2:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getActivity().runOnUiThread(() -> {
                                Toasty.error(getActivity(), "该账号未注册", Toast.LENGTH_SHORT, true).show();
                                showBottomLayout_back();
                                spinKitView.setVisibility(View.GONE);
                            });
                            Thread.sleep(250);
                            getActivity().runOnUiThread(() -> {
                                login_text.setVisibility(View.VISIBLE);
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
            default:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getActivity().runOnUiThread(() -> {
                                Toasty.error(getActivity(), "未知错误", Toast.LENGTH_SHORT, true).show();
                                showBottomLayout_back();
                                spinKitView.setVisibility(View.GONE);
                            });
                            Thread.sleep(250);
                            getActivity().runOnUiThread(() -> {
                                login_text.setVisibility(View.VISIBLE);
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

        }
        return false;
    });
    private LinearLayout qq;

    public FragmentLogin() {
    }

    public static FragmentLogin newInstance(String param1, String param2) {
        return new FragmentLogin();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }


    private void initView() {
        login_text = getActivity().findViewById(R.id.login_text);
        tickView = getActivity().findViewById(R.id.tickView);
        sign_up = getActivity().findViewById(R.id.sign_up);
        login = getActivity().findViewById(R.id.login);
        spinKitView = getActivity().findViewById(R.id.spin_kit);
        forget = getActivity().findViewById(R.id.forget);
        back_main = getActivity().findViewById(R.id.back_main);
        pw_ph = getActivity().findViewById(R.id.pw_ph);
        pw_ph_img = getActivity().findViewById(R.id.pw_ph_img);
        card_ph_pw = getActivity().findViewById(R.id.card_ph_pw);
        password = getActivity().findViewById(R.id.password);
        username = getActivity().findViewById(R.id.username);
        send = getActivity().findViewById(R.id.send);
        qq = getActivity().findViewById(R.id.qq);

        qq.setOnClickListener(v -> {
        });

        pw_ph.setOnClickListener(v -> {
            if (pw_ph_flag) {
                pw_ph_flag = false;
                pw_ph_img.setImageResource(R.mipmap.pw_write);
                card_ph_pw.setImageResource(R.mipmap.ph_hui);
                password.setHint(new SpannableString("验证码"));
                username.setHint(new SpannableString("手机号"));
                send.setVisibility(View.VISIBLE);
            } else {
                pw_ph_flag = true;
                pw_ph_img.setImageResource(R.mipmap.ph_write);
                card_ph_pw.setImageResource(R.mipmap.password);
                password.setHint(new SpannableString("密码"));
                username.setHint(new SpannableString("用户名/电话号"));
                send.setVisibility(View.GONE);
            }
        });

        //back to main
        back_main.setOnClickListener(v -> {
            ((MainActivity) getActivity()).goTOMainActivity();
        });

        //忘记密码
        forget.setOnClickListener(v -> {
            ((MainActivity) getActivity()).setViewPager(0);
        });

        //注册
        sign_up.setOnClickListener(v -> {
            ((MainActivity) getActivity()).setViewPager(2);
        });

        //登录
        login.setOnClickListener(v -> {
            showBottomLayout();
            login_text.setVisibility(View.GONE);
            spinKitView.setVisibility(View.VISIBLE);
            new Thread(() -> {
                try {
                    //验证
                    String username_str = username.getText().toString();
                    String password_str = password.getText().toString();
                    if ("".equals(username_str) || "".equals(password_str)) {
                        getActivity().runOnUiThread(() -> {
                            Toasty.warning(getActivity(), "账号密码不能为空", Toast.LENGTH_SHORT, true).show();
                            showBottomLayout_back();
                            spinKitView.setVisibility(View.GONE);
                        });
                        Thread.sleep(250);
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                            login_text.setVisibility(View.VISIBLE);
                        });
                    } else if (SpecialChar.isSpecialChar(username_str) || SpecialChar.isSpecialChar(password_str)) {
                        getActivity().runOnUiThread(() -> {
                            Toasty.warning(getActivity(), "账号密码不能包含特殊字符[ _`~!@#$%^&*()+=|{}':;',[].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|", Toast.LENGTH_SHORT, true).show();
                            showBottomLayout_back();
                            spinKitView.setVisibility(View.GONE);
                        });
                        Thread.sleep(250);
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                            login_text.setVisibility(View.VISIBLE);
                        });
                    } else if (username_str.length() < 2 || username_str.length() > 15) {
                        getActivity().runOnUiThread(() -> {
                            Toasty.warning(getActivity(), "用户名长度不能小于2，大于15", Toast.LENGTH_SHORT, true).show();
                            showBottomLayout_back();
                            spinKitView.setVisibility(View.GONE);
                        });
                        Thread.sleep(250);
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                            login_text.setVisibility(View.VISIBLE);
                        });
                    } else if (password_str.length() < 6 || password_str.length() > 30) {
                        getActivity().runOnUiThread(() -> {
                            Toasty.warning(getActivity(), "密码长度不能小于6，大于30", Toast.LENGTH_SHORT, true).show();
                            showBottomLayout_back();
                            spinKitView.setVisibility(View.GONE);
                        });
                        Thread.sleep(250);
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                            login_text.setVisibility(View.VISIBLE);
                        });
                    } else {
                        sendRequestWithOkHttp(username_str, password_str);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    private void sendRequestWithOkHttp(String username_str, String password_str) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    Request request = new Request.Builder()
                            .url("http://localhost:8080/ks_server/toDo?cmd=0&username=" + username_str + "&password=" + password_str)
                            .build();//创建一个Request对象
                    Response response = client.newCall(request).execute();//发送请求获取返回数据
                    String responseData = response.body().string();//处理返回的数据
                    LogUtils.d(responseData);
                    readJson(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void readJson(String str) throws JSONException {
        JSONObject jsonObject = new JSONObject(str);
        int code = jsonObject.getInt("code");
        mHandler_fr_login.sendEmptyMessage(code);
        if (code == 0) {
            SharedPreferences sp = getActivity().getSharedPreferences("user", 0);
            @SuppressLint("CommitPrefEdits") Editor editor = sp.edit();
            JSONObject jsonObject_res = new JSONObject(jsonObject.getString("result"));
            editor.putBoolean("isLogin", true);
            editor.putString("userid", jsonObject_res.getString("userid"));
            editor.putString("username", jsonObject_res.getString("username"));
            editor.putString("password", jsonObject_res.getString("password"));
            editor.apply();
        }
    }


    private void showBottomLayout() {
        TypeEvaluator<ViewGroup.LayoutParams> evaluator = new HeightEvaluator();

        ViewGroup.LayoutParams start = new ViewGroup.LayoutParams(CompatUtils.dp2px(Objects.requireNonNull(getActivity()), 200), CompatUtils.dp2px(getActivity(), 50));
        ViewGroup.LayoutParams end = new ViewGroup.LayoutParams(CompatUtils.dp2px(getActivity(), 50), CompatUtils.dp2px(getActivity(), 50));
        ValueAnimator animator = ObjectAnimator.ofObject(login, "layoutParams", evaluator, start, end);

        AnimatorSet set = new AnimatorSet();
        set.play(animator);
        set.setDuration(400);
        set.start();
    }

    private void showBottomLayout_back() {
        TypeEvaluator<ViewGroup.LayoutParams> evaluator = new HeightEvaluator();

        ViewGroup.LayoutParams start = new ViewGroup.LayoutParams(CompatUtils.dp2px(Objects.requireNonNull(getActivity()), 50), CompatUtils.dp2px(getActivity(), 50));
        ViewGroup.LayoutParams end = new ViewGroup.LayoutParams(CompatUtils.dp2px(getActivity(), 200), CompatUtils.dp2px(getActivity(), 50));
        ValueAnimator animator = ObjectAnimator.ofObject(login, "layoutParams", evaluator, start, end);

        AnimatorSet set = new AnimatorSet();
        set.play(animator);
        set.setDuration(400);
        set.start();
    }



    class HeightEvaluator implements TypeEvaluator<ViewGroup.LayoutParams> {

        @Override
        public ViewGroup.LayoutParams evaluate(float fraction, ViewGroup.LayoutParams startValue, ViewGroup.LayoutParams endValue) {
            ViewGroup.LayoutParams params = login.getLayoutParams();
            params.width = (int) (startValue.width + fraction * (endValue.width - startValue.width));
            return params;
        }
    }





    private void checkQQUser(String qqId,String username_str, String sex,String year,String imgUrl) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                Request request = new Request.Builder()
                        .url("http://localhost:8080/ks_server/toDo?cmd=2&username=" + username_str + "&qqid=" + qqId + "&sex=" + sex + "&year=" + year + "&imgurl=" + imgUrl)
                        .build();//创建一个Request对象
                Response response = client.newCall(request).execute();//发送请求获取返回数据
                String responseData = response.body().string();//处理返回的数据
                LogUtils.d(responseData);
                readJson(responseData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}