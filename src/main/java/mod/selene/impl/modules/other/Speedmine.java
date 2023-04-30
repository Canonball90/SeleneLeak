package mod.selene.impl.modules.other;

import mod.selene.api.utils.render.RenderBuilder;
import mod.selene.api.utils.render.RenderUtil2;
import mod.selene.impl.Module;
import mod.selene.loader.SeleneLoader;
import mod.selene.managers.InventoryManager2;
import mod.selene.system.Setting;
import mod.selene.world.BlockEvent;
import mod.selene.world.BlockResetEvent;
import mod.selene.world.PacketEvent;
import mod.selene.world.Render3DEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class Speedmine extends Module {
    public static Speedmine INSTANCE;
    private static float mineDamage;
    private final Setting<InventoryManager2.Switch> mineSwitch = this.register(new Setting<InventoryManager2.Switch>("SwitchMode", InventoryManager2.Switch.PACKET));
    private final Setting<Boolean> strict = this.register(new Setting<Boolean>("Strict", false));
    private final Setting<Double> range = this.register(new Setting<Double>("Range", 6.0, 0.0, 10.0));
    private final Setting<Boolean> abortPacket = this.register(new Setting<Boolean>("AbortPacket", true));
    private final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    private final Setting<Boolean> noDelay = this.register(new Setting<Boolean>("NoDelay", false));
    public Setting<Boolean> reset = register(new Setting<>("NoMineReset", false));
    public Setting<Boolean> strictMine = register(new Setting<>("StrictMine", false));
    private final Setting<Integer> maxReMines = this.register(new Setting<Integer>("MaxReMines", 1, 1, 20, v -> strictMine.getValue()));
    public Setting<Boolean> render = register(new Setting<>("Render", false));
    private final Setting<RenderBuilder.Box> renderMode = register(new Setting<RenderBuilder.Box>("RenderMode", RenderBuilder.Box.GLOW, v -> render.getValue()));
    private final Setting<Integer> boxAlpha = this.register(new Setting<Integer>("BoxAlpha", 60, 0, 255, v -> render.getValue()));
    private int mineBreaks;
    private int previousHaste;
    private EnumFacing mineFacing;
    private BlockPos minePosition;

    public Speedmine() {
        super("Speedmine", "Mines faster.", Module.Category.OTHER, true, false, false);
        INSTANCE = this;
    }

    public static boolean canBreak(BlockPos pos) {
        IBlockState blockState = mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, mc.world, pos) != -1.0f;
    }

    public static double getDistanceToCenter(EntityPlayer entityPlayer, BlockPos blockPos) {
        double d = (double) blockPos.getX() + 0.5 - entityPlayer.posX;
        double d2 = (double) blockPos.getY() + 0.5 - entityPlayer.posY;
        double d3 = (double) blockPos.getZ() + 0.5 - entityPlayer.posZ;
        return StrictMath.sqrt(d * d + d2 * d2 + d3 * d3);
    }

    public static void rotatePacket(BlockPos pos) {
        double diffX = pos.getX() - mc.player.posX + 0.5;
        double diffY = pos.getY() - (mc.player.posY + (double) mc.player.getEyeHeight());
        double diffZ = pos.getZ() - mc.player.posZ + 0.5;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        Speedmine.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(yaw, pitch, mc.player.onGround));
    }

    @Override
    public void onEnable() {
        if (Speedmine.mc.player.isPotionActive(MobEffects.HASTE)) {
            this.previousHaste = Speedmine.mc.player.getActivePotionEffect(MobEffects.HASTE).getDuration();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send send) {
        if (send.getPacket() instanceof CPacketHeldItemChange && this.strict.getValue().booleanValue()) {
            mineDamage = 0.0f;
        }
    }

    @SubscribeEvent
    public void onBlockReset(BlockResetEvent event) {

        if (reset.getValue()) {
            event.setCanceled(true);
        }
    }

    public float getBlockStrength(IBlockState iBlockState, BlockPos blockPos) {
        float f = iBlockState.getBlockHardness(Speedmine.mc.world, blockPos);
        if (f < 0.0f) {
            return 0.0f;
        }
        if (!this.canHarvestBlock(iBlockState.getBlock(), blockPos)) {
            return this.getDigSpeed(iBlockState) / f / 100.0f;
        }
        return this.getDigSpeed(iBlockState) / f / 30.0f;
    }

    public float getDigSpeed(IBlockState iBlockState) {
        ItemStack itemStack;
        int n;
        float f = this.getDestroySpeed(iBlockState);
        if (f > 1.0f && (n = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, itemStack = this.getEfficientItem(iBlockState))) > 0 && !itemStack.isEmpty()) {
            f = (float) ((double) f + (StrictMath.pow(n, 2.0) + 1.0));
        }
        if (Speedmine.mc.player.isPotionActive(MobEffects.HASTE)) {
            f *= 1.0f + (float) (Speedmine.mc.player.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2f;
        }
        if (Speedmine.mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {
            float f2;
            switch (Speedmine.mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0: {
                    f2 = 0.3f;
                    break;
                }
                case 1: {
                    f2 = 0.09f;
                    break;
                }
                case 2: {
                    f2 = 0.0027f;
                    break;
                }
                default: {
                    f2 = 8.1E-4f;
                }
            }
            f *= f2;
        }
        if (Speedmine.mc.player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(Speedmine.mc.player)) {
            f /= 5.0f;
        }
        if (!Speedmine.mc.player.onGround) {
            f /= 5.0f;
        }
        return f < 0.0f ? 0.0f : f;
    }

    public float getDestroySpeed(IBlockState iBlockState) {
        float f = 1.0f;
        if (this.getEfficientItem(iBlockState) != null && !this.getEfficientItem(iBlockState).isEmpty()) {
            f *= this.getEfficientItem(iBlockState).getDestroySpeed(iBlockState);
        }
        return f;
    }

    @Override
    public void onUpdate() {
        if (noDelay.getValue()) {
            mc.playerController.blockHitDelay = 0;
        }
        if (!Speedmine.mc.player.capabilities.isCreativeMode) {
            if (this.minePosition != null) {
                double d = Speedmine.getDistanceToCenter(Speedmine.mc.player, this.minePosition);
                if (this.mineBreaks >= this.maxReMines.getValue() && strictMine.getValue() || d > this.range.getValue()) {
                    this.minePosition = null;
                    this.mineFacing = null;
                    mineDamage = 0.0f;
                    this.mineBreaks = 0;
                }
            }
            if (this.minePosition != null && !Speedmine.mc.world.isAirBlock(this.minePosition)) {
                if (mineDamage >= 1.0f/* && !AutoCrystal.getInstance().rotating.getValue()*/) {
                    ItemStack itemStack;
                    short s;
                    int n = Speedmine.mc.player.inventory.currentItem;
                    int n2 = SeleneLoader.inventoryManager2.searchSlot(this.getEfficientItem(Speedmine.mc.world.getBlockState(this.minePosition)).getItem(), InventoryManager2.InventoryRegion.HOTBAR) + 36;
                    if (this.strict.getValue().booleanValue()) {
                        s = Speedmine.mc.player.openContainer.getNextTransactionID(Speedmine.mc.player.inventory);
                        itemStack = Speedmine.mc.player.openContainer.slotClick(n2, Speedmine.mc.player.inventory.currentItem, ClickType.SWAP, Speedmine.mc.player);
                        Speedmine.mc.player.connection.sendPacket(new CPacketClickWindow(Speedmine.mc.player.inventoryContainer.windowId, n2, Speedmine.mc.player.inventory.currentItem, ClickType.SWAP, itemStack, s));
                    } else {
                        SeleneLoader.inventoryManager2.switchToItem(this.getEfficientItem(Speedmine.mc.world.getBlockState(this.minePosition)).getItem(), this.mineSwitch.getValue());
                    }
                    Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.minePosition, this.mineFacing));
                    if (this.abortPacket.getValue().booleanValue()) {
                        Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.minePosition, EnumFacing.UP));
                    }
                    if (this.strict.getValue().booleanValue()) {
                        Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.minePosition, this.mineFacing));
                    }
                    Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.minePosition, this.mineFacing));
                    if (n != -1) {
                        if (this.strict.getValue().booleanValue()) {
                            s = Speedmine.mc.player.openContainer.getNextTransactionID(Speedmine.mc.player.inventory);
                            itemStack = Speedmine.mc.player.openContainer.slotClick(n2, Speedmine.mc.player.inventory.currentItem, ClickType.SWAP, Speedmine.mc.player);
                            Speedmine.mc.player.connection.sendPacket(new CPacketClickWindow(Speedmine.mc.player.inventoryContainer.windowId, n2, Speedmine.mc.player.inventory.currentItem, ClickType.SWAP, itemStack, s));
                            Speedmine.mc.player.connection.sendPacket(new CPacketConfirmTransaction(Speedmine.mc.player.inventoryContainer.windowId, s, true));
                        } else {
                            SeleneLoader.inventoryManager2.switchToSlot(n, InventoryManager2.Switch.PACKET);
                        }
                    }
                    mineDamage = 0.0f;
                    ++this.mineBreaks;
                }
                if (/*!AutoCrystal.getInstance().rotate.getValue() && */(double) mineDamage > 0.95 && this.rotate.getValue().booleanValue()) {
                    Speedmine.rotatePacket(this.minePosition);
                }
                mineDamage += this.getBlockStrength(Speedmine.mc.world.getBlockState(this.minePosition), this.minePosition);
            }
        }
    }

    public boolean canHarvestBlock(Block block, BlockPos blockPos) {
        IBlockState iBlockState = Speedmine.mc.world.getBlockState(blockPos);
        IBlockState iBlockState2 = iBlockState.getBlock().getActualState(iBlockState, Speedmine.mc.world, blockPos);
        if (iBlockState2.getMaterial().isToolNotRequired()) {

            return true;
        }
        ItemStack itemStack = this.getEfficientItem(iBlockState2);
        String string = block.getHarvestTool(iBlockState2);
        if (itemStack.isEmpty() || string == null) {
            return Speedmine.mc.player.canHarvestBlock(iBlockState2);
        }
        int n = itemStack.getItem().getHarvestLevel(itemStack, string, Speedmine.mc.player, iBlockState2);
        if (n < 0) {
            return Speedmine.mc.player.canHarvestBlock(iBlockState2);
        }
        return n >= block.getHarvestLevel(iBlockState2);
    }

    public ItemStack getEfficientItem(IBlockState iBlockState) {
        int n = -1;
        double d = 0.0;
        for (int i = 0; i < 9; ++i) {
            float f;
            if (Speedmine.mc.player.inventory.getStackInSlot(i).isEmpty() || !((f = Speedmine.mc.player.inventory.getStackInSlot(i).getDestroySpeed(iBlockState)) > 1.0f))
                continue;
            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, Speedmine.mc.player.inventory.getStackInSlot(i)) > 0) {
                f = (float) ((double) f + (StrictMath.pow(EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, Speedmine.mc.player.inventory.getStackInSlot(i)), 2.0) + 1.0));
            }
            if (!((double) f > d)) continue;
            d = f;
            n = i;
        }
        if (n != -1) {
            return Speedmine.mc.player.inventory.getStackInSlot(n);
        }
        return Speedmine.mc.player.inventory.getStackInSlot(Speedmine.mc.player.inventory.currentItem);
    }

    @SubscribeEvent
    public void onBlockEvent(BlockEvent blockEvent) {
        if (Speedmine.canBreak(blockEvent.pos) && !Speedmine.mc.player.capabilities.isCreativeMode && !blockEvent.pos.equals(this.minePosition)) {
            this.minePosition = blockEvent.pos;
            this.mineFacing = blockEvent.facing;
            mineDamage = 0.0f;
            this.mineBreaks = 0;
            if (this.minePosition != null && this.mineFacing != null) {
                Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.minePosition, this.mineFacing));
                Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.minePosition, EnumFacing.UP));
            }
        }
    }

    @Override
    public void onRender3D(Render3DEvent render3DEvent) {
        if (render.getValue()) {
            if (this.minePosition != null && !Speedmine.mc.world.isAirBlock(this.minePosition) && !mc.player.capabilities.isCreativeMode) {

                AxisAlignedBB mineBox = mc.world.getBlockState(minePosition).getSelectedBoundingBox(mc.world, minePosition);

                Vec3d mineCenter = mineBox.getCenter();

                AxisAlignedBB shrunkMineBox = new AxisAlignedBB(mineCenter.x, mineCenter.y, mineCenter.z, mineCenter.x, mineCenter.y, mineCenter.z);

                RenderUtil2.drawBox(new RenderBuilder()
                        .position(shrunkMineBox.grow(((mineBox.minX - mineBox.maxX) * 0.5) * MathHelper.clamp(mineDamage, 0, 1), ((mineBox.minY - mineBox.maxY) * 0.5) * MathHelper.clamp(mineDamage, 0, 1), ((mineBox.minZ - mineBox.maxZ) * 0.5) * MathHelper.clamp(mineDamage, 0, 1)))
                        .color((double) mineDamage >= 0.95 ? new Color(0, 255, 0, boxAlpha.getValue()) : new Color(255, 0, 0, boxAlpha.getValue()))
                        .box(renderMode.getValue())
                        .setup()
                        .line(1.5F)
                        .cull(renderMode.getValue().equals(RenderBuilder.Box.GLOW) || renderMode.getValue().equals(RenderBuilder.Box.REVERSE))
                        .shade(renderMode.getValue().equals(RenderBuilder.Box.GLOW) || renderMode.getValue().equals(RenderBuilder.Box.REVERSE))
                        .alpha(renderMode.getValue().equals(RenderBuilder.Box.GLOW) || renderMode.getValue().equals(RenderBuilder.Box.REVERSE))
                        .depth(true)
                        .blend()
                        .texture());
            }
        }
    }

    @Override
    public void onDisable() {
        if (Speedmine.mc.player.isPotionActive(MobEffects.HASTE)) {
            Speedmine.mc.player.removePotionEffect(MobEffects.HASTE);
        }
        if (this.previousHaste > 0) {
            Speedmine.mc.player.addPotionEffect(new PotionEffect(MobEffects.HASTE, this.previousHaste));
        }
        this.minePosition = null;
        this.mineFacing = null;
        mineDamage = 0.0f;
        this.mineBreaks = 0;
    }
}
