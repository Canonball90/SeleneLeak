package mod.selene.world;

import mod.selene.loader.EventHandler;
import net.minecraft.entity.Entity;

public class EntityRemovedEvent extends EventHandler {
    private final Entity entity;

    public EntityRemovedEvent(Entity entity) {
        this.setStage(0);
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
