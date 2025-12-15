package db;

import model.*;
import pattern.UserFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Database {
    private static Database instance;

    private Database() {}

    public static Database getInstance() {
        if (instance == null) instance = new Database();
        return instance;
    }

    // Kullanıcı giriş bilgilerini doğrular
    public Optional<User> login(String input, String password) {
        String sql = "SELECT * FROM users WHERE (username=? OR email=? OR tc=?) AND password=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, input);
            pst.setString(2, input);
            pst.setString(3, input);
            pst.setString(4, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                User user = UserFactory.createUser(
                        rs.getString("role"),
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("room_number")
                );
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setTc(rs.getString("tc"));
                return Optional.of(user);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return Optional.empty();
    }

    // Yeni öğrenci kaydı oluşturur
    public boolean addStudent(User student) {
        String sql = "INSERT INTO users (username, password, name, role, email, phone, tc, room_number) VALUES (?, ?, ?, 'STUDENT', ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, student.getUsername());
            pst.setString(2, student.getPassword());
            pst.setString(3, student.getName());
            pst.setString(4, student.getEmail());
            pst.setString(5, student.getPhone());
            pst.setString(6, student.getTc());
            if(student instanceof Student) pst.setString(7, ((Student)student).getRoomNumber());
            else pst.setString(7, null);
            return pst.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // Öğrenci iletişim bilgilerini günceller
    public boolean updateStudentProfile(int studentId, String phone, String email, String address) {
        String sql = "UPDATE users SET phone=?, email=?, address=? WHERE id=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, phone);
            pst.setString(2, email);
            pst.setString(3, address);
            pst.setInt(4, studentId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // Öğrenciyi ve ilişkili izinlerini siler
    public boolean deleteStudent(int studentId) {
        try (Connection con = DatabaseConnection.getInstance().getConnection()) {
            String sqlLeaves = "DELETE FROM leave_requests WHERE student_id=?";
            try (PreparedStatement pst = con.prepareStatement(sqlLeaves)) {
                pst.setInt(1, studentId);
                pst.executeUpdate();
            }
            String sqlUser = "DELETE FROM users WHERE id=? AND role='STUDENT'";
            try (PreparedStatement pst = con.prepareStatement(sqlUser)) {
                pst.setInt(1, studentId);
                return pst.executeUpdate() > 0;
            }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // Tüm öğrencileri listeler
    public List<Student> getStudents() {
        List<Student> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             ResultSet rs = con.createStatement().executeQuery("SELECT * FROM users WHERE role='STUDENT'")) {
            while (rs.next()) {
                User u = UserFactory.createUser("STUDENT", rs.getInt("id"), rs.getString("username"), rs.getString("name"), rs.getString("password"), rs.getString("room_number"));
                u.setEmail(rs.getString("email")); u.setPhone(rs.getString("phone")); u.setTc(rs.getString("tc")); u.setAddress(rs.getString("address"));
                list.add((Student) u);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Yeni bir oda ekler
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, capacity, type) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, room.getRoomNumber());
            pst.setInt(2, room.getCapacity());
            pst.setString(3, "Standart");
            return pst.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // Odayı siler
    public boolean deleteRoom(String roomNumber) {
        String sql = "DELETE FROM rooms WHERE room_number=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, roomNumber);
            return pst.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // Tüm odaları listeler
    public List<Room> getRooms() {
        List<Room> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             ResultSet rs = con.createStatement().executeQuery("SELECT * FROM rooms")) {
            while (rs.next()) {
                list.add(new Room(rs.getString("room_number"), rs.getInt("capacity")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Öğrenciyi odaya atar
    public void assignStudentToRoom(int studentId, String roomNum) {
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement("UPDATE users SET room_number=? WHERE id=?")) {
            pst.setString(1, roomNum);
            pst.setInt(2, studentId);
            pst.executeUpdate();
        } catch(Exception e) { e.printStackTrace(); }
    }

    // Odadaki öğrenci sayısını döndürür
    public int getStudentCountInRoom(String roomNumber) {
        String sql = "SELECT COUNT(*) FROM users WHERE role='STUDENT' AND room_number=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, roomNumber);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // Öğrencinin bir odası olup olmadığını kontrol eder
    public boolean hasRoom(int studentId) {
        String sql = "SELECT room_number FROM users WHERE id=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String room = rs.getString("room_number");
                return room != null && !room.isEmpty();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Odadaki diğer öğrencileri getirir
    public List<Student> getRoommates(String roomNumber) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role='STUDENT' AND room_number=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, roomNumber);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                User u = UserFactory.createUser("STUDENT", rs.getInt("id"), rs.getString("username"), rs.getString("name"), "", roomNumber);
                u.setEmail(rs.getString("email"));
                u.setPhone(rs.getString("phone"));
                list.add((Student) u);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Öğrencinin oda numarasını getirir
    public String getStudentRoomNumber(int studentId) {
        String sql = "SELECT room_number FROM users WHERE id=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getString("room_number");
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // İzin talebi ekler
    public void addLeaveRequest(LeaveRequest req) {
        String sql = "INSERT INTO leave_requests (student_id, start_date, end_date, reason, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, req.getStudentId());
            pst.setDate(2, java.sql.Date.valueOf(req.getStartDate()));
            pst.setDate(3, java.sql.Date.valueOf(req.getEndDate()));
            pst.setString(4, req.getReason());
            pst.setString(5, "Beklemede");
            pst.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Tüm izin taleplerini getirir
    public List<LeaveRequest> getLeaveRequests() {
        List<LeaveRequest> list = new ArrayList<>();
        String sql = "SELECT l.*, u.name as student_name FROM leave_requests l JOIN users u ON l.student_id = u.id";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             ResultSet rs = con.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                LeaveRequest lr = new LeaveRequest(
                        rs.getInt("student_id"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate()
                );
                lr.setDbId(rs.getInt("id"));
                lr.setStudentName(rs.getString("student_name"));
                lr.setReason(rs.getString("reason"));
                lr.setStatusByString(rs.getString("status"));
                list.add(lr);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Belirli bir öğrencinin izinlerini getirir
    public List<LeaveRequest> getStudentLeaveRequests(int studentId) {
        List<LeaveRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM leave_requests WHERE student_id=? ORDER BY start_date DESC";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                LeaveRequest lr = new LeaveRequest(
                        rs.getInt("student_id"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate()
                );
                lr.setDbId(rs.getInt("id"));
                lr.setReason(rs.getString("reason"));
                lr.setStatusByString(rs.getString("status"));
                list.add(lr);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // İzin durumunu günceller
    public void updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE leave_requests SET status=? WHERE id=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, status);
            pst.setInt(2, requestId);
            pst.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Son izin durumunu getirir
    public String getLastLeaveStatus(int studentId) {
        String sql = "SELECT status FROM leave_requests WHERE student_id=? ORDER BY id DESC LIMIT 1";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, studentId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getString("status");
        } catch (Exception e) { e.printStackTrace(); }
        return "Yok";
    }

    // İsme göre öğrenci arar
    public List<Student> searchStudentsByName(String partialName) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role='STUDENT' AND name LIKE ?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, "%" + partialName + "%");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Student s = (Student) UserFactory.createUser("STUDENT",
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        "",
                        rs.getString("room_number"));

                s.setPhone(rs.getString("phone"));
                s.setEmail(rs.getString("email"));
                s.setTc(rs.getString("tc"));
                list.add(s);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Belirli tarih aralığındaki onaylı izinleri arar
    public List<LeaveRequest> searchLeavesByDate(java.time.LocalDate filterStart, java.time.LocalDate filterEnd) {
        List<LeaveRequest> list = new ArrayList<>();

        String sql = "SELECT l.*, u.name as student_name FROM leave_requests l " +
                "JOIN users u ON l.student_id = u.id " +
                "WHERE (l.status = 'Onaylandı' OR l.status = 'Approved') " +
                "AND l.start_date <= ? AND l.end_date >= ?";

        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setDate(1, java.sql.Date.valueOf(filterEnd));
            pst.setDate(2, java.sql.Date.valueOf(filterStart));

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                LeaveRequest lr = new LeaveRequest(
                        rs.getInt("student_id"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate()
                );
                lr.setDbId(rs.getInt("id"));
                lr.setReason(rs.getString("reason"));
                lr.setStatusByString(rs.getString("status"));
                lr.setStudentName(rs.getString("student_name"));
                list.add(lr);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Toplam öğrenci sayısını getirir
    public int getTotalStudentCount() {
        String sql = "SELECT COUNT(*) FROM users WHERE role='STUDENT'";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // Doluluk oranını hesaplar
    public String getOccupancyRate() {
        int totalCapacity = 0;
        int usedCapacity = 0;
        try (Connection con = DatabaseConnection.getInstance().getConnection()) {
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT SUM(capacity) FROM rooms")) {
                if (rs.next()) totalCapacity = rs.getInt(1);
            }
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users WHERE role='STUDENT' AND room_number IS NOT NULL AND room_number != ''")) {
                if (rs.next()) usedCapacity = rs.getInt(1);
            }
            if (totalCapacity == 0) return "%0";
            int rate = (usedCapacity * 100) / totalCapacity;
            return "%" + rate;
        } catch (Exception e) { e.printStackTrace(); }
        return "%0";
    }

    // Bekleyen izin sayısını getirir
    public int getPendingLeaveRequestCount() {
        String sql = "SELECT COUNT(*) FROM leave_requests WHERE status='Beklemede' OR status='Pending'";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
}