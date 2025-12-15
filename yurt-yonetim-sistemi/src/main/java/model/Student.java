package model;

// Bu sınıf, Factory Design Pattern yapısında "Concrete Product" (Somut Ürün) rolündedir.
// UserFactory tarafından "STUDENT" tipi istendiğinde bu sınıf üretilir.
public class Student extends User {

    // Öğrenciye özgü alanlar
    private String roomNumber;
    private String phone;
    private String email;
    private String address;
    private String tc;

    // Öğrenci nesnesini oluşturan kurucu metot
    public Student(int id, String username, String name, String password, String roomNumber) {
        // Üst sınıf (User) kurucusunu çağırır ve rolü otomatik "STUDENT" yapar
        super(id, username, name, password, "STUDENT");
        this.roomNumber = roomNumber;
    }

    // --- Getter ve Setter Metotları ---

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getTc() {
        return tc;
    }

    @Override
    public void setTc(String tc) {
        this.tc = tc;
    }

    // Soyut metodun öğrenciye özel uygulaması
    @Override
    public String getDashboardInfo() {
        return "Öğrenci Paneli - Oda: " + (roomNumber != null ? roomNumber : "Atanmadı");
    }
}