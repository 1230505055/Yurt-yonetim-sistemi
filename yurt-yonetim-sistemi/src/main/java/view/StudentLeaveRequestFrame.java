package view;

import db.Database;
import model.LeaveRequest;
import model.Student;
import pattern.AdminObserver;
import pattern.Observer;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class StudentLeaveRequestFrame extends JDialog {

    public StudentLeaveRequestFrame(Student student) {
        // Pencere ayarları
        setTitle("İzin Talebi Oluştur");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setModal(true); // Arka planı kilitler
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Arka plan rengi
        getContentPane().setBackground(new Color(246, 246, 240));

        // Form paneli
        JPanel pnlForm = new JPanel(new GridLayout(3, 2, 10, 10));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlForm.setBackground(new Color(246, 246, 240));

        // Varsayılan tarihleri ayarla (Bugün ve 2 gün sonrası)
        JTextField txtStart = new JTextField(LocalDate.now().toString());
        JTextField txtEnd = new JTextField(LocalDate.now().plusDays(2).toString());
        JTextField txtReason = new JTextField();

        pnlForm.add(new JLabel("Başlangıç (YYYY-AA-GG):"));
        pnlForm.add(txtStart);
        pnlForm.add(new JLabel("Bitiş (YYYY-AA-GG):"));
        pnlForm.add(txtEnd);
        pnlForm.add(new JLabel("İzin Sebebi:"));
        pnlForm.add(txtReason);

        add(pnlForm, BorderLayout.CENTER);

        // Buton paneli
        JPanel pnlButtons = new JPanel();
        pnlButtons.setBackground(new Color(246, 246, 240));

        JButton btnSend = new JButton("İzin İste");
        JButton btnBack = new JButton("İptal");

        // Buton stilleri
        btnSend.setBackground(new Color(118, 142, 82)); // Yeşil
        btnSend.setForeground(Color.WHITE);
        btnSend.setFont(new Font("SansSerif", Font.BOLD, 12));

        btnBack.setBackground(new Color(187, 189, 149)); // Açık yeşil
        btnBack.setForeground(new Color(31, 31, 31));
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 12));

        pnlButtons.add(btnSend);
        pnlButtons.add(btnBack);

        add(pnlButtons, BorderLayout.SOUTH);

        // Aksiyonlar
        btnBack.addActionListener(e -> dispose());

        btnSend.addActionListener(e -> {
            try {
                String r = txtReason.getText().trim();
                if(r.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lütfen bir sebep giriniz.", "Eksik Bilgi", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                LocalDate start = LocalDate.parse(txtStart.getText().trim());
                LocalDate end = LocalDate.parse(txtEnd.getText().trim());

                if (end.isBefore(start)) {
                    JOptionPane.showMessageDialog(this, "Bitiş tarihi başlangıçtan önce olamaz!", "Tarih Hatası", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // İzin talebi oluştur
                LeaveRequest req = new LeaveRequest(student.getId(), start, end);
                req.setReason(r);

                // Veritabanına kaydet
                Database.getInstance().addLeaveRequest(req);

                // Admin'e bildirim gönder (Observer Pattern)
                Observer obs = new AdminObserver();
                obs.update("YENİ İZİN TALEBİ: " + student.getName());

                JOptionPane.showMessageDialog(this, "Talep başarıyla gönderildi!");
                dispose();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Tarih formatı hatalı! (YYYY-AA-GG formatında olmalı)\nÖrnek: 2025-05-20", "Format Hatası", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Bir hata oluştu: " + ex.getMessage());
            }
        });
    }
}