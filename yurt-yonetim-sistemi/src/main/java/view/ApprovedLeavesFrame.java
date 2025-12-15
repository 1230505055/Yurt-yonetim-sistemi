package view;

import db.Database;
import model.LeaveRequest;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ApprovedLeavesFrame extends JFrame {

    public ApprovedLeavesFrame() {
        // Pencere temel ayarları
        setTitle("Onaylı İzin Geçmişi");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Arka plan rengi
        getContentPane().setBackground(new Color(246, 246, 240));

        // Tablo yapısının oluşturulması
        String[] columns = {"Öğrenci Adı", "Başlangıç", "Bitiş", "Durum"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hücre düzenlemeyi kapat
            }
        };

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);

        // Veritabanından tüm izinleri çekip filtreleme
        List<LeaveRequest> allRequests = Database.getInstance().getLeaveRequests();

        for (LeaveRequest req : allRequests) {
            // Sadece durumu "Onaylandı" olanları listeye ekle
            // getStatus() metodu State nesnesini kontrol edip String döndürür
            if ("Onaylandı".equalsIgnoreCase(req.getStatus()) || "Approved".equalsIgnoreCase(req.getStatus())) {
                model.addRow(new Object[]{
                        req.getStudentName(),
                        req.getStartDate(),
                        req.getEndDate(),
                        req.getStatus()
                });
            }
        }

        // Tabloyu kaydırılabilir panel içine ekle
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}