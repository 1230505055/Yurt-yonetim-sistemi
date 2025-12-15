package view;

import db.Database;
import model.LeaveRequest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class StaffLeaveSearchByDateFrame extends JFrame {
    private DefaultTableModel tableModel;

    public StaffLeaveSearchByDateFrame() {
        // Pencere temel ayarları
        setTitle("Tarih Aralığına Göre İzin Arama");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Arka plan rengi
        getContentPane().setBackground(new Color(246, 246, 240));

        // Üst panel (Arama kriterleri)
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Arama Kriterleri"));
        pnlTop.setBackground(new Color(246, 246, 240));

        JTextField txtStart = new JTextField(LocalDate.now().toString(), 10);
        JTextField txtEnd = new JTextField(LocalDate.now().plusDays(7).toString(), 10);

        JButton btnSearch = new JButton("Ara / Listele");
        btnSearch.setBackground(new Color(118, 142, 82));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("SansSerif", Font.BOLD, 12));

        pnlTop.add(new JLabel("Başlangıç (YYYY-AA-GG):"));
        pnlTop.add(txtStart);
        pnlTop.add(new JLabel("Bitiş:"));
        pnlTop.add(txtEnd);
        pnlTop.add(btnSearch);

        add(pnlTop, BorderLayout.NORTH);

        // Orta panel (Sonuç tablosu)
        String[] cols = {"Öğrenci", "Başlangıç", "Bitiş", "Sebep", "Durum"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tablo düzenlenemez olsun
            }
        };

        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Arama butonu aksiyonu
        btnSearch.addActionListener(e -> {
            try {
                LocalDate start = LocalDate.parse(txtStart.getText().trim());
                LocalDate end = LocalDate.parse(txtEnd.getText().trim());

                // Tarih mantık kontrolü
                if (end.isBefore(start)) {
                    JOptionPane.showMessageDialog(this, "Bitiş tarihi başlangıçtan önce olamaz!", "Hata", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Veritabanı sorgusu
                List<LeaveRequest> results = Database.getInstance().searchLeavesByDate(start, end);

                // Tabloyu temizle ve yeni verileri ekle
                tableModel.setRowCount(0);
                for (LeaveRequest lr : results) {
                    tableModel.addRow(new Object[]{
                            lr.getStudentName(),
                            lr.getStartDate(),
                            lr.getEndDate(),
                            lr.getReason(),
                            lr.getStatus() // LeaveRequest sınıfındaki güvenli metot
                    });
                }

                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Bu tarih aralığında onaylı izin kaydı bulunamadı.");
                }

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Tarih formatı hatalı! Lütfen YYYY-AA-GG formatında giriniz.\nÖrnek: 2025-05-20", "Format Hatası", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}