package pattern;

import javax.swing.JOptionPane;

public class AdminObserver implements Observer {
    @Override
    public void update(String message) {
        // Konsola yaz (Loglama için kalsın)
        System.out.println("LOG: " + message);

        // EKRANA POP-UP PENCERE AÇ (Kullanıcının göreceği kısım burası)
        JOptionPane.showMessageDialog(null,
                message,
                "Admin Bildirimi",
                JOptionPane.INFORMATION_MESSAGE);
    }
}