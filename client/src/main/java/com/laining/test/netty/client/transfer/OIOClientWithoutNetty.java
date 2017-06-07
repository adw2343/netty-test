package com.laining.test.netty.client.transfer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

public class OIOClientWithoutNetty {

    public static void main(String[] args) {
        new OIOClientWithoutNetty().send("192.168.1.103", 8888);
    }

    public void send(String host, int port) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            outputStream = socket.getOutputStream();
            outputStream.write("Sb!/r/n".getBytes(Charset.forName("UTF-8")));
            outputStream.flush();
            socket.shutdownOutput();

            inputStream = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String info = null;
            while ((info = br.readLine()) != null) {
                System.out.println("Hello,我是客户端，服务器说：" + info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (socket != null)
                    socket.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

}
