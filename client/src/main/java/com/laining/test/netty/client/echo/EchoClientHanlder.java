package com.laining.test.netty.client.echo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;


@ChannelHandler.Sharable
public class EchoClientHanlder extends SimpleChannelInboundHandler<ByteBuf> {

	

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(Unpooled.copiedBuffer("Hello sb!", CharsetUtil.UTF_8));
	}

	@Override
	protected void channelRead0(ChannelHandlerContext paramChannelHandlerContext, ByteBuf paramI) throws Exception {
		System.out.println("Client received:" + paramI.toString(CharsetUtil.UTF_8));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	

}
