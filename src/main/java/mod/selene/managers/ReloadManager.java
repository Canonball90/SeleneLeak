package mod.selene.managers;

import mod.selene.api.utils.TextUtil;
import mod.selene.loader.Feature;
import mod.selene.loader.SeleneLoader;
import mod.selene.system.Command;
import mod.selene.world.PacketEvent;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReloadManager extends Feature {

    public String prefix;

    public void init(String prefix) {
        this.prefix = prefix;
        MinecraftForge.EVENT_BUS.register(this);
        if (!fullNullCheck()) {
            Command.sendMessage(TextUtil.RED + "SeleneLoader has been unloaded. Type " + prefix + "reload to reload.");
        }
    }

    public void unload() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketChatMessage) {
            CPacketChatMessage packet = event.getPacket();
            if (packet.getMessage().startsWith(this.prefix) && packet.getMessage().contains("reload")) {
                SeleneLoader.load();
                event.setCanceled(true);
            }
        }
    }
}
