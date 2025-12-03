<?php
// api/register/index.php
require '../config.php';

$input = json_input();
$missing = requireFields($input, ['name', 'email', 'password']);
if ($missing) sendResponse(['success' => false, 'message' => "Thiếu $missing"], 400);

$name = trim($input['name']);
$email = strtolower(trim($input['email']));
$password = $input['password'];
$phone = trim($input['phone'] ?? '');  // ← ĐÃ THÊM: Nhận phone từ app

if (strpos($name, ' ') === false) sendResponse(['success' => false, 'message' => 'Vui lòng nhập đầy đủ họ tên'], 400);
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) sendResponse(['success' => false, 'message' => 'Email không đúng định dạng'], 400);
if (strlen($password) < 6) sendResponse(['success' => false, 'message' => 'Mật khẩu phải từ 6 ký tự trở lên'], 400);

// ← ĐÃ THÊM: Kiểm tra phone nếu có
if (!empty($phone) && !preg_match('/^[0-9]{9,11}$/', $phone)) {
    sendResponse(['success' => false, 'message' => 'Số điện thoại không hợp lệ (9-11 chữ số)'], 400);
}

// Kiểm tra email trùng – ĐÃ TỐI ƯU: Chỉ check COUNT để nhanh hơn
$stmt = $pdo->prepare("SELECT COUNT(*) FROM Users WHERE email = ?");
$stmt->execute([$email]);
if ($stmt->fetchColumn() > 0) sendResponse(['success' => false, 'message' => 'Email đã được sử dụng'], 409);

$userId = newId('U');
$hash = password_hash($password, PASSWORD_BCRYPT);

try {
    $stmt = $pdo->prepare("INSERT INTO Users (userId, name, email, phone, passwordHash) VALUES (?, ?, ?, ?, ?)");  // ← ĐÃ THÊM: Insert phone
    $stmt->execute([$userId, $name, $email, $phone, $hash]);
    
    sendResponse([
        'success' => true,
        'message' => 'Đăng ký thành công!',
        'userId'  => $userId,
        'name'    => $name,
        'email'   => $email
    ]);
} catch (PDOException $e) {
    sendResponse(['success' => false, 'message' => 'Lỗi hệ thống: ' . $e->getMessage()], 500);  // ← ĐÃ THÊM: Xử lý lỗi DB chi tiết hơn
}