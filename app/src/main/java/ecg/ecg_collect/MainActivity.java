package ecg.ecg_collect;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import ecg.ecg_collect.blueTooth.BlueInfo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private TextView notice;
    private ImageButton BlueCheckImage;
    private ImageButton RegisterBtm;
    private ImageButton LoginBtm;

    private globalPool mGP = null;

    boolean isConnected=false;
    boolean isSocket=false;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice mBDevice = null;
    private BluetoothSocket btSocket =null;
    private BlueInfo blueInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 蓝牙模块不存在
        if (null == mBluetoothAdapter){ //系统中不存在蓝牙模块
            Toast.makeText(this, "Bluetooth module not found", Toast.LENGTH_LONG).show();
            notice.setText("Bluetooth module not found");
            this.finish();
        }
        initView();
        // 扫描 蓝牙
        blueProcess();

    }

    public void initView(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        notice = findViewById(R.id.notice);
        BlueCheckImage = findViewById(R.id.BlueCheckImage);
        RegisterBtm = findViewById(R.id.RegisterBtm);
        LoginBtm = findViewById(R.id.LoginBtm);
        RegisterBtm.setOnClickListener(this);
        LoginBtm.setOnClickListener(this);

        //本地数据库
        LitePal.getDatabase();
        // pool
        this.mGP = ((globalPool)this.getApplicationContext());

    }

    public void blueProcess(){
        if (!mBluetoothAdapter.isEnabled()) {
            //若没打开则打开蓝牙    直接打开
            mBluetoothAdapter.enable();
        }
        // 注册 监听器
        IntentFilter filter =new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver,filter);
        IntentFilter filter1=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver,filter1);

        mBluetoothAdapter.startDiscovery();
    }

    // 定义广播接收器
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_FOUND)){

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(MainActivity.this, device.getName(), Toast.LENGTH_LONG).show();
                try {
                    if(device.getName().equals("HMSoft")){
                        String sMAC = device.getAddress();

                        if  (device.getBondState() == BluetoothDevice.BOND_BONDED){
                            //已存在配对关系，建立与远程设备的连接
                            mBDevice = mBluetoothAdapter.getRemoteDevice(sMAC);
                            isConnected=true;// 用于后面的两个按钮
                        }
                    }
                }catch (RuntimeException e){}
            }else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                Toast.makeText(MainActivity.this, "搜索完成...", Toast.LENGTH_LONG).show();
                unregisterReceiver(mReceiver);
                if(mBDevice!=null){
                    new connectBlueSocket().start();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notice.setText(mBDevice.getName()+" "+mBDevice.getAddress());
                            BlueCheckImage.setImageResource(R.drawable.iright);
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notice.setText("未发现蓝牙设备");
                        }
                    });
                }
            }
        }
    };

    class connectBlueSocket extends Thread{
        @Override
        public void run() {
            try {
                btSocket = mBDevice.createInsecureRfcommSocketToServiceRecord(mBDevice.getUuids()[0].getUuid());
                isSocket = true;
                blueInfo = new BlueInfo(mBDevice,btSocket);
                mGP.blueInfo=blueInfo;
            } catch (IOException e) {
                new connectBlueSocket().start();
                isSocket = false;
                Toast.makeText(MainActivity.this, "通信接口失败...重新连接中", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);                                            // 显示 菜单
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                                         // 菜单 事件
        switch (item.getItemId()){
            case R.id.BarSearch:
                Toast.makeText(MainActivity.this, "重新搜索开始...", Toast.LENGTH_LONG).show();
                blueProcess();
                break;
            case R.id.BarManage:
                startActivity(new Intent(MainActivity.this,UserManageActivity.class));
                break;
            case R.id.BarBack:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override            // 2个监听
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.RegisterBtm:
//                if(isConnected&&isSocket){
                    final ProgressDialog dialog2 = ProgressDialog.show(this, "请准备", "您有3秒的准备时间");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            dialog2.cancel();
                            startActivity(new Intent(MainActivity.this,CollectResgisterActivity.class));
                        }
                    },3000);
//                }
                break;
            case R.id.LoginBtm:
                if(isConnected&&isSocket){
                    final ProgressDialog dialog3 = ProgressDialog.show(this, "请准备", "您有3秒的准备时间");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            dialog3.cancel();
                            startActivity(new Intent(MainActivity.this,CollectLoginActivity.class));
                        }
                    },3000);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        Log.e("D e story","解除注册");
    }
}
