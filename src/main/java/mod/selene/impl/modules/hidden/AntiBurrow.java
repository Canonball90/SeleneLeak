package mod.selene.impl.modules.hidden;

import mod.selene.loader.SeleneLoader;
import mod.selene.impl.Module;
import mod.selene.impl.modules.hidden.InstantMine;
import mod.selene.system.Setting;
import mod.selene.api.utils.BlockUtil;
import mod.selene.api.utils.EntityUtil;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class AntiBurrow
extends Module {
    public static BlockPos pos;
    private final Setting<Double> range = this.register(new Setting<Double>("Range", 5.0, 1.0, 8.0));
    private final Setting<Boolean> toggle = this.register(new Setting<Boolean>("Toggle", false));

    private boolean isOnLiquid() {
        double d = AntiBurrow.mc.player.posY - 0.03;
        for (int i = MathHelper.floor((double)AntiBurrow.mc.player.posX); i < MathHelper.ceil((double)AntiBurrow.mc.player.posX); ++i) {
            for (int j = MathHelper.floor((double)AntiBurrow.mc.player.posZ); j < MathHelper.ceil((double)AntiBurrow.mc.player.posZ); ++j) {
                BlockPos blockPos = new BlockPos(i, MathHelper.floor((double)d), j);
                if (!(AntiBurrow.mc.world.getBlockState(blockPos).getBlock() instanceof BlockLiquid)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean isInLiquid() {
        double d = AntiBurrow.mc.player.posY + 0.01;
        for (int i = MathHelper.floor((double)AntiBurrow.mc.player.posX); i < MathHelper.ceil((double)AntiBurrow.mc.player.posX); ++i) {
            for (int j = MathHelper.floor((double)AntiBurrow.mc.player.posZ); j < MathHelper.ceil((double)AntiBurrow.mc.player.posZ); ++j) {
                BlockPos blockPos = new BlockPos(i, (int)d, j);
                if (!(AntiBurrow.mc.world.getBlockState(blockPos).getBlock() instanceof BlockLiquid)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public void onUpdate() {
        if (AntiBurrow.fullNullCheck()) {
            return;
        }
        if (AntiBurrow.mc.currentScreen instanceof GuiHopper) {
            return;
        }
        EntityPlayer entityPlayer = this.getTarget(this.range.getValue());
        if (this.toggle.getValue().booleanValue()) {
            this.disable();
        }
        if (entityPlayer == null) {
            return;
        }
        pos = new BlockPos(entityPlayer.posX, entityPlayer.posY + 0.5, entityPlayer.posZ);
        if (InstantMine.breakPos != null) {
            if (InstantMine.breakPos.equals((Object)pos)) {
                return;
            }
            if (InstantMine.breakPos.equals((Object)new BlockPos(AntiBurrow.mc.player.posX, AntiBurrow.mc.player.posY + 2.0, AntiBurrow.mc.player.posZ))) {
                return;
            }
            if (InstantMine.breakPos.equals((Object)new BlockPos(AntiBurrow.mc.player.posX, AntiBurrow.mc.player.posY - 1.0, AntiBurrow.mc.player.posZ))) {
                return;
            }
            if (AntiBurrow.mc.world.getBlockState(InstantMine.breakPos).getBlock() == Blocks.WEB) {
                return;
            }
        }
        if (AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.AIR && AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.WEB && AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.REDSTONE_WIRE && !this.isOnLiquid() && !this.isInLiquid() && AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.WATER && AntiBurrow.mc.world.getBlockState(pos).getBlock() != Blocks.LAVA) {
            AntiBurrow.mc.playerController.onPlayerDamageBlock(pos, BlockUtil.getRayTraceFacing(pos));
        }
    }

    public AntiBurrow() {
        super("AntiBurrow", "AntiBurrow", Category.COMBAT, true, false, false);
    }

    private EntityPlayer getTarget(double d) {
        EntityPlayer entityPlayer = null;
        double d2 = Math.pow(d, 2.0) + 1.0;
        for (EntityPlayer entityPlayer2 : AntiBurrow.mc.world.playerEntities) {
            if (EntityUtil.isntValid((Entity)entityPlayer2, d) || SeleneLoader.speedManager.getPlayerSpeed(entityPlayer2) > 10.0) continue;
            if (entityPlayer == null) {
                entityPlayer = entityPlayer2;
                d2 = AntiBurrow.mc.player.getDistanceSq((Entity)entityPlayer2);
                continue;
            }
            if (AntiBurrow.mc.player.getDistanceSq((Entity)entityPlayer2) >= d2) continue;
            entityPlayer = entityPlayer2;
            d2 = AntiBurrow.mc.player.getDistanceSq((Entity)entityPlayer2);
        }
        return entityPlayer;
    }
}

