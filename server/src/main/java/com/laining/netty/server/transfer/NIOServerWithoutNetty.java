package com.laining.netty.server.transfer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class NIOServerWithoutNetty {

	public void serve(int port) throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		ServerSocket serverSocket = serverSocketChannel.socket();
		InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
		serverSocket.bind(inetSocketAddress);
		Selector selector = Selector.open();
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		for (;;) {
			try {
				selector.select();
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
			Set<SelectionKey> readyKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = readyKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				iterator.remove();
				try {
					if (key.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel client = server.accept();
						client.configureBlocking(false);
						client.register(key.selector(), SelectionKey.OP_READ | SelectionKey.OP_WRITE);
						System.out.println("Accepted connection from " + client);
					}
					if (key.isWritable()) {
						SocketChannel client = (SocketChannel) key.channel();
						ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes(Charset.forName("UTF-8")));
						while (msg.hasRemaining()) {
							if (client.write(msg) == 0) {
								break;
							}
						}
					}
					if (key.isReadable()) {
						SocketChannel sc = (SocketChannel) key.channel();
						ByteBuffer buf = ByteBuffer.allocate(1024);
						int len = -1;
						while ((len = sc.read(buf)) > 0) {
							byte[] data = new byte[0];
							data = Arrays.copyOf(data, len);
							System.arraycopy(buf.array(), 0, data, data.length - len, len);
							buf.rewind();
							if (data.length > 0) {
								System.out.print("Server received:" + new String(data));
							}
						}

					}
				} catch (IOException e) {
					e.printStackTrace();
					key.cancel();
					try {
						key.channel().close();
					} catch (IOException ioe) {

					}
				}
			}
		}

	}

	public static void main(String[] args) {
		try {
			new NIOServerWithoutNetty().serve(8888);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
