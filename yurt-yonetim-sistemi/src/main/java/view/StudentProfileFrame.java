package view;

import model.Student;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class StudentProfileFrame extends JFrame {

    public StudentProfileFrame(Student student) {
        // Pencere Ayarları
        setTitle("Profil Bilgilerim");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Arka Plan Rengi (Proje standardı)
        getContentPane().setBackground(new Color(246, 246, 240)); // Cultured

        // Düzen (Layout) - Etiketler solda, değerler sağda
        setLayout(new MigLayout("fillx, insets 25", "[120!][grow]", "[]10[]"));

        // --- Başlık ---
        JLabel lblTitle = new JLabel("Öğrenci Profili");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle, "span, center, wrap, gapbottom 20");

        // --- Bilgileri Listele (Yardımcı metot ile) ---
        addField("Ad Soyad:", student.getName());
        addField("Kullanıcı Adı:", student.getUsername());
        addField("TC Kimlik:", student.getTc());
        addField("E-Posta:", student.getEmail());
        addField("Telefon:", student.getPhone());

        // Oda numarası kontrolü
        String room = (student.getRoomNumber() != null && !student.getRoomNumber().isEmpty())
                ? student.getRoomNumber()
                : "Atanmadı";
        addField("Oda No:", room);

        // --- Kapat Butonu ---
        JButton btnClose = new JButton("Kapat");
        btnClose.setBackground(new Color(187, 189, 149)); // Sage rengi
        btnClose.setForeground(new Color(31, 31, 31));
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());

        add(btnClose, "span, center, gaptop 20, width 100!, height 35!");
    }

    // Tekrarlanan kodları önlemek için yardımcı metot
    private void addField(String labelText, String valueText) {
        // Etiket Stili
        JLabel lblLabel = new JLabel(labelText);
        lblLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblLabel.setForeground(new Color(100, 100, 100)); // Hafif gri

        // Değer Stili
        JLabel lblValue = new JLabel(valueText != null ? valueText : "-");
        lblValue.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblValue.setForeground(new Color(30, 30, 30)); // Koyu siyah

        // Panele Ekle
        add(lblLabel);
        add(lblValue, "wrap");

        // Altına ince çizgi çek
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(220, 220, 220));
        add(sep, "span, growx, gapbottom 5, wrap");
    }
}