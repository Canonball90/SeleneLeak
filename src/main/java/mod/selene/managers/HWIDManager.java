package mod.selene.managers;

import org.apache.commons.codec.digest.DigestUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static mod.selene.api.utils.interfaces.Util.mc;

public class HWIDManager {

    public static final String pastebinURL = "Vm1wR1lWWXlTWGhXV0dST1ZsZG9WbGxVU2pSV2JGcHlWMjVrVldKSVFsaFdWelZQVkdzeFdGVnNhRnBXVmxVeFZrZDRTMlJXUm5OaVJscE9ZV3hhUlZkV1dtdFNNVnBYVjI1V1UySklRbTlaVkVvelpXeGtjbGt6YUZWTmJFcElWVEkxUjFaWFJqWlNhemxhWVRKb1JGWnFSbUZTTVdSMFpFZDBUbFp0ZHpCV01uUlhZakZrU0ZKWWJGVldSM001";

    public static List<String> hwids = new ArrayList<>();

    public static void hwidCheck() {

        hwids = HWIDManager.readURL();

        boolean isHwidPresent = hwids.contains(HWIDManager.getSystemInfo());

        if (!isHwidPresent) {

            HWIDManager.Display();
            mc.shutdown();
        }
    }

    public static List<String> readURL() {

        List<String> s = new ArrayList<>();

        try {

            byte[] decodedBytes = Base64.getDecoder().decode(HWIDManager.pastebinURL);

            byte[] decodedBytes2 = Base64.getDecoder().decode(decodedBytes);

            byte[] decodedBytes3 = Base64.getDecoder().decode(decodedBytes2);

            byte[] decodedBytes4 = Base64.getDecoder().decode(decodedBytes3);

            byte[] decodedBytes5 = Base64.getDecoder().decode(decodedBytes4);

            byte[] decodedBytes6 = Base64.getDecoder().decode(decodedBytes5);

            byte[] decodedBytes7 = Base64.getDecoder().decode(decodedBytes6);

            String decodedPastebinURL = new String(decodedBytes7);

            final URL url = new URL(decodedPastebinURL);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));

            String hwid;

            while ((hwid = bufferedReader.readLine()) != null) {

                s.add(hwid);
            }

        } catch (Exception e) {

            //FMLLog.log.info(e);

        }
        return s;
    }

    public static String getSystemInfo() {

        return DigestUtils.sha256Hex(DigestUtils.sha256Hex(System.getenv("os") + System.getProperty("os.name") + System.getProperty("os.arch") + System.getProperty("user.name") + System.getenv("SystemRoot") + System.getenv("HOMEDRIVE") + System.getenv("PROCESSOR_LEVEL") + System.getenv("PROCESSOR_REVISION") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITECTURE") + System.getenv("PROCESSOR_ARCHITEW6432") + System.getenv("NUMBER_OF_PROCESSORS")));

    }

    public static void Display() {

        Frame frame = new Frame();

        frame.setVisible(false);
        mc.shutdown();

    }

    public static class Frame extends JFrame {

        public Frame() {

            this.setTitle("Verification failed.");

            this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            this.setLocationRelativeTo(null);

            copyToClipboard();

            String message = "Sorry, you are not on the HWID list." + "\n" + "HWID: " + HWIDManager.getSystemInfo() + "\n(Copied to clipboard.)";

            JOptionPane.showMessageDialog(this, message, "Could not verify your HWID successfully.", JOptionPane.PLAIN_MESSAGE, UIManager.getIcon("OptionPane.errorIcon"));

        }

        public static void copyToClipboard() {

            StringSelection selection = new StringSelection(HWIDManager.getSystemInfo());

            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            clipboard.setContents(selection, selection);

        }
    }
}
