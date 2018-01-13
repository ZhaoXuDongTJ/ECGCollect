package ecg.ecg_collect.blueTooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * Created by 92198 on 2018/1/5.
 */

public class BlueInfo {
    private BluetoothDevice mBDevice = null;
    private BluetoothSocket btSocket =null;

    public BlueInfo() {

    }

    public BlueInfo(BluetoothDevice mBDevice, BluetoothSocket btSocket) {
        this.mBDevice = mBDevice;
        this.btSocket = btSocket;
    }

    public BluetoothDevice getmBDevice() {
        return mBDevice;
    }

    public void setmBDevice(BluetoothDevice mBDevice) {
        this.mBDevice = mBDevice;
    }

    public BluetoothSocket getBtSocket() {
        return btSocket;
    }

    public void setBtSocket(BluetoothSocket btSocket) {
        this.btSocket = btSocket;
    }
}
