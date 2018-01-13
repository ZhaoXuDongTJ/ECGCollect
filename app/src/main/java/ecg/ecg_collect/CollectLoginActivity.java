package ecg.ecg_collect;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ecg.ecg_collect.Algorithm.CommonUtils;
import ecg.ecg_collect.blueTooth.BlueInfo;

public class CollectLoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView collect_login_notice;
    private TextView collect_login_return;
    private TextView collect_login_next;
    private ImageView collect_login_image;

    private BlueInfo blueInfo = null;
    private globalPool globalPool = null;
    private BluetoothSocket btSocket =null;
    // store
    public byte[] Buffer = new byte[10*250*5*10];
    public int indexBuff = 0;
    private String filePath =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_login);
        init();
    }
    protected void init(){
        collect_login_notice = findViewById(R.id.collect_login_notice);
        collect_login_return = findViewById(R.id.collect_login_return);
        collect_login_next = findViewById(R.id.collect_login_next);
        collect_login_image = findViewById(R.id.collect_login_image);
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

                break;
            default:
                break;
        }
    }
    private class ConnectedThread extends Thread{
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            collect_login_notice.setText("状态 :保存");
                        }
                    });

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
}
