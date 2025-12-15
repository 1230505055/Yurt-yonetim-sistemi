package pattern;

import db.DatabaseConnection;
import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Bu sınıf, Strategy Design Pattern (Strateji Deseni) kullanarak isme göre arama algoritmasını uygular.
public class SearchByName implements SearchStrategy {

    @Override
    public List<Student> search(List<Student> list, String keyword) {
        List<Student> results = new ArrayList<>();

        // Veritabanı bağlantısı alınır ve SQL sorgusu hazırlanır
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT * FROM users WHERE role='STUDENT' AND name LIKE ?")) {

            // Parametreye girilen kelimeyi içeren kayıtları arar (%kelime%)
            pst.setString(1, "%" + keyword + "%");

            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                // Bulunan sonuçlar Student listesine eklenir
                results.add(new Student(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("name"),
                        "", // Arama sonucunda şifreye ihtiyaç yoktur, boş geçilir
                        rs.getString("room_number")
                ));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}