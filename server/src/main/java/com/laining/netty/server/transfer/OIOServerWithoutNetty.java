package com.laining.netty.server.transfer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class OIOServerWithoutNetty {
	
	public void serve(int port) throws IOException{
		final ServerSocket serverSocket = new ServerSocket(port);
		try{
			for(;;){
				final Socket clientSocket = serverSocket.accept(); //will block
				System.out.println("Accepted connection from " + clientSocket);
				new Thread(() -> {
					OutputStream out = null;
					try {
						out = clientSocket.getOutputStream();
						out.write("Hi!\r\n".getBytes(Charset.forName("UTF-8")));
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						try {
							out.close();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					
				}).start();
			}
				
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		try {
			new OIOServerWithoutNetty().serve(8888);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
