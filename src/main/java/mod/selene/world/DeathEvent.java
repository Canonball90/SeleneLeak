package mod.selene.world;

import mod.selene.loader.EventHandler;
import net.minecraft.entity.player.EntityPlayer;

public class DeathEvent extends EventHandler {

    public EntityPlayer player;

    public DeathEvent(EntityPlayer player) {
        super();
        this.player = player;
    }

}
