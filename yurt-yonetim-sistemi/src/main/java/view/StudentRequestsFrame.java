package view;

import db.Database;
import model.LeaveRequest;
import model.Student;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentRequestsFrame extends JFrame {

    public StudentRequestsFrame(Student student) {
        // Pencere Ayarları
        setTitle("İzin Taleplerim");
        setSize(500, 400); // Biraz daha genişletildi
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Arka Plan Rengi
        getContentPane().setBackground(new Color(246, 246, 240));

        // Liste Modeli
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);

        // Liste Görünüm Ayarları
        list.setBackground(new Color(246, 246, 240));
        list.setFont(new Font("SansSerif", Font.PLAIN, 14));
        list.setSelectionBackground(new Color(187, 189, 149)); // Seçim rengi (Sage)
        list.setSelectionForeground(new Color(31, 31, 31));
        list.setFixedCellHeight(30); // Satır yüksekliği

        // Veritabanından SADECE bu öğrenciye ait izinleri çek (Optimize Edildi)
        // Eski yöntem: Tüm izinleri çekip döngüyle arıyordu (Yavaştı)
        // Yeni yöntem: SQL sorgusu ile sadece ilgili öğrencininkiler geliyor
        List<LeaveRequest> myRequests = Database.getInstance().getStudentLeaveRequests(student.getId());

        if (myRequests.isEmpty()) {
            listModel.addElement("Henüz bir izin talebiniz bulunmamaktadır.");
        } else {
            for (LeaveRequest req : myRequests) {
                // Bilgileri formatla
                String info = String.format("📅 %s - %s  |  Durum: %s  |  Sebep: %s",
                        req.getStartDate(),
                        req.getEndDate(),
                        req.getStatus(), // Güvenli metot kullanımı
                        req.getReason()
                );
                listModel.addElement(info);
            }
        }

        // ScrollPane ekle (Kenarlıksız, temiz görünüm)
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getViewport().setBackground(new Color(246, 246, 240));

        add(scrollPane, BorderLayout.CENTER);

        // Alt kısma kapat butonu ekle
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBottom.setBackground(new Color(246, 246, 240));

        JButton btnClose = new JButton("Kapat");
        btnClose.setBackground(new Color(118, 142, 82)); // Yeşil
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnClose.addActionListener(e -> dispose());

        pnlBottom.add(btnClose);
        add(pnlBottom, BorderLayout.SOUTH);
    }
}