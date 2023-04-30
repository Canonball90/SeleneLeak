package mod.selene.impl.modules.combat;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import mod.selene.api.utils.*;
import mod.selene.api.utils.interfaces.Util;
import mod.selene.api.utils.math.MathUtil;
import mod.selene.api.utils.render.PaletteHelper;
import mod.selene.api.utils.render.RenderUtil;
import mod.selene.impl.Module;
import mod.selene.injections.inj.ICPacketUseEntity;
import mod.selene.loader.Feature;
import mod.selene.loader.SeleneLoader;
import mod.selene.system.Setting;
import mod.selene.world.PacketEvent;
import mod.selene.world.Render3DEvent;
import mod.selene.world.UpdateWalkingPlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.network.play.client.CPacketUseEntity.Action.ATTACK;
import static org.lwjgl.opengl.GL11.*;

public class AutoCrystal extends Module {

    public static final EnumFacing[] VALUESNODOWN = new EnumFacing[]{
            EnumFacing.UP,
            EnumFacing.NORTH,
            EnumFacing.SOUTH,
            EnumFacing.WEST,
            EnumFacing.EAST
    };
    public static float hue;
    private final Setting<Values> setting = register(new Setting<>("Settings", Values.PLACE));
    private final Setting<Integer> switchCooldown = register(new Setting<>("Cooldown", 500, 0, 1000, v -> setting.getValue() == Values.MISC));
    private final Setting<Timing> timingMode = register(new Setting<>("Timing", Timing.BREAKPLACE, v -> setting.getValue() == Values.MISC));
    private final Setting<Swing> swingMode = register(new Setting<>("Swing", Swing.MAINHAND, v -> setting.getValue() == Values.MISC));
    private final Object2LongOpenHashMap<BlockPos> renderMap = new Object2LongOpenHashMap<>();
    private final Object2LongOpenHashMap<EntityPlayer> popMap = new Object2LongOpenHashMap<>();
    private final ObjectSet<BlockPos> placedPos = new ObjectOpenHashSet<>();
    private final Timer breakTimer = new Timer();
    private final Timer placeTimer = new Timer();
    //PLACE
    public Setting<Boolean> place = register(new Setting<>("Place", true, v -> setting.getValue() == Values.PLACE));
    public Setting<Integer> placeDelay = register(new Setting<>("PlaceDelay", 0, 0, 1000, v -> setting.getValue() == Values.PLACE && place.getValue()));
    public Setting<Float> placeRange = register(new Setting<>("PlaceRange", 6.0f, 0.0f, 6.0f, v -> setting.getValue() == Values.PLACE && place.getValue()));
    //public Setting<Boolean> inhibit = new Setting<>("Inhibit", false, v -> setting.getValue() == Values.BREAK && explode.getValue());
    public Setting<Float> placeTrace = register(new Setting<>("PlaceTrace", 6.0f, 0.0f, 6.0f, v -> setting.getValue() == Values.PLACE && place.getValue()));
    public Setting<Boolean> instantPlace = register(new Setting<>("InstantPlace", true, v -> setting.getValue() == Values.PLACE));
    public Setting<Boolean> dotThirteen = register(new Setting<>("1.13", false, v -> setting.getValue() == Values.PLACE));
    //BREAK
    public Setting<Boolean> explode = register(new Setting<>("Break", true, v -> setting.getValue() == Values.BREAK));
    public Setting<Integer> breakDelay = register(new Setting<>("BreakDelay", 0, 0, 1000, v -> setting.getValue() == Values.BREAK && explode.getValue()));
    public Setting<Float> breakRange = register(new Setting<>("BreakRange", 6.0f, 0.0f, 6.0f, v -> setting.getValue() == Values.BREAK && explode.getValue()));
    public Setting<Float> breakTrace = register(new Setting<>("BreakTrace", 6.0f, 0.0f, 6.0f, v -> setting.getValue() == Values.BREAK && explode.getValue()));
    public Setting<Boolean> packetExplode = register(new Setting<>("PacketBreak", true, v -> setting.getValue() == Values.BREAK));
    public Setting<Boolean> inhibit = register(new Setting<>("Inhibit", true, v -> setting.getValue() == Values.BREAK));
    public Setting<Integer> startVal = register(new Setting<>("StartValue", 200, 1, 1000, v -> setting.getValue() == Values.BREAK && inhibit.getValue()));
    public Setting<Integer> endVal = register(new Setting<>("EndValue", 400, 1, 1000, v -> setting.getValue() == Values.BREAK && inhibit.getValue()));
    /*public Setting<Boolean> fadeRender = register(new Setting<>("Fade", true, v -> setting.getValue() == Values.RENDER && render.getValue()));
    public Setting<Boolean> customOutline = register(new Setting<>("CustomLine", false, v -> setting.getValue() == Values.RENDER && render.getValue() && outline.getValue()));
    private final Setting<Integer> cRed = register(new Setting<>("OL-Red", 255, 0, 255, v -> setting.getValue() == Values.RENDER && render.getValue() && customOutline.getValue() && outline.getValue()));
    private final Setting<Integer> cGreen = register(new Setting<>("OL-Green", 255, 0, 255, v -> setting.getValue() == Values.RENDER && render.getValue() && customOutline.getValue() && outline.getValue()));
    private final Setting<Integer> cBlue = register(new Setting<>("OL-Blue", 255, 0, 255, v -> setting.getValue() == Values.RENDER && render.getValue() && customOutline.getValue() && outline.getValue()));*/

    //MISC
    public Setting<Boolean> instantExplode = register(new Setting<>("InstantBreak", true, v -> setting.getValue() == Values.BREAK));
    public Setting<Integer> packetAmount = register(new Setting<>("PacketAmount", 1, 1, 20, v -> setting.getValue() == Values.BREAK && instantExplode.getValue()));
    //RENDER
    public Setting<Boolean> render = register(new Setting<>("Render", true, v -> setting.getValue() == Values.RENDER));
    public Setting<Boolean> outline = register(new Setting<>("Outline", true, v -> setting.getValue() == Values.RENDER && render.getValue()));
    private final Setting<Float> lineWidth = register(new Setting<>("LineWidth", 1.5f, 0.1f, 5.0f, v -> setting.getValue() == Values.RENDER && render.getValue() && outline.getValue()));
    public Setting<Boolean> singleRender = register(new Setting<>("SingleRender", true, v -> setting.getValue() == Values.RENDER && render.getValue()));
    public Setting<Integer> renderTime = register(new Setting<>("RenderTimeMS", 500, 0, 2000, v -> setting.getValue() == Values.RENDER && render.getValue()));
    private final Setting<Color> color = register(new Setting<>("Color", new Color(255, 0, 0, 100), v -> setting.getValue() == Values.RENDER && render.getValue()));
    public Setting<Boolean> text = register(new Setting("Text", false, v -> setting.getValue() == Values.RENDER && render.getValue()));
    public Setting<Boolean> rainbow = register(new Setting<>("Rainbow", true, v -> setting.getValue() == Values.RENDER));
    public Setting<Boolean> astolfo = register(new Setting<>("Astolfo", false, v -> setting.getValue() == Values.RENDER && !rainbow.getValue()));
    private final Setting<Float> rainbowSpeed = register(new Setting<>("RainbowSpeed", 1f, 0f, 100f, v -> setting.getValue() == Values.RENDER && render.getValue() && rainbow.getValue()));
    public Setting<Float> minDamage = register(new Setting<>("MinDamage", 4.0f, 0.1f, 36.0f, v -> setting.getValue() == Values.MISC));
    public Setting<Float> facePlace = register(new Setting<>("FacePlace", 8.0f, 0.1f, 36.0f, v -> setting.getValue() == Values.MISC));
    public Setting<Float> maxSelf = register(new Setting<>("MaxSelf", 8.0f, 0.1f, 36.0f, v -> setting.getValue() == Values.MISC));
    public Setting<Float> armorPercent = register(new Setting<>("Armor%", 10.0F, 0.0f, 100.0f, v -> setting.getValue() == Values.MISC));
    public Setting<Float> range = register(new Setting<>("Range", 12.0f, 0.1f, 20.0f, v -> setting.getValue() == Values.MISC));
    public Setting<Switch> switchValue = register(new Setting<>("Switch", Switch.NONE, v -> setting.getValue() == Values.MISC));
    public Setting<Boolean> second = register(new Setting<>("Second", false, v -> setting.getValue() == Values.MISC));
    public Setting<Boolean> soundRemove = register(new Setting<>("SoundRemove", false, v -> setting.getValue() == Values.MISC));
    public Setting<Boolean> setDead = register(new Setting<>("SetDead", false, v -> setting.getValue() == Values.MISC));
    public Setting<Boolean> optimize = register(new Setting<>("Optimize", true, v -> setting.getValue() == Values.MISC));
    public Setting<Optimize> optimizeType = register(new Setting<>("OptimizeType", Optimize.NONE, v -> setting.getValue() == Values.MISC && optimize.getValue()));
    public Setting<Boolean> multiTask = register(new Setting<>("MultiTask", true, v -> setting.getValue() == Values.MISC));
    public Setting<Boolean> traceSides = register(new Setting<>("TraceSides", true, v -> setting.getValue() == Values.MISC));
    public Setting<Boolean> sequential = register(new Setting<>("Sequential", true, v -> setting.getValue() == Values.MISC));
    //DOUBLEPOP
    public Setting<Boolean> antiTotem = register(new Setting<>("AntiTotem", false, v -> setting.getValue() == Values.ANTITOTEM));
    public Setting<Integer> popTime = register(new Setting<>("Time", 500, 0, 2000, v -> setting.getValue() == Values.ANTITOTEM));
    public Setting<Float> popDamage = register(new Setting<>("Damage", 5.2F, 0.0F, 20.0F, v -> setting.getValue() == Values.ANTITOTEM));
    //ROTATE
    public Setting<Boolean> rotate = register(new Setting<>("Rotate", true, v -> setting.getValue() == Values.ROTATE));
    public Setting<Rotate> rotateType = register(new Setting<>("RotateType", Rotate.PACKET, v -> setting.getValue() == Values.ROTATE && rotate.getValue()));
    public Map<Integer, Integer> colorHeightMap = new HashMap<Integer, Integer>();
    Timer timer = new Timer();
    Inhibitator inhibitator = new Inhibitator();
    private EntityPlayer currentTarget;
    private BlockPos lastPos;
    private boolean mainHand;
    private boolean offHand;
    private double renderDamage = 0.0;

    public AutoCrystal() {
        super("AutoCrystal", "Attacks entities in radius.", Category.COMBAT, true, false, false);
    }

    public static void enableGL3D() {
        enableGL3D(1.0F);
    }

    public static void enableGL3D(final float lineWidth) {
        GL11.glLineWidth(lineWidth);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        //GlStateManager.disableLighting();
        GL11.glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
    }

    public static void disableGL3D() {
        GL11.glDisable(GL_LINE_SMOOTH);
        //GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
    }

    public static int getRainbow() {
        return Color.HSBtoRGB(hue, 150 / 255.0F, 150 / 255.0F);
    }

    public static AxisAlignedBB getRenderBB(BlockPos pos, float height) {
        final Minecraft mc = Minecraft.getMinecraft();
        return new AxisAlignedBB(
                pos.getX() - mc.getRenderManager().viewerPosX,
                pos.getY() - mc.getRenderManager().viewerPosY,
                pos.getZ() - mc.getRenderManager().viewerPosZ,
                pos.getX() + 1 - mc.getRenderManager().viewerPosX,
                pos.getY() + height - mc.getRenderManager().viewerPosY,
                pos.getZ() + 1 - mc.getRenderManager().viewerPosZ
        );
    }

    public static float[] getRotations(double x, double y, double z) {
        double xDiff = x - Util.mc.player.posX;
        double yDiff = y - getEyeHeight(Util.mc.player);
        double zDiff = z - Util.mc.player.posZ;
        double dist = MathHelper.sqrt(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
        float diff = yaw - Util.mc.player.rotationYaw;

        if (diff < -180.0f || diff > 180.0f) {
            float round = Math.round(Math.abs(diff / 360.0f));
            diff = diff < 0.0f ? diff + 360.0f * round : diff - (360.0f * round);
        }

        return new float[]{
                Util.mc.player.rotationYaw + diff, pitch
        };
    }

    public static double getEyeHeight(final Entity entity) {
        return entity.posY + entity.getEyeHeight();
    }

    public static Vec3d getEyesPos(final Entity entity) {
        return new Vec3d(entity.posX, getEyeHeight(entity), entity.posZ);
    }

    public static EnumFacing getFacing(Entity entity, BlockPos pos, boolean verticals) {
        Vec3d eyePos = getEyesPos(entity);
        for (EnumFacing facing : VALUESNODOWN) {
            RayTraceResult result = Util.mc.world.rayTraceBlocks(eyePos, new Vec3d(pos.getX() + 0.5 + facing.getDirectionVec().getX() * 1.0 / 2.0, pos.getY() + 0.5 + facing.getDirectionVec().getY() * 1.0 / 2.0, pos.getZ() + 0.5 + facing.getDirectionVec().getZ() * 1.0 / 2.0), false, true, false);

            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && result.getBlockPos().equals(pos)) {
                return facing;
            }
        }

        if (verticals) {
            if (pos.getY() > Util.mc.player.posY + Util.mc.player.getEyeHeight()) {
                return EnumFacing.DOWN;
            }

            return EnumFacing.UP;
        }

        return null;
    }

    public static boolean canBlockBeSeen(Entity entity, BlockPos pos, boolean proper) {
        if (proper) {
            return raytracePlaceCheck(entity, pos);
        }
        return Util.mc.world.rayTraceBlocks(getEyesPos(entity), new Vec3d(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D), false, false, false) == null;
    }

    public static boolean raytracePlaceCheck(Entity entity, BlockPos pos) {
        return getFacing(entity, pos, false) != null;
    }

    public static boolean isArmorUnderPercent(EntityPlayer player, float percent) {
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack == null || stack.isEmpty || getDamageInPercent(stack) <= percent) {
                return true;
            }
        }
        return false;
    }

    public static float getDamageInPercent(ItemStack stack) {
        float green = ((float) stack.getMaxDamage() - (float) stack.getItemDamage()) / (float) stack.getMaxDamage();
        float red = 1.0f - green;
        return 100 - (int) (red * 100.0f);
    }

    public static long getPing() {
        return SeleneLoader.serverManager.getPing();
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject && explode.getValue()) {
            final SPacketSpawnObject packet = event.getPacket();
            if (packet.getType() == 51) {
                final BlockPos pos = new BlockPos(packet.getX(), packet.getY() - 1, packet.getZ());
                if (sequential.getValue() && lastPos != null && lastPos.equals(pos)) {
                    Util.mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(lastPos, EnumFacing.UP, offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5F, 1.0F, 0.5F));
                }
                breakTimer.reset();
            }
        }

        if (event.getPacket() instanceof SPacketSoundEffect && soundRemove.getValue()) {
            final SPacketSoundEffect packet = event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                Util.mc.addScheduledTask(() -> {
                    for (Entity entity : Util.mc.world.loadedEntityList) {
                        if (entity instanceof EntityEnderCrystal && entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < 36) {
                            entity.setDead();
                        }
                    }
                });
            }
        }

        if (event.getPacket() instanceof SPacketEntityStatus && antiTotem.getValue()) {
            SPacketEntityStatus packet = event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(Util.mc.world) instanceof EntityPlayer) {
                popMap.put((EntityPlayer) packet.getEntity(Util.mc.world), System.currentTimeMillis());
            }
        }

        if (event.getPacket() instanceof SPacketDestroyEntities) {
            final SPacketDestroyEntities packet = event.getPacket();
            for (int id : packet.getEntityIDs()) {
                Entity entity = Util.mc.world.getEntityByID(id);
                if (entity instanceof EntityEnderCrystal) {
                    placedPos.remove(new BlockPos(entity.posX, entity.posY - 1, entity.posZ));
                }
                SPacketSpawnObject spawnedCrystal = new SPacketSpawnObject();
                if (event.getPacket() instanceof SPacketSpawnObject && (spawnedCrystal = event.getPacket()).getType() == 51 && this.explode.getValue() && this.instantExplode.getValue()) {
                    CPacketUseEntity attackPacket = new CPacketUseEntity();
                    ((ICPacketUseEntity) attackPacket).setEntityId(spawnedCrystal.getEntityID());
                    ((ICPacketUseEntity) attackPacket).setAction(ATTACK);

                    for (int i = 1; i <= packetAmount.getValue(); i++) {
                        mc.player.connection.sendPacket(attackPacket);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (inhibit.getValue()) {
            inhibitator.doInhibitation(breakDelay, 0, endVal.getValue(), startVal.getValue(), 5);
        }
        if (optimizeType.getValue() == Optimize.PRE && event.getStage() == 1) return;
        if (optimizeType.getValue() == Optimize.POST && event.getStage() == 0) return;
        if (rotate.getValue() || optimize.getValue() && optimizeType.getValue() == Optimize.NONE) {
            doAutoCrystal();
        }
        int colorSpeed = (int) (101 - this.rainbowSpeed.getValue());
        hue = (float) (System.currentTimeMillis() % (360L * colorSpeed)) / (360.0f * (float) colorSpeed);
        for (int i = 0; i <= 510; ++i) {
            this.colorHeightMap.put(i, Color.HSBtoRGB(hue, (float) 150 / 255.0f, (float) 150 / 255.0f));
            hue += 0.0013071896f;
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        BlockPos removable = null;
        if (render.getValue() && (offHand || mainHand || switchValue.getValue() == Switch.SILENT) && !renderMap.isEmpty()) {
            removable = null;

            Color color = astolfo.getValue() ? PaletteHelper.astolfo(false, 1) : rainbow.getValue() ? new Color(getRainbow()) : new Color(this.color.getValue().getRed(), this.color.getValue().getGreen(), this.color.getValue().getBlue());
            enableGL3D();
            for (Object2LongMap.Entry<BlockPos> entry : renderMap.object2LongEntrySet()) {
                BlockPos pos = entry.getKey();
                long millis = entry.getLongValue();
                long dura = System.currentTimeMillis() - millis;
                if (dura > renderTime.getValue() || pos == null) {
                    removable = pos;
                    continue;
                }
//              GradientShader.setup(Colors.INSTANCE.opacity.getValue());
//              RenderUtil.boxShader(pos);
//              RenderUtil.outlineShader(pos);
//              GradientShader.finish();
                RenderUtil.drawBox(pos, color, true, this.color.getValue().getAlpha());
                if (outline.getValue()) {
                    RenderUtil.drawBlockOutline(pos, color, this.lineWidth.getValue(), false);
                }
                if (text.getValue()) {
                    RenderUtil.drawText(pos, (Math.floor(renderDamage) == renderDamage ? (int) renderDamage : String.format("%.1f", renderDamage)) + "");
                }
            }
        }

        if (removable != null) {
            renderMap.removeLong(removable);
        }

        disableGL3D();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacketSend(PacketEvent.Send event) {
        CPacketUseEntity packet = new CPacketUseEntity();
        if (instantPlace.getValue() && event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet = event.getPacket()).getAction() == ATTACK && packet.getEntityFromWorld(Util.mc.world) instanceof EntityEnderCrystal) {
            doPlace();
        }
        if (setDead.getValue() && event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet = event.getPacket()).getAction() == ATTACK && packet.getEntityFromWorld(AutoCrystal.mc.world) instanceof EntityEnderCrystal) {
            Entity entity = packet.getEntityFromWorld(Util.mc.world);
            if (entity.isAddedToWorld()) {
                entity.setDead();
                Util.mc.world.removeEntityFromWorld(entity.entityId);
            }
        }
    }

    @Override
    public void onExplosion() {
        if (instantPlace.getValue()) {
            doPlace();
        }
    }

    @Override
    public void onRenderTick() {
        if (inhibit.getValue()) {
            inhibitator.doInhibitation(breakDelay, 0, endVal.getValue(), startVal.getValue(), 5);
        }
        if (!rotate.getValue()) {
            doAutoCrystal();
        }
    }

    public void doAutoCrystal() {
        lastPos = null;
        if (Feature.fullNullCheck()) {
            return;
        }

        if (!multiTask.getValue()) {
            if (Util.mc.player.getHeldItemMainhand().getItem() instanceof ItemFood && Util.mc.player.isHandActive() && Util.mc.player.getActiveHand() == EnumHand.MAIN_HAND) {
                return;
            }
        }

        offHand = Util.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        mainHand = Util.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL;
        currentTarget = null;
        if (!placedPos.isEmpty() && placeTimer.passedMs(2500))
            placedPos.clear();
        if (timingMode.getValue() == Timing.BREAKPLACE) {
            doBreak();
            doPlace();
        } else if (timingMode.getValue() == Timing.PLACEBREAK) {
            doPlace();
            doBreak();
        }
    }

    public void doBreak() {
        if (explode.getValue() && breakTimer.passedMs(breakDelay.getValue()) && (offHand || mainHand || switchValue.getValue() != Switch.NONE)) {
            Entity crystal = calculateBreak(currentTarget);
            if (crystal != null) {
                if (rotate.getValue()) {
                    float[] rotations = getRotations(crystal.posX, crystal.posY, crystal.posZ);
                    if (rotateType.getValue() == Rotate.PACKET) {
                        RotationUtil.rotatePacket(crystal.getPosition());
                    }
                    if (rotateType.getValue() == Rotate.CLIENT) {
                        SeleneLoader.rotationManager.setPlayerRotations(rotations[0], rotations[1]);
                    }
                }

                if (packetExplode.getValue()) {
                    AutoCrystal.mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                } else {
                    Util.mc.playerController.attackEntity(Util.mc.player, crystal);
                }
                swing();
                breakTimer.reset();
            }
        }
    }

    public void doPlace() {
        if (place.getValue() && placeTimer.passedMs(placeDelay.getValue()) && (offHand || mainHand || switchValue.getValue() != Switch.NONE)) {
            BlockPos pos = calculatePlace();
            if (pos != null) {
                placedPos.add(pos);
                if (rotate.getValue()) {
                    float[] rotations = getRotations(pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F);
                    Vec3d crystalVec = new Vec3d(pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F);
                    if (rotateType.getValue() == Rotate.PACKET) {
                        RotationUtil.rotatePacket(crystalVec);
                    }
                    if (rotateType.getValue() == Rotate.CLIENT) {
                        SeleneLoader.rotationManager.setPlayerRotations(rotations[0], rotations[1]);
                    }
                }

                int lastSlot = -1;
                if (!offHand && !mainHand) {
                    int crystalSlot = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
                    if (crystalSlot == -1) return;
                    if (switchValue.getValue() == Switch.NORMAL) {
                        InventoryUtil.switchToHotbarSlot(crystalSlot, false);
                        if (switchCooldown.getValue() > 0) {
                            return;
                        }
                    } else {
                        lastSlot = Util.mc.player.inventory.currentItem;
                        Util.mc.getConnection().sendPacket(new CPacketHeldItemChange(crystalSlot));
                        Util.mc.player.swingArm(EnumHand.MAIN_HAND);
                    }
                }
                lastPos = pos;
                Util.mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5F, 1.0F, 0.5F));
                if (lastSlot != -1) {
                    Util.mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
                }
                placeTimer.reset();

                if (singleRender.getValue()) {
                    renderMap.clear();
                }
                renderMap.put(pos, System.currentTimeMillis());
                swing();
            }
            if (currentTarget != null) {
                renderDamage = DamageUtil.calculateDamage(pos, currentTarget);
            }
        }
    }

    public BlockPos calculatePlace() {
        BlockPos blockPos = null;
        float maxDamage = 1.0F;

        float radius = placeRange.getValue();
        EntityPlayer target = null;
        BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
        for (float x = radius; x >= -radius; x--) {
            for (float z = radius; z >= -radius; z--) {
                for (float y = radius; y >= -radius; y--) {
                    pos.setPos(Util.mc.player.posX + x, Util.mc.player.posY + y, Util.mc.player.posZ + z);
                    final double distance = Util.mc.player.getDistanceSq(pos);
                    if (distance > radius * radius)
                        continue;

                    if (BlockUtil.canPlaceCrystal(pos, second.getValue(), dotThirteen.getValue())) {
                        if (distance > MathUtil.square(placeTrace.getValue()) && !canBlockBeSeen(Util.mc.player, pos, traceSides.getValue()))
                            continue;

                        final float selfDamage = DamageUtil.calculateDamage(pos, Util.mc.player);
                        if (selfDamage + 0.5F < EntityUtil.getHealth(Util.mc.player) && maxSelf.getValue() > selfDamage) {
                            for (EntityPlayer player : Util.mc.world.playerEntities) {
                                if (EntityUtil.isPlayerValid(player, range.getValue())) {
                                    float damage = DamageUtil.calculateDamage(pos, player);
                                    if (isDoublePoppable(player, damage) || damage >= maxDamage && (damage >= minDamage.getValue() || EntityUtil.getHealth(player) <= facePlace.getValue() || isArmorUnderPercent(player, armorPercent.getValue())) && (damage > selfDamage || damage > EntityUtil.getHealth(player) + 1.0F)) {
                                        maxDamage = damage;
                                        blockPos = pos.toImmutable();
                                        target = player;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        pos.release();
        currentTarget = target;
        return blockPos;
    }

    public void swing() {
        if (swingMode.getValue() == Swing.OFFHAND) {
            Util.mc.player.swingArm(EnumHand.OFF_HAND);
        }
        if (this.swingMode.getValue() == Swing.MAINHAND) {
            Util.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        if (swingMode.getValue() == Swing.AUTO && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            Util.mc.player.swingArm(EnumHand.MAIN_HAND);
        } else if (swingMode.getValue() == Swing.AUTO && mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            Util.mc.player.swingArm(EnumHand.OFF_HAND);
        }
    }

    public Entity calculateBreak(EntityPlayer player) {
        Entity crystal = null;
        float maxDamage = 0.1F;
        final boolean lowArmor = armorPercent.getValue() > 0.0F && player != null && isArmorUnderPercent(player, armorPercent.getValue());
        for (Entity entity : Util.mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderCrystal && !entity.isDead) {
                double distance = Util.mc.player.getDistanceSq(entity);
                if (distance > MathUtil.square(breakRange.getValue())) {
                    continue;
                }

                if (distance > MathUtil.square(breakTrace.getValue()) && !Util.mc.player.canEntityBeSeen(entity)) {
                    continue;
                }

                final float selfDamage = DamageUtil.calculateDamage(entity, Util.mc.player);
                if (selfDamage + 0.5F < EntityUtil.getHealth(Util.mc.player) && maxSelf.getValue() > selfDamage) {
                    if (player != null) {
                        final float damage = DamageUtil.calculateDamage(entity, player);
                        if (isDoublePoppable(player, damage) || damage > maxDamage && (damage >= minDamage.getValue() || EntityUtil.getHealth(player) <= facePlace.getValue() || lowArmor) || damage > EntityUtil.getHealth(player) + 1.0F) {
                            crystal = entity;
                            maxDamage = damage;
                        }
                    } else {
                        for (EntityPlayer player1 : Util.mc.world.playerEntities) {
                            if (EntityUtil.isPlayerValid(player1, range.getValue())) {
                                final float damage = DamageUtil.calculateDamage(entity, player1);
                                if (isDoublePoppable(player1, damage) || damage > maxDamage && (damage >= minDamage.getValue() || EntityUtil.getHealth(player1) <= facePlace.getValue() || isArmorUnderPercent(player1, armorPercent.getValue())) || damage > EntityUtil.getHealth(player1) + 1.0F) {
                                    crystal = entity;
                                    maxDamage = damage;
                                }
                            }
                        }
                    }
                }
            }
        }

        return crystal;
    }

    private boolean isDoublePoppable(EntityPlayer player, float damage) {
        if (antiTotem.getValue()) {
            float health = EntityUtil.getHealth(player);
            if (health <= 5.0F && damage > health + 0.5 && damage <= this.popDamage.getValue()) {
                long ms = popMap.getLong(player);
                return System.currentTimeMillis() - ms >= popTime.getValue();
            }
        }
        return false;
    }

    public enum Switch {
        NORMAL,
        SILENT,
        NONE
    }

    public enum Rotate {
        PACKET,
        CLIENT
    }

    public enum Optimize {
        NONE,
        POST,
        PRE
    }

    public enum Swing {
        AUTO,
        MAINHAND,
        OFFHAND,
        NONE
    }

    public enum Timing {
        PLACEBREAK,
        BREAKPLACE
    }

    public enum Values {
        PLACE,
        BREAK,
        RENDER,
        ROTATE,
        MISC,
        ANTITOTEM
    }

}