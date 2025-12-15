package view;

import com.formdev.flatlaf.FlatClientProperties;
import db.Database;
import model.Staff;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Locale;

public class StaffPanel extends JFrame {

    private final Staff staff;
    private JPanel contentPanel;
    private JPanel dashboardHomePanel;

    public StaffPanel(Staff staff) {
        this.staff = staff;
        initUI();
    }

    // Arayüzün temel ayarlarını yapar
    private void initUI() {
        setTitle("Yurt Otomasyonu - Yönetim Paneli");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        setLayout(new MigLayout("fill, insets 0, gap 0", "[260!, fill][grow, fill]", "[grow, fill]"));

        add(createSidebar(), "growy");

        JPanel mainContainer = new JPanel(new MigLayout("fillx, insets 0", "[fill]", "[grow, fill]"));
        mainContainer.setBackground(new Color(246, 246, 240));

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        createDashboardHome();
        switchContent(dashboardHomePanel);

        mainContainer.add(contentPanel, "grow");
        add(mainContainer, "grow");
    }

    // Sol menü panelini oluşturur
    private JPanel createSidebar() {
        // Layout: Butonlar yukarıda, Çıkış butonu en aşağıda (push)
        JPanel sidebar = new JPanel(new MigLayout("wrap, fillx, insets 20 15 20 15", "[fill]", "[]30[]10[]15[]5[]5[]5[]push[]")) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                ImageIcon icon = new ImageIcon("src/main/resources/aaaaa.png");
                if (icon.getIconWidth() > 0) {
                    g2d.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(31, 31, 31),
                            0, getHeight(), new Color(44, 53, 37));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        JLabel lblBrand = new JLabel();
        ImageIcon logoIcon = new ImageIcon("src/main/resources/kyk.png");
        if (logoIcon.getIconWidth() > 0) {
            Image scaledLogo = logoIcon.getImage().getScaledInstance(130, -1, Image.SCALE_SMOOTH);
            lblBrand.setIcon(new ImageIcon(scaledLogo));
        } else {
            lblBrand.setText("KYKLife");
            lblBrand.putClientProperty(FlatClientProperties.STYLE, "font: Serif bold +12; foreground: #ffffff");
        }
        sidebar.add(lblBrand, "align center");

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 50));
        sep.setBackground(new Color(255, 255, 255, 50));
        sidebar.add(sep, "growx");

        JLabel lblMenu = new JLabel("YÖNETİM");
        lblMenu.putClientProperty(FlatClientProperties.STYLE, "font: SansSerif bold -2; foreground: #bdc3c7");
        sidebar.add(lblMenu);

        // Menü butonları
        sidebar.add(createSidebarButton("Ana Sayfa", e -> switchContent(dashboardHomePanel)));
        sidebar.add(createSidebarButton("Öğrenci Yönetimi", e -> switchContent(new StudentManagementPanel())));
        sidebar.add(createSidebarButton("Oda Yönetimi", e -> switchContent(new RoomListFrame())));
        sidebar.add(createSidebarButton("İzin Talepleri", e -> switchContent(new StaffRequestsFrame(staff))));

        JButton btnLogout = createSidebarButton("Çıkış Yap", e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        // Çıkış butonu stili
        btnLogout.putClientProperty(FlatClientProperties.STYLE,
                "foreground: #ff6b6b; " +
                        "hoverBackground: #3d2222; " +
                        "font: SansSerif bold +2; " +
                        "arc: 10; " +
                        "borderWidth: 0; " +
                        "focusWidth: 0; " +
                        "margin: 10,20,10,20"
        );
        // Hizalamayı Java kodu ile yapıyoruz (Style string içinden sildik)
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);

        sidebar.add(btnLogout, "bottom");

        return sidebar;
    }

    // Ana sayfa özet ekranı
    private void createDashboardHome() {
        dashboardHomePanel = new JPanel(new MigLayout("fillx, insets 30 40 30 40", "[fill]", "[]20[]"));
        dashboardHomePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Yönetim Paneli");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: Serif bold +22; foreground: #333");
        dashboardHomePanel.add(lblTitle, "wrap");

        JLabel lblSub = new JLabel("DormLife sistemi hazır.");
        lblSub.putClientProperty(FlatClientProperties.STYLE, "font: SansSerif; foreground: #7f8c8d");
        dashboardHomePanel.add(lblSub, "wrap, gaptop 5");

        JPanel cardsPanel = new JPanel(new MigLayout("insets 0, gap 20", "[grow, fill]20[grow, fill]20[grow, fill]", "[140!]"));
        cardsPanel.setOpaque(false);

        new Thread(() -> {
            int totalStudents = Database.getInstance().getTotalStudentCount();
            String occupancy = Database.getInstance().getOccupancyRate();
            int pendingRequests = Database.getInstance().getPendingLeaveRequestCount();

            SwingUtilities.invokeLater(() -> {
                cardsPanel.removeAll();
                cardsPanel.add(createStatCard("Toplam Öğrenci", String.valueOf(totalStudents), new Color(118, 142, 82)));
                cardsPanel.add(createStatCard("Doluluk", occupancy, new Color(187, 189, 149)));
                cardsPanel.add(createStatCard("Bekleyen İzin", String.valueOf(pendingRequests), new Color(192, 57, 43)));
                cardsPanel.revalidate();
                cardsPanel.repaint();
            });
        }).start();

        dashboardHomePanel.add(cardsPanel, "wrap, gaptop 20");
    }

    // Sidebar buton oluşturucu
    private JButton createSidebarButton(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);

        // Sola yaslama işlemi Java koduyla yapılır
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10; " +
                        "background: null; " +
                        "foreground: #333333; " +
                        "hoverBackground: #BBBD95; " +
                        "borderWidth: 0; " +
                        "focusWidth: 0; " +
                        "font: SansSerif bold +1; " +
                        "margin: 10,20,10,20"
        );
        return btn;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new MigLayout("insets 20, fill")) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(245, 245, 240));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, color));

        JLabel lblTitle = new JLabel(title);
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: SansSerif bold; foreground: #7f8c8d");

        card.add(lblTitle, "wrap");
        JLabel val = new JLabel(value);
        val.putClientProperty(FlatClientProperties.STYLE, "font: Serif bold +20; foreground: #2d3436");
        card.add(val);
        return card;
    }

    private void switchContent(Container content) {
        contentPanel.removeAll();
        contentPanel.add(content, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}