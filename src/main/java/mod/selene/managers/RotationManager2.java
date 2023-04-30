package mod.selene.managers;

import net.minecraft.client.Minecraft;

public class RotationManager2 {

    public static final RotationManager2 INSTANCE = new RotationManager2();
    private final Minecraft mc = Minecraft.getMinecraft();
    private float yaw, pitch;
    private boolean rotated;
    private int ticksSinceNoRotate;

    public static RotationManager2 getInstance() {
        return INSTANCE;
    }

    public void updateRotations() {
        yaw = mc.player.rotationYaw;
        pitch = mc.player.rotationPitch;
    }

    public void restoreRotations() {
        ticksSinceNoRotate++;
        if (ticksSinceNoRotate > 2) {
            rotated = false;
        }
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }

    public void setPlayerRotations(float yaw, float pitch) {
        rotated = true;
        ticksSinceNoRotate = 0;
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public boolean isRotated() {
        return rotated;
    }

}