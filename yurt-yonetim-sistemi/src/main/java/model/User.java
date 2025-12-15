package model;

// Bu sınıf, Factory Design Pattern yapısında "Abstract Product" (Soyut Ürün) rolündedir.
// Tüm kullanıcı tipleri (Student, Staff) bu sınıftan türetilir.
public abstract class User {
    protected int id;
    protected String username;
    protected String name;
    protected String password;
    protected String role;

    // Ortak kullanıcı özelliklerini başlatan kurucu metot
    public User(int id, String username, String name, String password, String role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    // Alt sınıfların (Student, Staff) kendine özgü panel bilgisini dönmesi için soyut metot
    public abstract String getDashboardInfo();

    // Getter ve Setter metotları
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // Profil güncelleme işlemleri için setter'lar
    public void setEmail(String email) {}
    public void setPhone(String phone) {}
    public void setAddress(String address) {}
    public void setTc(String tc) {}

    // Polymorphism için getter'lar (Override edilebilir)
    public String getEmail() { return null; }
    public String getPhone() { return null; }
    public String getAddress() { return null; }
    public String getTc() { return null; }

    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}