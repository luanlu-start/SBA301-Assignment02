# Assignment 02 - Đọc theo luồng và từng file

## 1. Mở app

1. Backend: `LuanLHCE191099_Application.java` gọi SpringApplication.run.
2. Spring đọc application.properties và file local, mở DataSource, Hibernate tạo/cập nhật bảng trong database riêng.
3. `config/LuanLHCE191099_SeedData.java` tạo dữ liệu mẫu nếu chưa có dữ liệu khởi tạo.
4. Frontend: index.html nạp `src/LuanLHCE191099_main.jsx`, main render App.
5. `src/LuanLHCE191099_App.jsx` ghép Router, AuthProvider và menu. Trang đầu hiển thị dữ liệu công khai.

## 2. Đăng nhập

```text
LoginPage: Form.onSubmit
-> useAuth().login(email, password)
-> api.post('/auth/login')
-> AuthController
-> AuthService kiểm tra tài khoản/password
-> JwtService ký token
-> trả token + UserPrincipal không có password
-> AuthProvider lưu token vào sessionStorage và đổi state user
-> Router chuyển tới màn hình phù hợp role
```

File cần mở theo thứ tự:

| Bước | File source |
|---|---|
| Gõ và Submit | frontend/src/LuanLHCE191099_LoginPage.jsx |
| Lưu phiên và cập nhật React | frontend/src/LuanLHCE191099_Auth.jsx |
| Gửi request, gắn Authorization | frontend/src/LuanLHCE191099_api.js |
| Nhận Email/Password | backend controller/LuanLHCE191099_AuthController.java |
| Xác minh tài khoản | backend service/LuanLHCE191099_AuthService.java |
| Tạo token | backend security/LuanLHCE191099_JwtService.java |

Khi request sau gửi lên, JwtFilter kiểm tra chữ ký/hạn dùng/issuer, nạp lại người dùng rồi đặt Authentication vào SecurityContext. SecurityConfig và @PreAuthorize quyết định endpoint nào được gọi.

## 3. Đọc danh sách và tìm kiếm

```text
Page được Router chọn
-> useApiList(endpoint + '?q=...')
-> Axios GET kèm token nếu có
-> Controller -> Service -> Repository -> SQL Server
-> Service chuyển Entity thành Response DTO trong transaction
-> JSON response -> state của hook -> JSX map thành bảng/card
```

`LuanLHCE191099_useApiList.js` hủy request cũ khi đổi từ khóa/page để tránh response cũ ghi đè kết quả mới. Entity dùng getter để đọc quan hệ Hibernate; không trả Entity trực tiếp ra JSON.

## 4. CRUD cơ bản

Mở `frontend/src/LuanLHCE191099_CrudPage.jsx`:

1. Nút Add gọi open() để tạo bản nháp từ các field.
2. FormFields nối value/onChange với state form.
3. Submit kiểm tra HTML input rồi POST/PUT JSON tới API.
4. @Valid kiểm tra request DTO ở backend. Service kiểm tra quy tắc nghiệp vụ trong transaction.
5. Repository lưu dữ liệu, Service trả DTO; frontend đóng popup và reload danh sách.
6. Edit lấy id dòng làm editingId; payload được chọn theo các field của form, không gửi nguyên response object.
7. Delete mở xác nhận trước. API thành công thì refresh; lỗi ràng buộc hiện trong hộp xác nhận để người dùng hiểu vì sao chưa xóa.

API mới là nơi lưu dữ liệu chính. State React giữ dữ liệu để hiển thị; sessionStorage chỉ giữ token đăng nhập.

## 5. Theo dõi một Category và News

```text
CategoryPage -> CrudPage -> POST /api/categories
-> CategoryController -> CategoryService -> CategoryRepository
-> bảng Category -> response -> reload UI
```

CategoryService kiểm tra parentId tồn tại và không tạo chu trình cha/con. Khi xóa, service kiểm tra News tham chiếu trước khi gọi repository.delete.

NewsPage đọc Categories để tạo dropdown. Tags được nhập mỗi dòng dạng `name | note`, rồi đổi thành mảng object khi Save.

```text
NewsPage -> POST /api/news
-> NewsController lấy actor từ token
-> NewsService đọc Category, gán createdBy và createdDate
-> tạo NewsArticle cùng Tags/NewsTag trong một transaction
-> trả NewsResponse có categoryName, author và tags
```

Khi sửa News, createdBy giữ nguyên; updatedBy và modifiedDate nhận người/thời điểm sửa. Khách chỉ gọi `/api/public/news`, service lọc status=1. Staff gọi `/api/news/history`, author được lấy từ token nên không thể gửi một authorId khác để xem lịch sử riêng của người khác.

## 6. Account và Profile

AccountPage dùng /api/accounts; backend chỉ cho ADMIN. AccountService kiểm tra email trùng, hash password và chặn xóa tài khoản đã tạo News.

ProfilePage dùng /api/profile; backend lấy accountId từ principal hiện tại. ProfileInput không có role, nên gửi thêm role bị từ chối. Đổi hồ sơ xong frontend gọi /auth/me để cập nhật tên trên menu.

## 7. Cách tự lần theo code

Chọn một thao tác rồi trả lời lần lượt: sự kiện bắt đầu ở đâu -> handler nào nhận -> request gì được gửi -> Controller nào nhận -> Service kiểm tra gì -> Repository đọc/ghi bảng nào -> response gì về -> state/UI đổi ra sao.

Backend có 3 tầng chính Controller -> Service -> Repository. DTO, Entity và Security hỗ trợ các tầng đó; chúng không phải các màn hình frontend.

Khi gặp lỗi, xem request trong DevTools Network trước: URL, method, payload, status và message. Sau đó mở đúng service liên quan. Trong SQL Server, chạy SELECT ở database/LuanLHCE191099_verification.sql để xác nhận dữ liệu đã lưu.
