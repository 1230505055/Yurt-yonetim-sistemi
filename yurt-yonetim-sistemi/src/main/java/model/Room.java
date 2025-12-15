package model;

public class Room {
    private String roomNumber;
    private int capacity;

    // Odayı oluştururken gerekli bilgileri alan kurucu metot
    public Room(String roomNumber, int capacity) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
    }

    // --- Getter ve Setter Metotları ---

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    // Arayüzlerde (ComboBox vb.) sadece oda numarasının görünmesi için
    @Override
    public String toString() {
        return roomNumber;
    }
}