package com.mcdz.seeip;

import android.graphics.drawable.Icon;
import android.os.Handler;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

public class TileView extends TileService {

    Tile tile;
    boolean sw = true;

    @Override
    public void onClick() {
        super.onClick();
        displayIp();
        sw = !sw;
    }

    @Override
    public void onStartListening() {
        tile = getQsTile();
        tile.setIcon(Icon.createWithResource(this,
                R.mipmap.ic_ip3));
        tile.setLabel(getString(R.string.tile_fetching_text));
        tile.setState(Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    // will be cleaning up this method soon
    public void displayIp() {
        if (sw) {
            tile.setIcon(Icon.createWithResource(TileView.this,
                    R.mipmap.ic_web));
            tile.setLabel("Local..");
            tile.setState(Tile.STATE_ACTIVE);
            getQsTile().updateTile();
            new Thread(new Runnable() {
                public void run() {
                    android.os.SystemClock.sleep(240);
                    if (!getLocalWifiIpAddress().equals("0.0.0.0")) {
                        getQsTile().setLabel(getLocalWifiIpAddress());
                        getQsTile().setIcon(Icon.createWithResource(TileView.this,
                                R.mipmap.ic_ip3));
                    } else {
                        getQsTile().setLabel("0.0.0.0");
                        getQsTile().setIcon(Icon.createWithResource(TileView.this,
                                R.mipmap.ic_nonet));
                    }
                    tile.setState(Tile.STATE_INACTIVE);
                    getQsTile().updateTile();
                }
            }).start();
        } else {
            tile.setIcon(Icon.createWithResource(this,
                    R.mipmap.ic_wait));
            tile.setState(Tile.STATE_INACTIVE);
            getQsTile().updateTile();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getQsTile().setLabel("Public…");
                    tile.setIcon(Icon.createWithResource(TileView.this,
                            R.mipmap.ic_ip3));
                    tile.setState(Tile.STATE_ACTIVE);

                    getQsTile().updateTile();
                    try {
                        try {
                            getQsTile().setLabel(getPublicIpAddress());
                            tile.setIcon(Icon.createWithResource(TileView.this,
                                    R.mipmap.ic_web));
                            tile.setState(Tile.STATE_INACTIVE);
                            getQsTile().updateTile();
                        } catch (IOException e) {
                            e.printStackTrace();
                            getQsTile().setLabel("No Network");
                            tile.setIcon(Icon.createWithResource(TileView.this,
                                    R.mipmap.ic_nonet));
                            tile.setState(Tile.STATE_INACTIVE);
                            getQsTile().updateTile();
                        }
                        getQsTile().updateTile();   // unnecessary.
                    } catch (NullPointerException e) {
                        getQsTile().setLabel("No Network");
                        tile.setIcon(Icon.createWithResource(TileView.this,
                                R.mipmap.ic_nonet));
                        tile.setState(Tile.STATE_INACTIVE);
                        getQsTile().updateTile();
                    } catch (Exception e) {
                        e.printStackTrace();
                        tile.setIcon(Icon.createWithResource(TileView.this,
                                R.mipmap.ic_error));
                        getQsTile().setLabel("Error");
                        tile.setState(Tile.STATE_INACTIVE);
                        getQsTile().updateTile();
                    }
                }
            });
            thread.start();
        }
    }

    // found this on web
    private String getLocalWifiIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int ipAddress;
        if (wifiManager != null) {
            ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        } else {
            return "0.0.0.0";
        }

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            return "0.0.0.0";
        }
        try {
            return ipAddressString;
        } catch (NullPointerException e) {
            return "0.0.0.0";
        }
    }

    public String getPublicIpAddress() throws IOException {

        URL connection = new URL("http://checkip.amazonaws.com/");
        URLConnection con = connection.openConnection();
        String str;
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        str = reader.readLine();
        try {
            return str;
        } catch (NullPointerException e) {
            return "null";
        }

    }

}