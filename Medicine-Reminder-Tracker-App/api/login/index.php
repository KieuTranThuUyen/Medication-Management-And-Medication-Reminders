<?php
// api/login/index.php
require '../config.php';

$input = json_input();
$missing = requireFields($input, ['email', 'password']);
if ($missing) sendResponse(['success' => false, 'message' => "Thiếu $missing"], 400);

$email = trim($input['email']);
$stmt = $pdo->prepare("SELECT userId, name, passwordHash FROM Users WHERE email = ?");
$stmt->execute([$email]);
$user = $stmt->fetch();

if (!$user || !password_verify($input['password'], $user['passwordHash'])) {
    sendResponse(['success' => false, 'message' => 'Email hoặc mật khẩu sai'], 401);
}

sendResponse([
    'success' => true,
    'userId'  => $user['userId'],
    'name'    => $user['name'],
    'email'   => $email
]);