package model;

public class Staff extends User {

    // Yönetici/Personel nesnesini oluşturan kurucu metot
    // Rol parametresi otomatik olarak "STAFF" olarak atanır
    public Staff(int id, String username, String name, String password) {
        super(id, username, name, password, "STAFF");
    }

    // Kullanıcıya özel panel bilgisini döndüren metodun uygulaması
    @Override
    public String getDashboardInfo() {
        return "Yönetici Paneli";
    }
}