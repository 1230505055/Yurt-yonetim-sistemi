package pattern;

// Bu arayüz, Observer Design Pattern (Gözlemci Deseni) yapısındaki "Observer" rolündedir.
// Bildirim almak isteyen tüm sınıflar (örneğin AdminObserver) bu arayüzü uygulamalıdır.
public interface Observer {

    // Gözlemlenen nesne (Subject) bir değişiklik olduğunda bu metodu çağırır.
    // message: Gönderilen bildirim mesajı
    void update(String message);
}