<?php
// config.php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Content-Type: application/json; charset=utf-8");

// Xử lý preflight OPTIONS request (rất quan trọng cho CORS)
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

const DB_HOST = "localhost";
const DB_NAME = "MedicineReminderApp";
const DB_USER = "root";        // Thay bằng user thật khi deploy
const DB_PASS = "";            // Thay bằng password thật khi deploy
const DB_CHARSET = "utf8mb4";

try {
    $pdo = new PDO(
        "mysql:host=" . DB_HOST . ";dbname=" . DB_NAME . ";charset=" . DB_CHARSET,
        DB_USER,
        DB_PASS,
        [
            PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES   => false,
        ]
    );
    
    // Đặt múi giờ Việt Nam (rất quan trọng cho lịch uống thuốc và log)
    $pdo->exec("SET time_zone = '+07:00'");
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["error" => "Database connection failed"]);
    exit();
}

// Hàm đọc JSON từ body
function json_input(): array {
    $data = json_decode(file_get_contents("php://input"), true);
    return is_array($data) ? $data : [];
}

// Tạo ID ngắn, đẹp, không trùng (dùng cho userId, medicineId, scheduleId, logId...)
function newId(string $prefix = 'U'): string {
    // 16 ký tự hex → 32 ký tự sau prefix → rất khó trùng
    return $prefix . strtoupper(bin2hex(random_bytes(8)));
    // Ví dụ: U8F3A1C9D2E4B5F6
}

// Gửi JSON response chuẩn (dễ dùng trong Android)
function sendResponse(array $data, int $statusCode = 200): void {
    http_response_code($statusCode);
    echo json_encode($data, JSON_UNESCAPED_UNICODE);
    exit();
}

// Kiểm tra bắt buộc các field
function requireFields(array $input, array $fields): ?string {
    foreach ($fields as $field) {
        if (!isset($input[$field]) || trim($input[$field]) === '') {
            return $field;
        }
    }
    return null;
}

// (Tùy chọn sau này) Gửi FCM notification
function sendFCM($fcmToken, $title, $body, $data = []) {
    $url = 'https://fcm.googleapis.com/fcm/send';
    
    $serverKey = 'YOUR_FCM_SERVER_KEY_HERE'; // Sẽ thêm sau
    
    $payload = [
        "to" => $fcmToken,
        "notification" => [
            "title" => $title,
            "body"  => $body,
            "sound" => "default"
        ],
        "data" => $data
    ];

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, [
        "Authorization: key=$serverKey",
        "Content-Type: application/json"
    ]);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($payload));
    curl_exec($ch);
    curl_close($ch);
}