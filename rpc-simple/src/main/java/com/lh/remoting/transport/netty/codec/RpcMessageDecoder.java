package com.lh.remoting.transport.netty.codec;

import com.lh.compress.Compress;
import com.lh.enums.CompressTypeEnum;
import com.lh.enums.SerializationTypeEnum;
import com.lh.extension.ExtensionLoader;
import com.lh.remoting.constants.RpcConstants;
import com.lh.remoting.dto.RpcMessage;
import com.lh.remoting.dto.RpcRequest;
import com.lh.remoting.dto.RpcResponse;
import com.lh.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    public RpcMessageDecoder() {
        this(RpcConstants.MAX_FRAME_LENGTH,5,4,-9,0);
    }

    public RpcMessageDecoder( int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super( maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf){
            ByteBuf frame = (ByteBuf) decode;
            if (frame.readableBytes() >= RpcConstants.TOTAL_LENGTH){
                try {
                    return decodeFrame(frame);
                }catch (Exception e){
                    log.error("Decode frame error!", e);
                    throw e;
                }finally {
                    frame.release();
                }
            }
        }
        return decode;
    }

    private Object decodeFrame(ByteBuf in) {
        checkMagicNumber(in);
        checkVersion(in);

        int fullLength = in.readInt();
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();

        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestID(requestId)
                .messageType(messageType)
                .build();
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE){
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE){
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        int bodyLenth = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLenth > 0){
            byte[] bs = new byte[bodyLenth];
            in.readBytes(bs);
            String name = CompressTypeEnum.getName(compressType);
            Compress compress = ExtensionLoader.getExtensionLoader(Compress.class)
                    .getExtension(name);
            bs = compress.decompress(bs);
            String codecName = SerializationTypeEnum.getName(rpcMessage.getCodec());
            log.info("codec name: [{}]", codecName);
            Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                    .getExtension(codecName);
            if (messageType == RpcConstants.REQUEST_TYPE){
                RpcRequest tmp = serializer.deserialize(bs, RpcRequest.class);
                rpcMessage.setData(tmp);
            }else {
                RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
                rpcMessage.setData(tmpValue);
            }
        }
        return  rpcMessage;

    }

    private void checkVersion(ByteBuf in){
        byte version = in.readByte();
        if (version != RpcConstants.VERSION){
            throw new RuntimeException("version isn't compatile" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in){
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]){
                throw new IllegalStateException("Unknown magic code:"+ Arrays.toString(tmp));
            }
        }
    }
}
