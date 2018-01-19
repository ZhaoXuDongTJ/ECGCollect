package ecg.ecg_collect;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ecg.ecg_collect.Algorithm.CommonUtils;
import ecg.ecg_collect.Algorithm.ECGutils;
import ecg.ecg_collect.LiteSQL.signListLite;
import ecg.ecg_collect.LiteSQL.userLite;
import ecg.ecg_collect.blueTooth.BlueInfo;

public class CollectLoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView collect_login_notice;
    private TextView collect_login_return;
    private TextView collect_login_next;
    private TextView collect_login_time;
    private ImageView collect_login_image;

    private BlueInfo blueInfo = null;
    private globalPool globalPool = null;
    private BluetoothSocket btSocket =null;
    // store
    public byte[] Buffer = new byte[10*250*5*10];
    public int indexBuff = 0;
    private String filePath =null;
    // 是否可以签到
    private boolean isSign = false;
    private boolean canSign = true;
    private userLite us = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_login);
        init();

        if(btSocket.isConnected()){
            final ConnectedThread con = new ConnectedThread(btSocket);
            con.start();
            new Timer().schedule(new TimerTask() {
                int flag = 10;
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            collect_login_time.setText("倒计时："+(flag--));
                        }
                    });
                    if(flag==0){
                        new SaveFile().start();
                        con.isCollect = false;
                        con.interrupt();
                        //  修改UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                collect_login_time.setVisibility(View.GONE);
                                collect_login_notice.setVisibility(View.VISIBLE);
                            }
                        });
                        // 开一个查询任务
                        new Query().start();
                    }
                }
            },0,1000);
        }

    }
    protected void init(){
        collect_login_notice = findViewById(R.id.collect_login_notice);
        collect_login_return = findViewById(R.id.collect_login_return);
        collect_login_next = findViewById(R.id.collect_login_next);
        collect_login_image = findViewById(R.id.collect_login_image);
        collect_login_time = findViewById(R.id.collect_login_time);
        collect_login_notice.setOnClickListener(this);
        collect_login_return.setOnClickListener(this);
        collect_login_next.setOnClickListener(this);

        globalPool = (globalPool) this.getApplicationContext();
        blueInfo = globalPool.blueInfo;
        btSocket = blueInfo.getBtSocket();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.collect_login_notice:
                break;
            case R.id.collect_login_return:
                finish();
                break;
            case R.id.collect_login_next:
                if(isSign&&canSign){
                    // 签到表
                    signListLite sign = new signListLite();
                    sign.setDataAddress(us.getDataAddress());
                    sign.setObjectID(us.getObjectID());
                    sign.setName(us.getUserName());
                    sign.setTime(new Date());
                    sign.save();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            collect_login_notice.setText("成功签到");
                        }
                    });
                    canSign = false;
                }else {
                    if(canSign){

                    }else {
                        Toast.makeText(CollectLoginActivity.this,"已经签到",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }
    public class ConnectedThread extends Thread{
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        public boolean isCollect = true;
        public ConnectedThread(BluetoothSocket socket){
            this.socket=socket;
            InputStream input = null;
            OutputStream output = null;
            try{
                input = socket.getInputStream();
                output = socket.getOutputStream();
            }catch (IOException e){
                e.printStackTrace();
            }
            this.inputStream = input;
            this.outputStream = output;
        }
        @Override
        public void run() {
            byte[] InnerBuff = new byte[5*10];
            while (isCollect){
                synchronized (this){
                    try {
                        inputStream.read(InnerBuff,0,5*10);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (int i=0;i<50;i++){
                        Buffer[indexBuff++]=InnerBuff[i];
                    }
                }
            }
        }
        public void write(byte[] bytes) throws IOException {
            outputStream.write(bytes);
        }
        public void concel() throws IOException {
            socket.close();
        }
    }

    class SaveFile extends Thread{
        @Override
        public void run() {
            synchronized (this){
                // 临时数据地址
                filePath = CommonUtils.getTempHeartPath();
                FileOutputStream outputStreams;
                File file = new File(filePath);

                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CollectLoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                try {
                    outputStreams = new FileOutputStream(file,true);
                    outputStreams.write(Buffer,0,indexBuff);
                    outputStreams.flush();
                    outputStreams.close();
                } catch (FileNotFoundException e) {
                    Toast.makeText(CollectLoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Query extends Thread{
        @Override
        public void run() {
            List<userLite> userLites = DataSupport.findAll(userLite.class);
            double [] SimilarityDegree = new double[userLites.size()];
            int index=0;
            userLite temUser = userLites.get(0);
            double temUserDouble = 0.0;
            double[] tempData = ECGutils.getDataByPath(filePath);
            tempData = ECGutils.filterWave(tempData);
            for(userLite us : userLites){
                String address =  us.getDataAddress();
                double[] originData = ECGutils.getDataByPath(address);
                originData = ECGutils.filterWave(originData);
                double n = ECGutils.getSimlary2(originData,tempData);
                Toast.makeText(CollectLoginActivity.this,"相似度："+n,Toast.LENGTH_SHORT).show();
                SimilarityDegree[index++] = n;
                if(n>temUserDouble){
                    temUserDouble = n;
                    temUser = us;
                }
            }
            if(temUserDouble<0.5){
                isSign = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        collect_login_notice.setText("无匹配，请未注册！");
                    }
                });
            }else {
                isSign = true;
                File outputImage = new File(temUser.getUserPicAddress());
                Uri imageUri;
                if(Build.VERSION.SDK_INT>=24){
                    imageUri = FileProvider.getUriForFile(CollectLoginActivity.this,"com.example.cameraalbumtest.fileprovider",outputImage);
                }else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap finalBitmap = bitmap;
                final userLite finalTemUser = temUser;
                us = finalTemUser;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        collect_login_notice.setText(finalTemUser.getUserName());
                        collect_login_image.setImageBitmap(finalBitmap);
                    }
                });
            }
        }
    }
}
