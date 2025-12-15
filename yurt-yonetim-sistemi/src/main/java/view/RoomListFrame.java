package view;

import com.formdev.flatlaf.FlatClientProperties;
import db.Database;
import model.Room;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Oda listesini gösteren panel
public class RoomListFrame extends JPanel {

    private DefaultListModel<Room> listModel;
    private JList<Room> roomList;

    public RoomListFrame() {
        initUI();
        loadRooms();
    }

    private void initUI() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]20[grow]"));
        setOpaque(false); // Arka plan rengini ana panelden al

        // 1. Başlık ve Bilgi Paneli
        JPanel headerPanel = new JPanel(new MigLayout("insets 0", "[]push[]"));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Oda Yönetimi");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: Serif bold +10; foreground: #333");

        JLabel lblInfo = new JLabel("Yeşil: Müsait | Kırmızı: Dolu");
        lblInfo.putClientProperty(FlatClientProperties.STYLE, "foreground: #7f8c8d; font: bold -2");

        headerPanel.add(lblTitle);
        headerPanel.add(lblInfo);
        add(headerPanel, "wrap, growx");

        // 2. Oda Listesi (Grid Görünümü)
        listModel = new DefaultListModel<>();
        roomList = new JList<>(listModel);

        // Listeyi ızgara (Grid) görünümüne çevir
        roomList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        roomList.setVisibleRowCount(-1); // Sığdığı kadar yan yana dizer
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.setBackground(new Color(245, 247, 250));

        // Özel Kart Tasarımını Bağla
        roomList.setCellRenderer(new RoomCardRenderer());

        // Kaydırma Paneli
        JScrollPane scrollPane = new JScrollPane(roomList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Akıcı kaydırma
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, "grow, wrap");

        // 3. İşlem Butonları
        JPanel actionPanel = new JPanel(new MigLayout("insets 0", "[]10[]10[]push[]", "[]"));
        actionPanel.setOpaque(false);

        JButton btnAdd = createActionButton("Oda Ekle", new Color(118, 142, 82), Color.WHITE);
        btnAdd.addActionListener(e -> {
            new AddRoomFrame().setVisible(true);
            // Ekleme penceresi kapandığında listeyi yenilemek için listener eklenebilir
            // Basit çözüm: Kullanıcı manuel yeniler veya Thread ile kontrol edilir
        });

        JButton btnDelete = createActionButton("Oda Sil", new Color(192, 57, 43), Color.WHITE);
        btnDelete.addActionListener(e -> deleteSelectedRoom());

        // [YENİ] Öğrenci Yerleştir Butonu
        JButton btnAssign = createActionButton("Öğrenci Yerleştir", new Color(70, 130, 180), Color.WHITE);
        btnAssign.addActionListener(e -> new AssignStudentToRoomFrame().setVisible(true));

        JButton btnRefresh = createActionButton("Yenile", new Color(187, 189, 149), new Color(31, 31, 31));
        btnRefresh.addActionListener(e -> loadRooms());

        actionPanel.add(btnAdd);
        actionPanel.add(btnDelete);
        actionPanel.add(btnAssign);
        actionPanel.add(btnRefresh, "right");

        add(actionPanel, "growx");
    }

    private JButton createActionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        // FlatLaf stil stringi kaldırıldı (Güvenlik için)
        return btn;
    }

    private void deleteSelectedRoom() {
        Room selected = roomList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek odayı seçin.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                selected.getRoomNumber() + " numaralı odayı silmek istediğinize emin misiniz?",
                "Silme Onayı", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                boolean success = Database.getInstance().deleteRoom(selected.getRoomNumber());
                SwingUtilities.invokeLater(() -> {
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Oda başarıyla silindi.");
                        loadRooms();
                    } else {
                        JOptionPane.showMessageDialog(this, "Oda silinemedi (Dolu olabilir).");
                    }
                });
            }).start();
        }
    }

    private void loadRooms() {
        new Thread(() -> {
            java.util.List<Room> rooms = Database.getInstance().getRooms();
            SwingUtilities.invokeLater(() -> {
                listModel.clear();
                for (Room room : rooms) {
                    listModel.addElement(room);
                }
            });
        }).start();
    }

    // --- ÖZEL KART TASARIMI (RENDERER) ---
    private static class RoomCardRenderer extends JPanel implements ListCellRenderer<Room> {
        private JLabel lblRoomNo = new JLabel();
        private JLabel lblCapacity = new JLabel();
        private JLabel lblStatus = new JLabel();

        public RoomCardRenderer() {
            setLayout(new MigLayout("insets 10, fill", "[grow]", "[]5[]10[]"));
            setPreferredSize(new Dimension(180, 120));

            // Bileşen stilleri
            lblRoomNo.setFont(new Font("SansSerif", Font.BOLD, 18));
            lblCapacity.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblStatus.setFont(new Font("SansSerif", Font.BOLD, 12));

            add(lblRoomNo, "wrap");
            add(lblCapacity, "wrap");
            add(lblStatus, "right, bottom");
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Room> list, Room room, int index, boolean isSelected, boolean cellHasFocus) {
            lblRoomNo.setText("Oda " + room.getRoomNumber());

            // Not: ListCellRenderer içinde veritabanı sorgusu yapmak performans açısından önerilmez.
            // Ancak basit uygulamalarda kabul edilebilir.
            int current = Database.getInstance().getStudentCountInRoom(room.getRoomNumber());
            int capacity = room.getCapacity();

            lblCapacity.setText("Doluluk: " + current + " / " + capacity);

            if (current >= capacity) {
                // DOLU (Kırmızı)
                setBackground(isSelected ? new Color(150, 40, 30) : new Color(192, 57, 43));
                lblStatus.setText("DOLU");
                lblRoomNo.setForeground(Color.WHITE);
                lblCapacity.setForeground(Color.WHITE);
                lblStatus.setForeground(Color.WHITE);
            } else {
                // MÜSAİT (Yeşil tonları)
                setBackground(isSelected ? new Color(118, 142, 82).darker() : new Color(187, 189, 149));
                lblStatus.setText("MÜSAİT");
                lblRoomNo.setForeground(new Color(31, 31, 31));
                lblCapacity.setForeground(new Color(31, 31, 31));
                lblStatus.setForeground(new Color(31, 31, 31));
            }

            // Kenarlık (Border) ile kart görünümü ver
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 5, 5), // Dış boşluk
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1) // Çerçeve
            ));

            return this;
        }
    }
}