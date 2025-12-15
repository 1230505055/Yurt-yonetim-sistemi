package view;

import db.Database;
import model.Student;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class StudentProfileEditFrame extends JDialog {

    private final Student student;
    private boolean isEditMode = false; // Düzenleme modu kontrolü

    // Düzenlenebilir Alanlar
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtAddress;
    private JButton btnAction;

    public StudentProfileEditFrame(Student student) {
        this.student = student;
        initUI();
    }

    private void initUI() {
        // Pencere Ayarları
        setTitle("Profil Bilgilerim");
        setSize(450, 550);
        setLocationRelativeTo(null);
        setModal(true); // Arka planı kilitler
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Ana Panel ve Arka Plan Rengi
        JPanel mainPanel = new JPanel(new MigLayout("fillx, insets 20", "[120!][grow, fill]", "[]15[]15[]"));
        mainPanel.setBackground(new Color(246, 246, 240));
        getContentPane().setBackground(new Color(246, 246, 240));

        // 1. Başlık
        JLabel lblTitle = new JLabel("Profil Kartı");
        lblTitle.setFont(new Font("Serif", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 60, 60));
        mainPanel.add(lblTitle, "span, center, wrap, gapbottom 10");

        // 2. Sabit Bilgiler (Değiştirilemez)
        addReadOnlyField(mainPanel, "Ad Soyad:", student.getName());
        addReadOnlyField(mainPanel, "T.C. Kimlik:", student.getTc() != null ? student.getTc() : "-");
        addReadOnlyField(mainPanel, "Kullanıcı Adı:", student.getUsername());

        // Oda bilgisini veritabanından güncel çek
        String roomNum = Database.getInstance().getStudentRoomNumber(student.getId());
        addReadOnlyField(mainPanel, "Oda Numarası:", (roomNum != null && !roomNum.isEmpty()) ? roomNum : "Atanmadı");

        JSeparator sep = new JSeparator();
        sep.setForeground(Color.LIGHT_GRAY);
        mainPanel.add(sep, "span, growx, gaptop 10, gapbottom 10, wrap");

        // 3. Güncellenebilir Bilgiler
        mainPanel.add(new JLabel("Telefon:"));
        txtPhone = new JTextField(student.getPhone());
        setupEditableField(txtPhone);
        mainPanel.add(txtPhone, "wrap");

        mainPanel.add(new JLabel("E-Posta:"));
        txtEmail = new JTextField(student.getEmail());
        setupEditableField(txtEmail);
        mainPanel.add(txtEmail, "wrap");

        mainPanel.add(new JLabel("Adres:"));
        txtAddress = new JTextField(student.getAddress());
        setupEditableField(txtAddress);
        mainPanel.add(txtAddress, "wrap");

        // 4. Butonlar
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        JButton btnClose = new JButton("Kapat");
        btnClose.setBackground(new Color(187, 189, 149)); // Açık yeşil/gri
        btnClose.setForeground(new Color(31, 31, 31));
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnClose.addActionListener(e -> dispose());

        btnAction = new JButton("Bilgileri Güncelle");
        btnAction.setBackground(new Color(118, 142, 82)); // Yeşil
        btnAction.setForeground(Color.WHITE);
        btnAction.setFont(new Font("SansSerif", Font.BOLD, 12));

        btnAction.addActionListener(e -> toggleEditMode());

        btnPanel.add(btnClose);
        btnPanel.add(btnAction);

        mainPanel.add(btnPanel, "span, gaptop 20");

        add(mainPanel);
    }

    // Yardımcı Metot: Sadece okunan alan ekler
    private void addReadOnlyField(JPanel panel, String label, String value) {
        panel.add(new JLabel(label));
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblValue.setForeground(new Color(80, 80, 80));
        panel.add(lblValue, "wrap");
    }

    // Yardımcı Metot: Editlenebilir alanların varsayılan ayarı (Kilitli başlar)
    private void setupEditableField(JTextField field) {
        disableField(field); // Başlangıçta pasif
    }

    // Düzenleme Modu Geçiş Mantığı
    private void toggleEditMode() {
        if (!isEditMode) {
            // 1. DÜZENLEME MODUNU AÇ
            isEditMode = true;

            // Alanları aç
            enableField(txtPhone);
            enableField(txtEmail);
            enableField(txtAddress);

            // Butonu "Kaydet" yap
            btnAction.setText("Değişiklikleri Kaydet");
            btnAction.setBackground(new Color(212, 172, 13)); // Turuncu (Uyarı rengi)

            txtPhone.requestFocus();

        } else {
            // 2. KAYDETME İŞLEMİ
            String newPhone = txtPhone.getText().trim();
            String newEmail = txtEmail.getText().trim();
            String newAddr = txtAddress.getText().trim();

            // Veritabanına Kaydet
            boolean success = Database.getInstance().updateStudentProfile(student.getId(), newPhone, newEmail, newAddr);

            if (success) {
                // Nesneyi güncelle (Panel yenilenince görünsün)
                student.setPhone(newPhone);
                student.setEmail(newEmail);
                student.setAddress(newAddr);

                JOptionPane.showMessageDialog(this, "Bilgileriniz başarıyla güncellendi!");

                // Modu kapat, eski haline dön
                isEditMode = false;
                disableField(txtPhone);
                disableField(txtEmail);
                disableField(txtAddress);

                btnAction.setText("Bilgileri Güncelle");
                btnAction.setBackground(new Color(118, 142, 82)); // Yeşile dön
            } else {
                JOptionPane.showMessageDialog(this, "Güncelleme sırasında hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Alanı düzenlenebilir yapar (Beyaz arka plan, kenarlık)
    private void enableField(JTextField field) {
        field.setEditable(true);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    // Alanı kilitler (Arka plan rengi, kenarlık yok)
    private void disableField(JTextField field) {
        field.setEditable(false);
        field.setBackground(new Color(246, 246, 240)); // Panel rengiyle aynı
        field.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Çerçevesiz
    }
}