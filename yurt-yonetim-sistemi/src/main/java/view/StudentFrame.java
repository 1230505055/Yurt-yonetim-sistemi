package view;

import db.Database;
import model.LeaveRequest;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class StudentFrame extends JFrame {
    private Student student;
    private DefaultTableModel model;

    public StudentFrame(Student student) {
        this.student = student;

        // Pencere ayarları
        setTitle("Öğrenci Paneli - " + student.getName());
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // 1. Sekme: Öğrenci Bilgileri
        JPanel pnlInfo = new JPanel(new GridLayout(3, 1));
        pnlInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblName = new JLabel("Hoşgeldiniz: " + student.getName());
        lblName.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel lblRoom = new JLabel("Oda Numaranız: " +
                (student.getRoomNumber() != null ? student.getRoomNumber() : "Atanmadı"));
        lblRoom.setFont(new Font("SansSerif", Font.PLAIN, 14));

        pnlInfo.add(lblName);
        pnlInfo.add(lblRoom);

        // 2. Sekme: İzin Talepleri
        JPanel pnlRequest = new JPanel(new BorderLayout());

        // Tablo yapısı
        model = new DefaultTableModel(new String[]{"Başlangıç", "Bitiş", "Sebep", "Durum"}, 0);
        JTable table = new JTable(model);
        loadRequests(); // Verileri veritabanından çek

        // Alt panel (Talep Gönderme)
        JPanel pnlBottom = new JPanel(new FlowLayout());
        JTextField txtReason = new JTextField(20);
        JButton btnSend = new JButton("İzin İste");

        btnSend.addActionListener(e -> {
            if(!txtReason.getText().trim().isEmpty()) {
                sendRequest(txtReason.getText());
                txtReason.setText("");
                loadRequests(); // Tabloyu yenile
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen bir sebep giriniz.");
            }
        });

        pnlBottom.add(new JLabel("Sebep:"));
        pnlBottom.add(txtReason);
        pnlBottom.add(btnSend);

        pnlRequest.add(new JScrollPane(table), BorderLayout.CENTER);
        pnlRequest.add(pnlBottom, BorderLayout.SOUTH);

        // Sekmeleri ekle
        tabs.addTab("Bilgilerim", pnlInfo);
        tabs.addTab("İzin Taleplerim", pnlRequest);
        add(tabs);
    }

    // Yeni izin talebi oluşturur ve veritabanına kaydeder
    private void sendRequest(String reason) {
        // Not: Basit arayüzde tarih seçimi olmadığı için varsayılan olarak
        // Bugün ve Yarın tarihlerini atıyoruz.
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(1);

        LeaveRequest req = new LeaveRequest(student.getId(), start, end);
        req.setReason(reason);
        // Durum varsayılan olarak "Beklemede" atanır (LeaveRequest constructor içinde)

        Database.getInstance().addLeaveRequest(req);
        JOptionPane.showMessageDialog(this, "İzin talebi gönderildi.");
    }

    // Öğrencinin geçmiş izin taleplerini listeler
    private void loadRequests() {
        model.setRowCount(0);
        List<LeaveRequest> requests = Database.getInstance().getStudentLeaveRequests(student.getId());

        for (LeaveRequest req : requests) {
            model.addRow(new Object[]{
                    req.getStartDate(),
                    req.getEndDate(),
                    req.getReason(),
                    req.getStatus() // State pattern üzerinden string durumu alır
            });
        }
    }
}