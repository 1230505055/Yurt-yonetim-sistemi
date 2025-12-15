package view;

import com.formdev.flatlaf.FlatClientProperties;
import db.Database;
import model.Student;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class StudentPanel extends JFrame {

    private final Student student;
    private JPanel contentPanel;
    private JPanel dashboardHomePanel;

    public StudentPanel(Student student) {
        this.student = student;
        initUI();
    }

    // Arayüzün temel ayarlarını yapar
    private void initUI() {
        setTitle("Yurt Otomasyonu - Öğrenci Paneli");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        // Ana yerleşim: Sol Menü + Sağ İçerik
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

    // Sol menüyü oluşturur
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new MigLayout("wrap, fillx, insets 20 15 20 15", "[fill]", "[]40[]10[]10[]10[]push[]")) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                ImageIcon icon = new ImageIcon("src/main/resources/aaaaa.png");
                if (icon.getIconWidth() > 0) {
                    g2d.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(31, 31, 31), 0, getHeight(), new Color(44, 53, 37));
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
        sidebar.add(sep, "growx, gaptop 10, gapbottom 10");

        // Menü Butonları
        sidebar.add(createSidebarButton("Ana Sayfa", e -> showDashboard()));
        sidebar.add(createSidebarButton("Profilim", e -> showProfilePanel()));
        sidebar.add(createSidebarButton("İzin İşlemleri", e -> showLeaveHistoryPanel()));

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
        // Hizalama Java koduyla yapıldı (Stil stringinden silindi)
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);

        sidebar.add(btnLogout, "bottom");

        return sidebar;
    }

    // Ana sayfa (Dashboard) içeriği
    private void createDashboardHome() {
        dashboardHomePanel = new JPanel(new MigLayout("fillx, insets 30 40 30 40", "[fill]", "[]20[]"));
        dashboardHomePanel.setOpaque(false);

        String name = (student.getName() != null) ? student.getName().split(" ")[0] : "Öğrenci";
        JLabel lblWelcome = new JLabel("Hoş Geldin, " + name);
        lblWelcome.putClientProperty(FlatClientProperties.STYLE, "font: Serif bold +22; foreground: #333");
        dashboardHomePanel.add(lblWelcome, "split 2");

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(new Locale("tr", "TR")));
        JLabel lblDate = new JLabel(dateStr);
        lblDate.putClientProperty(FlatClientProperties.STYLE, "font: Serif italic +2; foreground: #7f8c8d");
        dashboardHomePanel.add(lblDate, "gapleft push, wrap");

        JLabel lblSub = new JLabel("Bugün yurtta her şey yolunda görünüyor.");
        lblSub.putClientProperty(FlatClientProperties.STYLE, "font: SansSerif; foreground: #888");
        dashboardHomePanel.add(lblSub, "wrap, gaptop 5");

        JPanel cardsPanel = new JPanel(new MigLayout("insets 0, gap 20", "[grow, fill]20[grow, fill]20[grow, fill]", "[130!]"));
        cardsPanel.setOpaque(false);

        new Thread(() -> {
            String roomNum = student.getRoomNumber();
            String lastLeaveStatus = Database.getInstance().getLastLeaveStatus(student.getId());
            int totalInRoom = Database.getInstance().getStudentCountInRoom(roomNum);
            int roommates = (totalInRoom > 0) ? totalInRoom - 1 : 0;
            String roommatesText = (roommates > 0) ? roommates + " Arkadaş" : "Yalnız";

            SwingUtilities.invokeLater(() -> {
                cardsPanel.removeAll();
                cardsPanel.add(createStatCard("Oda Numaran", (roomNum == null || roomNum.isEmpty() || roomNum.equals("0")) ? "Yok" : roomNum, new Color(118, 142, 82)));
                cardsPanel.add(createStatCard("Son İzin Durumu", lastLeaveStatus, new Color(187, 189, 149)));

                JPanel roommatesCard = createStatCard("Oda Arkadaşları", roommatesText, new Color(212, 172, 13));
                if (roommates > 0) {
                    roommatesCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    roommatesCard.setToolTipText("Detayları görmek için tıkla");
                    roommatesCard.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            showRoommatesDialog(roomNum);
                        }
                    });
                }
                cardsPanel.add(roommatesCard);
                cardsPanel.revalidate();
                cardsPanel.repaint();
            });
        }).start();

        dashboardHomePanel.add(cardsPanel, "wrap, gaptop 20");
    }

    // Profil görüntüleme ve düzenleme paneli
    private void showProfilePanel() {
        JPanel profilePanel = new JPanel(new MigLayout("fill, insets 30 40 30 40", "[grow]", "[]20[]"));
        profilePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Profil Bilgilerim");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: Serif bold +20; foreground: #333");
        profilePanel.add(lblTitle, "wrap");

        JPanel card = new JPanel(new MigLayout("wrap 2, fillx, insets 30", "[150!]30[grow, fill]", "[]15[]15[]15[]20[]"));
        card.setBackground(Color.WHITE);
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 20");

        card.add(new JLabel("Ad Soyad:"), "align label");
        JLabel lblName = new JLabel(student.getName());
        lblName.putClientProperty(FlatClientProperties.STYLE, "font: bold +2");
        card.add(lblName);

        card.add(new JLabel("T.C. Kimlik:"), "align label");
        card.add(new JLabel(student.getTc() != null ? student.getTc() : "-"));

        card.add(new JLabel("Oda Numarası:"), "align label");
        String roomNum = Database.getInstance().getStudentRoomNumber(student.getId());
        card.add(new JLabel((roomNum != null && !roomNum.isEmpty()) ? roomNum : "Atanmadı"));

        JTextField txtPhone = createProfileField(student.getPhone());
        JTextField txtEmail = createProfileField(student.getEmail());
        JTextField txtAddress = createProfileField(student.getAddress());

        card.add(new JSeparator(), "span, growx, gaptop 10, gapbottom 10");

        card.add(new JLabel("Telefon:"), "align label");
        card.add(txtPhone);

        card.add(new JLabel("E-Posta:"), "align label");
        card.add(txtEmail);

        card.add(new JLabel("Adres:"), "align label");
        card.add(txtAddress);

        JButton btnUpdate = new JButton("Bilgileri Güncelle");
        btnUpdate.setBackground(new Color(118, 142, 82));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.putClientProperty(FlatClientProperties.STYLE, "arc: 10; font: bold; margin: 8,20,8,20");

        btnUpdate.addActionListener(e -> {
            if (btnUpdate.getText().equals("Bilgileri Güncelle")) {
                txtPhone.setEditable(true); txtPhone.setBackground(Color.WHITE);
                txtEmail.setEditable(true); txtEmail.setBackground(Color.WHITE);
                txtAddress.setEditable(true); txtAddress.setBackground(Color.WHITE);
                txtPhone.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                txtEmail.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                txtAddress.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                btnUpdate.setText("Değişiklikleri Kaydet");
                btnUpdate.setBackground(new Color(212, 172, 13));
            } else {
                boolean success = Database.getInstance().updateStudentProfile(
                        student.getId(), txtPhone.getText(), txtEmail.getText(), txtAddress.getText()
                );

                if (success) {
                    student.setPhone(txtPhone.getText());
                    student.setEmail(txtEmail.getText());
                    student.setAddress(txtAddress.getText());

                    JOptionPane.showMessageDialog(this, "Bilgiler güncellendi.");

                    txtPhone.setEditable(false); txtPhone.setBackground(new Color(246, 246, 240));
                    txtEmail.setEditable(false); txtEmail.setBackground(new Color(246, 246, 240));
                    txtAddress.setEditable(false); txtAddress.setBackground(new Color(246, 246, 240));
                    txtPhone.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
                    txtEmail.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
                    txtAddress.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

                    btnUpdate.setText("Bilgileri Güncelle");
                    btnUpdate.setBackground(new Color(118, 142, 82));
                } else {
                    JOptionPane.showMessageDialog(this, "Hata oluştu.");
                }
            }
        });

        card.add(btnUpdate, "span, align right, gaptop 10");

        profilePanel.add(card, "growx, pushy, aligny top");
        switchContent(profilePanel);
    }

    // İzin geçmişi paneli
    private void showLeaveHistoryPanel() {
        JPanel historyPanel = new JPanel(new MigLayout("fill, insets 30 40 30 40", "[grow]", "[]20[]20[grow]"));
        historyPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("İzin Geçmişim");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: Serif bold +20; foreground: #333");

        JButton btnNewRequest = new JButton("Yeni İzin Talebi Oluştur");
        btnNewRequest.putClientProperty(FlatClientProperties.STYLE, "background: #446E5C; foreground: #fff; font: bold +2; arc: 10; margin: 8,15,8,15");
        btnNewRequest.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnNewRequest.addActionListener(e -> {
            JDialog frame = new StudentLeaveRequestFrame(student);
            frame.setVisible(true);
            showLeaveHistoryPanel();
        });

        JPanel headerPanel = new JPanel(new MigLayout("insets 0, fillx", "[grow]push[]"));
        headerPanel.setOpaque(false);
        headerPanel.add(lblTitle);
        headerPanel.add(btnNewRequest);
        historyPanel.add(headerPanel, "wrap, growx");

        String[] columns = {"Başlangıç", "Bitiş", "Sebep", "Durum"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        java.util.List<model.LeaveRequest> leaves = Database.getInstance().getStudentLeaveRequests(student.getId());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        if (leaves.isEmpty()) {
            model.addRow(new Object[]{"-", "-", "Henüz bir izin talebiniz yok.", "-"});
        } else {
            for (model.LeaveRequest lr : leaves) {
                model.addRow(new Object[]{
                        lr.getStartDate().format(dtf), lr.getEndDate().format(dtf), lr.getReason(), lr.getStatus()
                });
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);

        table.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                setFont(new Font("SansSerif", Font.BOLD, 13));
                setHorizontalAlignment(JLabel.CENTER);
                if ("Onaylandı".equalsIgnoreCase(status) || "Approved".equalsIgnoreCase(status)) setForeground(new Color(46, 125, 50));
                else if ("Reddedildi".equalsIgnoreCase(status) || "Rejected".equalsIgnoreCase(status)) setForeground(new Color(198, 40, 40));
                else setForeground(new Color(230, 81, 0));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        historyPanel.add(scrollPane, "grow");

        switchContent(historyPanel);
    }

    private void showDashboard() {
        createDashboardHome();
        switchContent(dashboardHomePanel);
    }

    // [GÜVENLİ] Profil Textfield Oluşturucu
    private JTextField createProfileField(String text) {
        String safeText = (text != null) ? text : "";

        JTextField field = new JTextField(safeText);
        field.setEditable(false);
        field.setBackground(new Color(246, 246, 240));
        field.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Not: 'arc' stili TextField için hata verdiği için kaldırıldı.
        return field;
    }

    // Oda arkadaşları popup
    private void showRoommatesDialog(String roomNum) {
        JDialog dialog = new JDialog(this, "Oda Arkadaşları", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        String[] columns = {"Ad Soyad", "Telefon Numarası", "E-Posta Adresi"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));

        java.util.List<Student> list = Database.getInstance().getRoommates(roomNum);
        boolean found = false;
        for (Student s : list) {
            if (s.getId() != student.getId()) {
                model.addRow(new Object[]{ s.getName(), (s.getPhone() == null || s.getPhone().isEmpty()) ? "-" : s.getPhone(), (s.getEmail() == null || s.getEmail().isEmpty()) ? "-" : s.getEmail() });
                found = true;
            }
        }
        if (!found) { JOptionPane.showMessageDialog(this, "Veri çekilemedi veya oda boş."); return; }

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton btnClose = new JButton("Kapat");
        btnClose.addActionListener(e -> dialog.dispose());
        dialog.add(btnClose, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Sidebar buton oluşturucu
    private JButton createSidebarButton(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.addActionListener(action);

        // Sola yaslama Java metodu ile yapılıyor
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

    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new MigLayout("insets 20, fill")) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(245, 245, 240));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor));
        JLabel lblTitle = new JLabel(title);
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: SansSerif bold; foreground: #95a5a6");
        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(new Color(44, 62, 80));
        lblValue.putClientProperty(FlatClientProperties.STYLE, "font: Serif bold +16");
        card.add(lblTitle, "wrap"); card.add(lblValue);
        return card;
    }

    private void switchContent(Container content) {
        contentPanel.removeAll();
        contentPanel.add(content, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}