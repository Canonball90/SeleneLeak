package mod.selene.impl.modules.combat.godmodule;

import mod.selene.api.utils.interfaces.Util;
import mod.selene.injections.inj.ICPacketUseEntity;
import mod.selene.system.Command;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

import java.util.concurrent.TimeUnit;

public class UseThread extends Thread {
    private final int id;
    private final int delay;

    public UseThread(int idIn, int delayIn) {
        this.id = idIn;
        this.delay = delayIn;
    }

    @Override
    public void run() {
        try {
            if (this.delay != 0) {
                TimeUnit.MILLISECONDS.sleep(this.delay);
            }
            Util.mc.addScheduledTask(() -> {
                CPacketUseEntity attackPacket = new CPacketUseEntity();
                ((ICPacketUseEntity) attackPacket).setEntityId(id);
                ((ICPacketUseEntity) attackPacket).setAction(CPacketUseEntity.Action.ATTACK);
                if (GodModule.INSTANCE.debug.getValue()) {
                    Command.sendMessage("BiggestID is " + GodModule.biggestEntityID);
                }
                Util.mc.player.connection.sendPacket(attackPacket);
                if (GodModule.INSTANCE.swing.getValue()) {
                    Util.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
