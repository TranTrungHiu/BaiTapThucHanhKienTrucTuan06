# Hệ thống Microservices Thương mại điện tử

## Giới thiệu

Dự án này là một hệ thống microservices được xây dựng bằng Spring Boot và Spring Cloud, tập trung vào việc xây dựng một nền tảng thương mại điện tử với các dịch vụ riêng biệt cho từng chức năng như quản lý khách hàng, sản phẩm, đơn hàng, xác thực người dùng, và gateway API.

## Kiến trúc hệ thống

![Kiến trúc Microservices](architecture_diagram.png)

Hệ thống bao gồm các thành phần chính sau:

### 1. Eureka Server (Port 8761)

Service discovery, giúp đăng ký và quản lý tất cả các microservice trong hệ thống. Các service khác đăng ký với Eureka Server và sử dụng nó để tìm kiếm và giao tiếp với nhau.

### 2. API Gateway (Port 8080)

Cổng vào chính của hệ thống, điều hướng các request từ client đến các microservice phù hợp. Xử lý việc xác thực JWT và bảo mật cho toàn hệ thống. Gateway cấu hình các route sau:

- `/auth/**` -> Identity Service (xác thực)
- `/products/**` -> Product Service (quản lý sản phẩm)
- `/orders/**` -> Order Service (quản lý đơn hàng)
- `/customers/**` -> Customer Service (quản lý khách hàng)

### 3. Identity Service

Chịu trách nhiệm xác thực người dùng, tạo JWT token và quản lý thông tin đăng nhập. Service này cung cấp các endpoint cho đăng nhập, đăng ký và quản lý xác thực.

### 4. Customer Service

Quản lý thông tin khách hàng, bao gồm đăng ký, cập nhật thông tin cá nhân và quản lý tài khoản.

### 5. Product Service

Quản lý danh mục sản phẩm, thông tin sản phẩm, giá cả, tồn kho và các thuộc tính khác của sản phẩm.

### 6. Order Service

Xử lý đơn hàng, bao gồm tạo đơn hàng mới, cập nhật trạng thái đơn hàng, và quản lý lịch sử đơn hàng.

### 7. RabbitMQ Service

Message broker được triển khai qua Docker Compose, giúp các microservice giao tiếp với nhau thông qua hàng đợi tin nhắn, đảm bảo tính nhất quán và độ tin cậy trong giao tiếp giữa các service.

## Công nghệ sử dụng

- **Spring Boot 3.4.4**: Framework Java để phát triển các ứng dụng standalone, production-ready
- **Spring Cloud 2024.0.1**: Tập hợp các công cụ phát triển microservices
- **Java 17**: Ngôn ngữ lập trình chính của dự án
- **Spring Cloud Gateway**: Cổng API để điều hướng các request đến các service tương ứng
- **Spring Cloud Netflix Eureka**: Service discovery để quản lý các microservice
- **Spring Security**: Bảo mật cho ứng dụng
- **JWT (JSON Web Token)**: Xác thực và phân quyền người dùng
- **Maven**: Công cụ quản lý phụ thuộc và build project
- **RabbitMQ**: Message broker để giao tiếp giữa các service

## Cài đặt và Khởi động

### Yêu cầu hệ thống

- JDK 17 hoặc cao hơn
- Maven 3.6.3 hoặc cao hơn
- Docker và Docker Compose (cho RabbitMQ)

### Bước 1: Clone dự án

```bash
git clone <repository-url>
cd BaiTapThucHanhKienTrucTuan05
```

### Bước 2: Khởi động Eureka Server

```bash
cd week06/eureka-server
./mvnw spring-boot:run
```

### Bước 3: Khởi động RabbitMQ (nếu cần)

```bash
cd week06/rabbitmq-service
docker-compose up -d
```

### Bước 4: Khởi động các Microservice (chạy từng lệnh trong các cửa sổ terminal riêng biệt)

```bash
# Identity Service
cd week06/identity-service
./mvnw spring-boot:run

# Customer Service
cd week06/customer-service
./mvnw spring-boot:run

# Product Service
cd week06/product-service
./mvnw spring-boot:run

# Order Service
cd week06/order-service
./mvnw spring-boot:run
```

### Bước 5: Khởi động API Gateway

```bash
cd week06/api-gateway
./mvnw spring-boot:run
```

## Hướng dẫn sử dụng API

### Xác thực người dùng

1. **Đăng ký tài khoản mới**

```
POST /auth/register
Content-Type: application/json

{
  "username": "nguyenvan",
  "password": "password123",
  "fullName": "Nguyễn Văn A",
  "email": "nguyenvan@example.com"
}
```

2. **Đăng nhập**

```
POST /auth/login
Content-Type: application/json

{
  "username": "nguyenvan",
  "password": "password123"
}
```

Phản hồi sẽ bao gồm JWT token cần được sử dụng trong các request tiếp theo.

### Quản lý khách hàng

1. **Xem thông tin cá nhân**

```
GET /customers/me
Authorization: Bearer {jwt_token}
```

2. **Cập nhật thông tin cá nhân**

```
PUT /customers/{customerId}
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "fullName": "Nguyễn Văn A",
  "email": "nguyenvan.new@example.com",
  "phone": "0123456789",
  "address": "123 Đường ABC, Quận XYZ, TP.HCM"
}
```

### Quản lý sản phẩm

1. **Xem danh sách sản phẩm**

```
GET /products
```

2. **Tìm kiếm sản phẩm**

```
GET /products/search?name=điện thoại
```

3. **Chi tiết sản phẩm**

```
GET /products/{productId}
```

### Quản lý đơn hàng

1. **Tạo đơn hàng mới**

```
POST /orders
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ],
  "shippingAddress": "123 Đường ABC, Quận XYZ, TP.HCM",
  "paymentMethod": "COD"
}
```

2. **Xem chi tiết đơn hàng**

```
GET /orders/{orderId}
Authorization: Bearer {jwt_token}
```

3. **Lịch sử đơn hàng**

```
GET /orders/history
Authorization: Bearer {jwt_token}
```

## Demo Screenshots

![Dashboard](screenshot_dashboard.png)
![Product List](screenshot_products.png)
![Order Management](screenshot_orders.png)

## Cấu trúc dự án

```
week06/
├── api-gateway/           # API Gateway Service
├── customer-service/      # Customer Management Service
├── eureka-server/         # Service Discovery
├── identity-service/      # Authentication & Authorization Service
├── order-service/         # Order Management Service
├── product-service/       # Product Management Service
└── rabbitmq-service/      # Message Broker Configuration
```

## Thông tin bổ sung

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **RabbitMQ Management**: http://localhost:15672 (username: guest, password: guest)

## Những phát triển tiếp theo

1. Thêm service thanh toán (Payment Service)
2. Thêm service quản lý kho (Inventory Service)
3. Triển khai Resilience4j để xử lý Circuit Breaker
4. Thêm ELK Stack để quản lý logging tập trung
5. Triển khai Prometheus và Grafana để giám sát hệ thống
