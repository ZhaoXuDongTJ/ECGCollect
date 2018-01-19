package ecg.ecg_collect.Algorithm;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ecg.ecg_collect.Algorithm.Filter.DigitalFilter;
import ecg.ecg_collect.Algorithm.Filter.LowPassFilter;
import ecg.ecg_collect.Algorithm.Filter.MovingAverageFilter;

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

    // 计算余弦
    public static double getSimlary2(double[] host, double [] guest){

        int len = host.length<guest.length?host.length:guest.length;

        double a = MathModule2(host,len);
        double b = MathModule2(guest,len);
        double c = a*b;
        return dot2(host,guest,len)/Math.sqrt(c);
    }

    public static double MathModule2(double [] t,int len){
        double sum=0.0;
        for(int i=0;i<len;i++){
            sum += t[i]*t[i];
        }
        return sum;
    }

    public static double dot2(double[] host, double [] guest,int len){
        double nn=0;
        for(int i=0;i<len;i++){
            nn += host[i]*guest[i];
        }
        return nn;
    }

    public static double[] filterWave(double[] data){
        //数字滤波，去基线偏移
        double[] datatemp = new double[data.length];
        double k = 0.7;
        int samplerate = 250;
        double fc = 0.8 / samplerate;
        double[] a = new double[2];
        double[] b = new double[2];
        double alpha = (1 - k * Math.cos(2 * Math.PI * fc) - Math.sqrt(2 * k * (1 - Math.cos(2 * Math.PI * fc)) - Math.pow(k, 2) * Math.pow(Math.sin(2 * Math.PI * fc), 2))) / (1 - k);
        a[0] = 1 - alpha;
        a[1] = 0;
        b[0] = 1;
        b[1] = -1 * alpha;
        DigitalFilter digitalFilter = new DigitalFilter(a, b, data, 1d);
        datatemp = digitalFilter.zeroFilter();

        for(int i= 0; i<data.length; i++){
            data[i] = data[i]-datatemp[i];
        }

        //低通滤波
        LowPassFilter lowPassFilter = new LowPassFilter();
        data = lowPassFilter.lowPassFilter(data);
        //滑动平均滤波
        MovingAverageFilter movingAverageFilter = new MovingAverageFilter();
        data = movingAverageFilter.filter(data);

        return data;

    }
}
