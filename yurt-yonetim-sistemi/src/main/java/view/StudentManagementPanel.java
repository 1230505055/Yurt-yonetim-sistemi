package view;

import com.formdev.flatlaf.FlatClientProperties;
import db.Database;
import model.LeaveRequest;
import model.Student;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentManagementPanel extends JPanel {

    private JTextField txtSearchName;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JTable table;
    private DefaultTableModel tableModel;

    public StudentManagementPanel() {
        initUI();
    }

    // Arayüz bileşenlerini oluşturur
    private void initUI() {
        setLayout(new MigLayout("fill, insets 30 40 30 40", "[grow]", "[]20[]20[grow]"));
        setOpaque(false);

        JLabel lblTitle = new JLabel("Öğrenci Yönetimi ve Sorgulama");
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: Serif bold +24; foreground: #333");
        add(lblTitle, "wrap");

        JPanel filterPanel = new JPanel(new MigLayout("fillx, insets 0, gap 20", "[grow]20[grow]", "[]"));
        filterPanel.setOpaque(false);

        // --- SOL KUTU: İSİM İLE ARAMA ---
        JPanel pnlNameSearch = createCard("Öğrenci Ara");
        txtSearchName = new JTextField();
        txtSearchName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Öğrenci adı veya soyadı...");

        JButton btnSearchName = createButton("Ara", new Color(118, 142, 82));

        pnlNameSearch.add(txtSearchName, "growx, pushx, split 2, h 35!");
        pnlNameSearch.add(btnSearchName, "width 80!, h 35!");

        // --- SAĞ KUTU: TARİH İLE İZİN SORGULAMA ---
        JPanel pnlDateSearch = createCard("İzinli Öğrenci Listele");
        txtStartDate = new JTextField(LocalDate.now().toString());
        txtEndDate = new JTextField(LocalDate.now().plusDays(7).toString());

        txtStartDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "YYYY-AA-GG");
        txtEndDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "YYYY-AA-GG");

        JButton btnFilterDate = createButton("Listele", new Color(187, 189, 149));

        pnlDateSearch.add(new JLabel("Başlangıç:"), "split 5");
        pnlDateSearch.add(txtStartDate, "width 100!, h 35!");
        pnlDateSearch.add(new JLabel("Bitiş:"), "gapleft 10");
        pnlDateSearch.add(txtEndDate, "width 100!, h 35!");
        pnlDateSearch.add(btnFilterDate, "gapleft 10, width 80!, h 35!");

        filterPanel.add(pnlNameSearch, "grow");
        filterPanel.add(pnlDateSearch, "grow");

        add(filterPanel, "wrap, growx");

        // Tablo yapısı
        String[] cols = {"ID / Oda", "Ad Soyad", "İletişim / Durum", "Detay"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, "grow");

        // Aksiyonlar
        btnSearchName.addActionListener(e -> searchStudent());
        txtSearchName.addActionListener(e -> searchStudent());
        btnFilterDate.addActionListener(e -> searchLeaves());
    }

    // İsme göre öğrenci arar
    private void searchStudent() {
        String name = txtSearchName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen bir isim giriniz.");
            return;
        }
        List<Student> students = Database.getInstance().searchStudentsByName(name);
        updateTableForStudents(students);
    }

    // Tarihe göre izin arar
    private void searchLeaves() {
        try {
            LocalDate start = LocalDate.parse(txtStartDate.getText());
            LocalDate end = LocalDate.parse(txtEndDate.getText());

            if (start.isAfter(end)) {
                JOptionPane.showMessageDialog(this, "Başlangıç tarihi bitişten büyük olamaz.");
                return;
            }

            List<LeaveRequest> leaves = Database.getInstance().searchLeavesByDate(start, end);
            updateTableForLeaves(leaves);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Tarih formatı hatalı! (YYYY-AA-GG formatında olmalı)\n" + ex.getMessage());
        }
    }

    // Tabloyu öğrenci verisiyle doldurur
    private void updateTableForStudents(List<Student> students) {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"ID", "Ad Soyad", "Oda No", "Telefon"});

        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kriterlere uygun kayıt bulunamadı.");
            return;
        }

        for (Student s : students) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    (s.getRoomNumber() == null ? "-" : s.getRoomNumber()),
                    (s.getPhone() == null ? "-" : s.getPhone())
            });
        }
    }

    // Tabloyu izin verisiyle doldurur
    private void updateTableForLeaves(List<LeaveRequest> leaves) {
        tableModel.setRowCount(0);
        tableModel.setColumnIdentifiers(new String[]{"Öğrenci Adı", "Başlangıç Tarihi", "Bitiş Tarihi", "İzin Sebebi"});

        if (leaves.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bu tarih aralığında izinli öğrenci yok.");
            return;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (LeaveRequest lr : leaves) {
            tableModel.addRow(new Object[]{
                    lr.getStudentName(),
                    lr.getStartDate().format(dtf),
                    lr.getEndDate().format(dtf),
                    lr.getReason()
            });
        }
    }

    // Arama kartlarını oluşturur (Stil temizlendi)
    private JPanel createCard(String title) {
        JPanel card = new JPanel(new MigLayout("wrap, fillx, insets 20", "[grow]", "[]10[]"));
        card.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.putClientProperty(FlatClientProperties.STYLE, "font: SansSerif bold; foreground: #7f8c8d");
        card.add(lblTitle);
        return card;
    }

    // Buton oluşturucu (Stil temizlendi)
    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        // FlatLaf stil stringleri (arc, margin) kaldırıldı
        return btn;
    }
}