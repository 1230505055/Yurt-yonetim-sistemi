package view;

import db.Database;
import model.Room;
import pattern.RoomFactory;

import javax.swing.*;
import java.awt.*;

public class AddRoomFrame extends JFrame {

    public AddRoomFrame() {
        // Pencere temel ayarları
        setTitle("Yeni Oda Ekle");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Arka plan rengi
        getContentPane().setBackground(new Color(246, 246, 240));

        // Form paneli (Giriş alanları)
        JPanel pnlForm = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlForm.setBackground(new Color(246, 246, 240));

        JTextField txtRoomNo = new JTextField();
        JTextField txtCapacity = new JTextField();

        pnlForm.add(new JLabel("Oda Numarası:"));
        pnlForm.add(txtRoomNo);
        pnlForm.add(new JLabel("Kapasite:"));
        pnlForm.add(txtCapacity);

        add(pnlForm, BorderLayout.CENTER);

        // Buton paneli
        JPanel pnlButtons = new JPanel();
        pnlButtons.setBackground(new Color(246, 246, 240));

        JButton btnAdd = new JButton("Oda Ekle");
        JButton btnBack = new JButton("Geri Dön");

        // Buton stilleri
        btnAdd.setBackground(new Color(118, 142, 82));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("SansSerif", Font.BOLD, 12));

        btnBack.setBackground(new Color(187, 189, 149));
        btnBack.setForeground(new Color(31, 31, 31));
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 12));

        pnlButtons.add(btnAdd);
        pnlButtons.add(btnBack);

        add(pnlButtons, BorderLayout.SOUTH);

        // Geri dön butonu aksiyonu
        btnBack.addActionListener(e -> dispose());

        // Ekleme butonu aksiyonu
        btnAdd.addActionListener(e -> {
            try {
                String roomNo = txtRoomNo.getText().trim();
                String capStr = txtCapacity.getText().trim();

                if (roomNo.isEmpty() || capStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.");
                    return;
                }

                int capacity = Integer.parseInt(capStr);

                // Factory Pattern kullanarak nesne üretimi
                Room room = RoomFactory.createRoom(roomNo, capacity);

                // Veritabanı kayıt işlemi
                boolean success = Database.getInstance().addRoom(room);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Oda başarıyla eklendi!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Oda eklenemedi (Aynı numara mevcut olabilir).");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Kapasite alanına geçerli bir sayı giriniz!");
            }
        });
    }
}