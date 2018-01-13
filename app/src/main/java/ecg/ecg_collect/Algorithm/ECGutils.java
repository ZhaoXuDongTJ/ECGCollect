package ecg.ecg_collect.Algorithm;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 92198 on 2018/1/13.
 */

public class ECGutils {
    private static final int StartFlag = 0xFC;
    private static final int EndFlag = 0xFD;
    private static final int EscapeFlag = 0xEF;
    private static final int EscapeValue = 0x20;
    public static double[] getDataByPath(String path){
        ArrayList<Byte> dataBufList = new ArrayList<Byte>();
        List<Float> dataList = new ArrayList<Float>();

        try {
            FileInputStream fin = new FileInputStream(path);
            int length = fin.available();
            byte [] buffer = new byte[length];

            fin.read(buffer);
            fin.close();
            System.out.println(buffer.length);
            //i为读取字节下标
            int i = 0;

            for(; i<buffer.length; i++)
                if((buffer[i]&0xFF) == StartFlag)
                    break;
            for(; i<buffer.length; i++){
                if((buffer[i]&0xFF) == StartFlag){
                    continue;
                }else if((buffer[i]&0xFF) == EndFlag){
                    if(dataBufList.size()<10)
                        if(dataBufList.size()==3){
                            if((dataBufList.get(0))>0){
                                dataList.add((float)((dataBufList.get(0)&0xFF)<<16|(dataBufList.get(1)&0xFF)<<8|(dataBufList.get(2)&0xFF)));
                            }else{
                                dataList.add((float)((0xff << 24)|(dataBufList.get(0)&0xFF)<<16|(dataBufList.get(1)&0xFF)<<8|(dataBufList.get(2)&0xFF)));
                            }
                            dataBufList.clear();
                        }
                    continue;
                }else{
                    if((buffer[i]&0xFF) == EscapeFlag){
                        dataBufList.add((byte)((buffer[i++]&0xFF)^EscapeValue));
                    }else{
                        dataBufList.add(buffer[i]);
                    }
                }
            }
            buffer = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        double[] data = new double[dataList.size()];
        for(int j = 0; j<dataList.size(); j++){
            data[j]=dataList.get(j);
        }

        dataBufList.clear();
        dataList.clear();

        for(int i = 0;i<data.length;i++){
            data[i] = -(500-data[i]/450+1) - 300;
        }

        return data;
    }

    public static double[] getDataByByte(byte[] path){
        ArrayList<Byte> dataBufList = new ArrayList<Byte>();
        List<Float> dataList = new ArrayList<Float>();
        byte [] buffer = path;
        //i为读取字节下标
        int i = 0;
        for(; i<buffer.length; i++)
            if((buffer[i]&0xFF) == StartFlag)
                break;
        for(; i<buffer.length; i++){
            if((buffer[i]&0xFF) == StartFlag){
                continue;
            }else if((buffer[i]&0xFF) == EndFlag){
                if(dataBufList.size()<10)
                    if(dataBufList.size()==3){
                        if((dataBufList.get(0))>0){
                            dataList.add((float)((dataBufList.get(0)&0xFF)<<16|(dataBufList.get(1)&0xFF)<<8|(dataBufList.get(2)&0xFF)));
                        }else{
                            dataList.add((float)((0xff << 24)|(dataBufList.get(0)&0xFF)<<16|(dataBufList.get(1)&0xFF)<<8|(dataBufList.get(2)&0xFF)));
                        }
                        dataBufList.clear();
                    }
                continue;
            }else{
                if((buffer[i]&0xFF) == EscapeFlag){
                    dataBufList.add((byte)((buffer[i++]&0xFF)^EscapeValue));
                }else{
                    dataBufList.add(buffer[i]);
                }
            }
        }
        buffer = null;

        double[] data = new double[dataList.size()];
        for(int j = 0; j<dataList.size(); j++){
            data[j]=dataList.get(j);
        }

        dataBufList.clear();
        dataList.clear();

        for(int ii = 0;ii<data.length;ii++){
            data[ii] = -(500-data[ii]/450 + 1);
        }
        return data;
    }
}
