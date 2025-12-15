package pattern;

import model.Room;

// Bu sınıf, Factory Design Pattern (Fabrika Deseni) kullanarak oda nesneleri üretir.
public class RoomFactory {

    // İstenilen özelliklere sahip yeni bir Room nesnesi oluşturup döndürür
    public static Room createRoom(String no, int cap) {
        return new Room(no, cap);
    }
}