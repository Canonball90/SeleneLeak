package mod.selene.world;

import mod.selene.loader.EventHandler;
import net.minecraft.entity.player.EntityPlayer;

public class TotemPopEvent extends EventHandler {

    private final EntityPlayer entity;

    public TotemPopEvent(EntityPlayer entity) {
        super();
        this.entity = entity;
    }

    public EntityPlayer getEntity() {
        return entity;
    }

}
