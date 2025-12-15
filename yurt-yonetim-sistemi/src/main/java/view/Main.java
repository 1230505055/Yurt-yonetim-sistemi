package view;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        FlatMacLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}