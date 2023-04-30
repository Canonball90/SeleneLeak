package mod.selene.managers;

import com.google.gson.*;
import mod.selene.api.utils.interfaces.Util;
import mod.selene.impl.Module;
import mod.selene.loader.Feature;
import mod.selene.loader.SeleneLoader;
import mod.selene.system.Setting;
import mod.selene.system.impl.Bind;
import mod.selene.system.impl.EnumConverter;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class ConfigManager implements Util {

    public final String CONFIG = "selene/config/";
    public ArrayList<Feature> features = new ArrayList<>();

    //TODO: String[] Array for FriendList? Also ENUMS!!!!!
    public static void setValueFromJson(Feature feature, Setting setting, JsonElement element) {
        switch (setting.getType()) {
            case "Boolean":
                setting.setValue(element.getAsBoolean());
                break;
            case "Double":
                setting.setValue(element.getAsDouble());
                break;
            case "Float":
                setting.setValue(element.getAsFloat());
                break;
            case "Integer":
                setting.setValue(element.getAsInt());
                break;
            case "String":
                String str = element.getAsString();
                setting.setValue(str.replace("_", " "));
                break;
            case "Bind":
                setting.setValue(new Bind.BindConverter().doBackward(element));
                break;
            case "Color":
                setting.setValue(new Color(element.getAsInt(), true));
                break;
            case "Enum":
                try {
                    EnumConverter converter = new EnumConverter(((Enum) setting.getValue()).getClass());
                    Enum value = converter.doBackward(element);
                    setting.setValue(value == null ? setting.getDefaultValue() : value);
                    break;
                } catch (Exception e) {
                    break;
                }
            default:
                SeleneLoader.LOGGER.error("Unknown Setting type for: " + feature.getName() + " : " + setting.getName());
        }
    }

    private static void loadFile(JsonObject input, Feature feature) {
        for (Map.Entry<String, JsonElement> entry : input.entrySet()) {
            String settingName = entry.getKey();
            JsonElement element = entry.getValue();
            if (feature instanceof FriendManager) {
                try {
                    SeleneLoader.friendManager.addFriend(new FriendManager.Friend(element.getAsString(), UUID.fromString(settingName)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                boolean settingFound = false;
                for (Setting setting : feature.getSettings()) {
                    if (settingName.equals(setting.getName())) {
                        try {
                            setValueFromJson(feature, setting, element);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        settingFound = true;
                    }
                }

                if (settingFound) {
                    continue;
                }
            }
        }
    }

    public void loadConfig() {
        SeleneLoader.friendManager.onLoad();
        for (Feature feature : this.features) {
            try {
                loadSettings(feature);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveConfig() {
        SeleneLoader.friendManager.saveFriends();
        for (Feature feature : this.features) {
            try {
                saveSettings(feature);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void resetConfig(boolean saveConfig) {
        for (Feature feature : this.features) {
            feature.reset();
        }
        if (saveConfig) saveConfig();
    }

    public void saveSettings(Feature feature) throws IOException {
        JsonObject object = new JsonObject();
        String featureName = CONFIG + getDirectory(feature) + feature.getName() + ".json";
        Path outputFile = Paths.get(featureName);
        if (!Files.exists(outputFile)) {
            Files.createFile(outputFile);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(writeSettings(feature));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile)));
        writer.write(json);
        writer.close();
    }

    //TODO: add everything with Settings here
    public void init() {
        features.addAll(SeleneLoader.moduleManager.modules);
        features.add(SeleneLoader.friendManager);

        loadConfig();
        SeleneLoader.LOGGER.info("Config loaded.");
    }

    private void loadSettings(Feature feature) throws IOException {
        String featureName = CONFIG + getDirectory(feature) + feature.getName() + ".json";
        Path featurePath = Paths.get(featureName);
        if (!Files.exists(featurePath)) {
            return;
        }
        loadPath(featurePath, feature);
    }

    private void loadPath(Path path, Feature feature) throws IOException {
        InputStream stream = Files.newInputStream(path);
        try {
            loadFile(new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject(), feature);
        } catch (IllegalStateException e) {
            SeleneLoader.LOGGER.error("Bad Config File for: " + feature.getName() + ". Resetting...");
            loadFile(new JsonObject(), feature);
        }
        stream.close();
    }

    public JsonObject writeSettings(Feature feature) {
        JsonObject object = new JsonObject();
        JsonParser jp = new JsonParser();
        for (Setting setting : feature.getSettings()) {
            if (setting.getValue() instanceof Color) {
                object.add(setting.getName(), jp.parse(String.valueOf(((Color) setting.getValue()).getRGB())));
                continue;
            }
            if (setting.isEnumSetting()) {
                EnumConverter converter = new EnumConverter(((Enum) setting.getValue()).getClass());
                object.add(setting.getName(), converter.doForward((Enum) setting.getValue()));
                continue;
            }

            if (setting.isStringSetting()) {
                String str = (String) setting.getValue();
                setting.setValue(str.replace(" ", "_"));
            }

            try {
                object.add(setting.getName(), jp.parse(setting.getValueAsString()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return object;
    }

    public String getDirectory(Feature feature) {
        String directory = "";
        if (feature instanceof Module) {
            directory = directory + ((Module) feature).getCategory().getName() + "/";
        }
        return directory;
    }
}
