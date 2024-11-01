package com.example.speedycontrolapp

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.speedycontrolapp.ui.theme.SpeedyControlAppTheme

class MainActivity : ComponentActivity() {
    private val REQUEST_ENABLE_BT = 1
    private lateinit var bleHandler: BLEHandler
    //val bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bleHandler = BLEHandler(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_ENABLE_BT)
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_ENABLE_BT)
            }

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH), REQUEST_ENABLE_BT)
            }
        }

        setContent {
            SpeedyControlAppTheme{
                BLEApp(applicationContext)
            }
        }

//        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
//            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT)
//        }

        // Iniciar el escaneo de dispositivos BLE
        bleHandler.startScan()
        Log.e("BLEScan", "iniciando escaneo")
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido
                } else {
                    // Permiso denegado
                }
            }
        }
    }

    @Composable fun BLEApp(context: Context) {
        var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }
        val devices = remember { mutableStateListOf<BluetoothDevice>() }

        LaunchedEffect(bleHandler.scannedDevices) {
            devices.clear()
            Log.e("BLEScan", "DispositivosADD")
            devices.addAll(bleHandler.scannedDevices)
        }

        Column( modifier = Modifier.fillMaxSize().padding(16.dp) ) {
            Button(onClick = {
                bleHandler.startScan()
                Log.e("BLEScan", "iniciando escaneo")
            }) {
                Text(text = "Scan")
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(bleHandler.scannedDevices) { device ->
                    DeviceItem(context = context, device = device, onClick = { selectedDevice = device })
                }
            }
            selectedDevice?.let { device ->
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                Text("Selected device: ${device.name} - ${device.address}")
                Button(onClick = { bleHandler.connectDevice(device.address) }) {
                    Text(text = "Connect")
                }
            }
            Button(onClick = { bleHandler.sendCharacter('A') }) {
                Text(text = "Send A")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { bleHandler.sendCharacter('B') }) {
                Text(text = "Send B")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { bleHandler.sendCharacter('C') }) {
                Text(text = "Send C")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { bleHandler.sendCharacter('D') }) {
                Text(text = "Send D")
            }
        }
    }

    @Composable fun DeviceItem(context: Context, device: BluetoothDevice, onClick: () -> Unit) {
        Column(modifier = Modifier.fillMaxWidth() .padding(8.dp)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            Text(text = device.name?: "Unnamed device", style = MaterialTheme.typography.headlineSmall)
            Text(text = device.address, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onClick) {
                Text(text = "Select")
            }
        }
    }
}
