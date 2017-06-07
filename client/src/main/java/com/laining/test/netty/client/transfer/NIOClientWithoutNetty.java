package com.laining.test.netty.client.transfer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class NIOClientWithoutNetty {
    private Executor executor = Executors.newFixedThreadPool(5);
    private BlockingQueue<String> messages = new LinkedBlockingQueue<>();

    public static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public void connect(String host, int port) throws IOException {
        SocketChannel client = SocketChannel.open();
        client.configureBlocking(false);
        client.connect(new InetSocketAddress(host, port));
        Selector selector = Selector.open();
        client.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        executor.execute(() -> {
            while (true) {
                try {
                    if (selector.select(20) == 0) {
                        continue;
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey sKey = iterator.next();
                        iterator.remove();
                        if (sKey.isConnectable()) {
                            SocketChannel socketChannel = (SocketChannel) sKey.channel();
                            socketChannel.finishConnect();
                            System.out.println("Conneced to server!");
                            // socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        }
                        if (sKey.isWritable()) {
                            SocketChannel wChannel = (SocketChannel) sKey.channel();
                            String requestMsg = messages.take();
                            ByteBuffer byteBuffer = ByteBuffer.wrap(requestMsg.getBytes(Charset.forName("UTF-8")));
                            while (byteBuffer.hasRemaining()) {
                                if (wChannel.write(byteBuffer) == 0) {
                                    break;
                                }
                            }
                        }
                        if (sKey.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) sKey.channel();
                            ByteBuffer tmp = ByteBuffer.allocate(1024);
                            int len = -1;
                            byte[] data = new byte[0];
                            if ((len = socketChannel.read(tmp)) > 0) {
                                data = Arrays.copyOf(data, data.length + len);
                                System.arraycopy(tmp.array(), 0, data, data.length - len, len);
                                tmp.rewind();
                            }
                            if (data.length > 0) {
                                String recived = new String(data);
                                System.out.println("Client received:" + recived);
                            }
                        }
                    }
                } catch (Exception e) {
                    try {
                        selector.close();
                        client.close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        });
    }

    class SendTask implements Runnable {

        private final SocketChannel client;

        public SendTask(SocketChannel client) {
            super();
            this.client = client;
        }

        @Override
        public void run() {
            String requestMsg;
            while (true) {
                try {
                    requestMsg = messages.take();
                    ByteBuffer byteBuffer = ByteBuffer.wrap(requestMsg.getBytes(Charset.forName("UTF-8")));
                    while (byteBuffer.hasRemaining()) {
                        if (client.write(byteBuffer) == 0) {
                            break;
                        }
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                    try {
                        client.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }

        }
    }

    public void send(String message) {
        messages.add(message);
    }

    public static void main(String[] args) {
        NIOClientWithoutNetty clientWithoutNetty = new NIOClientWithoutNetty();
        try {
            clientWithoutNetty.connect("192.168.1.103", 8888);
            clientWithoutNetty.send("Sb!\r\n");
            clientWithoutNetty.send("Hello!\r\n");
            clientWithoutNetty.send("HaHa!\r\n");
            new Thread(() -> {
                Random random = new Random();
                while (true) {
                    String message = CHARS.substring(random.nextInt(CHARS.length()), CHARS.length());
                    clientWithoutNetty.send(message + "\r\n");
                }
            }).start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
