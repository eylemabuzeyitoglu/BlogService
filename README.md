# BlogService Service Katmanı

## Proje Hakkında
BlogService, kullanıcıların blog yazıları oluşturmasına, düzenlemesine ve silmesine olanak tanır. Mikroservis mimarisi ile farklı hizmetlere entegre edilebilir ve bağımsız olarak çalışabilir.

## Teknolojiler
- **Spring Boot 3.2.3**
- **Spring Security**
- **Spring Cloud OpenFeign**
- **JWT (JSON Web Token)**
- **PostgreSQL**
- **RabbitMQ**
- **Docker**
- **Maven**
- **Lombok**

## Proje Yapısı
- **Controller**: REST API endpoint'leri
- **Service**: İş mantığı
- **Repository**: Veritabanı işlemleri
- **Model**: Veri modelleri
- **DTO**: Veri transfer nesneleri (Common modülünde tanımlı)
- **Mapper**: DTO ve Entity dönüşümleri
- **Events**: Uygulama içi olay tanımları (Blog Oluşturuldu, Güncellendi, Silindi, Beğenildi)
- **Security**: JWT tabanlı güvenlik (Authservice modülünden yararlanır)

## Kurulum

### Gereksinimler
- Java 17
- Maven
- PostgreSQL
- RabbitMQ
- Docker

### Adımlar
1. **Veritabanı Kurulumu**
   ```sql
   CREATE DATABASE blog_service;
   ```

2. **RabbitMQ Kurulumu**
   - RabbitMQ sunucusunu başlatın
   - Varsayılan port: 5672

3. **Projeyi Klonlama**
   ```bash
   git clone [proje-url]
   ```

4. **Common Modülünü Derleme**
   ```bash
   cd Common
   mvn clean install
   ```

5. **BlogService'i Başlatma**
   ```bash
   cd BlogService
   mvn spring-boot:run
   ```

## API Endpoint'leri

### BlogService API Endpoint'leri
- `GET /api/blog`: Tüm blogları listele
- `GET /api/blog/{id}`: Belirli bir blogun detaylarını getirir.
- `GET /api/blog/index`: En son 5 blog yazısını listeler.
- `POST /api/blog`: Yeni bir blog yazısı oluşturur. **Yetkilendirme:** `ADMIN` rolü gerektirir.
- `PATCH /api/blog/{id}`: Belirli bir blog yazısını günceller. **Yetkilendirme:** `ADMIN` rolü gerektirir.
- `DELETE /api/blog/{id}`: Belirli bir blog yazısını siler. **Yetkilendirme:** `ADMIN` rolü gerektirir.
- `GET /api/blog/admin/test`: Admin yetkisi gerektiren test endpoint'i. **Yetkilendirme:** `ADMIN` rolü gerektirir.

## Güvenlik
- **JWT Tabanlı Kimlik Doğrulama**: Kullanıcı girişi sırasında JWT token üretilir ve bu token ile kullanıcı kimliği doğrulanır.
- **Role Bazlı Yetkilendirme**: Kullanıcıların rollerine göre (USER, ADMIN) erişim kontrolü sağlanır.
- **Token Doğrulama ve Yenileme**: JWT token'ları doğrulanır ve gerekirse yenilenir. Bu işlem `JwtUtil` sınıfında gerçekleştirilir.
- **CORS Yapılandırması**: Cross-Origin Resource Sharing (CORS) ayarları, farklı kaynaklardan gelen isteklerin güvenli bir şekilde işlenmesini sağlar.


## Mikroservis İletişimi
- **OpenFeign**: HTTP tabanlı servis iletişimi
- **RabbitMQ**: Event-driven iletişim
  - Blog oluşturma bildirimi
  - Blog güncelleme bildirimi
  - Blog silme bildirimi

## Veritabanı Şeması

### BlogService
```sql
CREATE TABLE blog (
    blog_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    full_content TEXT,
    short_content TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    img_url VARCHAR(255)
);

CREATE TABLE blog_liked_user_ids (
    blog_id BIGINT,
    liked_user_id BIGINT,
    FOREIGN KEY (blog_id) REFERENCES blog(blog_id)
);
```

## Hata Yönetimi
- Global exception handler
- Özel exception sınıfları
- HTTP durum kodları
- Hata mesajları

## Deployment
- Docker container'ları




