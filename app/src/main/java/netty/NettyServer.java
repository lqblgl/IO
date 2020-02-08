//package com.dahua.netty;
//
//import java.io.UnsupportedEncodingException;
//
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.string.StringDecoder;
//import io.netty.util.AttributeKey;
//import io.netty.util.concurrent.Future;
//import io.netty.util.concurrent.GenericFutureListener;
//
//public class NettyServer {
//    public static void main(String[] args) {
//        ServerBootstrap serverBootstrap = new ServerBootstrap();
//        NioEventLoopGroup boos = new NioEventLoopGroup();
//        NioEventLoopGroup worker = new NioEventLoopGroup();
//        serverBootstrap
//                .group(boos, worker)
//                .channel(NioServerSocketChannel.class)
//                .option(ChannelOption.SO_BACKLOG, 1024)// 连接数
//                .childOption(ChannelOption.TCP_NODELAY, true)// 不延迟，消息立即发送
//                .childOption(ChannelOption.SO_KEEPALIVE, true)// 长连接
//                .attr(AttributeKey.newInstance("serverName"), "nettyServer")
//                .handler(new ChannelInitializer<NioServerSocketChannel>() {
//                    @Override
//                    protected void initChannel(NioServerSocketChannel ch) throws Exception {
//                        System.out.println("服务器启动中");
//                    }
//                })
//                .childHandler(new ChannelInitializer<NioSocketChannel>() {
//                    @Override
//                    protected void initChannel(NioSocketChannel ch) throws Exception {
//                        ch.pipeline().addLast(new StringDecoder());
//                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
//                            @Override
//                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
//                                System.out.println(msg);
//                            }
//                        });
//                    }
//                });
//        bind(serverBootstrap, 8000);
//    }
//
//    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
//        serverBootstrap.bind(port).addListener(new GenericFutureListener<Future<? super Void>>() {
//            @Override
//            public void operationComplete(Future<? super Void> future) throws Exception {
//                if (future.isSuccess()) {
//                    System.out.println("端口" + port + "绑定成功");
//                } else {
//                    System.out.println("端口" + port + "绑定失败");
//                    bind(serverBootstrap, port + 1);
//                }
//            }
//        });
//    }
//}

package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import netty.stringcodec.BytesToStringDecoder;
import netty.stringcodec.MsgServerHandler;
import netty.stringcodec.StringToBytesEncoder;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(boos, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)// 连接数
                    .childOption(ChannelOption.TCP_NODELAY, true)// 不延迟，消息立即发送
                    .childOption(ChannelOption.SO_KEEPALIVE, true)// 长连接
                    .attr(AttributeKey.newInstance("serverName"), "nettyServer")
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO)); //日志打印
                            ch.pipeline().addLast(new LengthFieldPrepender(4));
                            ch.pipeline().addLast(new StringToBytesEncoder());
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            ch.pipeline().addLast(new BytesToStringDecoder());
                            ch.pipeline().addLast(new MsgServerHandler("服务端"));
//                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(8000).sync();
            // 等待服务端关闭连接
            channelFuture.channel().closeFuture().sync();
        }finally {
            boos.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}

