/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

import rip.visionmc.spigotv.SpigotV;
import rip.visionmc.spigotv.handler.PacketHandler;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.authlib.properties.Property;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.crypto.SecretKey;
import net.minecraft.server.CancelledPacketHandleException;
import net.minecraft.server.ChatComponentText;
import net.minecraft.server.ChatMessage;
import net.minecraft.server.EnumProtocol;
import net.minecraft.server.EnumProtocolDirection;
import net.minecraft.server.IChatBaseComponent;
import net.minecraft.server.IUpdatePlayerListBox;
import net.minecraft.server.ItemStack;
import net.minecraft.server.LazyInitVar;
import net.minecraft.server.MinecraftEncryption;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Packet;
import net.minecraft.server.PacketCompressor;
import net.minecraft.server.PacketDecompressor;
import net.minecraft.server.PacketDecrypter;
import net.minecraft.server.PacketEncrypter;
import net.minecraft.server.PacketListener;
import net.minecraft.server.PacketPlayInBlockPlace;
import net.minecraft.server.PacketPlayInCustomPayload;
import net.minecraft.server.PlayerConnection;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkManager
extends SimpleChannelInboundHandler<Packet> {
    private static final Logger g = LogManager.getLogger();
    public static final Marker a = MarkerManager.getMarker("NETWORK");
    public static final Marker b = MarkerManager.getMarker("NETWORK_PACKETS", a);
    public static final AttributeKey<EnumProtocol> c = AttributeKey.valueOf("protocol");
    public static final LazyInitVar<NioEventLoopGroup> d = new LazyInitVar(){

        protected NioEventLoopGroup a() {
            return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
        }

        protected Object init() {
            return this.a();
        }
    };
    public static final LazyInitVar<EpollEventLoopGroup> e = new LazyInitVar(){

        protected EpollEventLoopGroup a() {
            return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
        }

        protected Object init() {
            return this.a();
        }
    };
    public static final LazyInitVar<LocalEventLoopGroup> f = new LazyInitVar(){

        protected LocalEventLoopGroup a() {
            return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
        }

        protected Object init() {
            return this.a();
        }
    };
    private final EnumProtocolDirection h;
    private final Queue<QueuedPacket> i = Queues.newConcurrentLinkedQueue();
    private final ReentrantReadWriteLock j = new ReentrantReadWriteLock();
    public Channel channel;
    public SocketAddress l;
    public UUID spoofedUUID;
    public Property[] spoofedProfile;
    public boolean preparing = true;
    private PacketListener m;
    private IChatBaseComponent n;
    private boolean o;
    private boolean p;
    private boolean openedBook;

    public NetworkManager(EnumProtocolDirection enumprotocoldirection) {
        this.h = enumprotocoldirection;
    }

    @Override
    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelActive(channelhandlercontext);
        this.channel = channelhandlercontext.channel();
        this.l = this.channel.remoteAddress();
        this.preparing = false;
        try {
            this.a(EnumProtocol.HANDSHAKING);
        }
        catch (Throwable throwable) {
            g.fatal(throwable);
        }
    }

    public void a(EnumProtocol enumprotocol) {
        this.channel.attr(c).set(enumprotocol);
        this.channel.config().setAutoRead(true);
        g.debug("Enabled auto read");
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelhandlercontext) throws Exception {
        this.close(new ChatMessage("disconnect.endOfStream", new Object[0]));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) throws Exception {
        ChatMessage chatmessage = throwable instanceof TimeoutException ? new ChatMessage("disconnect.timeout", new Object[0]) : new ChatMessage("disconnect.genericReason", "Internal Exception: " + throwable);
        this.close(chatmessage);
        if (MinecraftServer.getServer().isDebugging()) {
            throwable.printStackTrace();
        }
    }

    protected void a(ChannelHandlerContext channelhandlercontext, Packet packet) throws Exception {
        if (this.channel.isOpen()) {
            ItemStack stack;
            if (packet instanceof PacketPlayInCustomPayload) {
                PacketPlayInCustomPayload payload = (PacketPlayInCustomPayload)packet;
                String name = payload.a();
                if ((name.equalsIgnoreCase("MC|BSign") || name.equalsIgnoreCase("MC|BEdit")) && this.m instanceof PlayerConnection) {
                    byte[] data = payload.b().array();
                    if (data.length > 15000) {
                        this.close(new ChatMessage("Invalid book packet", new Object[0]));
                        return;
                    }
                    if (!this.openedBook) {
                        this.close(new ChatMessage("Invalid book packet", new Object[0]));
                        return;
                    }
                    this.openedBook = false;
                }
            } else if (packet instanceof PacketPlayInBlockPlace && (stack = ((PacketPlayInBlockPlace)packet).getItemStack()) != null && stack.getItem() != null && stack.getItem().getName().equalsIgnoreCase("item.writingBook")) {
                this.openedBook = true;
            }
            try {
                packet.a(this.m);
            }
            catch (CancelledPacketHandleException cancelledPacketHandleException) {}
            if (this.m instanceof PlayerConnection) {
                try {
                    for (PacketHandler handler : SpigotV.INSTANCE.getPacketHandlers()) {
                        handler.handleReceivedPacket((PlayerConnection)this.m, packet);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void a(PacketListener packetlistener) {
        Validate.notNull(packetlistener, "packetListener", new Object[0]);
        g.debug("Set listener of {} to {}", this, packetlistener);
        this.m = packetlistener;
    }

    public void handle(Packet packet) {
        if (this.g()) {
            this.m();
            this.a(packet, null);
        } else {
            this.j.writeLock().lock();
            try {
                this.i.add(new QueuedPacket(packet, null));
            }
            finally {
                this.j.writeLock().unlock();
            }
        }
    }

    public void a(Packet packet, GenericFutureListener<? extends Future<? super Void>> genericfuturelistener, GenericFutureListener<? extends Future<? super Void>> ... agenericfuturelistener) {
        if (this.g()) {
            this.m();
            this.a(packet, ArrayUtils.add(agenericfuturelistener, 0, genericfuturelistener));
        } else {
            this.j.writeLock().lock();
            try {
                this.i.add(new QueuedPacket(packet, ArrayUtils.add(agenericfuturelistener, 0, genericfuturelistener)));
            }
            finally {
                this.j.writeLock().unlock();
            }
        }
    }

    private void a(final Packet packet, final GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener) {
        final EnumProtocol enumprotocol = EnumProtocol.a(packet);
        final EnumProtocol enumprotocol1 = this.channel.attr(c).get();
        if (enumprotocol1 != enumprotocol) {
            g.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }
        if (this.channel.eventLoop().inEventLoop()) {
            if (enumprotocol != enumprotocol1) {
                this.a(enumprotocol);
            }
            ChannelFuture channelfuture = this.channel.writeAndFlush(packet);
            if (agenericfuturelistener != null) {
                channelfuture.addListeners(agenericfuturelistener);
            }
            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(new Runnable(){

                @Override
                public void run() {
                    if (enumprotocol != enumprotocol1) {
                        NetworkManager.this.a(enumprotocol);
                    }
                    ChannelFuture channelfuture = NetworkManager.this.channel.writeAndFlush(packet);
                    if (agenericfuturelistener != null) {
                        channelfuture.addListeners(agenericfuturelistener);
                    }
                    channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            });
        }
    }

    private void m() {
        if (this.channel != null && this.channel.isOpen()) {
            this.j.readLock().lock();
            try {
                while (!this.i.isEmpty()) {
                    QueuedPacket networkmanager_queuedpacket = this.i.poll();
                    this.a(networkmanager_queuedpacket.a, networkmanager_queuedpacket.b);
                }
            }
            finally {
                this.j.readLock().unlock();
            }
        }
    }

    public void a() {
        this.m();
        if (this.m instanceof IUpdatePlayerListBox) {
            ((IUpdatePlayerListBox)((Object)this.m)).c();
        }
        this.channel.flush();
    }

    public SocketAddress getSocketAddress() {
        return this.l;
    }

    public void close(IChatBaseComponent ichatbasecomponent) {
        this.i.clear(); // KigPaper
        this.preparing = false;
        if (this.channel.isOpen()) {
            this.channel.close();
            this.n = ichatbasecomponent;
        }
    }

    public boolean c() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public void a(SecretKey secretkey) {
        this.o = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new PacketDecrypter(MinecraftEncryption.a(2, secretkey)));
        this.channel.pipeline().addBefore("prepender", "encrypt", new PacketEncrypter(MinecraftEncryption.a(1, secretkey)));
    }

    public boolean g() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean h() {
        return this.channel == null;
    }

    public PacketListener getPacketListener() {
        return this.m;
    }

    public IChatBaseComponent j() {
        return this.n;
    }

    public void k() {
        this.channel.config().setAutoRead(false);
    }

    public void a(int i) {
        if (i >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                ((PacketDecompressor)this.channel.pipeline().get("decompress")).a(i);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", new PacketDecompressor(i));
            }
            if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
                ((PacketCompressor)this.channel.pipeline().get("decompress")).a(i);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", new PacketCompressor(i));
            }
        } else {
            if (this.channel.pipeline().get("decompress") instanceof PacketDecompressor) {
                this.channel.pipeline().remove("decompress");
            }
            if (this.channel.pipeline().get("compress") instanceof PacketCompressor) {
                this.channel.pipeline().remove("compress");
            }
        }
    }

    public void l() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (!this.p) {
                this.p = true;
                if (this.j() != null) {
                    this.getPacketListener().a(this.j());
                } else if (this.getPacketListener() != null) {
                    this.getPacketListener().a(new ChatComponentText("Disconnected"));
                }
                this.i.clear();
            } else {
                g.warn("handleDisconnection() called twice");
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet object) throws Exception {
        this.a(channelhandlercontext, object);
    }

    public SocketAddress getRawAddress() {
        return this.channel.remoteAddress();
    }

    static class QueuedPacket {
        private final Packet a;
        private final GenericFutureListener<? extends Future<? super Void>>[] b;

        public QueuedPacket(Packet packet, GenericFutureListener<? extends Future<? super Void>> ... agenericfuturelistener) {
            this.a = packet;
            this.b = agenericfuturelistener;
        }
    }
}

