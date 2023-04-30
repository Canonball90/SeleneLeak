package mod.selene.impl.modules.hidden;

import mod.selene.world.PacketEvent;
import mod.selene.world.PlayerDamageBlockEvent;
import mod.selene.world.Render3DEvent;
import mod.selene.impl.Module;
import mod.selene.impl.modules.hidden.AntiBurrow;
import mod.selene.system.impl.Bind;
import mod.selene.system.Setting;
import mod.selene.api.utils.BlockUtil;
import mod.selene.api.utils.FadeUtils;
import mod.selene.api.utils.InventoryUtil;
import mod.selene.api.utils.render.RenderUtil;
import mod.selene.api.utils.RotationUtil;
import mod.selene.api.utils.Timer;
import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InstantMine
extends Module {
    boolean switched = false;
    Timer retryTime;
    private final Setting<Boolean> rotate;
    private final Setting<Integer> red;
    public final Setting<Boolean> db;
    public final Setting<Bind> bind;
    public static BlockPos breakPos;
    private final Timer breakSuccess;
    private static InstantMine INSTANCE;
    private final Setting<Boolean> render2;
    private final Setting<Integer> alpha;
    public FadeUtils secondFade;
    public static BlockPos breakPos2;
    public FadeUtils firstFade;
    private final Setting<Boolean> retry;
    private final Setting<Integer> green;
    private final Setting<Boolean> placeCrystal;
    public static final List<Block> godBlocks;
    private final Setting<Integer> pos1BoxAlpha;
    private final Setting<Integer> pos1FillAlpha;
    private final Setting<Boolean> ghostHand;
    public final Setting<Boolean> attackCrystal = this.register(new Setting<Boolean>("Attack Crystal", Boolean.TRUE));
    private EnumFacing facing;
    private final Setting<Boolean> render;
    int slotMain2;
    public final Setting<Float> health;
    private final Setting<Integer> blue;
    private final Setting<Boolean> crystalOnBreak;
    static int ticked;
    private boolean empty = false;
    private final Setting<Boolean> instant;
    private final Setting<Integer> alpha2;
    private boolean cancelStart = false;

    private boolean lambda$new$0(Float f) {
        return this.db.getValue();
    }

    @Override
    public void onUpdate() {
        int n;
        if (InstantMine.fullNullCheck()) {
            return;
        }
        if (InstantMine.mc.player.isCreative()) {
            return;
        }
        this.slotMain2 = InstantMine.mc.player.inventory.currentItem;
        if (ticked == 0) {
            breakPos2 = null;
        }
        if (breakPos2 != null) {
            if (ticked >= 60 || ticked > 1) {
                // empty if block
            }
            if (++ticked > 30 || ticked > 13 && InstantMine.mc.world.getBlockState(breakPos2).getBlock() == Blocks.ENDER_CHEST || ticked >= 3 && InstantMine.mc.world.getBlockState(breakPos2).getBlock() == Blocks.WEB) {
                if (InstantMine.mc.player.isHandActive()) {
                    this.resetMine();
                } else {
                    this.switchMine();
                }
            }
            if (InstantMine.mc.world.isAirBlock(breakPos2)) {
                this.resetMine();
                breakPos2 = null;
                ticked = 0;
            }
        } else {
            ticked = 0;
        }
        if (ticked > 80) {
            this.resetMine();
            breakPos2 = null;
            ticked = 0;
        }
        if (breakPos == null) {
            return;
        }
        if (!this.instant.getValue().booleanValue() && InstantMine.mc.world.isAirBlock(breakPos)) {
            breakPos = null;
            return;
        }
        if (!this.cancelStart) {
            return;
        }
        if (InstantMine.mc.player.getDistance((double)breakPos.getX(), (double)breakPos.getY(), (double)breakPos.getZ()) > 6.0) {
            return;
        }
        if (this.retry.getValue().booleanValue() && this.retryTime.passedMs(1000L) && !this.instant.getValue().booleanValue()) {
            this.retryTime.reset();
            InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakPos, this.facing));
            this.cancelStart = true;
            InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
        }
        if (this.attackCrystal.getValue().booleanValue() && InstantMine.mc.world.getBlockState(breakPos).getBlock() == Blocks.AIR) {
            InstantMine.attackcrystal(this.rotate.getValue());
        }
        if (this.bind.getValue().isDown() && this.placeCrystal.getValue().booleanValue() && InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1 && InstantMine.mc.world.getBlockState(breakPos).getBlock() == Blocks.AIR) {
            n = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int n2 = InstantMine.mc.player.inventory.currentItem;
            this.switchToSlot(n);
            BlockUtil.placeBlock(breakPos, EnumHand.MAIN_HAND, false, true, false);
            this.switchToSlot(n2);
        }
        if (InventoryUtil.getItemHotbar(Items.END_CRYSTAL) != -1 && this.placeCrystal.getValue().booleanValue() && InstantMine.mc.world.getBlockState(breakPos).getBlock() == Blocks.OBSIDIAN && !breakPos.equals((Object)AntiBurrow.pos)) {
            if (this.empty) {
                BlockUtil.placeCrystalOnBlock3(breakPos, EnumHand.MAIN_HAND, true, false, true);
            } else if (!this.crystalOnBreak.getValue().booleanValue()) {
                BlockUtil.placeCrystalOnBlock3(breakPos, EnumHand.MAIN_HAND, true, false, true);
            }
        }
        if (godBlocks.contains((Object)InstantMine.mc.world.getBlockState(breakPos).getBlock())) {
            return;
        }
        if (this.rotate.getValue().booleanValue()) {
            RotationUtil.facePos(breakPos);
        }
        if (InstantMine.mc.world.getBlockState(breakPos).getBlock() != Blocks.WEB) {
            if (this.ghostHand.getValue().booleanValue() && InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE) != -1 && InventoryUtil.getItemHotbars(Items.DIAMOND_PICKAXE) != -1) {
                n = InstantMine.mc.player.inventory.currentItem;
                if (InstantMine.mc.world.getBlockState(breakPos).getBlock() == Blocks.OBSIDIAN) {
                    if (!this.breakSuccess.passedMs(1400L)) {
                        return;
                    }
                    InstantMine.mc.player.inventory.currentItem = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
                    InstantMine.mc.playerController.updateController();
                    InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
                    InstantMine.mc.player.inventory.currentItem = n;
                    InstantMine.mc.playerController.updateController();
                    return;
                }
                InstantMine.mc.player.inventory.currentItem = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
                InstantMine.mc.playerController.updateController();
                InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
                InstantMine.mc.player.inventory.currentItem = n;
                InstantMine.mc.playerController.updateController();
                return;
            }
        } else if (this.ghostHand.getValue().booleanValue() && InventoryUtil.getItemHotbar(Items.DIAMOND_SWORD) != -1 && InventoryUtil.getItemHotbars(Items.DIAMOND_SWORD) != -1) {
            n = InstantMine.mc.player.inventory.currentItem;
            InstantMine.mc.player.inventory.currentItem = InventoryUtil.getItemHotbar(Items.DIAMOND_SWORD);
            InstantMine.mc.playerController.updateController();
            InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
            InstantMine.mc.player.inventory.currentItem = n;
            InstantMine.mc.playerController.updateController();
            return;
        }
        InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
    }

    @Override
    public void onRender3D(Render3DEvent render3DEvent) {
        if (!InstantMine.mc.player.isCreative()) {
            AxisAlignedBB axisAlignedBB;
            double d;
            double d2;
            double d3;
            double d4;
            double d5;
            double d6;
            AxisAlignedBB axisAlignedBB2;
            if (breakPos2 != null) {
                axisAlignedBB2 = InstantMine.mc.world.getBlockState(breakPos2).getSelectedBoundingBox((World)InstantMine.mc.world, breakPos2);
                d6 = axisAlignedBB2.minX + (axisAlignedBB2.maxX - axisAlignedBB2.minX) / 2.0;
                d5 = axisAlignedBB2.minY + (axisAlignedBB2.maxY - axisAlignedBB2.minY) / 2.0;
                d4 = axisAlignedBB2.minZ + (axisAlignedBB2.maxZ - axisAlignedBB2.minZ) / 2.0;
                d3 = this.secondFade.easeOutQuad() * (axisAlignedBB2.maxX - d6);
                d2 = this.secondFade.easeOutQuad() * (axisAlignedBB2.maxY - d5);
                d = this.secondFade.easeOutQuad() * (axisAlignedBB2.maxZ - d4);
                axisAlignedBB = new AxisAlignedBB(d6 - d3, d5 - d2, d4 - d, d6 + d3, d5 + d2, d4 + d);
                if (breakPos != null) {
                    if (!breakPos2.equals((Object)breakPos)) {
                        RenderUtil.drawBBBox(axisAlignedBB, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.alpha.getValue());
                        RenderUtil.drawBBFill(axisAlignedBB, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha2.getValue()), this.alpha2.getValue());
                    }
                } else {
                    RenderUtil.drawBBBox(axisAlignedBB, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.alpha.getValue());
                    RenderUtil.drawBBFill(axisAlignedBB, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha2.getValue()), this.alpha2.getValue());
                }
            }
            if (this.cancelStart && breakPos != null) {
                if (godBlocks.contains((Object)InstantMine.mc.world.getBlockState(breakPos).getBlock())) {
                    this.empty = true;
                }
                axisAlignedBB2 = InstantMine.mc.world.getBlockState(breakPos).getSelectedBoundingBox((World)InstantMine.mc.world, breakPos);
                d6 = axisAlignedBB2.minX + (axisAlignedBB2.maxX - axisAlignedBB2.minX) / 2.0;
                d5 = axisAlignedBB2.minY + (axisAlignedBB2.maxY - axisAlignedBB2.minY) / 2.0;
                d4 = axisAlignedBB2.minZ + (axisAlignedBB2.maxZ - axisAlignedBB2.minZ) / 2.0;
                d3 = this.firstFade.easeOutQuad() * (axisAlignedBB2.maxX - d6);
                d2 = this.firstFade.easeOutQuad() * (axisAlignedBB2.maxY - d5);
                d = this.firstFade.easeOutQuad() * (axisAlignedBB2.maxZ - d4);
                axisAlignedBB = new AxisAlignedBB(d6 - d3, d5 - d2, d4 - d, d6 + d3, d5 + d2, d4 + d);
                if (this.render.getValue().booleanValue()) {
                    RenderUtil.drawBBFill(axisAlignedBB, new Color(this.empty ? 0 : 255, this.empty ? 255 : 0, 0, 255), this.pos1FillAlpha.getValue());
                }
                if (this.render2.getValue().booleanValue()) {
                    RenderUtil.drawBBBox(axisAlignedBB, new Color(this.empty ? 0 : 255, this.empty ? 255 : 0, 0, 255), this.pos1BoxAlpha.getValue());
                }
            }
        }
    }

    private static Float lambda$attackcrystal$7(Entity entity) {
        return Float.valueOf(InstantMine.mc.player.getDistance(entity));
    }

    private boolean lambda$new$2(Integer n) {
        return this.render.getValue();
    }

    private boolean lambda$new$4(Object object) {
        return this.placeCrystal.getValue();
    }

    private boolean lambda$new$5(Boolean bl) {
        return this.placeCrystal.getValue();
    }

    private boolean lambda$new$1(Boolean bl) {
        return this.instant.getValue() == false;
    }

    public InstantMine() {
        super("InstantMine", "legacy", Category.PLAYER, true, false, false);
        this.db = this.register(new Setting<Boolean>("Silent Double", Boolean.TRUE));
        this.health = this.register(new Setting<Float>("Health", Float.valueOf(18.0f), Float.valueOf(0.0f), Float.valueOf(35.9f), this::lambda$new$0));
        this.breakSuccess = new Timer();
        this.instant = this.register(new Setting<Boolean>("Instant", true));
        this.retry = this.register(new Setting<Boolean>("Retry", Boolean.valueOf(true), this::lambda$new$1));
        this.rotate = this.register(new Setting<Boolean>("Rotate", true));
        this.ghostHand = this.register(new Setting<Boolean>("GhostHand", Boolean.TRUE));
        this.render = this.register(new Setting<Boolean>("Pos1Fill", true));
        this.pos1FillAlpha = this.register(new Setting<Integer>("Pos1FillAlpha", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), this::lambda$new$2));
        this.render2 = this.register(new Setting<Boolean>("Pos1Box", true));
        this.pos1BoxAlpha = this.register(new Setting<Integer>("Pos1BoxAlpha", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(255), this::lambda$new$3));
        this.placeCrystal = this.register(new Setting<Boolean>("Place Crystal", Boolean.TRUE));
        this.bind = this.register(new Setting<Object>("ObsidianBind", new Bind(-1), this::lambda$new$4));
        this.crystalOnBreak = this.register(new Setting<Boolean>("Crystal on Break", Boolean.TRUE, this::lambda$new$5));
        this.red = this.register(new Setting<Integer>("Pos2Red", 255, 0, 255));
        this.green = this.register(new Setting<Integer>("Pos2Green", 255, 0, 255));
        this.blue = this.register(new Setting<Integer>("Pos2Blue", 255, 0, 255));
        this.alpha = this.register(new Setting<Integer>("Pos2BoxAlpha", 150, 0, 255));
        this.alpha2 = this.register(new Setting<Integer>("Pos2FillAlpha", 70, 0, 255));
        this.firstFade = new FadeUtils(1500L);
        this.secondFade = new FadeUtils(1500L);
        this.retryTime = new Timer();
        this.setInstance();
    }

    @SubscribeEvent
    public void onBlockEvent(PlayerDamageBlockEvent playerDamageBlockEvent) {
        if (InstantMine.fullNullCheck()) {
            return;
        }
        if (InstantMine.mc.player.isCreative()) {
            return;
        }
        if (!BlockUtil.canBreak(playerDamageBlockEvent.pos)) {
            return;
        }
        if (breakPos != null) {
            if (breakPos.getX() == playerDamageBlockEvent.pos.getX() && breakPos.getY() == playerDamageBlockEvent.pos.getY() && breakPos.getZ() == playerDamageBlockEvent.pos.getZ()) {
                return;
            }
            if (breakPos.equals((Object)breakPos2)) {
                this.secondFade.reset();
            }
        }
        this.firstFade.reset();
        this.empty = false;
        this.cancelStart = false;
        breakPos = playerDamageBlockEvent.pos;
        this.breakSuccess.reset();
        this.facing = playerDamageBlockEvent.facing;
        if (breakPos == null) {
            return;
        }
        InstantMine.mc.player.swingArm(EnumHand.MAIN_HAND);
        InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakPos, this.facing));
        this.cancelStart = true;
        InstantMine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakPos, this.facing));
        playerDamageBlockEvent.setCanceled(true);
        if (breakPos2 == null) {
            ticked = 1;
            breakPos2 = playerDamageBlockEvent.pos;
            this.secondFade.reset();
        }
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private void switchToSlot(int n) {
        InstantMine.mc.player.inventory.currentItem = n;
        InstantMine.mc.playerController.updateController();
    }

    private void switchMine() {
        if (InstantMine.mc.player.getHealth() + InstantMine.mc.player.getAbsorptionAmount() >= this.health.getValue().floatValue()) {
            if (this.db.getValue().booleanValue()) {
                if (InstantMine.mc.world.getBlockState(breakPos2).getBlock() == Blocks.WEB) {
                    if (InventoryUtil.getItemHotbar(Items.DIAMOND_SWORD) != -1) {
                        InstantMine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(InventoryUtil.getItemHotbars(Items.DIAMOND_SWORD)));
                        this.switched = true;
                        ++ticked;
                    }
                } else if (InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE) != -1) {
                    InstantMine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(InventoryUtil.getItemHotbars(Items.DIAMOND_PICKAXE)));
                    this.switched = true;
                    ++ticked;
                }
            }
        } else if (this.switched) {
            InstantMine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.slotMain2));
            this.switched = false;
        }
    }

    private static boolean lambda$attackcrystal$6(Entity entity) {
        return entity instanceof EntityEnderCrystal && !entity.isDead;
    }

    public static void attackcrystal(boolean bl) {
        for (Entity entity : InstantMine.mc.world.loadedEntityList.stream().filter(InstantMine::lambda$attackcrystal$6).sorted(Comparator.comparing(InstantMine::lambda$attackcrystal$7)).collect(Collectors.toList())) {
            if (!(entity instanceof EntityEnderCrystal) || !(entity.getDistanceSq(breakPos) <= 2.0)) continue;
            InstantMine.mc.player.connection.sendPacket((Packet)new CPacketUseEntity(entity));
            InstantMine.mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
            if (!bl) continue;
            RotationUtil.facePos(new BlockPos(entity.posX, entity.posY + 0.5, entity.posZ));
        }
    }

    private void resetMine() {
        if (this.switched) {
            InstantMine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.slotMain2));
            this.switched = false;
        }
    }

    public static InstantMine INSTANCE() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new InstantMine();
        return INSTANCE;
    }

    private boolean lambda$new$3(Integer n) {
        return this.render2.getValue();
    }

    static {
        godBlocks = Arrays.asList(new Block[]{Blocks.AIR, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.BEDROCK});
        breakPos2 = null;
        ticked = 0;
        INSTANCE = new InstantMine();
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send send) {
        if (InstantMine.fullNullCheck()) {
            return;
        }
        if (InstantMine.mc.player.isCreative()) {
            return;
        }
        if (!(send.getPacket() instanceof CPacketPlayerDigging)) {
            return;
        }
        CPacketPlayerDigging cPacketPlayerDigging = (CPacketPlayerDigging)send.getPacket();
        if (cPacketPlayerDigging.getAction() != CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
            return;
        }
        send.setCanceled(this.cancelStart);
    }
}

