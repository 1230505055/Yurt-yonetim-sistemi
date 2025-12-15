package view;

import db.Database;
import model.Student;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentRoomInfoFrame extends JFrame {

    public StudentRoomInfoFrame(Student student) {
        // Pencere Ayarları
        setTitle("Oda Bilgilerim");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Arka Plan Rengi (Proje standardı)
        getContentPane().setBackground(new Color(246, 246, 240)); // Cultured

        String roomNum = student.getRoomNumber();

        // --- Durum 1: Öğrencinin Odası Yoksa ---
        if (roomNum == null || roomNum.isEmpty() || roomNum.equals("0")) {
            JLabel lblNoRoom = new JLabel("Henüz bir odaya atanmadınız.", SwingConstants.CENTER);
            lblNoRoom.setFont(new Font("SansSerif", Font.BOLD, 16));
            lblNoRoom.setForeground(new Color(100, 100, 100));
            add(lblNoRoom, BorderLayout.CENTER);

            // Kapat butonu ekleyelim ki boş ekranda kalmasın
            add(createBottomPanel(), BorderLayout.SOUTH);
            return;
        }

        // --- Durum 2: Odası Varsa ---

        // 1. Üst Panel: Oda Bilgisi
        JPanel pnlInfo = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        pnlInfo.setBackground(new Color(246, 246, 240));

        JLabel lblRoom = new JLabel("Oda Numaranız: " + roomNum);
        lblRoom.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblRoom.setForeground(new Color(118, 142, 82)); // Palm Leaf (Vurgu rengi)

        JLabel lblSub = new JLabel("Oda Arkadaşlarınız:");
        lblSub.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblSub.setForeground(new Color(60, 60, 60));

        pnlInfo.add(lblRoom);
        pnlInfo.add(lblSub);
        add(pnlInfo, BorderLayout.NORTH);

        // 2. Orta Panel: Oda Arkadaşları Listesi
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);

        // Liste Görünüm Ayarları (Diğer panellerle uyumlu)
        list.setBackground(new Color(255, 255, 255));
        list.setFont(new Font("SansSerif", Font.PLAIN, 14));
        list.setSelectionBackground(new Color(187, 189, 149)); // Sage
        list.setSelectionForeground(new Color(31, 31, 31));
        list.setFixedCellHeight(35); // Daha ferah satırlar

        // Veritabanından o odadaki herkesi çek
        List<Student> roommates = Database.getInstance().getRoommates(roomNum);
        boolean hasRoommate = false;

        for (Student s : roommates) {
            // Kendisini listede gösterme
            if (s.getId() != student.getId()) {
                String phone = (s.getPhone() != null && !s.getPhone().isEmpty()) ? s.getPhone() : "-";
                String email = (s.getEmail() != null && !s.getEmail().isEmpty()) ? s.getEmail() : "-";

                // HTML kullanarak çok satırlı gibi görünen liste elemanı yapabiliriz
                String display = String.format("<html><b>%s</b> <span style='color:gray; font-size:10px;'>(Tel: %s | %s)</span></html>",
                        s.getName(), phone, email);

                listModel.addElement(display);
                hasRoommate = true;
            }
        }

        if (!hasRoommate) {
            listModel.addElement("Bu odada şu an tek kalıyorsunuz.");
            list.setEnabled(false); // Tıklanmasın
        }

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Kenar boşluğu
        scrollPane.getViewport().setBackground(new Color(246, 246, 240));
        scrollPane.setBackground(new Color(246, 246, 240));

        add(scrollPane, BorderLayout.CENTER);

        // 3. Alt Panel: Kapat Butonu
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    // Alt panel oluşturucu (Kod tekrarını önlemek için)
    private JPanel createBottomPanel() {
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBottom.setBackground(new Color(246, 246, 240));
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton btnClose = new JButton("Kapat");
        btnClose.setBackground(new Color(118, 142, 82)); // Yeşil
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());

        pnlBottom.add(btnClose);
        return pnlBottom;
    }
}