# 🏢 Yurt Yönetim Sistemi (KYKLife)

**Yazılım Mimarisi ve Tasarımı Dersi Dönem Projesi**

Bu proje, modern yazılım mühendisliği prensipleri ve **Tasarım Desenleri (Design Patterns)** kullanılarak geliştirilmiş kapsamlı bir **Yurt Otomasyon Sistemidir**. Sistem; öğrenci barınma, izin yönetimi, oda atama ve personel idari işlemlerini dijitalleştirerek süreçleri hızlandırmayı ve hatasız yönetmeyi hedefler.

## 👥 Proje Ekibi

## Geliştiriciler
Proje sahiplerine ulaşmak için isimlere tıklayabilirsiniz:
- 👨‍💻 [Ertuğrul](https://github.com/1230505055)
- 👨‍💻 [Ertuğrul](https://github.com/1230505029)
- 👨‍💻 [Ertuğrul](https://github.com/1230505025)

## 🏗️ Kullanılan Teknolojiler ve Kütüphaneler

* **Programlama Dili:** Java (JDK 21+)
* **Arayüz (GUI):** Java Swing & AWT
* **Tema Motoru:** FlatLaf (MacLightLaf) - *Modern ve yuvarlak hatlı görünüm için*
* **Layout Manager:** MigLayout - *Esnek ve duyarlı arayüz yerleşimi için*
* **Veritabanı:** MySQL
* **IDE:** IntelliJ IDEA
* **Versiyon Kontrol:** Git & GitHub

## 🏛️ Projede Kullanılan Tasarım Desenleri

Projenin mimarisinde, kodun sürdürülebilirliğini ve genişletilebilirliğini sağlamak amacıyla aşağıdaki tasarım desenleri aktif olarak kullanılmıştır:

### 1. Singleton (Tekil) Deseni
* **Kullanım Yeri:** `Database.java` ve `DatabaseConnection.java`
* **Amaç:** Veritabanı bağlantısının ve yönetimsel işlemlerin uygulama genelinde tek bir nesne (instance) üzerinden yürütülmesini sağlamak. Bu sayede her işlemde yeni bir bağlantı açıp kapatma maliyeti engellenmiş ve veri tutarlılığı sağlanmıştır.

### 2. Factory (Fabrika) Deseni
* **Kullanım Yeri:** `UserFactory.java` ve `RoomFactory.java`
* **Amaç:** Nesne oluşturma mantığını soyutlamak. Sisteme giriş yapan kullanıcının rolüne göre (`STUDENT` veya `STAFF`) veya eklenecek odanın tipine göre doğru nesnenin üretilmesini sağlar. Kod içerisinde `new Student(...)` karmaşasını önler.

### 3. State (Durum) Deseni
* **Kullanım Yeri:** `LeaveRequest` (İzin Talepleri) ve `RequestState` arayüzü
* **Amaç:** Bir izin talebinin durumlarını (**Beklemede, Onaylandı, Reddedildi**) nesne yönelimli olarak yönetmek. Durum geçişleri ve her durumun davranışı (örn: isimlendirme) ilgili durum sınıfları (`ApprovedState`, `PendingState`) tarafından kontrol edilir.

### 4. Observer (Gözlemci) Deseni
* **Kullanım Yeri:** `Observer` arayüzü ve `AdminObserver.java`
* **Amaç:** Sistemdeki kritik olayları dinlemek. Örneğin; bir öğrenci yeni bir izin talebi oluşturduğunda, `AdminObserver` tetiklenerek yöneticiye otomatik bildirim (Pop-up) gönderilmesi sağlanır.

### 5. Strategy (Strateji) Deseni
* **Kullanım Yeri:** `SearchStrategy` arayüzü ve `SearchByName.java`
* **Amaç:** Arama algoritmalarını değiştirebilir kılmak. Şu an "İsme Göre Arama" stratejesi aktiftir, ancak ileride "TC'ye Göre" veya "Odaya Göre" arama eklendiğinde ana kod değiştirilmeden yeni stratejiler entegre edilebilir.

### 6. Facade (Ön Yüz) Deseni (Mantıksal)
* **Kullanım Yeri:** `Database.java`
* **Amaç:** Karmaşık SQL sorgularını (`PreparedStatement`, `ResultSet` işlemleri) Arayüz (View) katmanından gizlemek. Paneller sadece `database.getStudents()` gibi basit metodları çağırır, arka plandaki SQL karmaşasını bilmezler.

## ⚙️ Modüller ve Özellikler

### 👤 Öğrenci Paneli
* **Dashboard:** Anlık oda doluluk durumu, oda arkadaşları ve son izin durumu özeti.
* **Profil Yönetimi:** Telefon, E-posta ve Adres bilgilerini güncelleme.
* **İzin İşlemleri:** Yeni izin talebi oluşturma ve geçmiş izin durumlarını (Onay/Red) listeleme.
* **Oda Bilgisi:** Oda arkadaşlarının iletişim bilgilerini görüntüleme.

### 👔 Personel (Yönetim) Paneli
* **Dashboard:** Yurt doluluk oranı, toplam öğrenci sayısı ve bekleyen izin talepleri istatistikleri.
* **Öğrenci Yönetimi:** Yeni öğrenci kaydı, öğrenci arama ve listeleme.
* **Oda Yönetimi:** Yeni oda ekleme, silme ve öğrencileri odalara atama (Assign).
* **İzin Onay/Red:** Öğrencilerden gelen talepleri görüntüleme ve durumu değiştirme.

## 🚀 Kurulum ve Çalıştırma

Projeyi yerel makinenizde çalıştırmak için aşağıdaki adımları izleyin:

1.  **Veritabanı Kurulumu:**
    * MySQL'de `yurt_yonetim` adında bir veritabanı oluşturun.
    * Proje içerisindeki SQL tablolarını (`users`, `rooms`, `leave_requests`) oluşturun.

2.  **Bağlantı Ayarları:**
    * `src/db/DatabaseConnection.java` dosyasını açın.
    * Kendi MySQL şifrenizi `PASS` değişkenine girin (Varsayılan: `1327`).

3.  **Projeyi Çalıştırma:**
    * `src/view/Main.java` dosyasını çalıştırın.
    * `FlatMacLightLaf` teması otomatik yüklenecek ve giriş ekranı açılacaktır.

## 🔐 Varsayılan Giriş Bilgileri (Örnek)

Sistemi test etmek için veritabanına manuel olarak veya kod üzerinden aşağıdaki gibi kullanıcılar ekleyebilirsiniz:

**Yönetici (Staff):**
* **Kullanıcı Adı:** `admin`
* **Şifre:** `123`
* **Rol:** `STAFF`

**Öğrenci (Student):**
* **Kullanıcı Adı:** `erto`
* **Şifre:** `123`
* **Rol:** `STUDENT`
