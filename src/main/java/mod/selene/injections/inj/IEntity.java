package mod.selene.injections.inj;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface IEntity {

    @Accessor("inPortal")
    boolean getInPortal();

    @Accessor("inPortal")
    void setInPortal(boolean inPortal);

    @Accessor("isInWeb")
    boolean getInWeb();

    @Accessor("isInWeb")
    void setInWeb(boolean isInWeb);

    @Invoker("setSize")
    void setEntitySize(float width, float height);
}
