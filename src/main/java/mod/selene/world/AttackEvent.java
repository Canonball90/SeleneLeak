package mod.selene.world;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class AttackEvent extends Event {
    Entity entity;

    public AttackEvent(Entity attack) {
        this.entity = attack;
    }

    public Entity getEntity() {
        return entity;
    }
}
