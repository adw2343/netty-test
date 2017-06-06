package com.laining.test.netty.client.echo;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class EchoClient {
	
	private final String host;
	private final int port;

	public EchoClient(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}
	
	public void start() throws Exception{
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		try{
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).remoteAddress(new InetSocketAddress(host, port)).handler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new EchoClientHanlder());
					
				}
			});
			ChannelFuture channelFuture = bootstrap.connect().sync();
			channelFuture.channel().closeFuture().sync();
		}finally {
			eventLoopGroup.shutdownGracefully().sync();
		}
	}




	public static void main(String[] args) throws Exception {
		int port = 9999;
		String host = "192.168.1.103";
		if(args.length == 2){
			port = Integer.parseInt(args[1]);
			host = args[0];
		}
		new EchoClient(host, port).start();

	}

}
