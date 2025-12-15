package pattern;

import model.*;

// Bu sınıf, Factory Design Pattern (Fabrika Deseni) kullanarak kullanıcı nesneleri üretir.
public class UserFactory {

    // İstenilen role göre ("STUDENT" veya "STAFF") uygun nesneyi oluşturup döndürür
    public static User createUser(String role, int id, String username, String name, String password, String extra) {
        if ("STUDENT".equalsIgnoreCase(role)) {
            // Öğrenci nesnesi üretilir (extra parametresi oda numarasıdır)
            return new Student(id, username, name, password, extra);
        } else {
            // Personel nesnesi üretilir (Varsayılan durum)
            return new Staff(id, username, name, password);
        }
    }
}