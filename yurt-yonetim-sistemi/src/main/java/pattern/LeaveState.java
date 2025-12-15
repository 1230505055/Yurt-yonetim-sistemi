package pattern;

// Bu arayüz, State Design Pattern (Durum Deseni) yapısındaki "State" rolündedir.
// İzin talebinin farklı durumlarını temsil eden sınıflar bu arayüzü kullanır.
public interface LeaveState {

    // Durumun ismini (örn: "Beklemede", "Onaylandı") döndüren metot
    String getStatusName();
}