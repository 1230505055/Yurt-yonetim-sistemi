package view;

import com.formdev.flatlaf.FlatClientProperties;
import db.Database;
import model.LeaveRequest;
import model.Staff;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StaffRequestsFrame extends JPanel {

    private final Staff staff;
    private JTable table;
    private DefaultTableModel tableModel;

    public StaffRequestsFrame(Staff staff) {
        this.staff = staff;
        initUI();
        loadData();
    }

    // Arayüz bileşenlerini oluşturur
    private void initUI() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[]20[grow]10[]"));
        setOpaque(false);

        JLabel lblTitle = new JLabel("İzin Talepleri");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: bold +10; foreground: #333");
        add(lblTitle, "wrap");

        createTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Kenarlığı kaldırdık
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        add(scrollPane, "grow, wrap");

        JPanel actionPanel = new JPanel(new MigLayout("insets 0", "[]10[]", "[]"));
        actionPanel.setOpaque(false);

        JButton btnApprove = createActionButton("Onayla", new Color(118, 142, 82));
        btnApprove.addActionListener(e -> approveRequest());

        JButton btnReject = createActionButton("Reddet", new Color(192, 57, 43));
        btnReject.addActionListener(e -> rejectRequest());

        JButton btnRefresh = createActionButton("Yenile", new Color(187, 189, 149));
        btnRefresh.addActionListener(e -> loadData());

        // Tarih Aralığına Göre Arama Butonu (Ekstra Özellik)
        JButton btnSearchDate = createActionButton("Tarih Ara", new Color(70, 130, 180));
        btnSearchDate.addActionListener(e -> new StaffLeaveSearchByDateFrame().setVisible(true));

        actionPanel.add(btnApprove);
        actionPanel.add(btnReject);
        actionPanel.add(btnRefresh);
        actionPanel.add(btnSearchDate, "gapleft 20");

        add(actionPanel, "right");
    }

    // Tablo yapısını hazırlar
    private void createTable() {
        String[] columns = {"ID", "Öğrenci Adı", "Başlangıç", "Bitiş", "Sebep", "Durum"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.getTableHeader().setReorderingAllowed(false);

        // Başlık stili
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE,
                "font: bold; background: #BBBD95; foreground: #1F1F1F; height: 40");

        // Seçim renkleri
        table.setSelectionBackground(new Color(118, 142, 82));
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    // Buton oluşturucu (Güvenli stil)
    private JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        // FlatLaf stil stringi kaldırıldı (Unknown style hatasını önlemek için)
        return btn;
    }

    // Veritabanından verileri çeker ve tabloyu doldurur
    private void loadData() {
        tableModel.setRowCount(0);

        new Thread(() -> {
            try {
                List<LeaveRequest> requests = Database.getInstance().getLeaveRequests();

                SwingUtilities.invokeLater(() -> {
                    for (LeaveRequest req : requests) {
                        tableModel.addRow(new Object[]{
                                req.getDbId(),
                                req.getStudentName(),
                                req.getStartDate(),
                                req.getEndDate(),
                                req.getReason(),
                                req.getStatus() // LeaveRequest sınıfındaki güvenli metot
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Seçili talebi onaylar
    private void approveRequest() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int requestId = (int) table.getValueAt(selectedRow, 0); // ID sütunu
            String currentStatus = (String) table.getValueAt(selectedRow, 5); // Durum sütunu

            if (!"Pending".equalsIgnoreCase(currentStatus) && !"Beklemede".equalsIgnoreCase(currentStatus)) {
                JOptionPane.showMessageDialog(this, "Sadece bekleyen talepler onaylanabilir.");
                return;
            }

            Database.getInstance().updateRequestStatus(requestId, "Onaylandı");

            JOptionPane.showMessageDialog(this, "Talep Onaylandı.");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen bir satır seçin.");
        }
    }

    // Seçili talebi reddeder
    private void rejectRequest() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int requestId = (int) table.getValueAt(selectedRow, 0);
            String currentStatus = (String) table.getValueAt(selectedRow, 5);

            if (!"Pending".equalsIgnoreCase(currentStatus) && !"Beklemede".equalsIgnoreCase(currentStatus)) {
                JOptionPane.showMessageDialog(this, "Sadece bekleyen talepler reddedilebilir.");
                return;
            }

            Database.getInstance().updateRequestStatus(requestId, "Reddedildi");

            JOptionPane.showMessageDialog(this, "Talep Reddedildi.");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen bir satır seçin.");
        }
    }
}