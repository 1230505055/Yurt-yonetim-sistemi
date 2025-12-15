package view;

import com.formdev.flatlaf.FlatClientProperties;
import db.Database;
import model.User;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Yurt Otomasyonu - Giriş");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Ekranı ikiye böl (Sol: Görsel, Sağ: Form)
        setLayout(new MigLayout("fill, insets 0, gap 0", "[50%, fill][50%, fill]", "[grow, fill]"));

        add(createLeftPanel(), "grow");
        add(createRightPanel(), "grow");
    }

    // Sol taraftaki resimli ve logolu panel
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel() {
            private Image bgImage;

            {
                try {
                    // Resmi yüklemeye çalış
                    java.net.URL url = getClass().getResource("/2.jpeg");
                    if (url != null) {
                        bgImage = javax.imageio.ImageIO.read(url);
                    } else {
                        // IDE içinden çalışıyorsa alternatif yol
                        java.io.File f = new java.io.File("src/main/resources/2.jpeg");
                        if (f.exists()) {
                            bgImage = javax.imageio.ImageIO.read(f);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Arkaplan resmi yüklenemedi: " + e.getMessage());
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (bgImage != null) {
                    // Resmi panele sığdır
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);

                    // Yazıların okunması için karartma efekti
                    g.setColor(new Color(0, 0, 0, 100));
                    g.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    // Resim yoksa yeşil gradient arka plan
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    GradientPaint gp = new GradientPaint(0, 0, new Color(31, 31, 31),
                            getWidth(), getHeight(), new Color(118, 142, 82));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        panel.setLayout(new MigLayout("wrap, align center center", "[]", "[]20[]"));

        // Logo Alanı
        JLabel lblLogo = new JLabel();
        ImageIcon logoIcon = new ImageIcon("src/main/resources/kyk.png");
        if (logoIcon.getIconWidth() > 0) {
            Image scaledLogo = logoIcon.getImage().getScaledInstance(150, -1, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(scaledLogo));
        } else {
            lblLogo.setText("KYKLife");
            lblLogo.putClientProperty(FlatClientProperties.STYLE, "font: Serif bold +30; foreground: #fff");
        }
        panel.add(lblLogo);

        JLabel lblSlogan = new JLabel("Yurt Yönetiminde Modern Çözüm");
        lblSlogan.putClientProperty(FlatClientProperties.STYLE, "font: SansSerif regular +4; foreground: #eee");
        panel.add(lblSlogan);

        return panel;
    }

    // Sağ taraftaki giriş formu paneli
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 40 60 40 60", "[fill]", "[]10[]30[]20[]40[]"));

        // Başlıklar
        JLabel lblTitle = new JLabel("Hoş Geldiniz");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold +20");

        JLabel lblSub = new JLabel("Hesabınıza giriş yapın");
        lblSub.putClientProperty(FlatClientProperties.STYLE, "foreground: #888");

        panel.add(lblTitle);
        panel.add(lblSub);

        // Kullanıcı Adı
        txtUsername = new JTextField();
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Kullanıcı Adı");

        JLabel lblUser = new JLabel("Kullanıcı Adı");
        panel.add(lblUser, "gaptop 20");
        panel.add(txtUsername, "h 45!");

        // Şifre
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "••••••");
        // 'showRevealButton' FlatLaf'ın desteklediği güvenli bir özelliktir, kalabilir.
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");

        JLabel lblPass = new JLabel("Şifre");
        panel.add(lblPass, "gaptop 10");
        panel.add(txtPassword, "h 45!");

        // Giriş Butonu
        btnLogin = new JButton("Giriş Yap");
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> handleLogin());

        // Buton Stili (Java koduyla güvenli stil)
        btnLogin.setBackground(new Color(118, 142, 82)); // Palm Leaf (Yeşil)
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        // Stil stringi kaldırıldı (Unknown style hatasını önlemek için)

        panel.add(btnLogin, "h 50!, gaptop 20");

        // Enter tuşu ile giriş yapabilme
        getRootPane().setDefaultButton(btnLogin);

        return panel;
    }

    // Giriş işlemini yöneten metot
    private void handleLogin() {
        String user = txtUsername.getText();
        String pass = new String(txtPassword.getPassword());

        btnLogin.setEnabled(false);
        btnLogin.setText("Lütfen bekleyin...");

        // Veritabanı işlemini ayrı bir thread'de yap (Arayüz donmasın)
        new Thread(() -> {
            Optional<User> userOpt = Database.getInstance().login(user, pass);

            SwingUtilities.invokeLater(() -> {
                if (userOpt.isPresent()) {
                    dispose(); // Login ekranını kapat
                    User u = userOpt.get();

                    // Kullanıcı rolüne göre ilgili paneli aç
                    if (u instanceof model.Staff) {
                        new StaffPanel((model.Staff) u).setVisible(true);
                    } else if (u instanceof model.Student) {
                        new StudentPanel((model.Student) u).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Hatalı kullanıcı adı veya şifre.", "Hata", JOptionPane.ERROR_MESSAGE);
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Giriş Yap");
                }
            });
        }).start();
    }
}