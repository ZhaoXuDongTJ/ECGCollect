package ecg.ecg_collect;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import ecg.ecg_collect.Algorithm.CommonUtils;
import ecg.ecg_collect.LiteSQL.userLite;
import ecg.ecg_collect.blueTooth.BlueInfo;

import static ecg.ecg_collect.Algorithm.CommonUtils.getImagePath;

public class CollectResgisterActivity extends AppCompatActivity implements View.OnClickListener{
// UI
    private TextView collect_register_notice;
    private TextView collect_register_return;
    private TextView collect_register_next;
    private ImageView collect_register_image;
    private EditText collect_register_name;
// BlueToo
    private BlueInfo blueInfo = null;
    private globalPool globalPool = null;
    private BluetoothSocket btSocket =null;
// store
    public byte[] Buffer = new byte[10*250*5*10];
    public int indexBuff = 0;
    private String filePath =null;
// image photo
    public static final int TAKE_PHOTO = 1;
    private String ImageFilePath;
    private Uri imageUri;
    private File outputImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_resgister);
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
                            collect_register_notice.setText(collect_register_notice.getText()+""+(flag--));
                        }
                    });
                    if(flag==0){
                        new SaveFile().start();
                        con.isCollect = false;
                        con.interrupt();
                    }
                }
            },0,1000);
        }
    }
    public void init(){
        collect_register_notice = findViewById(R.id.collect_register_notice);
        collect_register_return= findViewById(R.id.collect_register_return);
        collect_register_next = findViewById(R.id.collect_register_next);
        collect_register_image = findViewById(R.id.collect_register_image);
        collect_register_name = findViewById(R.id.collect_register_name);
        collect_register_notice.setOnClickListener(this);
        collect_register_return.setOnClickListener(this);
        collect_register_next.setOnClickListener(this);
        collect_register_image.setOnClickListener(this);

        globalPool = (globalPool) this.getApplicationContext();
        blueInfo = globalPool.blueInfo;
        btSocket = blueInfo.getBtSocket();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.collect_register_notice:
                break;
            case R.id.collect_register_return:
                fileList();
                break;
            case R.id.collect_register_next:
                userLite us = new userLite();
                us.setObjectID(Math.floor(Math.random()*10000)+"");
                us.setUserName(collect_register_name.getText().toString());
                us.setDataAddress(filePath);
                us.setUserPicAddress(ImageFilePath);
                us.save();
                Toast.makeText(CollectResgisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CollectResgisterActivity.this,MainActivity.class));
                break;
            case R.id.collect_register_image:
                ImageFilePath = getImagePath();
                File file = new File(ImageFilePath);
                if(!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                outputImage = new File(ImageFilePath);
                try {
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri = FileProvider.getUriForFile(CollectResgisterActivity.this,"com.example.cameraalbumtest.fileprovider",outputImage);
                }else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
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
                filePath = CommonUtils.getHeartPath();
                FileOutputStream outputStreams;
                File file = new File(filePath);

                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CollectResgisterActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                try {
                    outputStreams = new FileOutputStream(file,true);
                    outputStreams.write(Buffer,0,indexBuff);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            collect_register_notice.setText("状态 :保存");
                        }
                    });

                    outputStreams.flush();
                    outputStreams.close();
                } catch (FileNotFoundException e) {
                    Toast.makeText(CollectResgisterActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                collect_register_image.setImageBitmap(bitmap);
                break;
            default:
                break;
        }
    }
}
