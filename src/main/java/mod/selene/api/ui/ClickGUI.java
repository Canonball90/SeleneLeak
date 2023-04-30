package mod.selene.api.ui;

import mod.selene.api.ui.components.Component;
import mod.selene.api.ui.components.items.Item;
import mod.selene.api.ui.components.items.buttons.ModuleButton;
import mod.selene.api.ui.particle.ParticleSystem;
import mod.selene.impl.Module;
import mod.selene.impl.modules.client.ClickGui;
import mod.selene.impl.modules.client.Particles;
import mod.selene.loader.SeleneLoader;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGUI extends GuiScreen {

    private static ClickGUI clickGUI;
    private static ClickGUI INSTANCE = new ClickGUI();
    private final ArrayList<Component> components = new ArrayList<>();
    public ParticleSystem particleSystem;

    public ClickGUI() {
        setInstance();
        load();
    }

    public static ClickGUI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGUI();
        }
        return INSTANCE;
    }

    public static ClickGUI getClickGui() {
        return getInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private void load() {
        int x = -84;
        for (Module.Category category : SeleneLoader.moduleManager.getCategories()) {
            components.add(new Component(category.getName(), x += 90, 4, true) {
                @Override
                public void setupItems() {
                    SeleneLoader.moduleManager.getModulesByCategory(category).forEach(module -> {
                        if (!module.hidden) {
                            addButton(new ModuleButton(module));
                        }
                    });
                }
            });
        }
        components.forEach(components -> components.getItems().sort((item1, item2) -> item1.getName().compareTo(item2.getName())));
    }

    public void updateModule(Module module) {
        for (Component component : this.components) {
            for (Item item : component.getItems()) {
                if (item instanceof ModuleButton) {
                    ModuleButton button = (ModuleButton) item;
                    Module mod = button.getModule();
                    if (module != null && module.equals(mod)) {
                        button.initSettings();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        checkMouseWheel();
        this.drawDefaultBackground();
        components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
        ScaledResolution sr = new ScaledResolution(this.mc);
        if (ClickGui.getInstance().gradiant.getValue().booleanValue()) {
            this.drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0, new Color(ClickGui.getInstance().gradiantColor.getValue().getRed(), ClickGui.getInstance().gradiantColor.getValue().getGreen(), ClickGui.getInstance().gradiantColor.getValue().getBlue(), ClickGui.getInstance().gradiantColor.getValue().getAlpha() / 2).getRGB());
        }
        if (this.particleSystem != null && SeleneLoader.moduleManager.isModuleEnabled(Particles.class)) {
            this.particleSystem.render(mouseX, mouseY);
        } else {
            this.particleSystem = new ParticleSystem(new ScaledResolution(this.mc));
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public final ArrayList<Component> getComponents() {
        return components;
    }

    public void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            components.forEach(component -> component.setY(component.getY() - 10));
        } else if (dWheel > 0) {
            components.forEach(component -> component.setY(component.getY() + 10));
        }
    }

    public int getTextOffset() {
        return -6;
    }

    public Component getComponentByName(String name) {
        for (Component component : this.components) {
            if (component.getName().equalsIgnoreCase(name)) {
                return component;
            }
        }
        return null;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
    }

    public void updateScreen() {
        if (this.particleSystem != null) {
            this.particleSystem.update();
        }
    }
}