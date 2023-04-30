package mod.selene.injections.inj;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderer.class)
public interface IEntityRenderer {
    @Invoker("setupCameraTransform")
    void setupCamera(float partialTicks, int pass);

    @Invoker("renderHand")
    void renderHand(float partialTicks, int pass);
}