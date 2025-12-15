package pattern;

// Bu arayüz, State Design Pattern (Durum Deseni) yapısındaki "State" rolündedir.
// Farklı durumların (Onaylandı, Reddedildi, Beklemede) ortak davranışını belirler.
public interface RequestState {

    // Mevcut durumun ismini döndüren soyut metot
    String getStateName();
}