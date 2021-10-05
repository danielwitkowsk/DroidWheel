package org.idk.droid_wheel;


import java.net.*;

public class UDPWorker {
    InetAddress local = null;
    int server_port = 12345;

    DatagramSocket s = null;

    public UDPWorker(){
        try {
            s = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void setHost(String host){
        try {
            local = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void setPort(int port) {
        server_port = port;
    }

    public void send(final byte[] mess) {
        new Thread(){
            @Override
            public void run(){
                try {
                    DatagramPacket p = new DatagramPacket(mess, mess.length, local, server_port);
                    s.send(p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}