package view;

import db.Database;
import model.User;
import pattern.UserFactory;

import javax.swing.*;
import java.awt.*;

public class AddStudentFrame extends JFrame {

    public AddStudentFrame() {
        // Pencere temel ayarları
        setTitle("Yeni Öğrenci Ekle");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Arka plan rengi
        getContentPane().setBackground(new Color(246, 246, 240));

        // Form paneli (Giriş alanları)
        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 10, 10));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlForm.setBackground(new Color(246, 246, 240));

        JTextField txtName = new JTextField();
        JTextField txtUser = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtEmail = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtTc = new JTextField();

        pnlForm.add(new JLabel("Ad Soyad:")); pnlForm.add(txtName);
        pnlForm.add(new JLabel("Kullanıcı Adı:")); pnlForm.add(txtUser);
        pnlForm.add(new JLabel("Şifre:")); pnlForm.add(txtPass);
        pnlForm.add(new JLabel("E-Posta:")); pnlForm.add(txtEmail);
        pnlForm.add(new JLabel("Telefon:")); pnlForm.add(txtPhone);
        pnlForm.add(new JLabel("TC Kimlik:")); pnlForm.add(txtTc);

        add(pnlForm, BorderLayout.CENTER);

        // Buton paneli
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlButtons.setBackground(new Color(246, 246, 240));

        JButton btnSave = new JButton("Kaydet");
        JButton btnBack = new JButton("Geri Dön");

        // Buton stilleri
        btnSave.setBackground(new Color(118, 142, 82));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 12));

        btnBack.setBackground(new Color(187, 189, 149));
        btnBack.setForeground(new Color(31, 31, 31));
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 12));

        pnlButtons.add(btnSave);
        pnlButtons.add(btnBack);

        add(pnlButtons, BorderLayout.SOUTH);

        // Geri dön butonu aksiyonu
        btnBack.addActionListener(e -> dispose());

        // Kaydet butonu aksiyonu
        btnSave.addActionListener(e -> {
            // Basit boş alan kontrolü
            if (txtUser.getText().trim().isEmpty() || new String(txtPass.getPassword()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kullanıcı adı ve şifre zorunludur!");
                return;
            }

            // Factory Pattern ile nesne üretimi
            User u = UserFactory.createUser("STUDENT", 0, txtUser.getText(), txtName.getText(), new String(txtPass.getPassword()), null);
            u.setEmail(txtEmail.getText());
            u.setPhone(txtPhone.getText());
            u.setTc(txtTc.getText());

            // Veritabanı kayıt işlemi
            boolean success = Database.getInstance().addStudent(u);
            if (success) {
                JOptionPane.showMessageDialog(this, "Öğrenci başarıyla eklendi!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Hata! Kullanıcı adı zaten kullanılıyor olabilir.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}