package pattern;

import model.Student;
import java.util.List;

// Bu arayüz, Strategy Design Pattern (Strateji Deseni) yapısındaki "Strategy" rolündedir.
// Farklı arama algoritmaları (örn: İsme göre, TC'ye göre) bu arayüzü uygulamalıdır.
public interface SearchStrategy {

    // Arama işlemini gerçekleştirecek soyut metot
    // sourceList: Bellek içi arama yapılacaksa kullanılır (SQL kullanıyorsak boş geçilebilir)
    // keyword: Aranacak kelime
    List<Student> search(List<Student> sourceList, String keyword);
}