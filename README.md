# A2LuanLH_SE1910 - FUNewsManagementSystem

SBA301 Assignment 02. Sinh viên: LuanLH - CE191099. Lớp: SE1910.

Spring Boot 3.5 / Java 21 / Spring Data JPA / SQL Server; React / Vite / React-Bootstrap / Axios. Đăng nhập bằng email/password và session lưu tại backend.

## Chạy trên máy hiện tại

1. Mở `backend/pom.xml` bằng IntelliJ, chọn JDK 21 rồi Reload Maven để nạp dependency và annotation processor của Lombok.
2. Dừng backend cũ và chạy lại class `LuanLHCE191099_Application`. Backend dùng cổng **8082**.
3. Trong `frontend`, chạy `npm ci` khi chưa có node_modules rồi `npm run dev`.
4. Mở **http://127.0.0.1:5175** để đến Login hoặc màn quản lý theo phiên hiện tại. Bảng tin công khai ở **http://127.0.0.1:5175/news**. Frontend chuyển request `/api` đến backend qua Vite proxy.

File `backend/application-local.properties` trên máy làm bài đã có kết nối database riêng, được bỏ qua bởi Git. Không cần cấu hình khóa ký để chạy ứng dụng.

Hoặc mở hai terminal tại thư mục gốc project:

```powershell
.\LuanLHCE191099_run-backend.ps1
```

```powershell
.\LuanLHCE191099_run-frontend.ps1
```

## Tài khoản mẫu

| Role  | Email                | Password    |
| ----- | -------------------- | ----------- |
| Admin | `admin@funews.local` | `Admin@123` |
| Staff | `staff@funews.local` | `Staff@123` |

Dữ liệu mẫu chỉ được thêm khi bảng tài khoản trống; không ghi đè tài khoản đã sửa. Khách có thể đọc tin Active mà không đăng nhập.

## Khi clone sang máy khác

1. Cài JDK 21, Node 22.12+ hoặc Node 24 và SQL Server.
2. Kết nối bằng driver **Microsoft SQL Server**, chạy `database/LuanLHCE191099_create-database.sql` để tạo database **A2LuanLH_SE1910**. Hibernate tạo/cập nhật các bảng khi khởi động.
3. Copy `backend/application-local.properties.example` thành `backend/application-local.properties`, điền tài khoản và mật khẩu SQL Server.
4. Trong `backend`, chạy `.\mvnw.cmd spring-boot:run`. Trong `frontend`, chạy `npm ci` rồi `npm run dev`.

## Đăng nhập bằng session

1. React gửi email/password đến `POST /api/auth/login`.
2. AuthService tìm account trong database và kiểm tra BCrypt password.
3. AuthController lưu người đăng nhập và quyền vào `HttpSession` thông qua Spring Security.
4. Backend đặt cookie HttpOnly tên `A2SESSION`. Trình duyệt tự lưu và gửi cookie cho các request tiếp theo.
5. F5 gọi `GET /api/auth/me` để phục hồi thông tin người dùng từ phiên.
6. Sign out gọi `POST /api/auth/logout`; Spring xóa phiên ở server và cookie ở trình duyệt.

Phiên hết hạn sau 30 phút không hoạt động. Frontend giữ user trong React state, không lưu thông tin đăng nhập bằng localStorage/sessionStorage. Quyền trong session được cập nhật khi đăng nhập hoặc gọi `/auth/me`.

Vì dùng cookie, Spring bật bảo vệ CSRF cho request ghi dữ liệu. Axios tự gọi `GET /api/auth/csrf` trước POST/PUT/DELETE và gửi giá trị nhận được qua header `X-CSRF-TOKEN`. Giá trị này kiểm tra request ghi; danh tính đăng nhập vẫn nằm trong session ở backend.

## Chức năng

- Khách: xem và tìm tin Active, mở nội dung bài viết.
- Admin: CRUD/Search Account; chặn xóa tài khoản đã tạo tin, tự xóa tài khoản hiện tại và xóa/hạ quyền Admin cuối cùng.
- Staff: CRUD/Search Category; chọn Category cha, chặn chu trình cha/con và xóa Category đang dùng.
- Staff: CRUD/Search News cùng tags; sửa hồ sơ cá nhân và xem tin do chính mình tạo.
- Create/Edit dùng popup; Delete luôn có hộp xác nhận.
- Role trong database: Admin = 1, Staff = 2. News/Category: Active = 1, Inactive = 0.
- Bảng: SystemAccount, Category, NewsArticle, Tag, NewsTag. Mỗi Tag thuộc tối đa một NewsArticle.

## Cách đọc source

Đọc [hướng dẫn chi tiết toàn bộ Assignment 02](HUONG_DAN_ASSIGNMENT02_CHI_TIET.md): đối chiếu A1 → A2, cấu trúc thư mục, đăng nhập/session, từng luồng CRUD, code quan trọng và cách tự kiểm tra. [Bản đọc nhanh theo luồng](docs/HUONG_DAN_THEO_LUONG.md) giúp tra cứu khi chạy bài.

Frontend chia thành `pages`, `components/Layout`, `components/Common`, `context`, `hooks` và `services`. Backend có ba tầng Controller -> Service -> Repository. Mỗi DTO có file riêng. Bốn entity dùng Lombok `@Getter`, `@Setter`, `@NoArgsConstructor` để sinh getter/setter và constructor rỗng. Entity ánh xạ bảng; SecurityConfig thiết lập session và phân quyền.

File Java/JS/JSX/CSS dùng tiền tố `LuanLHCE191099_`; các file cấu hình giữ tên chuẩn. Comment trong source dùng tiếng Anh.

## Format và kiểm tra

Java dùng 4 dấu cách; JavaScript/JSX/CSS dùng 2 dấu cách, theo `.editorconfig` và `.prettierrc.json`.

```powershell
# Trong frontend
npm run format
npm run format:check
npm run build
npm run lint

# Trong backend
.\mvnw.cmd clean test
```

Test backend dùng H2 riêng. Bộ kiểm tra gồm CRUD/Search, phân quyền, validation, session sau login, đổi session ID, logout và bảo vệ CSRF.

Import `postman/LuanLHCE191099_Assignment02.postman_collection.json` và Run theo thứ tự. Giữ cookie jar của Postman bật; collection tự lấy CSRF trước request ghi, tự giữ cookie đăng nhập và xóa các bản ghi thử ở cuối. Không cần nhập Authorization header.

`database/LuanLHCE191099_verification.sql` chứa SELECT đối chiếu dữ liệu/quan hệ. API trả 400 cho input sai, 401 khi chưa đăng nhập, 403 khi thiếu quyền/CSRF, 404 khi không tìm thấy và 409 khi xung đột dữ liệu. Response Account không chứa password/hash.
