# A2LuanLH_SE1910 - FUNewsManagementSystem

SBA301 Assignment 02. Sinh viên: LuanLH - CE191099. Lớp: SE1910.

Spring Boot 3.5 / Java 21 / Spring Data JPA / SQL Server; React / Vite / React-Bootstrap / Axios.

## Chạy trên máy hiện tại

Database và file `backend/application-local.properties` đã được cấu hình riêng tại máy làm bài. File này chứa thông tin kết nối local và không đưa lên Git hoặc ZIP.

1. Mở `backend/pom.xml` bằng IntelliJ, chọn JDK 21 và Reload Maven.
2. Chạy class `LuanLHCE191099_Application`. Backend dùng port **8082**.
3. Mở terminal thứ hai trong `frontend`, chạy `npm ci` khi chưa có node_modules rồi `npm run dev`.
4. Mở **http://127.0.0.1:5175**. Frontend proxy `/api` về backend.

Hoặc chạy hai terminal từ thư mục gốc project:

```powershell
.\LuanLHCE191099_run-backend.ps1
```

```powershell
.\LuanLHCE191099_run-frontend.ps1
```

Nếu đã có server đang chạy trên cổng trên thì dùng server đó hoặc dừng đúng process của project trước khi mở bản thứ hai.

## Tài khoản mẫu

| Role | Email | Password |
|---|---|---|
| Admin | `admin@funews.local` | `Admin@123` |
| Staff | `staff@funews.local` | `Staff@123` |

Đây là tài khoản thực hành. Dữ liệu mẫu chỉ được thêm khi chưa có dữ liệu khởi tạo; không ghi đè tài khoản đã sửa.

## Khi clone sang máy khác

1. Cài JDK 21, Node tương thích Vite 8 (Node 22.12+ hoặc Node 24) và SQL Server.
2. Dùng kết nối **Microsoft SQL Server**, chạy `database/LuanLHCE191099_create-database.sql` để tạo database **A2LuanLH_SE1910**. Spring Data JPA/Hibernate tạo các bảng khi backend khởi động.
3. Copy `backend/application-local.properties.example` thành `backend/application-local.properties`. Điền username/password SQL Server và một JWT secret ngẫu nhiên ít nhất 32 ký tự. Không cần đặt biến môi trường.
4. Có thể tạo JWT secret bằng `node -e "console.log(require('crypto').randomBytes(48).toString('base64url'))"` rồi dán kết quả vào file local.
5. Trong `backend`, chạy `.\mvnw.cmd spring-boot:run`. Trong `frontend`, chạy `npm ci` và `npm run dev`.

## Chức năng

- Khách xem danh sách và nội dung tin Active mà không cần đăng nhập.
- Admin: CRUD/Search tài khoản. Không xóa tài khoản đã tạo tin; bảo vệ tài khoản Admin cuối cùng và không tự xóa phiên đang dùng.
- Staff: CRUD/Search Category, có Category cha/con; chặn xóa Category đang được News sử dụng.
- Staff: CRUD/Search News cùng tags, chỉnh sửa hồ sơ cá nhân và xem lịch sử tin mình tạo.
- Thêm/sửa bằng popup. Xóa luôn qua hộp xác nhận.
- Role Admin = 1, Staff = 2. News/Category: Active = 1, Inactive = 0.
- Các bảng theo sơ đồ: SystemAccount, Category, NewsArticle, Tag, NewsTag. Tag thuộc tối đa một NewsArticle qua ràng buộc unique TagID trong NewsTag.

## Luồng và cấu trúc

```text
frontend/src/     React page -> Axios -> REST request
backend/.../controller/  HTTP input, validation, quyền truy cập
backend/.../service/     nghiệp vụ và transaction
backend/.../repository/  Spring Data JPA -> SQL Server
backend/.../entity/      mapping bảng và quan hệ
backend/.../security/    ký/đọc JWT và nạp người dùng hiện tại
```

File source Java, JSX, JS, CSS dùng tiền tố `LuanLHCE191099_`. Các file cấu hình như package.json, pom.xml, application.properties và index.html giữ tên chuẩn.

Xem [hướng dẫn theo luồng và từng file](docs/HUONG_DAN_THEO_LUONG.md).

## Kiểm tra

```powershell
# Trong backend
.\mvnw.cmd verify
# Trong frontend
npm run build
npm run lint
```

Test backend dùng H2 riêng, không sửa database SQL Server thực hành. Import `postman/LuanLHCE191099_Assignment02.postman_collection.json` vào Postman và Run collection theo thứ tự; collection tạo dữ liệu thử rồi xóa các bản ghi thử ở cuối. Base URL mặc định là `http://localhost:8082/api`.

`database/LuanLHCE191099_verification.sql` chứa các SELECT để đối chiếu dữ liệu và quan hệ mà không hiển thị password. Ảnh giao diện ở `docs/screenshots/`.

Kết quả kiểm tra ngày 29/10/2026: 8 test backend đạt; frontend build và lint đạt. Collection Postman chạy bằng Newman đạt 19 request / 22 assertions. Đã thử trên trình duyệt với SQL Server thật: đăng nhập theo role, CRUD/Search, popup, tags, lịch sử, profile, tin Inactive và chặn xóa Category đang dùng. Dữ liệu thử đã được dọn sau khi kiểm tra.

## Một số hành vi cần biết

- JWT lưu trong sessionStorage của tab, thời hạn mặc định 2 giờ. F5 gọi `/api/auth/me` để nạp lại phiên. Sign out xóa token ở client; token đã phát hành còn hiệu lực tới khi hết hạn.
- Password tài khoản trong database được BCrypt hash và không xuất trong response DTO. Role được kiểm tra ở backend, không dựa vào menu hoặc dữ liệu role do client gửi.
- Email trùng hoặc dữ liệu đang được tham chiếu trả 409; input sai trả 400; thiếu/không hợp lệ token trả 401; sai quyền trả 403; không tìm thấy trả 404.
- Create/Update chỉ chấp nhận các trường trong request DTO; không nhận ID chủ sở hữu hoặc tổng tiền tùy ý từ frontend.
- Bài 2 dùng JWT để giữ đăng nhập API. Admin quản lý Accounts; Staff quản lý Categories/News/Profile/History.
