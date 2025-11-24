# XTTest — Tóm tắt hệ thống & API 

**Phiên bản:** cơ sở mã hiện tại (branch `main`)

**Mục tiêu file:** mô tả sơ bộ hệ thống, liệt kê các API chính và hướng dẫn dành cho đội phát triển mobile (authentication, realtime/SSE, ví dụ request/response, mẹo triển khai).

**Base URL (local dev):** `http://localhost:8080`

**Tóm tắt hệ thống**
- Ứng dụng ôn tập / thi trực tuyến: quản lý bộ đề (QuestionSet), tạo/điều phối đề thi, học sinh tham gia làm bài, giáo viên quản lý đề và kỳ thi, admin quản trị hệ thống.
- Backend: Spring Boot (Java), JPA/Hibernate, Spring Security (JWT), SSE (`SseEmitter`) cho realtime notifications.
- Frontend web (example folder): React + axios (tham khảo nếu cần behavior tương tự trên mobile).

**Công nghệ chính**
- Java 17+ / Spring Boot
- Spring Security + JWT
- JPA/Hibernate + H2/MySQL (tùy cấu hình môi trường)
- SSE cho realtime (EventSource)

**Authentication (JWT)**
- Đăng nhập: `POST /api/auth/login` -> trả về token JWT (response có trường `token`).
- Token sử dụng header: `Authorization: Bearer <JWT>` cho hầu hết các request.
- Lưu ý SSE (EventSource) không hỗ trợ gửi header; server chấp nhận token dưới dạng query param `?token=<JWT>` cho endpoint stream.

----
**Danh sách API chính**
Lưu ý: mô tả dưới đây là tóm tắt từ các controller trong `demo/src/main/java/com/example/demo/controller`.

- Auth
  - `POST /api/auth/signup` — Đăng ký user. Body: `{username, password, roles?}`.
  - `POST /api/auth/login` — Đăng nhập. Body: `{username, password}`. Response: `{token, username, roles}`.
  - `POST /api/auth/logout` — Logout (stateless; client nên bỏ token).

- User
  - `GET /api/user/me` — Lấy profile user hiện tại. Auth required.
  - `PUT /api/user/me` — Cập nhật profile (body: `{email?, fullName?, password?}` ). Auth required.

- Notifications (user)
  - `GET /api/notifications` — Lấy danh sách thông báo cho user đang auth.
  - `PUT /api/notifications/{id}/read` — Đánh dấu thông báo là đã đọc. Auth required.
  - `GET /api/notifications/stream` — SSE stream cho realtime notifications. Auth via JWT header OR (for EventSource) `?token=<JWT>`. Server trả event `connected` on connect; Notification events sent when created; heartbeat events may be sent periodically.

- Admin notifications (admin)
  - `GET /api/admin/notifications` — Lấy inbox admin (auth role ADMIN required).
  - `GET /api/admin/notifications/all` — Lấy tất cả thông báo (quản trị).
  - `POST /api/admin/notifications` — Tạo thông báo. Body (JSON): `{ message, targetUsername?, targetRole?, toAll? }`. Auth role ADMIN required.
  - `PUT /api/admin/notifications/{id}/read` — Mark as read (admin action).

- Question sets (bộ đề)
  - `GET /api/question-sets` — Lấy tất cả bộ đề (có thể public/private). Query params supported by controller.
  - `GET /api/question-sets/{id}` — Lấy chi tiết 1 bộ đề.
  - `POST /api/question-sets` — Tạo bộ đề (Admin/teacher). Body: `{title, description, visibility?, subject?, isExamScoped?}`.
  - `PUT /api/question-sets/{id}` — Cập nhật bộ đề.
  - `DELETE /api/question-sets/{id}` — Xoá bộ đề.
  - `POST /api/question-sets/{id}/upload` — Upload câu hỏi từ file (multipart `file`).
  - `GET /api/question-sets/{id}/questions` — Lấy câu hỏi của bộ đề.
  - `POST /api/question-sets/{id}/questions` — Thêm câu hỏi vào bộ đề.
  - `PUT /api/question-sets/questions/{questionId}` — Cập nhật câu hỏi.
  - `DELETE /api/question-sets/questions/{questionId}` — Xoá câu hỏi.
  - `GET /api/question-sets/questions/{questionId}` — Lấy 1 câu hỏi.

- Practice (public)
  - `GET /api/practice/question-sets` — Lấy danh sách bộ đề public (no auth). Optional `?subject=` để lọc theo môn.
  - `GET /api/practice/question-sets/{setId}/questions` — Lấy câu hỏi để ôn (no auth).

- Student
  - `POST /api/student/join-class` — (stub) join class (not implemented fully).
  - `GET /api/student/question-sets` — Lấy bộ đề hiển thị cho student (public nếu anonymous)
  - `POST /api/student/exams/{accessCode}/start` — Bắt đầu bài thi bằng accessCode hoặc numeric id. Auth required.
  - `POST /api/student/exams/submit` — Nộp bài. Body depends on `SubmitExamRequest` payload. Auth required.
  - `GET /api/student/results` — Lấy lịch sử kết quả của student. Auth required.

- Teacher (role TEACHER)
  - `GET /api/teacher/classes` — Lấy lớp của teacher.
  - `GET /api/teacher/classes/{classId}/students` — Lấy sinh viên trong lớp.
  - `POST /api/teacher/exams` — Tạo exam. Body includes `title, questionSetId, startTime, endTime, durationMinutes, passingScore, classIds, ...` Auth TEACHER required.
  - `PUT /api/teacher/exams/{examId}` — Cập nhật exam.
  - `DELETE /api/teacher/exams/{examId}` — Xoá exam.
  - `GET /api/teacher/exams` — Lấy exam của teacher.
  - `GET /api/teacher/exams/{examId}` — Lấy chi tiết exam.
  - `POST /api/teacher/exams/{examId}/publish` — Publish exam to classes.
  - `GET /api/teacher/exams/{examId}/results` — Lấy kết quả exam.
  - `GET /api/teacher/exams/{examId}/statistics` — Lấy thống kê.
  - `GET /api/teacher/students/{studentId}/exams/{examId}/result` — Chi tiết 1 kết quả.
  - `GET /api/teacher/students/{studentId}/results` — Lấy tất cả kết quả của 1 student.

- Admin / Classes
  - `GET /api/admin/classes` — Lấy danh sách lớp (ADMIN endpoints under `/api/admin/*`).
  - `GET /api/admin/classes/{id}` — Chi tiết lớp.
  - `POST /api/admin/classes` — Tạo lớp.
  - `PUT /api/admin/classes/{id}` — Cập nhật lớp.
  - `DELETE /api/admin/classes/{id}` — Xoá lớp.
  - `POST /api/admin/classes/{id}/teacher/{teacherId}` — Gán giáo viên.
  - `POST /api/admin/classes/{id}/students` — Thêm sinh viên.
  - `DELETE /api/admin/classes/{id}/students/{studentId}` — Xoá sinh viên.
  - `GET /api/admin/classes/{id}/students` — Lấy danh sách sinh viên của lớp.

- Dashboard (metrics)
  - `GET /api/dashboard/stats` — Lấy số liệu (count students/teachers, etc.). Auth required.

----
Ví dụ response (JWT login):

Request:
```
POST /api/auth/login
Content-Type: application/json

{"username":"student@demo","password":"student"}
```

Response:
```
{
  "token": "eyJhbGciOi...",
  "username": "student@demo",
  "roles": ["ROLE_STUDENT"]
}
```

**Realtime notifications (SSE) — mobile guidance**
- Endpoint: `GET /api/notifications/stream`
- Because `EventSource` doesn't support custom headers across platforms, the backend accepts token as query param: `GET /api/notifications/stream?token=<JWT>`.
- Server sends an initial `connected` event and then named events for notifications. It also sends heartbeat events periodically. The mobile client should:
  - Open a single EventSource per user session (avoid duplicate connections).
  - Reconnect with exponential backoff on close/error.
  - Use the query param token only on secure channels (HTTPS) in production.
  - Listen for event types:
    - `connected` — initial confirmation
    - `notification` — payload: as in `NotificationDTO` (id, message, createdAt, isRead)
    - `heartbeat` — keep-alive; use to detect stale connections

Example JS (web) EventSource usage (mobile equivalent applies):
```js
const url = `https://api.example.com/api/notifications/stream?token=${token}`;
const es = new EventSource(url);
es.addEventListener('connected', () => console.log('SSE connected'));
es.addEventListener('notification', e => {
  const payload = JSON.parse(e.data);
  // show local notification or update inbox
});
es.addEventListener('heartbeat', () => {/* keep alive */});
es.onerror = () => { /* reconnect with backoff */ };
```

**Mobile integration best practices**
- Store JWT securely (Android: EncryptedSharedPreferences / iOS: Keychain).
- Use `Authorization: Bearer <JWT>` for standard REST requests.
- For SSE, attach token as query string: `?token=<JWT>` and always use HTTPS in production.
- Implement reconnect/backoff and deduplication (only one active connection per user device).
- Mark read: call `PUT /api/notifications/{id}/read` when user opens notification.
- Polling fallback: if SSE is not available, consider polling `GET /api/notifications` every 30-60s with ETag/If-Modified-Since.
- For push notifications in production, integrate FCM/APNs and have server trigger push when creating notifications (currently notifications are DB-persisted and emitted via SSE).

**Security considerations**
- JWT token expiry & refresh: current code issues JWT on login; consider implementing refresh tokens for long sessions.
- Protect SSE token in query string by always using HTTPS and short token lifetimes.
- Server includes a simple in-memory rate limiter on `/api/notifications/stream`; for production and multi-instance, move to Redis or API gateway rate limiter.

**Running locally (dev)**
Open a cmd shell in the repository root and start the backend (demo module):
```cmd
cd demo
mvn spring-boot:run
```
