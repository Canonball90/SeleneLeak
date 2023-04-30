package mod.selene.impl.modules.combat;

import mod.selene.api.utils.ColorUtil;
import mod.selene.api.utils.EntityUtil;
import mod.selene.api.utils.Timer;
import mod.selene.api.utils.interfaces.Util;
import mod.selene.api.utils.math.MathUtil;
import mod.selene.api.utils.render.PaletteHelper;
import mod.selene.api.utils.render.RenderUtil;
import mod.selene.impl.Module;
import mod.selene.injections.inj.IEntityPlayerSP;
import mod.selene.loader.SeleneLoader;
import mod.selene.system.Setting;
import mod.selene.world.Render3DEvent;
import mod.selene.world.UpdateWalkingPlayerEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class Aura extends Module {

    public static Aura INSTANCE;
    protected static Entity target;

    //Global
    private final Setting<Page> page =
            this.register(new Setting<>("Settings", Page.GLOBAL));
    public final Setting<Float> range =
            this.register(new Setting<>("Range", 6.0f, 0.1f, 7.0f, v -> page.getValue() == Page.GLOBAL));
    private final Setting<TargetMode> targetMode =
            this.register(new Setting<>("Filter", TargetMode.SMART, v -> page.getValue() == Page.GLOBAL));
    private final Setting<Float> targetHealth =
            this.register(new Setting<>("Health", 6.0f, 0.1f, 36.0f, v -> targetMode.getValue() == TargetMode.SMART && page.getValue() == Page.TARGETS));
    private final Setting<Float> wallRange =
            this.register(new Setting<>("WallRange", 6.0f, 0.1f, 7.0f, v -> page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> rotate =
            this.register(new Setting<>("Rotate", true, v -> page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> lookBack =
            this.register(new Setting<>("LookBack", true, v -> page.getValue() == Page.GLOBAL && rotate.getValue()));
    private final Setting<Float> yawStep =
            this.register(new Setting<>("YawStep", 0.3f, 0.1f, 1.0f, v -> page.getValue() == Page.GLOBAL && rotate.getValue()));
    private final Setting<Float> pitchAdd =
            this.register(new Setting<>("PitchAdd", 0.0f, 0.0f, 25.0f, v -> page.getValue() == Page.GLOBAL && rotate.getValue()));
    private final Setting<Boolean> randomPitch =
            this.register(new Setting<>("RandomizePitch", false, v -> page.getValue() == Page.GLOBAL && rotate.getValue()));
    private final Setting<Float> amplitude =
            this.register(new Setting<>("Amplitude", 3.0f, -5.0f, 5.0f, v -> page.getValue() == Page.GLOBAL && rotate.getValue() && randomPitch.getValue()));
    private final Setting<Boolean> oneEight =
            this.register(new Setting<>("OneEight", false, v -> page.getValue() == Page.GLOBAL));
    private final Setting<Float> minCps =
            this.register(new Setting<>("MinCps", 6.0f, 0.0f, 20.0f, v -> page.getValue() == Page.GLOBAL && oneEight.getValue()));
    private final Setting<Float> maxCps =
            this.register(new Setting<>("MaxCps", 9.0f, 0.0f, 20.0f, v -> page.getValue() == Page.GLOBAL && oneEight.getValue()));
    private final Setting<Float> randomDelay =
            this.register(new Setting<>("RandomDelay", 0.0f, 0.0f, 5.0f, v -> page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> fovCheck =
            this.register(new Setting<>("FovCheck", false, v -> page.getValue() == Page.GLOBAL));
    private final Setting<Float> angle =
            this.register(new Setting<>("Angle", 180.0f, 0.0f, 180.0f, v -> page.getValue() == Page.GLOBAL && fovCheck.getValue()));
    private final Setting<Boolean> stopSprint =
            this.register(new Setting<>("StopSprint", true, v -> page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> armorBreak =
            this.register(new Setting<>("ArmorBreak", false, v -> page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> whileEating =
            this.register(new Setting<>("WhileEating", true, v -> page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> weaponOnly =
            this.register(new Setting<>("WeaponOnly", true, v -> page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> tpsSync =
            this.register(new Setting<>("TpsSync", true, v -> page.getValue() == Page.GLOBAL));
    private final Setting<Boolean> packet =
            this.register(new Setting<>("Packet", false, v -> page.getValue() == Page.ADVANCED));
    private final Setting<Boolean> swing =
            this.register(new Setting<>("Swing", true, v -> page.getValue() == Page.GLOBAL));
    //Targets
    private final Setting<Boolean> sneak =
            this.register(new Setting<>("Sneak", false, v -> page.getValue() == Page.ADVANCED));
    private final Setting<Boolean> players =
            this.register(new Setting<>("Players", true, v -> page.getValue() == Page.TARGETS));
    private final Setting<Boolean> animals =
            this.register(new Setting<>("Animals", false, v -> page.getValue() == Page.TARGETS));
    private final Setting<Boolean> neutrals =
            this.register(new Setting<>("Neutrals", false, v -> page.getValue() == Page.TARGETS));
    private final Setting<Boolean> others =
            this.register(new Setting<>("Others", false, v -> page.getValue() == Page.TARGETS));
    private final Setting<Boolean> projectiles =
            this.register(new Setting<>("Projectiles", false, v -> page.getValue() == Page.TARGETS));
    private final Setting<Boolean> hostiles =
            this.register(new Setting<>("Hostiles", true, v -> page.getValue() == Page.TARGETS));

    //Advanced
    private final Setting<Boolean> onlyGhasts =
            this.register(new Setting<>("OnlyGhasts", false, v -> hostiles.getValue() && page.getValue() == Page.TARGETS));
    private final Setting<Boolean> teleport =
            this.register(new Setting<>("Teleport", false, v -> page.getValue() == Page.ADVANCED));
    private final Setting<Float> teleportRange =
            this.register(new Setting<>("TpRange", 15.0f, 0.1f, 50.0f, v -> teleport.getValue() && page.getValue() == Page.ADVANCED));
    private final Setting<Boolean> lagBack =
            this.register(new Setting<>("LagBack", true, v -> teleport.getValue() && page.getValue() == Page.ADVANCED));
    private final Setting<Boolean> delay32k =
            this.register(new Setting<>("32kDelay", false, v -> page.getValue() == Page.ADVANCED));
    private final Setting<Integer> packetAmount32k =
            this.register(new Setting<>("32kPackets", 2, v -> !delay32k.getValue() && page.getValue() == Page.ADVANCED));
    private final Setting<Integer> time32k =
            this.register(new Setting<>("32kTime", 5, 1, 50, v -> page.getValue() == Page.ADVANCED));
    private final Setting<Boolean> multi32k =
            this.register(new Setting<>("Multi32k", false, v -> page.getValue() == Page.ADVANCED));
    // Render
    private final Setting<RenderMode> render =
            this.register(new Setting<>("Render", RenderMode.BOX, v -> page.getValue() == Page.RENDER));
    private final Setting<Boolean> rainbow =
            this.register(new Setting<>("Rainbow", true, v -> page.getValue() == Page.RENDER));
    private final Setting<Boolean> astolfo =
            this.register(new Setting<>("Astolfo", false, v -> page.getValue() == Page.RENDER && !rainbow.getValue()));
    public final Setting<Color> color =
            this.register(new Setting<>("Color", new Color(255, 0, 0, 100), v -> page.getValue() == Page.RENDER && !rainbow.getValue()));
    private final Setting<Integer> rainbowDelay =
            this.register(new Setting<>("RainbowDelay", 20, 0, 255, v -> page.getValue() == Page.RENDER && rainbow.getValue()));
    private final Setting<Integer> rainbowSaturation =
            this.register(new Setting<>("RainbowSaturation", 150, 1, 255, v -> page.getValue() == Page.RENDER && rainbow.getValue()));
    private final Setting<Integer> rainbowBrightness =
            this.register(new Setting<>("RainbowBrightness", 150, 1, 255, v -> page.getValue() == Page.RENDER && rainbow.getValue()));
    private final Timer timer = new Timer();

    public Aura() {
        super("Aura", "Attacks entities in radius.", Category.COMBAT, true, false, false);
        INSTANCE = this;
    }

    public static Color pulseColor(Color color, int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs((System.currentTimeMillis() % ((long) 1230675006 ^ 0x495A9BEEL) / Float.intBitsToFloat(Float.floatToIntBits(0.0013786979f) ^ 0x7ECEB56D) + index / (float) count * Float.intBitsToFloat(Float.floatToIntBits(0.09192204f) ^ 0x7DBC419F)) % Float.intBitsToFloat(Float.floatToIntBits(0.7858098f) ^ 0x7F492AD5) - Float.intBitsToFloat(Float.floatToIntBits(6.46708f) ^ 0x7F4EF252));
        brightness = Float.intBitsToFloat(Float.floatToIntBits(18.996923f) ^ 0x7E97F9B3) + Float.intBitsToFloat(Float.floatToIntBits(2.7958195f) ^ 0x7F32EEB5) * brightness;
        hsb[2] = brightness % Float.intBitsToFloat(Float.floatToIntBits(0.8992331f) ^ 0x7F663424);
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static int getHitCoolDown(EntityPlayer player) {
        Item item = player.getHeldItemMainhand().getItem();

        if (item instanceof ItemSword) {
            return 600;
        }
        if (item instanceof ItemPickaxe) {
            return 850;
        }
        if (item == Items.IRON_AXE) {
            return 1100;
        }
        if (item == Items.STONE_HOE) {
            return 500;
        }
        if (item == Items.IRON_HOE) {
            return 350;
        }
        if (item == Items.WOODEN_AXE || item == Items.STONE_AXE) {
            return 1250;
        }
        if (item instanceof ItemSpade || item == Items.GOLDEN_AXE || item == Items.DIAMOND_AXE || item == Items.WOODEN_HOE || item == Items.GOLDEN_HOE) {
            return 1000;
        }
        return 250;
    }

    public static float randomBetween(float min, float max) {
        return min + (new Random().nextFloat() * (max - min));
    }

    public static boolean isArmorLow(EntityPlayer player, int durability) {
        for (ItemStack piece : player.inventory.armorInventory) {

            if (piece == null) {
                return true;
            }

            if (getDamagePercent(piece) >= durability) continue;
            return true;
        }
        return false;
    }

    public static int getDamagePercent(ItemStack stack) {
        return (int) ((stack.getMaxDamage() - stack.getItemDamage()) / Math.max(0.1, stack.getMaxDamage()) * 100.0f);
    }

    public static boolean isFeetVisible(Entity entity) {
        return Util.mc.world.rayTraceBlocks(
                new Vec3d(Util.mc.player.posX, Util.mc.player.posX + (double) Util.mc.player.getEyeHeight(), Util.mc.player.posZ),
                new Vec3d(entity.posX, entity.posY, entity.posZ),
                false,
                true,
                false) == null;
    }

    public static Color getRainbow() {
        return ColorUtil.rainbow(INSTANCE.rainbowSaturation.getValue(), INSTANCE.rainbowBrightness.getValue(), INSTANCE.rainbowDelay.getValue());
    }

    @Override
    public String getInfo() {
        String modeInfo = normalizeCases(targetMode.getValue());
        String targetInfo = target instanceof EntityPlayer ? ", " + (target.getName()) : "";

        return modeInfo + targetInfo;
    }

    public String normalizeCases(Object o) {
        return Character.toUpperCase(o.toString().charAt(0)) + o.toString().toLowerCase().substring(1);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (Util.mc == null) {
            return;
        }
        if (target != null) {
            if (render.getValue() == RenderMode.BOX) {
                Color renderColor = astolfo.getValue() ? PaletteHelper.astolfo(false, 1) : rainbow.getValue() ? getRainbow() : new Color(color.getValue().getRed(), color.getValue().getGreen(), color.getValue().getBlue());
                RenderUtil.DrawEntityBoxESP(target.getRenderBoundingBox(), renderColor, color.getValue().getAlpha());
            }
        }
        if (target != null) {

            if (render.getValue() == RenderMode.COOL) {
                double everyTime = 1500;
                double drawTime = (System.currentTimeMillis() % everyTime);
                boolean drawMode = drawTime > (everyTime / 2);
                double drawPercent = drawTime / (everyTime / 2);

                if (!drawMode) {
                    drawPercent = 1 - drawPercent;
                } else {
                    drawPercent -= 1;
                }

                drawPercent = easeInOutQuad(drawPercent);

                Util.mc.entityRenderer.disableLightmap();
                glPushMatrix();
                glDisable(GL_TEXTURE_2D);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glEnable(GL_LINE_SMOOTH);
                glEnable(GL_BLEND);

                glDisable(GL_DEPTH_TEST);
                glDisable(GL_CULL_FACE);
                glShadeModel(7425);
                Util.mc.entityRenderer.disableLightmap();

                double radius = target.width;
                double height = target.height + 0.1;

                double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * Util.mc.getRenderPartialTicks() - Util.mc.renderManager.viewerPosX;
                double y = (target.lastTickPosY + (target.posY - target.lastTickPosY) * Util.mc.getRenderPartialTicks() - Util.mc.renderManager.viewerPosY) + height * drawPercent;
                double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * Util.mc.getRenderPartialTicks() - Util.mc.renderManager.viewerPosZ;
                double eased = (height / 3) * ((drawPercent > 0.5) ? 1 - drawPercent : drawPercent) * ((drawMode) ? -1 : 1);

                for (int segments = 0; segments < 360; segments += 5) {
                    Color color = astolfo.getValue() ? PaletteHelper.astolfo(false, 1) : rainbow.getValue() ? getRainbow() : new Color(this.color.getValue().getRed(), this.color.getValue().getGreen(), this.color.getValue().getBlue());

                    double x1 = x - Math.sin(segments * Math.PI / 180F) * radius;
                    double z1 = z + Math.cos(segments * Math.PI / 180F) * radius;
                    double x2 = x - Math.sin((segments - 5) * Math.PI / 180F) * radius;
                    double z2 = z + Math.cos((segments - 5) * Math.PI / 180F) * radius;

                    glBegin(GL_QUADS);

                    glColor4f(pulseColor(color, 200, 1).getRed() / 255.0f, pulseColor(color, 200, 1).getGreen() / 255.0f, pulseColor(color, 200, 1).getBlue() / 255.0f, 0.0f);
                    glVertex3d(x1, y + eased, z1);
                    glVertex3d(x2, y + eased, z2);

                    glColor4f(pulseColor(color, 200, 1).getRed() / 255.0f, pulseColor(color, 200, 1).getGreen() / 255.0f, pulseColor(color, 200, 1).getBlue() / 255.0f, 200.0f);

                    glVertex3d(x2, y, z2);
                    glVertex3d(x1, y, z1);
                    glEnd();

                    glBegin(GL_LINE_LOOP);
                    glVertex3d(x2, y, z2);
                    glVertex3d(x1, y, z1);
                    glEnd();
                }

                glEnable(GL_CULL_FACE);
                glShadeModel(7424);
                glColor4f(1f, 1f, 1f, 1f);
                glEnable(GL_DEPTH_TEST);
                glDisable(GL_LINE_SMOOTH);
                glDisable(GL_BLEND);
                glEnable(GL_TEXTURE_2D);
                glPopMatrix();
            }
        }
    }

    @Override
    public void onRenderTick() {
        if (!rotate.getValue()) {
            doAura();
        }

        if (maxCps.getValue() < minCps.getValue()) {
            maxCps.setValue(minCps.getValue());
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && rotate.getValue()) {

            if (target != null) {
                float[] angle = MathUtil.calcAngle(Util.mc.player.getPositionEyes(Util.mc.getRenderPartialTicks()), target.getPositionEyes(Util.mc.getRenderPartialTicks()));
                float[] newAngle = injectYawStep(angle, yawStep.getValue());

                SeleneLoader.rotationManager.setPlayerRotations(newAngle[0],
                        newAngle[1] + pitchAdd.getValue() + (randomPitch.getValue() ? ((float) Math.random() * amplitude.getValue()) : 0.0f));
            }
        }
        doAura();
    }

    public float[] injectYawStep(float[] angle, float steps) {

        if (steps < 0.1f) steps = 0.1f;

        if (steps > 1) steps = 1;

        if (steps < 1 && angle != null) {

            float packetYaw = ((IEntityPlayerSP) Util.mc.player).getLastReportedYaw();
            float diff = MathHelper.wrapDegrees(angle[0] - packetYaw);

            if (Math.abs(diff) > 180 * steps) {
                angle[0] = (packetYaw + (diff * ((180 * steps) / Math.abs(diff))));
            }
        }

        return new float[]{
                angle[0],
                angle[1]
        };
    }

    private void doAura() {
        if (weaponOnly.getValue() && !EntityUtil.holdingWeapon(Util.mc.player)) {
            target = null;
            return;
        }
        int wait = (oneEight.getValue() || (EntityUtil.holding32k(Util.mc.player) && !delay32k.getValue())) ?
                (int) (randomBetween(minCps.getValue(), maxCps.getValue()) - new Random().nextInt(10) + new Random().nextInt(10) * 100
                        * (tpsSync.getValue() ? SeleneLoader.serverManager.getTpsFactor() : 1.0f)) :

                ((int) (getHitCoolDown(Util.mc.player) + ((float) Math.random() * randomDelay.getValue() * 100)
                        * (tpsSync.getValue() ? SeleneLoader.serverManager.getTpsFactor() : 1.0f)));

        if (!timer.passedMs(wait) || (!whileEating.getValue() && Util.mc.player.isHandActive() && (!Util.mc.player.getHeldItemOffhand().getItem().equals(Items.SHIELD) || Util.mc.player.getActiveHand() != EnumHand.OFF_HAND))) {
            return;
        }

        if (targetMode.getValue() != TargetMode.FOCUS || target == null || (Util.mc.player.getDistanceSq(target) >= MathUtil.square(range.getValue()) && (!teleport.getValue() || Util.mc.player.getDistanceSq(target) >= MathUtil.square(teleportRange.getValue()))) || (!Util.mc.player.canEntityBeSeen(target) && !isFeetVisible(target) && Util.mc.player.getDistanceSq(target) >= MathUtil.square(wallRange.getValue()) && !teleport.getValue())) {
            target = getTarget();
        }

        if (target == null) {
            return;
        }

        if (teleport.getValue()) {
            SeleneLoader.positionManager.setPositionPacket(target.posX, isFeetVisible(target) ? target.posY : (target.posY + target.getEyeHeight()), target.posZ, true, true, !this.lagBack.getValue());
        }
        if (EntityUtil.holding32k(Util.mc.player) && !delay32k.getValue()) {
            if (multi32k.getValue()) {
                for (EntityPlayer player : Util.mc.world.playerEntities) {
                    if (EntityUtil.isValid(player, range.getValue())) {
                        teekayAttack(player);
                    }
                }
            } else {
                teekayAttack(target);
            }
            timer.reset();
            return;
        }

        if (armorBreak.getValue()) {
            Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, 9, Util.mc.player.inventory.currentItem, ClickType.SWAP, Util.mc.player);
            attackEntity(target, packet.getValue(), swing.getValue());

            Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, 9, Util.mc.player.inventory.currentItem, ClickType.SWAP, Util.mc.player);
            attackEntity(target, packet.getValue(), swing.getValue());

        } else {
            boolean sneaking = Util.mc.player.isSneaking();
            boolean sprinting = Util.mc.player.isSprinting();

            if (sneak.getValue()) {
                if (sneaking) {
                    Util.mc.player.connection.sendPacket(new CPacketEntityAction(Util.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }
                if (sprinting) {
                    Util.mc.player.connection.sendPacket(new CPacketEntityAction(Util.mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
                }
            }

            attackEntity(target, packet.getValue(), swing.getValue());

            if (sneak.getValue()) {
                if (sprinting) {
                    Util.mc.player.connection.sendPacket(new CPacketEntityAction(Util.mc.player, CPacketEntityAction.Action.START_SPRINTING));
                }
                if (sneaking) {
                    Util.mc.player.connection.sendPacket(new CPacketEntityAction(Util.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }
            }

            if (stopSprint.getValue()) {
                Util.mc.player.connection.sendPacket(new CPacketEntityAction(Util.mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            }
        }
        timer.reset();

        if (rotate.getValue() && lookBack.getValue()) {
            SeleneLoader.rotationManager.reset();
        }
    }

    public void attackEntity(Entity entity, boolean packet, boolean swing) {
        double distance = (teleport.getValue() ? teleportRange.getValue() : ((double) (float) range.getValue()));

        if (Util.mc.player.getDistance(entity) <= distance) {
            if (packet) {
                Util.mc.player.connection.sendPacket(new CPacketUseEntity(entity));
            } else {
                Util.mc.playerController.attackEntity(Util.mc.player, entity);
            }
            if (Util.mc.player.getDistance(entity) <= distance) {
                if (swing) {
                    Util.mc.player.swingArm(EnumHand.MAIN_HAND);
                }
            }
        }
    }

    private void teekayAttack(Entity entity) {
        for (int i = 0; i < packetAmount32k.getValue(); ++i) {
            startEntityAttackThread(entity, i * time32k.getValue());
        }
    }

    private void startEntityAttackThread(Entity entity, int time) {
        new Thread(() -> {
            Timer timer = new Timer();
            timer.reset();
            try {
                Thread.sleep(time);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            attackEntity(entity, true, swing.getValue());
        }).start();
    }

    public Entity getTarget() {
        Entity target = null;
        double distance = (teleport.getValue() ? teleportRange.getValue() : ((double) (float) range.getValue()));
        double maxHealth = 36.0;

        for (Entity entity : Util.mc.world.loadedEntityList) {

            if ((players.getValue() && entity instanceof EntityPlayer) || (animals.getValue() && EntityUtil.isPassive(entity)) || (neutrals.getValue() && EntityUtil.isNeutralMob(entity)) || (hostiles.getValue() && EntityUtil.isMobAggressive(entity)) || (hostiles.getValue() && onlyGhasts.getValue() && entity instanceof EntityGhast) || (others.getValue() && EntityUtil.isVehicle(entity)) || (projectiles.getValue() && EntityUtil.isProjectile(entity))) {

                if (EntityUtil.isLiving(entity) && !EntityUtil.isValid(entity, distance)) {
                    continue;
                }

                if (!teleport.getValue() && !Util.mc.player.canEntityBeSeen(entity) && !isFeetVisible(entity) && Util.mc.player.getDistanceSq(entity) > MathUtil.square(wallRange.getValue())) {
                    continue;
                }

                if (fovCheck.getValue() && !isInFov(entity, angle.getValue().intValue())) {
                    continue;
                }

                if (target == null) {
                    target = entity;
                    distance = Util.mc.player.getDistanceSq(entity);
                    maxHealth = EntityUtil.getHealth(entity);

                } else {
                    if (entity instanceof EntityPlayer && isArmorLow((EntityPlayer) entity, 15)) {
                        target = entity;
                        break;
                    }

                    if (targetMode.getValue() == TargetMode.SMART && EntityUtil.getHealth(entity) < targetHealth.getValue()) {
                        target = entity;
                        break;
                    }

                    if (targetMode.getValue() != TargetMode.HEALTH && Util.mc.player.getDistanceSq(entity) < distance) {
                        target = entity;
                        distance = Util.mc.player.getDistanceSq(entity);
                        maxHealth = EntityUtil.getHealth(entity);
                    }

                    if (targetMode.getValue() != TargetMode.HEALTH || EntityUtil.getHealth(entity) >= maxHealth) {
                        continue;
                    }

                    target = entity;
                    distance = Util.mc.player.getDistanceSq(entity);
                    maxHealth = EntityUtil.getHealth(entity);
                }
            }
        }
        return target;
    }

    private boolean isInFov(Entity entity, float angle) {
        double x = entity.posX - Util.mc.player.posX;
        double z = entity.posZ - Util.mc.player.posZ;
        double yaw = Math.atan2(x, z) * 57.29577951308232D;
        yaw = -yaw;
        angle = (float) (angle * 0.5D);
        double angleDifference = ((Util.mc.player.rotationYaw - yaw) % 360.0D + 540.0D) % 360.0D - 180.0D;
        return ((angleDifference > 0.0D) && (angleDifference < angle)) || ((-angle < angleDifference) && (angleDifference < 0.0D));
    }

    private double easeInOutQuad(double x) {
        return (x < 0.5) ? 2 * x * x : 1 - Math.pow((-2 * x + 2), 2) / 2;
    }

    private enum Page {
        GLOBAL,
        TARGETS,
        ADVANCED,
        RENDER
    }

    private enum TargetMode {
        FOCUS,
        HEALTH,
        SMART
    }

    private enum RenderMode {
        BOX,
        COOL
    }
}