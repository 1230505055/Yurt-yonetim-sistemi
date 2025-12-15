package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // Veritabanı yapılandırma ayarları
    private final String URL = "jdbc:mysql://localhost:3306/yurt_yonetim";
    private final String USER = "root";
    private final String PASS = "1327"; // Veritabanı şifresi

    // Özel kurucu metot: Sürücüyü yükler ve bağlantıyı açar
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Singleton erişim noktası: Bağlantı yoksa veya koptuysa yeniden oluşturur
    public static DatabaseConnection getInstance() {
        try {
            if (instance == null || instance.getConnection() == null || instance.getConnection().isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instance;
    }

    // Aktif SQL bağlantısını döndürür
    public Connection getConnection() {
        return connection;
    }
}