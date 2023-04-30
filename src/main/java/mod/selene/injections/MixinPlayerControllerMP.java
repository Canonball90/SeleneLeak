package mod.selene.injections;

import mod.selene.world.AttackEvent;
import mod.selene.world.BlockEvent;
import mod.selene.world.BlockResetEvent;
import mod.selene.world.DamageBlockEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {


    @Inject(method = "resetBlockRemoving", at = @At("HEAD"), cancellable = true)
    public void resetBlockRemovingHook(CallbackInfo info) {
    }

    @Inject(method = "attackEntity", at = @At(value = "RETURN"), cancellable = true)
    public void attackEntity(EntityPlayer playerIn, Entity targetEntity, CallbackInfo info) {
        AttackEvent event = new AttackEvent(targetEntity);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
            info.cancel();
    }

    @Inject(method = "clickBlock", at = @At("HEAD"), cancellable = true)
    private void clickBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> info) {
        BlockEvent event = new BlockEvent(3, pos, face);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = "onPlayerDamageBlock", at = @At("HEAD"), cancellable = true)
    private void onPlayerDamageBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> info) {
        BlockEvent event = new BlockEvent(4, pos, face);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = "onPlayerDamageBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z", at = @At("HEAD"), cancellable = true)
    private void onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        DamageBlockEvent event = new DamageBlockEvent(posBlock, directionFacing);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "resetBlockRemoving", at = @At(value = "HEAD"), cancellable = true)
    private void onResetBlockRemoving(CallbackInfo info) {
        BlockResetEvent blockResetEvent = new BlockResetEvent();
        MinecraftForge.EVENT_BUS.post(blockResetEvent);

        if (blockResetEvent.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method = "getBlockReachDistance", at = @At("RETURN"), cancellable = true)
    private void getReachDistanceHook(CallbackInfoReturnable<Float> distance) {
    }

    @Redirect(method = "processRightClickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemBlock;canPlaceBlockOnSide(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)Z"))
    public boolean canPlaceBlockOnSideHook(ItemBlock itemBlock, World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        Block block = worldIn.getBlockState(pos).getBlock();

        if (block == Blocks.SNOW_LAYER && block.isReplaceable(worldIn, pos)) {
            side = EnumFacing.UP;
        } else if (!block.isReplaceable(worldIn, pos)) {
            pos = pos.offset(side);
        }

        IBlockState iblockstate1 = worldIn.getBlockState(pos);
        AxisAlignedBB axisalignedbb = itemBlock.block.getDefaultState().getCollisionBoundingBox(worldIn, pos);
        if (axisalignedbb != Block.NULL_AABB && !worldIn.checkNoEntityCollision(axisalignedbb.offset(pos), null)) {
        } else if (iblockstate1.getMaterial() == Material.CIRCUITS && itemBlock.block == Blocks.ANVIL) {
            return true;
        }

        return iblockstate1.getBlock().isReplaceable(worldIn, pos) && itemBlock.block.canPlaceBlockOnSide(worldIn, pos, side);
    }
}
