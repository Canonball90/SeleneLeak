/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.server.integrated.IntegratedServer
 *  net.minecraft.utils.Session
 *  net.minecraft.utils.Timer
 */
package mod.selene.injections.inj;

import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = {Minecraft.class})
public interface IMinecraft {
    @Accessor(value = "integratedServer")
    void setIntegratedServer(IntegratedServer var1);

    @Accessor(value = "timer")
    Timer getTimer();

    @Accessor(value = "rightClickDelayTimer")
    int getRightClickDelayTimer();

    @Accessor(value = "rightClickDelayTimer")
    void setRightClickDelayTimer(int var1);

    @Accessor(value = "session")
    void setSession(Session var1);

    @Invoker(value = "rightClickMouse")
    void rightClickMouse();
}

