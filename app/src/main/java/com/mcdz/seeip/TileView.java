package com.mcdz.seeip;

import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import android.net.wifi.WifiManager;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;


@RequiresApi(api = Build.VERSION_CODES.N)
public class TileView extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        displayIp();
    }

    Tile tile;
    Icon icon;

    @Override
    public void onStartListening() {
        tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this,
                R.mipmap.ic_ip3));
        tile.setLabel(getString(R.string.tile_name));
        tile.setState(Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    public void displayIp() {
        getQsTile().setLabel(getLocalWifiIpAddress());
//        toastMessage(getLocalWifiIpAddress());
        getQsTile().updateTile();
    }


    private String getLocalWifiIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            ipAddressString = null;
        }

        return ipAddressString;
    }

    public void toastMessage(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }
}