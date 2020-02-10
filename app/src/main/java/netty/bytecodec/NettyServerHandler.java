package netty.bytecodec;

import java.io.UnsupportedEncodingException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {

            ByteBuf buf = (ByteBuf) msg;

            String recieved = getMessage(buf);
            System.out.println("服务器接收到客户端消息：" + recieved);

            try {
                //具有写数据，已释放资源
                ctx.writeAndFlush(getSendByteBuf("你好，客户端"));
                System.out.println("服务器回复消息：你好，客户端");

//                ctx.writeAndFlush(getSendByteBuf("你好，客户端")).addListener(ChannelFutureListener.CLOSE);//客户端接收到数据之后关闭连接，必须在服务端操作；类似短链接，一次读写完成之后关闭连接
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        /*
         * 从ByteBuf中获取信息 使用UTF-8编码返回
         */
        private String getMessage(ByteBuf buf) {

            byte[] con = new byte[buf.readableBytes()];
            buf.readBytes(con);
            try {
                return new String(con, "UTF8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }

        private ByteBuf getSendByteBuf(String message)
                throws UnsupportedEncodingException {

            byte[] req = message.getBytes("UTF-8");
            ByteBuf pingMessage = Unpooled.buffer();
            pingMessage.writeBytes(req);

            return pingMessage;
        }
    }