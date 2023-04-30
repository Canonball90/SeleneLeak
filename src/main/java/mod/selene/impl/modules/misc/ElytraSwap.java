package mod.selene.impl.modules.misc;

import mod.selene.api.utils.Timer;
import mod.selene.api.utils.interfaces.Util;
import mod.selene.api.utils.render.RenderUtil;
import mod.selene.impl.Module;
import mod.selene.system.Command;
import mod.selene.system.Setting;
import mod.selene.world.Render2DEvent;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class ElytraSwap extends Module {
    private final ResourceLocation toelytra = new ResourceLocation("textures/swapel.png");
    private final ResourceLocation tochest = new ResourceLocation("textures/swapch.png");
    public Setting<Boolean> image = this.register(new Setting<Boolean>("Indicator", true));
    public Setting<Integer> imagex = this.register(new Setting<Integer>("IndicatorX", 512, 0, 1023, v -> image.getValue()));
    public Setting<Integer> imagey = this.register(new Setting<Integer>("IndicatorY", 512, 0, 1023, v -> image.getValue()));
    public Timer timer = new Timer();
    public int swap = 0;

    public ElytraSwap() {
        super("ElytraSwap", "Swap to elytra", Category.MISC, true, false, false);
    }

    public static int getChestPlateSlot() {
        Item[] items = {Items.DIAMOND_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.LEATHER_CHESTPLATE};

        for (Item item : items) {
            if (hasItem(item)) {
                return getSlot(item);
            }
        }

        return -1;
    }

    public static boolean hasItem(Item item) {
        return getAmountOfItem(item) != 0;
    }

    public static int getAmountOfItem(Item item) {
        int count = 0;

        for (ItemStackUtil itemStack : getAllItems()) {
            if (itemStack.itemStack != null && itemStack.itemStack.getItem().equals(item)) {
                count += itemStack.itemStack.getCount();
            }
        }

        return count;
    }

    public static void drawCompleteImage(float posX, float posY, int width, int height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, (float) height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f((float) width, (float) height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f((float) width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static int getClickSlot(int id) {
        if (id == -1) {
            return id;
        }

        if (id < 9) {
            id += 36;
            return id;
        }

        if (id == 39) {
            id = 5;
        } else if (id == 38) {
            id = 6;
        } else if (id == 37) {
            id = 7;
        } else if (id == 36) {
            id = 8;
        } else if (id == 40) {
            id = 45;
        }

        return id;
    }

    public static void clickSlot(int id) {
        if (id != -1) {
            try {
                mc.playerController.windowClick(mc.player.openContainer.windowId, getClickSlot(id), 0, ClickType.PICKUP, mc.player);
            } catch (Exception ignored) {

            }
        }
    }

    public static int getSlot(Item item) {
        try {
            for (ItemStackUtil itemStack : getAllItems()) {
                if (itemStack.itemStack.getItem().equals(item)) {
                    return itemStack.slotId;
                }
            }
        } catch (Exception ignored) {

        }

        return -1;
    }

    public static ArrayList<ItemStackUtil> getAllItems() {
        ArrayList<ItemStackUtil> items = new ArrayList<ItemStackUtil>();

        for (int i = 0; i < 36; i++) {
            items.add(new ItemStackUtil(getItemStack(i), i));
        }

        return items;
    }

    public static ItemStack getItemStack(int id) {
        try {
            return mc.player.inventory.getStackInSlot(id);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public void onEnable() {
        mc.player.setSneaking(true);
        timer.reset();
        ItemStack itemStack = getItemStack(38);

        if (itemStack.getItem() == Items.ELYTRA) {
            int slot = getChestPlateSlot();
            if (slot != -1) {
                clickSlot(slot);
                clickSlot(38);
                clickSlot(slot);
                swap = 1;
            } else {
                Command.sendMessage("You don't have a Chestplate!");
            }
        } else if (hasItem(Items.ELYTRA)) {
            int slot = getSlot(Items.ELYTRA);
            clickSlot(slot);
            clickSlot(38);
            clickSlot(slot);
            swap = 2;
        } else {
            Command.sendMessage("There's no elytra in your Inventory!");
        }
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        System.out.println(swap);
        double psx = imagex.getValue();
        double psy = imagey.getValue();
        float xOffset = (float) psx + 10;
        float yOffset = (float) psy;
        if (swap == 1) {
            RenderUtil.drawRect(400, 400, 400, 400, new Color(252, 252, 252, 255).getRGB());
            Util.mc.getTextureManager().bindTexture(this.toelytra);
            drawCompleteImage(xOffset - 1.0f, yOffset - 160.0f, 49, 49);
        }
        if (swap == 2) {
            RenderUtil.drawRect(400, 400, 400, 400, new Color(252, 252, 252, 255).getRGB());
            Util.mc.getTextureManager().bindTexture(this.tochest);
            drawCompleteImage(xOffset - 1.0f, yOffset - 160.0f, 49, 49);
        }
        if (timer.passedMs(1000)) {
            mc.player.setSneaking(false);
            disable();
        }

    }

    public static class ItemStackUtil {
        public ItemStack itemStack;
        public int slotId;

        public ItemStackUtil(ItemStack itemStack, int slotId) {
            this.itemStack = itemStack;
            this.slotId = slotId;
        }
    }

}
