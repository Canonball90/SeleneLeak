package mod.selene.impl.modules.movement;

import mod.selene.api.utils.EntityUtil;
import mod.selene.impl.Module;
import mod.selene.system.Setting;

/**
 * @author linustouchtips
 * @since 06/08/2021
 */
public class Sprint extends Module {
    public static Sprint INSTANCE;
    public Setting<Mode> mode = register(new Setting<>("Mode", Mode.DIRECTIONAL));

    // **************************** general ****************************
    public Setting<Boolean> safe = register(new Setting<>("Safe", true));
    public Setting<Boolean> strict = register(new Setting<>("Strict", false));

    public Sprint() {
        super("Sprint", "makes you sprint", Category.MOVEMENT, false, false, true);
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {

        // reset sprint state
        mc.player.setSprinting(false);

        // verify if the player's food level allows sprinting
        if (mc.player.getFoodStats().getFoodLevel() <= 6 && safe.getValue()) {
            return;
        }

        // verify whether or not the player can actually sprint
        if ((mc.player.isHandActive() || mc.player.isSneaking()) && strict.getValue()) {
            return;
        }

        // update player sprint state
        if (EntityUtil.isMoving()) {
            switch (mode.getValue()) {
                case DIRECTIONAL:
                    if ((mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) && !(mc.player.isSneaking() || mc.player.collidedHorizontally || mc.player.getFoodStats().getFoodLevel() <= 6f)) {
                        mc.player.setSprinting(true);
                    }
                case NORMAL:
                    if (mc.gameSettings.keyBindForward.isKeyDown() && !(mc.player.isSneaking() || mc.player.isHandActive() || mc.player.collidedHorizontally || mc.player.getFoodStats().getFoodLevel() <= 6f) && mc.currentScreen == null) {
                        mc.player.setSprinting(true);
                    }
                    break;
            }
        }
    }

    public enum Mode {
        /**
         * Allows you to sprint in all directions
         */
        DIRECTIONAL,

        /**
         * Allows sprinting when moving forward
         */
        NORMAL
    }
}