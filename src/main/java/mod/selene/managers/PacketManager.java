package mod.selene.managers;

import mod.selene.loader.Feature;
import net.minecraft.network.Packet;

import java.util.ArrayList;
import java.util.List;

public class PacketManager extends Feature {

    private final List<Packet<?>> noEventPackets = new ArrayList<>();

    /*public void sendAPacket(Packet<?> packet) {
        if(!nullCheck() && packet != null) {
            NetworkManager managers = mc.player.connection.getNetworkManager();
            if(managers.isChannelOpen()) {
                managers.flushOutboundQueue();
                managers.dispatchPacket(packet, null);
            } else {
                managers.readWriteLock.writeLock().lock();

                try {
                    managers.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packet, new GenericFutureListener[0]));
                } finally {
                    managers.readWriteLock.writeLock().unlock();
                }
            }
        }
    }*/

    public void sendPacketNoEvent(Packet<?> packet) {
        if (packet != null && !nullCheck()) {
            noEventPackets.add(packet);
            mc.player.connection.sendPacket(packet);
        }
    }

    public boolean shouldSendPacket(Packet<?> packet) {
        if (noEventPackets.contains(packet)) {
            noEventPackets.remove(packet);
            return false;
        }
        return true;
    }
}
