package com.mcdz.seeip;

import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.ByteOrder;


public class TileView extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        displayIp();
    }

    Tile tile;

    @Override
    public void onStartListening() {
        tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this,
                R.mipmap.ic_ip3));
        tile.setLabel(getString(R.string.tile_name));
        tile.setState(Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    boolean sw;

    public void displayIp() {
        sw = !sw;
        if (sw) {
            getQsTile().setLabel(getLocalWifiIpAddress());
            getQsTile().updateTile();
        } else {
            tile.setIcon(Icon.createWithResource(this,
                    R.mipmap.ic_public));
            getQsTile().updateTile();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        try {
                            getQsTile().setLabel(getPublicIpAddress());
                        } catch (IOException e) {
                            e.printStackTrace();
                            getQsTile().setLabel("Error");
                        }
                        getQsTile().updateTile();
                    } catch (Exception e) {
                        e.printStackTrace();
                        getQsTile().setLabel("Error");
                    }
                }
            });
            thread.start();
        }
    }

    // found this on web
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

    public String getPublicIpAddress() throws IOException {

        URL connection = new URL("http://checkip.amazonaws.com/");
        URLConnection con = connection.openConnection();
        String str;
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        str = reader.readLine();


        return str;
    }

}