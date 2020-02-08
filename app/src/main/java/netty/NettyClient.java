//package com.dahua.netty;
//
//import java.nio.charset.Charset;
//import java.util.Date;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.string.StringDecoder;
//import io.netty.util.AttributeKey;
//import io.netty.util.concurrent.Future;
//import io.netty.util.concurrent.GenericFutureListener;
//
//public class NettyClient {
//    public static void main(String[] args) {
//        Bootstrap bootstrap = new Bootstrap();
//        NioEventLoopGroup group = new NioEventLoopGroup();
//        bootstrap.group(group)
//                .channel(NioSocketChannel.class)
//                .attr(AttributeKey.newInstance("clientName"), "nettyClient")
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
//                .option(ChannelOption.SO_KEEPALIVE, true)
//                .option(ChannelOption.TCP_NODELAY, true)
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new StringDecoder());
//                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
//                            @Override
//                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
//                                super.channelActive(ctx);
//                                System.out.println(new Date() + ":客户端写出的数据");
//                                for (int i = 0;i<10;i++){
//                                    ByteBuf buffer = getByteBuf(ctx);
//                                    ctx.channel().writeAndFlush(buffer);
//                                }
//                            }
//                        });
//                    }
//                });
//        connect(bootstrap, "127.0.0.1", 8080);
//    }
//
//    private static ByteBuf getByteBuf(ChannelHandlerContext ctx) {
//        byte[] bytes = "你好！".getBytes(Charset.forName("utf-8"));
//        ByteBuf buffer = ctx.alloc().buffer();
//        buffer.writeBytes(bytes);
//        return buffer;
//    }
//
//    private static void connect(final Bootstrap bootstrap, final String host, final int port) {
//        Channel channel = bootstrap.connect(host, port).addListener(new GenericFutureListener<Future<? super Void>>() {
//            @Override
//            public void operationComplete(Future<? super Void> future) throws Exception {
//                if (future.isSuccess()) {
//                    System.out.println("连接成功");
//                } else {
//                    System.out.println("连接失败");
//                    connect(bootstrap, host, port);
//                }
//            }
//        }).channel();
//        while (true) {
//            channel.writeAndFlush(new Date() + ":hello world!");
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}

package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import netty.stringcodec.BytesToStringDecoder;
import netty.stringcodec.MsgClientHandler;
import netty.stringcodec.StringToBytesEncoder;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .attr(AttributeKey.newInstance("clientName"), "nettyClient")
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO)); //日志打印
                            ch.pipeline().addLast(new LengthFieldPrepender(4));
                            ch.pipeline().addLast(new StringToBytesEncoder());
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            ch.pipeline().addLast(new BytesToStringDecoder());
                            ch.pipeline().addLast(new MsgClientHandler("客户端"));
//                            ch.pipeline().addLast(new NettyClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8000).sync();
            for (int i = 0;i<10;i++){

                channelFuture.channel().writeAndFlush("abcd");
            }
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

}
