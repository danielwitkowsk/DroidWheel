package org.idk.droid_wheel;


import java.net.*;
import java.nio.charset.StandardCharsets;

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

    public void pre_send(float[] values) {
        float[] test=new float[3];
        double degree=(values[0]+Math.PI)*(360/(2*Math.PI));
        degree = (degree + postStart.offset)%360;
        if (degree < 0) degree = 360 +degree;
        postStart.img.setRotation(-(float)degree);
        byte[] mess = (String.valueOf((int)degree)).getBytes(StandardCharsets.UTF_8);
        send(mess);
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