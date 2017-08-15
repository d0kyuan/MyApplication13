package com.rayyuan.www.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DbAdapter dbHelper;
    ProgressBar unlimited_game_prog;
    int unlimited_game_all_count;
    int unlimited_game_right_count;
    int unlimited_game_fail_count;
    int unlimited_game_time = 360;
    ProgressDialog login_dialog;
    int all = 10;
    int now = 1;
    int leave = 9;
    int right = 0;
    static String rightans = "";
    View unlimited_game;
    int start_count_down;
    TextView unlimited_game_right_count_show;
    String right_count_text,fail_count_text;
    ArrayList q_his = new ArrayList();
    ArrayList a_his = new ArrayList();
    ArrayList q_text = new ArrayList();
    ArrayList cn = new ArrayList();
    ArrayList cv = new ArrayList();
    ListView list;
    HashMap<String, String> params;
    BufferedReader br;
    String feedback;
    listadapter adapter = null;
    listadapter_for_det adapter1 = null;
    ArrayList list_right_count = new ArrayList();
    ArrayList list_fail_count = new ArrayList();
    ArrayList list_isupload = new ArrayList();
    ArrayList list_record = new ArrayList();
    ArrayList list_mode = new ArrayList();
    ArrayList list_time = new ArrayList();
    ArrayList list_id = new ArrayList();
    ArrayList show_realans_list = new ArrayList();
    ArrayList show_ans_list = new ArrayList();
    ArrayList show_que_list = new ArrayList();
    ArrayList show_que_text_list = new ArrayList();
    String upload_id = "";
    String username;
    Boolean islogin =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dbHelper = new DbAdapter(this);
        dbHelper.open();
        dbHelper.create_table();

        Cursor c =  dbHelper.useq("select * from tb_config");
        for(int i = 0;i<c.getCount();i++){
            cn.add( c.getString(c.getColumnIndex("Cname")));
            cv.add(c.getString(c.getColumnIndex("Cvalue")));
            c.moveToNext();
        }
        View header=navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.username);

        Log.i("tt",cv.size()+"");
        for (int i = 0;i<cv.size();i++) {
            Log.i("tt",     cv.get(i).toString());
            Log.i("tt",     cn.get(i).toString());

        }
        Log.i("tt",cn.indexOf("name")+"");
        if(cv.size()<=0){
            name.setText("使用者 : 遊客");
            menu_login(true);
            menu_logout(false);
            menu_signup(true);
            islogin = false;
        }else{
            username = cv.get(cn.indexOf("name")).toString();
            name.setText("使用者 : "+username);
            menu_login(false);
            menu_logout(true);
            menu_signup(false);
            islogin = true;
        }

    }

    private void menu_login(Boolean a){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_Menu = navigationView.getMenu();
        MenuItem login = (MenuItem) nav_Menu.findItem(R.id.login);
        if(a)
            login.setVisible(true);
        else
            login.setVisible(false);
    }
    private void menu_signup(Boolean a){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_Menu = navigationView.getMenu();
        MenuItem sighup = (MenuItem) nav_Menu.findItem(R.id.sighup);
        if(a)
            sighup.setVisible(true);
        else
            sighup.setVisible(false);
    }
    private void menu_logout(Boolean a){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_Menu = navigationView.getMenu();
        MenuItem logout = (MenuItem) nav_Menu.findItem(R.id.logout);
        if(a)
            logout.setVisible(true);
        else
            logout.setVisible(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    Menu menu;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.sighup) {
            // Handle the camera action

            signup();
        } else if (id == R.id.login) {
            chang_to_login();
        } else if (id == R.id.startgame) {
            change(R.layout.gamemode_sele);
            change_to_game_mod_sele();
            //startgame();
        } else if (id == R.id.his) {
            change(R.layout.all_his_list);
            changetohispage();

        }else if(id == R.id.logout){
            go_logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //TODO 註冊
    private void signup(){
        change(R.layout.signup);
        Button signup_btn = (Button) findViewById(R.id.signup_btn);
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check_input_for_sighup()){
                    EditText account = (EditText) findViewById(R.id.account);
                    EditText password = (EditText) findViewById(R.id.password);
                    EditText name = (EditText) findViewById(R.id.name);
                    params =new HashMap<String, String>();
                    params.put("ac",account.getText().toString());
                    params.put("psw",password.getText().toString());
                    params.put("name",name.getText().toString());
                    Thread th = new Thread(SignupThread);
                    th.start();
                }
            }
        });
    }
    //TODO 所有輸入檢查
    private Boolean check_input_for_sighup(){
        String limitEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(limitEx);
        EditText account = (EditText) findViewById(R.id.account);
        EditText password = (EditText) findViewById(R.id.password);
        EditText password2 = (EditText) findViewById(R.id.password2);
        EditText name = (EditText) findViewById(R.id.name);
        if(TextUtils.isEmpty(account.getText())){
            account.setError("帳號不可為空白");
            account.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(password.getText())){
            password.setError("密碼號不可為空白");
            password.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(password2.getText())){
            password2.setError("請再輸入一次密碼");
            password2.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(name.getText())){
            name.setError("請輸入姓名");
            name.requestFocus();
            return false;
        }else if(pattern.matcher(account.getText().toString()).find()){
            account.setError("帳號不可含有特殊字元!");
            account.requestFocus();
            return false;
        }else if(pattern.matcher(password.getText().toString()).find()){
            password.setError("密碼不可含有特殊字元!");
            password.requestFocus();
            return false;
        }else if(pattern.matcher(password2.getText().toString()).find()){
            password2.setError("在一次密碼不可含有特殊字元!");
            password2.requestFocus();
            return false;
        }else if(pattern.matcher(name.getText().toString()).find()){
            name.setError("姓名不可含有特殊字元!");
            name.requestFocus();
            return false;
        }else if(!password.getText().toString().equals(password2.getText().toString())){
            password.setError("兩次密碼不相同");
            password.requestFocus();
            return false;
        }else{
            return true;
        }
    }
    //TODO 登出
    private void  go_logout(){


        //產生一個Builder物件
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //設定Dialog的標題
        builder.setTitle("您確定要登出嗎");
        //設定Dialog的內容
        builder.setMessage("登出後將會刪除所有遊戲紀錄 ");
        //設定Positive按鈕資料
        builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO 按下按鈕時顯示快顯
                dbHelper = new DbAdapter(MainActivity.this);
                dbHelper.open();
                dbHelper.useq("drop table tb_config");
                dbHelper.useq("drop table tb_game_his");
                dbHelper.useq("drop table tb_game_det");
                islogin = false;
                dbHelper.create_table();
                show_snk(getCurrentFocus(),"成功登出");
                change_to_home();
            }
        });
        //設定Negative按鈕資料
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO 按下按鈕時顯示快顯
                show_snk(getCurrentFocus(),"取消登出");
            }
        });

        builder.create().show();




    }


    //TODO 切換畫面func
    private void change(int a) {
        CoordinatorLayout v = (CoordinatorLayout) findViewById(R.id.mycontet);
        v.removeAllViews();
        View.inflate(this, a, (ViewGroup) findViewById(R.id.mycontet));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        check_login_for_changepage(header);
    }
    //TODO 檢查是否為登入中
    private void check_login_for_changepage( View a){
        TextView name = (TextView) a.findViewById(R.id.username);
        if(islogin){
            name.setText("使用者 : "+username);
            menu_login(false);
            menu_logout(true);
            menu_signup(false);
        }else{
            name.setText("使用者 : 遊客");
            menu_login(true);
            menu_logout(false);
            menu_signup(true);

        }
    }
    //TODO 首頁
    public void change_to_home() {
        change(R.layout.app_bar_main);
    }

    //TODO 登入畫面
    public void chang_to_login() {
        change(R.layout.login);

        dbHelper = new DbAdapter(this);
        dbHelper.open();

        Button goback = (Button) findViewById(R.id.login_goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_to_home();
            }
        });

        Button login_gologin = (Button)findViewById(R.id.login_gologin);
        login_gologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText account  = (EditText) findViewById(R.id.account);
                EditText password = (EditText) findViewById(R.id.password);
                InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                login(account.getText().toString(),password.getText().toString());
            }
        });
    }

    public void show_snk(View view,String a){
        Snackbar.make(view,a, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    //TODO 登入
    public void login(String account ,String password){

        params = new HashMap<String, String>();
        params.put("ac",account );
        params.put("ps", password);
        Thread thread = new Thread(LoginThread);
        thread.start();
    }
    //TODO 登入執行續
    public Runnable LoginThread = new Runnable() {
        @Override
        public void run() {

            MainActivity.this.runOnUiThread(new Runnable() {
                                                public void run() {
                                                    login_dialog = ProgressDialog.show(MainActivity.this,
                                                            "登入中", "登入中..請稍後...", true);
                                                }
                                            });
            URL url;
            String loginurl = "http://120.96.63.168/math/Php_pro/login.php";
            try {
                url = new URL(loginurl);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("invalid url: " + loginurl + "");
            }
            StringBuilder bodyBuilder = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            // constructs the POST body using the parameters
            while (iterator.hasNext()) {
                Map.Entry<String, String> param = iterator.next();
                bodyBuilder.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    bodyBuilder.append('&');
                }
            }
            String body = bodyBuilder.toString();
            Log.v("MainActivity", "Posting '" + body + "' to " + url);
            byte[] bytes = body.getBytes();
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");
                // post the request
                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();
                if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                    br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                    feedback = br.readLine();
                    Log.i("feedback", feedback);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                if (feedback.indexOf("noaccount") > -1) {
                                    login_dialog.dismiss();
                                    show_snk(findViewById(R.id.all_view),"無此帳號!! 請再次確認");
                                 //   Toast.makeText(MainActivity.this, "無此帳號!! 請再次確認", Toast.LENGTH_SHORT).show();
                                } else if (feedback.indexOf("faillogin") > -1) {
                                    login_dialog.dismiss();
                                    show_snk(findViewById(R.id.all_view),"密碼錯誤!! 請再次確認");
                                    //Toast.makeText(MainActivity.this, "密碼錯誤!! 請再次確認", Toast.LENGTH_SHORT).show();
                                } else if (feedback.indexOf("suc") > -1) {
                                    String[] feedbacksp = feedback.split(":");
                                    login_dialog.dismiss();
                                    Log.w("ttt", "sucess");
                                    dbHelper = new DbAdapter(MainActivity.this);
                                    dbHelper.open();
                                    EditText account = (EditText) findViewById(R.id.account);
                                    EditText password = (EditText) findViewById(R.id.password);
                                    dbHelper.useq("insert into tb_config(Cname,Cvalue) values ('account','"+account.getText().toString()+"')," +
                                            "('password','"+password.getText().toString()+"')," +
                                            "('name','"+feedbacksp[1]+"')," +
                                            "('id','"+feedbacksp[2]+"')");
                                    username = feedbacksp[1];
                                    dbHelper = new DbAdapter(MainActivity.this);
                                    dbHelper.open();
                                    dbHelper.create_table();

                                    Cursor c =  dbHelper.useq("select * from tb_config");
                                    for(int i = 0;i<c.getCount();i++){
                                        cn.add( c.getString(c.getColumnIndex("Cname")));
                                        cv.add(c.getString(c.getColumnIndex("Cvalue")));
                                        c.moveToNext();
                                    }
                                    show_snk(findViewById(R.id.all_view),"登入成功");
                                    islogin = true;
                                    change_to_home();
                                } else {
                                    show_snk(findViewById(R.id.all_view),"很抱歉發生不明錯誤 請向開發人員回報 ! 造成您的不便請見諒");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    conn.disconnect();
                } else {
                    br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                Toast.makeText(MainActivity.this, br.readLine(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    };
    //TODO 註冊執行續
    public Runnable SignupThread = new Runnable() {
        @Override
        public void run() {

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    login_dialog = ProgressDialog.show(MainActivity.this,
                            "登入中", "登入中..請稍後...", true);
                }
            });
            URL url;
            String loginurl = "http://120.96.63.168/math/Php_pro/signup.php";
            try {
                url = new URL(loginurl);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("invalid url: " + loginurl + "");
            }
            StringBuilder bodyBuilder = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            // constructs the POST body using the parameters
            while (iterator.hasNext()) {
                Map.Entry<String, String> param = iterator.next();
                bodyBuilder.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    bodyBuilder.append('&');
                }
            }
            String body = bodyBuilder.toString();
            Log.v("MainActivity", "Posting '" + body + "' to " + url);
            byte[] bytes = body.getBytes();
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");
                // post the request
                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();
                if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                    br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                    feedback = br.readLine();
                    Log.i("feedback", feedback);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            login_dialog.dismiss();
                            try {
                                if (feedback.indexOf("sameaccount") > -1) {

                                    EditText account = (EditText) findViewById(R.id.account);
                                    account.forceLayout();
                                    show_snk(findViewById(R.id.all_view),"帳號已存在!! 請再次確認");
                                    //   Toast.makeText(MainActivity.this, "無此帳號!! 請再次確認", Toast.LENGTH_SHORT).show();
                                } else if (feedback.indexOf("suc") > -1) {
                                    String[] feedbacksp = feedback.split(":");

                                    Log.w("ttt", "sucess");
                                    dbHelper = new DbAdapter(MainActivity.this);
                                    dbHelper.open();
                                    EditText account = (EditText) findViewById(R.id.account);
                                    EditText password = (EditText) findViewById(R.id.password);
                                    EditText name = (EditText) findViewById(R.id.name);
                                    dbHelper.useq("insert into tb_config(Cname,Cvalue) values ('account','"+account.getText().toString()+"')," +
                                            "('password','"+password.getText().toString()+"')," +
                                            "('name','"+name.getText().toString()+"')," +
                                            "('id','"+feedbacksp[1]+"')");
                                    dbHelper = new DbAdapter(MainActivity.this);
                                    dbHelper.open();
                                    dbHelper.create_table();

                                    Cursor c =  dbHelper.useq("select * from tb_config");
                                    for(int i = 0;i<c.getCount();i++){
                                        cn.add( c.getString(c.getColumnIndex("Cname")));
                                        cv.add(c.getString(c.getColumnIndex("Cvalue")));
                                        c.moveToNext();
                                    }
                                    username = cv.get(cn.indexOf("name")).toString();
                                    show_snk(findViewById(R.id.all_view),"註冊成功");
                                    islogin = true;
                                    change_to_home();
                                } else {
                                    show_snk(findViewById(R.id.all_view),"很抱歉發生不明錯誤 請向開發人員回報 ! 造成您的不便請見諒");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    conn.disconnect();
                } else {
                    br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                Toast.makeText(MainActivity.this, br.readLine(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    };
    //TODO 上傳執行續
    public Runnable upload = new Runnable() {
        @Override
        public void run() {

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    login_dialog = ProgressDialog.show(MainActivity.this,
                            "上傳中", "上傳中..請稍後...", true);
                }
            });
            URL url;
            String loginurl = "http://120.96.63.168/math/Php_pro/upload.php";
            try {
                url = new URL(loginurl);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("invalid url: " + loginurl + "");
            }
            StringBuilder bodyBuilder = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            // constructs the POST body using the parameters
            while (iterator.hasNext()) {
                Map.Entry<String, String> param = iterator.next();
                bodyBuilder.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    bodyBuilder.append('&');
                }
            }
            String body = bodyBuilder.toString();
            Log.v("MainActivity", "Posting '" + body + "' to " + url);
            byte[] bytes = body.getBytes();
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");
                // post the request
                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();
                if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                    br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                    feedback = br.readLine();
                    Log.i("feedback", feedback);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {

                            try {
                                if (feedback.indexOf("error") > -1) {
                                    login_dialog.dismiss();
                                    show_snk(findViewById(R.id.all_view),"上傳失敗");
                                    //   Toast.makeText(MainActivity.this, "無此帳號!! 請再次確認", Toast.LENGTH_SHORT).show();
                               } else if (feedback.indexOf("suc") > -1) {
                                    login_dialog.dismiss();
                                    show_snk(getCurrentFocus(),"上傳成功");
                                    dbHelper = new DbAdapter(MainActivity.this);
                                    dbHelper.useq("update tb_game_his set isupload = '1' where _id like '"+upload_id+"'");
                                    his_list_ref();
                                } else {
                                    login_dialog.dismiss();
                                    show_snk(findViewById(R.id.all_view),"很抱歉發生不明錯誤 請向開發人員回報 ! 造成您的不便請見諒");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    conn.disconnect();
                } else {
                    br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                Toast.makeText(MainActivity.this, br.readLine(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    };


    //TODO 刷新紀錄畫面
    public void his_list_ref() {
        list_id.clear();
        list_mode.clear();
        list_time.clear();
        list_record.clear();
        list_right_count.clear();
        list_fail_count.clear();
        list_isupload.clear();
        dbHelper = new DbAdapter(this);
        dbHelper.open();

        String sql =
                "select * from tb_game_his";
        Cursor c = dbHelper.useq(sql);

        if (c.getCount() == 0) {
            list_record.add("目前沒有進行過遊戲");
            list_time.add("");
            list_mode.add("");
        }

        for (int i = 0; i < c.getCount(); i++) {
            list_id.add(c.getString(c.getColumnIndex("_id")));
            list_record.add(c.getString(c.getColumnIndex("Grecord")));;
            list_time.add(c.getString(c.getColumnIndex("Gtime")));
            list_mode.add(c.getString(c.getColumnIndex("Gmode")));
            list_right_count.add(c.getString(c.getColumnIndex("Rcount")));
            list_fail_count.add(c.getString(c.getColumnIndex("Fcount")));
            list_isupload.add(c.getString(c.getColumnIndex("isupload")));
            Log.i("ttt",c.getString(c.getColumnIndex("Gmode")));

            c.moveToNext();
        }
        Collections.reverse(list_id);
        Collections.reverse(list_mode);
        Collections.reverse(list_time);
        Collections.reverse(list_record);
        Collections.reverse(list_right_count);
        Collections.reverse(list_fail_count);
        Collections.reverse(list_isupload);
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);
    }


    //TODO 切換紀錄畫面
    public void changetohispage() {
        list_id.clear();
        list_mode.clear();
       list_time.clear();
       list_record.clear();
       list_right_count.clear();
       list_fail_count.clear();
        list_isupload.clear();
        dbHelper = new DbAdapter(this);
        dbHelper.open();

        String sql =
                "select * from tb_game_his";
        Cursor c = dbHelper.useq(sql);

        if (c.getCount() == 0) {
            list_record.add("目前沒有進行過遊戲");
            list_time.add("");
            list_mode.add("");
            list_right_count.add("");
            list_fail_count.add("");
            list_isupload.add("");
        }

        for (int i = 0; i < c.getCount(); i++) {
            list_id.add(c.getString(c.getColumnIndex("_id")));
            list_record.add(c.getString(c.getColumnIndex("Grecord")));;
            list_time.add(c.getString(c.getColumnIndex("Gtime")));
            list_mode.add(c.getString(c.getColumnIndex("Gmode")));
            list_right_count.add(c.getString(c.getColumnIndex("Rcount")));
            list_fail_count.add(c.getString(c.getColumnIndex("Fcount")));
            list_isupload.add(c.getString(c.getColumnIndex("isupload")));
            Log.i("ttt",c.getString(c.getColumnIndex("Gmode")));

            c.moveToNext();
        }
        Collections.reverse(list_id);
        Collections.reverse(list_mode);
        Collections.reverse(list_time);
        Collections.reverse(list_record);
        Collections.reverse(list_right_count);
        Collections.reverse(list_fail_count);
        Collections.reverse(list_isupload);
        list = (ListView) findViewById(R.id.Mylist);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, final View arg1,
                                           final int pos, long id) {
                // TODO Auto-generated method stub
                PopupMenu popup = new PopupMenu(MainActivity.this, arg1);

                popup.getMenuInflater().inflate(R.menu.his_pop, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int i = item.getItemId();
                        if (i == R.id.upload_his) {
                            //do something
                            if(islogin){
                                if(list_isupload.get(pos).toString().equals("0")){
                                    String Mid =   cv.get(cn.indexOf("id")).toString();
                                    params = new HashMap<String, String>();
                                    params.put("Mid",Mid);
                                    params.put("sc",list_record.get(pos).toString());
                                    params.put("time",list_time.get(pos).toString());
                                    params.put("mode",list_mode.get(pos).toString());
                                    params.put("right",list_right_count.get(pos).toString());
                                    params.put("fail",list_fail_count.get(pos).toString());
                                    upload_id = list_id.get(pos).toString();
                                    Thread th = new Thread(upload);
                                    th.start();
                                }else{
                                    show_snk(getCurrentFocus(),"此筆紀錄已經上傳過了");
                                }

                            }else{
                                show_snk(getCurrentFocus(),"請先登入!");
                            }

                            return true;
                        } else if (i == R.id.delete_his) {
                            //do something
                            DbAdapter dbHelper1 = new DbAdapter(MainActivity.this);
                            dbHelper1.open();
                            Log.i("tt", "DELETE FROM tb_game_his " +
                                    "WHERE Grecord like '" + list_record.get(pos) + "' and Gtime like '" + list_time.get(pos) + "' and Gmode like '"+list_mode.get(pos)+"' and _id = "+list_id.get(pos)+";");
                            dbHelper1.useq("DELETE FROM tb_game_his " +
                                    "WHERE Grecord like '" + list_record.get(pos) + "' and Gtime like '" + list_time.get(pos) + "'and Gmode like '"+list_mode.get(pos)+"' and _id = "+list_id.get(pos)+";");
                            his_list_ref();
                            View a = findViewById(R.id.all_his);
                            Snackbar.make(a, "已刪除", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            return true;
                        } else {
                            return onMenuItemClick(item);
                        }
                    }
                });
                popup.show();
                Log.v("long clicked", "pos: " + pos);

                return true;
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Log.v("Click", "pos: " + position);
                //TODO  change to on det
                if (!list_record.get(position).toString().equals("目前沒有進行過遊戲")) {
                    change(R.layout.his_det_list);
                    change_to_det_list(list_id.get(position).toString(),list_right_count.get(position).toString(),list_fail_count.get(position).toString());
                }
            }
        });


        adapter = new listadapter(this);
        list.setAdapter(adapter);


    }

    //TODO 詳細記錄畫面
    public void change_to_det_list(String id,String a,String b) {
        show_ans_list.clear();
        show_que_list.clear();
        show_realans_list.clear();
        TextView right_text = (TextView) findViewById(R.id.right_text);
        right_text.setText("答對了 :"+a+"題");
        TextView fail_text = (TextView) findViewById(R.id.fail_text);
        fail_text.setText("答錯了 :"+b+"題");
        Button goback = (Button) findViewById(R.id.det_goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change(R.layout.all_his_list);
                changetohispage();
            }
        });
        dbHelper = new DbAdapter(this);
        dbHelper.open();

        String sql =
                "select * from tb_game_det where Gid like '" + id + "'";
        Cursor c = dbHelper.useq(sql);

        int dem_count = 0;
        Log.i("tt", "讀取締 " + c.getCount() + "");
        for (int i = 0; i < c.getCount(); i++) {
            Log.i("tt", "讀取締 " + dem_count);
            dem_count++;
            show_ans_list.add(c.getString(c.getColumnIndex("A")));
            ;
            show_que_list.add(c.getString(c.getColumnIndex("Q")));
            show_realans_list.add(c.getString(c.getColumnIndex("realA")));
            show_que_text_list.add(c.getString(c.getColumnIndex("Q_text")));
            c.moveToNext();
        }
        list = (ListView) findViewById(R.id.det_list);


        adapter1 = new listadapter_for_det(this);
        list.setAdapter(adapter1);
    }


    //TODO 選擇遊戲模式
    public void change_to_game_mod_sele() {
        Button lim = (Button) findViewById(R.id.nomal_btn);
        //TODO 一班模式開始
        lim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change(R.layout.gameview);
                startgame();
            }
        });
        Button unlimited_btn = (Button) findViewById(R.id.nolim_btn);
        unlimited_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change(R.layout.umlim_game);
                unlimited_game_countdown();
            }
        });
        Button custom_btn = (Button) findViewById(R.id.custom_g_btn);
        custom_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout page  = (LinearLayout) findViewById(R.id.ref_page);
                page.removeAllViews();
            }
        });

    }



    //TODO 開始前倒數計時
    public void unlimited_game_countdown() {
        //隱藏畫面
        unlimited_game = findViewById(R.id.game_all);
        unlimited_game.setVisibility(View.INVISIBLE);
        //    顯示倒數
        start_count_down = 5;
        Toast.makeText(MainActivity.this, "5秒後開始遊戲", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                Log.i("tt",start_count_down+"");
                if (start_count_down  > 0) {
                    Toast.makeText(MainActivity.this, start_count_down + "", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(this, 2000);
                    start_count_down--;
                } else {
                    Toast.makeText(MainActivity.this, "遊戲開始!!", Toast.LENGTH_SHORT).show();
                    start_unlimited_game();

                }
            }
        }, 1500);
    }


    //TODO 開始限時模式
    public void start_unlimited_game() {

        unlimited_game.setVisibility(View.VISIBLE);
        unlimited_game_right_count = 0;
        unlimited_game_fail_count = 0;
        unlimited_game_all_count = 0;
        rightans = "";
        myans.clear();
        a_his.clear();
        q_his.clear();
        unlimited_game_prog = (ProgressBar) findViewById(R.id.progressBar);
        unlimited_game_prog.setMax(360);
        unlimited_game_right_count_show = (TextView) findViewById(R.id.unlimited_right_count);
        unlimited_game_right_count_show.setText(unlimited_game_right_count+"");
        rightans = "";
        myans.clear();
        a_his.clear();
        q_his.clear();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (unlimited_game_time - 1 > 0) {
                    unlimited_game_time--;
                    unlimited_game_prog.setProgress(unlimited_game_time);
                    new Handler().postDelayed(this, 1000);
                } else {
                    //遊戲結束
                    save_unlimited_game();
                }
            }

        }, 1000);
        setques();
        Button checkans = (Button) findViewById(R.id.checkans);
        Button giveup = (Button) findViewById(R.id.giveup);
        giveup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unlimited_game_check_ans(true);
            }
        });
        ans = (EditText) findViewById(R.id.ans_text);
        ans.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    unlimited_game_check_ans(false);

                    return true;
                }
                return false;
            }
        });
        checkans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unlimited_game_check_ans(false);
            }

        });
    }
    //TODO 限時模式檢查答案
    public void unlimited_game_check_ans(Boolean giveup) {

        unlimited_game_all_count++;
        String tempans;
        if (giveup) {
            tempans = "放棄";
        } else {
            if (TextUtils.isEmpty(ans.getText().toString())) {
                View a = findViewById(R.id.game_all);
                Snackbar.make(a, "請輸入答案", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            } else {
                tempans = ans.getText().toString();
            }

        }

        myans.add(tempans);
        if (giveup) {
            unlimited_game_fail_count++;
            unlimited_game_time-=20;
            unlimited_game_prog.setProgress(unlimited_game_time);
            View a = findViewById(R.id.game_all);
            Snackbar.make(a, "放棄 -20秒 答案是:" + rightans, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();

        } else {
            if (ans.getText().toString().equals(rightans)) {
                unlimited_game_right_count++;
                unlimited_game_time+=10;
                unlimited_game_prog.setProgress(unlimited_game_time);

                if(unlimited_game_right_count>hist){
                    hist+=10;
                    Ques *= 10;
                }
                View a = findViewById(R.id.game_all);
                Snackbar.make(a, "恭喜答對 +10秒", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                unlimited_game_right_count_show.setText(unlimited_game_right_count+"");

            } else {
                unlimited_game_fail_count++;
                unlimited_game_time-=20;
                unlimited_game_prog.setProgress(unlimited_game_time);
                View a = findViewById(R.id.game_all);
                Snackbar.make(a, "抱歉答錯了  -20秒 答案是:" + rightans, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

            }
        }

        ans.setText("");
        setques();
    }
    //TODO 限時模式儲存遊系結果
    public  void save_unlimited_game(){
        dbHelper  = new DbAdapter(MainActivity.this);
        dbHelper.open();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt=new Date();
        String dts=sdf.format(dt);
        // 1是一般模式 2是無限   可擴充
        Long myid =   dbHelper.add_game_his_db((unlimited_game_right_count*10)+"",dts,"2",unlimited_game_right_count+"",unlimited_game_fail_count+"","0");


        Log.i("ttt","size"+q_text.size());
        for(int i =0;i<myans.size();i++){
            dbHelper.add_game_det_db(String.valueOf(myid),q_his.get(i)+"",a_his.get(i)+"",myans.get(i)+"",q_text.get(i)+"");
            Log.i("t","這是第 "+i+"個"+String.valueOf(myid)+":"+q_his.get(i)+":"+a_his.get(i)+":"+myans.get(i)+":"+q_text.get(i)+"");
        }
        finishgame((unlimited_game_right_count*10));
    }



    //TODO 自訂模式
    public void start_coustom_game(){
        //TODO 歸零
        all = 10;
        now = 1;
        leave = 9;
        right = 0;
        rightans = "";
        myans.clear();
        a_his.clear();
        q_his.clear();
        dbHelper = new DbAdapter(this);
        dbHelper.open();
        setques();
        TextView nowques_text = (TextView) findViewById(R.id.nowques);
        //剩餘題數
        TextView alcount_text = (TextView) findViewById(R.id.alcount);
        //答對題數
        TextView rightans_text = (TextView) findViewById(R.id.rightans);
        nowques_text.setText(now+"");
        alcount_text.setText(leave+"");
        rightans_text.setText(right+"");
        Button checkans = (Button) findViewById(R.id.checkans);
        Button giveup = (Button) findViewById(R.id.giveup);
        giveup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_ans(true);
            }
        });
        ans = (EditText) findViewById(R.id.ans_text);
        ans.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    check_ans(false);

                    return true;
                }
                return false;
            }
        });
        checkans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_ans(false);


            }

        });
    }


    //TODO  開始一般遊戲
    EditText ans;
    public void startgame(){
        //TODO 歸零
        all = 10;
        now = 1;
        leave = 9;
        right = 0;
        rightans = "";
        myans.clear();
        a_his.clear();
        q_his.clear();
        dbHelper = new DbAdapter(this);
        dbHelper.open();
        setques();
        TextView nowques_text = (TextView) findViewById(R.id.nowques);
        //剩餘題數
        TextView alcount_text = (TextView) findViewById(R.id.alcount);
        //答對題數
        TextView rightans_text = (TextView) findViewById(R.id.rightans);
        nowques_text.setText(now+"");
        alcount_text.setText(leave+"");
        rightans_text.setText(right+"");
        Button checkans = (Button) findViewById(R.id.checkans);
        Button giveup = (Button) findViewById(R.id.giveup);
        giveup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_ans(true);
            }
        });
       ans = (EditText) findViewById(R.id.ans_text);
        ans.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    check_ans(false);

                    return true;
                }
                return false;
            }
        });
        checkans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_ans(false);


            }

        });
    }

    int Ques = 1000;
    int hist = 10;
    //TODO 遊戲結束
    public  void finishgame(int a){
        AlertDialog builder = new AlertDialog.Builder(MainActivity.this).create();;
        //設定Dialog的標題
        builder.setTitle("遊戲結束");
        builder.setCanceledOnTouchOutside (false);
        //設定Dialog的內容
        builder.setMessage("您的得分是 :"+a);
        //設定Positive按鈕資料
        builder.setButton(AlertDialog.BUTTON_NEUTRAL, "確認",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        change(R.layout.all_his_list);
                        changetohispage();
                    }
                });

        builder.show();
    }

    //TODO 檢查答案
    private void check_ans(Boolean giveup){
        String tempans ;
        if(giveup){
            tempans = "放棄";
        }else{
            if(TextUtils.isEmpty(ans.getText().toString())){
                View a = findViewById(R.id.game_all);
                Snackbar.make(a, "請輸入答案", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }else {
                tempans = ans.getText().toString();
            }

        }

        myans.add(tempans);

        if(leave-1 ==-1){
            dbHelper  = new DbAdapter(MainActivity.this);
            dbHelper.open();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dt=new Date();
            String dts=sdf.format(dt);
            // 1是一般模式 2是無限   可擴充
          Long myid =   dbHelper.add_game_his_db((right*10)+"",dts,"1",right+"",(all-right)+"","0");


            Log.i("ttt","size"+q_text.size());
            for(int i =0;i<10;i++){
                dbHelper.add_game_det_db(String.valueOf(myid),q_his.get(i)+"",a_his.get(i)+"",myans.get(i)+"",q_text.get(i)+"");
                Log.i("t","這是第 "+i+"個"+String.valueOf(myid)+":"+q_his.get(i)+":"+a_his.get(i)+":"+myans.get(i)+":"+q_text.get(i)+"");
            }
            finishgame((right*10));
        }else{

            if(giveup){
                changecount(false);
                View a = findViewById(R.id.game_all);
                Snackbar.make(a, "答案是:" + rightans, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

            }else{
                if(ans.getText().toString().equals(rightans)){
                    changecount(true);
                    View a = findViewById(R.id.game_all);
                    Snackbar.make(a, "恭喜答對", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                }else{
                    changecount(false);
                    View a = findViewById(R.id.game_all);
                    Snackbar.make(a, "放棄  答案是:" + rightans, Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                }
            }

            ans.setText("");
            setques();
        }

    }

    //TODO 更換題號
    private  void changecount(Boolean isright){
        if(isright){

            right++;
        }
        now++;
        leave--;
        //現在題數
        TextView nowques = (TextView) findViewById(R.id.nowques);
        //剩餘題數
        TextView alcount = (TextView) findViewById(R.id.alcount);
        //答對題數
        TextView rightans = (TextView) findViewById(R.id.rightans);
        nowques.setText(now+"");
        alcount.setText(leave+"");
        rightans.setText(right+"");
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        int a = now;
        pb.setProgress(a);
    }


    ArrayList myans = new ArrayList();
    //TODO 出題目
    private  void setques(){

        Random ran = new Random();

        int type =   ran.nextInt(3);
        ran = new Random();
        int type2 =  ran.nextInt(3);

        // 2 8 10 16
        String[] Str_ary = {"二","十","八","十六"};
        int[] Int_ary = {2,10,8,16};
        TextView show_tips = (TextView) findViewById(R.id.show_tips);

        if (type2==type) {
            if(Str_ary[type].equals(Str_ary[type2])){
                if(type2+1 >3){
                    type2 -=1;
                }else{
                    type2+=1;
                }
            }
        }


        show_tips.setText("請從"+Str_ary[type]+"進制轉成"+Str_ary[type2]+"進制");

        q_text.add("請從"+Str_ary[type]+"進制轉成"+Str_ary[type2]+"進制");

        int ques =   ran.nextInt(Ques);
        // Integer.valueOf(a,2).toString()
        TextView ques_text = (TextView) findViewById(R.id.ques_text);
        System.out.print(ques+":"+Int_ary[type]);

        String newques = null;
        switch (Int_ary[type]){
            case 2:
                newques = Integer.toBinaryString(ques);
                break;
            case 10:
                newques = ques+"";
                break;
            case 8:
                newques = Integer.toOctalString(ques);
                break;
            case 16:
                newques =  Integer.toHexString(ques);
                break;
        }
        String newans = null;
        switch (Int_ary[type2]){
            case 2:
                newans = Integer.toBinaryString(ques);
                break;
            case 10:
                newans = ques+"";
                break;
            case 8:
                newans = Integer.toOctalString(ques);
                break;
            case 16:
                newans =  Integer.toHexString(ques);
                break;
        }

            ques_text.setText(newques+"");
            Log.i("tt","ques_text this not 10 :"+newques);
            q_his.add(newques+"");



            Log.i("tt","rightans this not 10 :"+newans);
            rightans = newans+"";
            a_his.add(newans+"");
    }


    //TODO 自製列表class
    public class listadapter extends BaseAdapter {
        private LayoutInflater myInflater;
        public listadapter(Context context){
            myInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return list_record.size();
        }

        @Override
        public Object getItem(int i) {
            return list_record.get(i);
        }

        @Override
        public long getItemId(int i) {
            return list_record.indexOf(getItem(i));
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = myInflater.inflate(R.layout.custom_all_hi_list, null);
            TextView userTitle = (TextView) view.findViewById(R.id.record_text);
            TextView  userlast = (TextView) view.findViewById(R.id.time_text);
            TextView  mod_text = (TextView) view.findViewById(R.id.mod_text);
            if(i>list_mode.size()+1){
                mod_text.setText("");
            }else {

                switch (list_mode.get(i).toString()) {
                    case "1":
                        mod_text.setText("一般模式");
                        break;
                    case "2":
                        mod_text.setText("無限模式");
                        break;
                    default:
                        mod_text.setText("");
                        break;
                }

            }
            if(i>list_record.size()+1){
                userTitle.setText("");
            }else{
                if(list_record.get(i).toString().equals("目前沒有進行過的遊戲"))
                    userTitle.setText("得分 :" +list_record.get(i).toString());
                else
                    userTitle.setText(list_record.get(i).toString());

            }


            if(i>list_time.size()+1){
                userlast.setText("");
            }else{
                if(list_record.get(i).toString().equals("目前沒有進行過的遊戲"))
                    userlast.setText("時間 : "+ list_time.get(i).toString());
                else
                    userlast.setText(list_time.get(i).toString());
            }

            return view;
        }
    }
    public class listadapter_for_det extends BaseAdapter {
        private LayoutInflater myInflater;
        public listadapter_for_det(Context context){
            myInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return show_que_list.size();
        }

        @Override
        public Object getItem(int i) {
            return show_que_list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return show_que_list.indexOf(getItem(i));
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = myInflater.inflate(R.layout.custom, null);

            TextView det_count = (TextView) view.findViewById(R.id.det_count);
            TextView Q = (TextView) view.findViewById(R.id.det_Q);
            TextView  A = (TextView) view.findViewById(R.id.det_A);
            TextView  realA = (TextView) view.findViewById(R.id.det_real);
            TextView  det_q_text = (TextView) view.findViewById(R.id.det_q_text);
            det_count.setText((i+1)+". ");
            if(i>show_que_list.size()+1){
                Q.setText("");
            }else{
                Q.setText("題目 :" +show_que_list.get(i).toString());
            }


            if(i>show_realans_list.size()+1){
                A.setText("");
            }else{
                A.setText("您 : "+ show_realans_list.get(i).toString());
            }
            if(i>show_ans_list.size()+1){
                realA.setText("");
            }else{
                realA.setText("正確: "+ show_ans_list.get(i).toString());
            }
            if(i>show_que_text_list.size()+1){
                det_q_text.setText("");
            }else{
                det_q_text.setText("敘述 : \r\n"+ show_que_text_list.get(i).toString());
            }

            return view;
        }
    }


}
