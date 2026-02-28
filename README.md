# Math Online Judge (MathOJ) - AI Powered Judge

**Math Online Judge** là một hệ thống chấm bài toán học trực tuyến hiện đại, nơi các bài làm được đánh giá và chấm điểm tự động thông qua trí tuệ nhân tạo (GPT). Dự án kết hợp sức mạnh của Spring Boot ở backend và React ở frontend để tạo ra một trải nghiệm học tập và kiểm tra tối ưu.

## 🌟 Tính năng chính

* **Chấm bài bằng AI**: Sử dụng OpenAI GPT để phân tích và chấm điểm các lời giải toán học phức tạp.
* **Quản lý bài tập**: Hệ thống quản lý danh sách bài tập (Problems) theo môn học (Subjects) và nhãn (Tags).
* **Quản lý người dùng**: Đăng ký, đăng nhập và bảo mật tài khoản với JWT.
* **Lịch sử nộp bài**: Theo dõi danh sách và trạng thái các bài đã nộp.
* **Tài liệu API**: Tích hợp Swagger UI để dễ dàng tra cứu và thử nghiệm các endpoint.

## 🛠️ Công nghệ sử dụng

### Backend

* **Ngôn ngữ**: Java 21
* **Framework**: Spring Boot 3.5.5
* **Bảo mật**: Spring Security & JSON Web Token (JWT)
* **Dữ liệu**: Spring Data JPA, MySQL Connector
* **AI Integration**: OpenAI Java SDK
* **Công cụ hỗ trợ**: Lombok, SpringDoc OpenAPI (Swagger)

### Frontend

* **Framework**: React 19 với TypeScript
* **Công cụ build**: Vite
* **Routing**: React Router DOM
* **Icons**: Lucide React
* **HTTP Client**: Axios

## 🚀 Hướng dẫn cài đặt

### Yêu cầu hệ thống

* Java 21 trở lên
* Node.js
* Maven
* MySQL Server

### 1. Cấu hình Backend

1. Truy cập thư mục `backend/onlinejudge`.
2. Cấu hình cơ sở dữ liệu MySQL và các API Key (OpenAI) trong tệp `src/main/resources/application.properties`.
3. Chạy ứng dụng bằng Maven:
```bash
mvn spring-boot:run

```



### 2. Cấu hình Frontend

1. Truy cập thư mục `frontend/mathoj---ai-powered-judge`.
2. Cài đặt các thư viện cần thiết:
```bash
npm install

```


3. Tạo tệp `.env.local` và thiết lập `GEMINI_API_KEY` hoặc các biến môi trường cần thiết khác.
4. Khởi chạy ứng dụng ở chế độ phát triển:
```bash
npm run dev

```



## 📖 Sử dụng

* **Người dùng**: Đăng ký tài khoản, xem danh sách bài tập, nộp lời giải và nhận phản hồi tức thì từ AI.
* **Admin**: Quản lý kho bài tập, môn học và theo dõi toàn bộ hệ thống qua các API được cung cấp.
* **Swagger UI**: Truy cập tài liệu API tại đường dẫn (mặc định): `http://localhost:8080/swagger-ui.html`.

## 📝 Giấy phép

Dự án được phát triển cho mục đích giáo dục và thử nghiệm ứng dụng AI trong giáo dục.

