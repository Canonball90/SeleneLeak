/*
 * Decompiled with CFR 0.150.
 *
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.entity.EntityOtherPlayerMP
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.MoverType
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.network.play.server.SPacketDestroyEntities
 *  net.minecraft.network.play.server.SPacketEntityStatus
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.utils.EnumParticleTypes
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package mod.selene.impl.modules.player;

import com.mojang.authlib.GameProfile;
import mod.selene.impl.Module;
import mod.selene.system.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.MoverType;

import java.util.Random;
import java.util.UUID;

public class FakePlayer
        extends Module {
    public final Setting<Boolean> inv = this.register(new Setting<Boolean>("Copy Inventory", true));
    public final Setting<Boolean> move = this.register(new Setting<Boolean>("Can Move", true));
    private EntityOtherPlayerMP fakePlayer;

    public FakePlayer() {
        super("FakePlayer", "Spawns in a fake player for testing purposes", Category.PLAYER, true, false, false);
    }

    @Override
    public void onEnable() {
        if (FakePlayer.nullCheck()) {
            return;
        }
        this.fakePlayer = new EntityOtherPlayerMP(FakePlayer.mc.world, new GameProfile(UUID.fromString("fdee323e-7f0c-4c15-8d1c-0f277442342a"), "MrBubblecum123"));
        this.fakePlayer.copyLocationAndAnglesFrom(FakePlayer.mc.player);
        this.fakePlayer.rotationYawHead = FakePlayer.mc.player.rotationYawHead;
        if (this.inv.getValue().booleanValue()) {
            this.fakePlayer.inventory.copyInventory(FakePlayer.mc.player.inventory);
        }
        FakePlayer.mc.world.addEntityToWorld(-100, this.fakePlayer);
    }

    @Override
    public void onDisable() {
        try {
            if (FakePlayer.nullCheck()) {
                return;
            }
            FakePlayer.mc.world.removeEntity(this.fakePlayer);
        } catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void onUpdate() {
        if (FakePlayer.nullCheck()) {
            this.setEnabled(false);
        }
    }

    @Override
    public void onLogout() {
        this.disable();
    }

    public void travel(float f, float f2, float f3) {
        double d = this.fakePlayer.posY;
        float f4 = 0.8f;
        float f5 = 0.02f;
        float f6 = EnchantmentHelper.getDepthStriderModifier(this.fakePlayer);
        if (f6 > 3.0f) {
            f6 = 3.0f;
        }
        if (!this.fakePlayer.onGround) {
            f6 *= 0.5f;
        }
        if (f6 > 0.0f) {
            f4 += (0.54600006f - f4) * f6 / 3.0f;
            f5 += (this.fakePlayer.getAIMoveSpeed() - f5) * f6 / 4.0f;
        }
        this.fakePlayer.moveRelative(f, f2, f3, f5);
        this.fakePlayer.move(MoverType.SELF, this.fakePlayer.motionX, this.fakePlayer.motionY, this.fakePlayer.motionZ);
        this.fakePlayer.motionX *= f4;
        this.fakePlayer.motionY *= 0.8f;
        this.fakePlayer.motionZ *= f4;
        if (!this.fakePlayer.hasNoGravity()) {
            this.fakePlayer.motionY -= 0.02;
        }
        if (this.fakePlayer.collidedHorizontally && this.fakePlayer.isOffsetPositionInLiquid(this.fakePlayer.motionX, this.fakePlayer.motionY + (double) 0.6f - this.fakePlayer.posY + d, this.fakePlayer.motionZ)) {
            this.fakePlayer.motionY = 0.3f;
        }
    }

    @Override
    public void onTick() {
        if (this.fakePlayer != null) {
            Random random = new Random();
            this.fakePlayer.moveForward = FakePlayer.mc.player.moveForward + (float) random.nextInt(5) / 10.0f;
            this.fakePlayer.moveStrafing = FakePlayer.mc.player.moveStrafing + (float) random.nextInt(5) / 10.0f;
            if (this.move.getValue().booleanValue()) {
                this.travel(this.fakePlayer.moveStrafing, this.fakePlayer.moveVertical, this.fakePlayer.moveForward);
            }
        }
    }
}

