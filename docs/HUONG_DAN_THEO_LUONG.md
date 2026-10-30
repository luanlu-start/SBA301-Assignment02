# Assignment 02 - Đọc theo luồng và từng file

Đây là bản tra cứu nhanh. Mở [hướng dẫn chi tiết toàn bộ Assignment 02](../HUONG_DAN_ASSIGNMENT02_CHI_TIET.md) để đọc giải thích code, đối chiếu A1 → A2, danh sách file và các bước kiểm tra.

## 1. Kiến trúc

```text
React page -> Axios -> Controller -> Service -> Repository -> SQL Server
                     <- JSON DTO <- dữ liệu được kiểm tra và chuyển đổi
```

- Controller nhận HTTP request, kiểm tra DTO bằng `@Valid`, lấy người dùng bằng `@AuthenticationPrincipal` và gọi service.
- Service xử lý nghiệp vụ, transaction và chuyển Entity thành Response DTO.
- Repository đọc/ghi database qua Spring Data JPA.
- Entity ánh xạ bảng và các quan hệ; DTO mô tả dữ liệu nhận/trả của API. Entity dùng Lombok `@Getter`, `@Setter`, `@NoArgsConstructor`: các phương thức `getName()`, `setName()` vẫn được tạo khi compile dù không viết tay trong file.

## 2. Mở ứng dụng

Backend chạy `LuanLHCE191099_Application.java`, đọc cấu hình, mở DataSource, tạo Spring beans và JPA mappings. `LuanLHCE191099_SeedData.java` tạo account/category/news mẫu khi bảng account chưa có dữ liệu.

Frontend: `index.html` -> `LuanLHCE191099_main.jsx` -> `LuanLHCE191099_App.jsx`. App đặt Router và AuthProvider quanh giao diện. AppLayout ghép Header và Outlet. Đường dẫn `/` chờ khôi phục phiên rồi chuyển đến Login hoặc màn quản lý theo role; `/news` là bảng tin Active cho khách.

AuthProvider luôn gọi `/auth/me` khi tải ứng dụng. Nếu trình duyệt còn cookie phiên hợp lệ, backend trả user; nếu chưa đăng nhập thì nhận 401 và hiện giao diện khách.

## 3. Đăng nhập

```text
LoginPage.handleLogin
-> AuthProvider.login(email, password)
-> Axios POST /api/auth/login
-> AuthController.login
-> AuthService.authenticate
-> AccountRepository tìm email + PasswordEncoder kiểm tra BCrypt
-> AuthController lưu SecurityContext vào HttpSession
-> response { user } + cookie A2SESSION
-> AuthProvider cập nhật React state
-> Router mở màn hình theo role
```

Mở file theo thứ tự:

| Bước                                           | File                                                        |
| ---------------------------------------------- | ----------------------------------------------------------- |
| Form và sự kiện submit                         | `frontend/src/pages/LuanLHCE191099_LoginPage.jsx`           |
| Lưu user trong state, khôi phục phiên, logout  | `frontend/src/context/LuanLHCE191099_AuthContext.jsx`       |
| Gửi HTTP cùng cookie, lấy CSRF cho request ghi | `frontend/src/services/LuanLHCE191099_api.js`               |
| Nhận login và lưu phiên                        | `backend/.../controller/LuanLHCE191099_AuthController.java` |
| Tìm account và kiểm tra mật khẩu               | `backend/.../service/LuanLHCE191099_AuthService.java`       |
| Thiết lập session, CSRF và logout              | `backend/.../config/LuanLHCE191099_SecurityConfig.java`     |

`request.changeSessionId()` đổi mã phiên sau khi đăng nhập thành công. `saveAuthentication()` lưu danh tính/quyền vào SecurityContextRepository. Cookie HttpOnly do trình duyệt quản lý, JavaScript không cần đọc nó.

Các request tiếp theo tự gửi cookie. Spring Security đọc session để biết ai đang gọi. `@AuthenticationPrincipal` lấy user đó vào tham số controller; `@PreAuthorize` kiểm tra role.

## 4. Danh sách Account và Search

```text
AccountManagement khai báo endpoint /accounts
-> CrudPage + useApiList gửi GET /api/accounts?q=...
-> AccountController.getAccounts
-> q rỗng: AccountService.getAllAccounts
-> có từ khóa: AccountService.searchAccounts
-> AccountRepository.findAll(Sort.by("id"))
-> vòng lặp chọn account, toResponse(account) tạo DTO
-> JSON -> hook cập nhật state -> bảng React render lại
```

| HTTP request                | Hàm service                        |
| --------------------------- | ---------------------------------- |
| GET `/api/accounts`         | `getAllAccounts()`                 |
| GET `/api/accounts?q=luan`  | `searchAccounts(q)`                |
| GET `/api/accounts/{id}`    | `getAccountById(id)`               |
| POST `/api/accounts`        | `createAccount(input)`             |
| PUT `/api/accounts/{id}`    | `updateAccount(id, input)`         |
| DELETE `/api/accounts/{id}` | `deleteAccount(id, currentUserId)` |

Tìm kiếm hiện lọc trong Java sau khi đọc dữ liệu, chưa phân trang. AccountResponse chỉ có id/name/email/role. Mỗi DTO có file riêng dưới `backend/src/main/java/fu/edu/sba301/a2/dto/`.

## 5. Thêm, sửa và xóa

Mở `frontend/src/components/Common/LuanLHCE191099_CrudPage.jsx`:

1. `openCreateModal()` tạo form từ giá trị mặc định. `openEditModal(record)` lấy giá trị từ dòng được chọn.
2. FormFields.updateField tạo object mới khi input thay đổi rồi gọi setter của parent.
3. `handleSave()` ngăn submit tải lại trang, đổi trường numeric thành number và tạo request body.
4. Axios lấy CSRF rồi gửi POST khi tạo hoặc PUT khi sửa. Backend dùng `@Valid` kiểm tra DTO và service kiểm tra nghiệp vụ.
5. Repository lưu thay đổi trong transaction. API thành công thì đóng popup và đọc lại danh sách.
6. `openDeleteModal(record)` chỉ mở xác nhận; `handleDelete()` mới gửi DELETE.
7. Nếu server trả 409 vì dữ liệu đang dùng, giữ popup để hiển thị lý do.

`getAllAccounts`, `searchAccounts`, `createAccount`, `updateAccount`, `deleteAccount` được tách rõ trong AccountService. `applyInput` dùng chung validation/email/password; `toResponse` chuyển Entity thành DTO.

## 6. Category và News

CategoryManagement đọc Categories để dựng lựa chọn parent. CategoryService đi lên chuỗi parent để ngăn chu trình; trước khi xóa kiểm tra News và Category con đang tham chiếu.

NewsManagement nhập tags dạng từng dòng `name | note`. `prepareRequest()` chuyển thành mảng object gửi server; `mapRecordToForm()` chuyển lại thành text khi mở Edit.

NewsController lấy currentUserId từ principal trong session. NewsService.createNews gán tác giả/thời gian ở server và lưu article/tags cùng transaction. Update giữ tác giả gốc, chỉ cập nhật người sửa/thời gian sửa.

PublicController gọi `getPublicNews`/`getPublicNewsById` để chỉ trả tin Active. History lấy author ID từ người đang đăng nhập, không nhận author ID tùy ý từ trình duyệt.

## 7. Profile, F5 và Sign out

ProfilePage gọi `/api/profile` để đọc/sửa account của chính Staff. ProfileInput không có role. Sau khi lưu, `refresh()` gọi `/auth/me` để cập nhật user trên menu và trong session.

F5 làm React state khởi tạo lại, nhưng cookie phiên vẫn còn: AuthProvider gọi `/auth/me` để khôi phục user. Phiên hết hạn sau 30 phút không hoạt động.

Sign out gọi `/api/auth/logout`. Spring invalidates session, xóa cookie A2SESSION; frontend mới xóa user khỏi state. Chỉ thay đổi state ở frontend sẽ chưa kết thúc phiên backend.

## 8. CSRF và cách kiểm tra lỗi

Với cookie session, Spring yêu cầu giá trị CSRF cho POST/PUT/DELETE. API client tự lấy từ `/auth/csrf` và gửi qua `X-CSRF-TOKEN`; người dùng chỉ thao tác form bình thường. Postman collection cũng tự thực hiện bước này và giữ cookie.

Khi gặp lỗi, xem DevTools Network: URL, HTTP method, body, cookie/header, status và message. Sau đó mở đúng controller/service. Chạy SELECT trong `database/LuanLHCE191099_verification.sql` để kiểm tra dữ liệu đã lưu.

Chạy `npm run format` từ frontend trước khi bàn giao thay đổi; `npm run format:check` kiểm tra quy tắc định dạng.
