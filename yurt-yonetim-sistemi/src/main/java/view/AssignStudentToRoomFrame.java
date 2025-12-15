package view;

import db.Database;
import model.Room;
import model.Student;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AssignStudentToRoomFrame extends JFrame {

    public AssignStudentToRoomFrame() {
        // Pencere temel ayarları
        setTitle("Öğrenci Yerleştir");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Arka plan rengi
        getContentPane().setBackground(new Color(246, 246, 240));

        // Form paneli
        JPanel pnlForm = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlForm.setBackground(new Color(246, 246, 240));

        // Veritabanından verileri çek
        List<Student> students = Database.getInstance().getStudents();
        List<Room> rooms = Database.getInstance().getRooms();

        JComboBox<Student> cmbStudents = new JComboBox<>(students.toArray(new Student[0]));
        JComboBox<Room> cmbRooms = new JComboBox<>(rooms.toArray(new Room[0]));

        // Öğrenci listesi görünümü (Zaten odası varsa belirtir)
        cmbStudents.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof Student) {
                    Student s = (Student) value;
                    String status = (s.getRoomNumber() != null && !s.getRoomNumber().isEmpty()) ? " [Odası Var]" : "";
                    setText(s.getName() + status);
                }
                return this;
            }
        });

        // Oda listesi görünümü (Doluluk durumuna göre renk değişimi)
        cmbRooms.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof Room) {
                    Room r = (Room) value;
                    int currentCount = Database.getInstance().getStudentCountInRoom(r.getRoomNumber());
                    setText(r.getRoomNumber() + " (" + currentCount + "/" + r.getCapacity() + ")");

                    // Oda doluysa kırmızı göster
                    if (currentCount >= r.getCapacity()) {
                        setForeground(Color.RED);
                    } else {
                        setForeground(Color.BLACK);
                    }
                }
                return this;
            }
        });

        pnlForm.add(new JLabel("Öğrenci Seç:"));
        pnlForm.add(cmbStudents);
        pnlForm.add(new JLabel("Oda Seç:"));
        pnlForm.add(cmbRooms);

        add(pnlForm, BorderLayout.CENTER);

        // Buton paneli
        JPanel pnlButtons = new JPanel();
        pnlButtons.setBackground(new Color(246, 246, 240));

        JButton btnAssign = new JButton("Yerleştir");
        JButton btnBack = new JButton("Geri Dön");

        // Buton stilleri
        btnAssign.setBackground(new Color(118, 142, 82));
        btnAssign.setForeground(Color.WHITE);

        btnBack.setBackground(new Color(187, 189, 149));
        btnBack.setForeground(new Color(31, 31, 31));

        pnlButtons.add(btnAssign);
        pnlButtons.add(btnBack);

        add(pnlButtons, BorderLayout.SOUTH);

        // Geri dön aksiyonu
        btnBack.addActionListener(e -> dispose());

        // Yerleştirme işlemi aksiyonu
        btnAssign.addActionListener(e -> {
            Student selectedStudent = (Student) cmbStudents.getSelectedItem();
            Room selectedRoom = (Room) cmbRooms.getSelectedItem();

            if (selectedStudent != null && selectedRoom != null) {
                // Öğrencinin zaten odası var mı?
                if (Database.getInstance().hasRoom(selectedStudent.getId())) {
                    int secim = JOptionPane.showConfirmDialog(this,
                            "Öğrencinin zaten odası var. Değiştirilsin mi?",
                            "Onay", JOptionPane.YES_NO_OPTION);
                    if (secim == JOptionPane.NO_OPTION) return;
                }

                // Seçilen oda dolu mu?
                int currentCount = Database.getInstance().getStudentCountInRoom(selectedRoom.getRoomNumber());
                if (currentCount >= selectedRoom.getCapacity()) {
                    JOptionPane.showMessageDialog(this, "Seçilen oda tamamen dolu!", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Atama işlemini yap
                Database.getInstance().assignStudentToRoom(selectedStudent.getId(), selectedRoom.getRoomNumber());
                JOptionPane.showMessageDialog(this, "Öğrenci odaya başarıyla yerleştirildi!");
                dispose();
            }
        });
    }
}