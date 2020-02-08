package netty.stringcodec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class MsgServerHandler extends ChannelInboundHandlerAdapter {

    String name;

    public MsgServerHandler(String name) {
        this.name = name;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = (String) msg;
        System.out.println("服务端收到:" + str);
        ctx.writeAndFlush("abcd");
        //释放msg
        ReferenceCountUtil.release(msg);
    }
}