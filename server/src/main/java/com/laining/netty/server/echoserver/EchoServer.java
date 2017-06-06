package com.laining.netty.server.echoserver;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

public class EchoServer {
	
	private final int port;

	public EchoServer(int port) {
		super();
		this.port = port;
	}
	
	public void start() throws InterruptedException{
		final EchoServerHandler echoServerHandler = new EchoServerHandler();
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		try{
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(eventLoopGroup).localAddress(new InetSocketAddress(port)).childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel paramC) throws Exception {
					paramC.pipeline().addLast(echoServerHandler);
				}
			});
			ChannelFuture future = serverBootstrap.bind().sync();
			future.channel().closeFuture().sync();
		}finally {
			eventLoopGroup.shutdownGracefully().sync();
		}
		
	}



	public static void main(String[] args) throws InterruptedException {
		int localPort = 9999;
		if(args.length == 1){
			localPort = Integer.parseInt(args[0]);
		}
		new EchoServer(localPort).start();

	}

}
