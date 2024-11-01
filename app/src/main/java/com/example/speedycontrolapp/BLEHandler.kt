package com.example.speedycontrolapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import java.util.UUID

class BLEHandler(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = context.getSystemService(ComponentActivity.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private var bluetoothGatt: BluetoothGatt? = null
    private val characteristicUUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")
    private val serviceUUID = UUID.fromString("536d0764-717d-4a13-8825-58cc690213d2")
    val scannedDevices = mutableListOf<BluetoothDevice>()

    fun connectDevice(deviceAddress: String) {
        val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            // Manejar cambio de estado de conexión
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            // Manejar descubrimiento de servicios
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            // Manejar escritura de característica
        }
    }

    fun sendCharacter(character: Char) {
        val characteristic = bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicUUID)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        if (characteristic != null) {
            characteristic.value = byteArrayOf(character.code.toByte())
            bluetoothGatt?.writeCharacteristic(characteristic)
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device: BluetoothDevice = result.device
            Log.i("BLEHandler", "Device found: ${device.name} - ${device.address}")
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            if (!scannedDevices.contains(device)) {
                scannedDevices.add(device)
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            for (result in results) {
                val device: BluetoothDevice = result.device
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                Log.i("BLEHandler", "Device found: ${device.name} - ${device.address}")
            // Aquí puedes manejar la lógica para seleccionar el dispositivo
            }
        }
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e("BLEHandler", "Scan failed with error code: $errorCode")
        }
    }

    fun startScan() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        if (bluetoothAdapter.isEnabled) {
            bluetoothAdapter.bluetoothLeScanner.startScan(scanCallback)
        }else {
            Log.e("BLEHandler", "Bluetooth not enabled")
        }
    }

    fun stopScan() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
    }
}
