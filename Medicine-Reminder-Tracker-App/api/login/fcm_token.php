<?php
// api/login/fcm_token.php
require '../config.php';

$input = json_input();
$missing = requireFields($input, ['userId', 'fcmToken']);
if ($missing) sendResponse(['success' => false, 'message' => 'Thiếu dữ liệu'], 400);

$stmt = $pdo->prepare("INSERT INTO UserToken (userId, fcmToken) VALUES (?, ?) 
                       ON DUPLICATE KEY UPDATE fcmToken = ?");
$stmt->execute([$input['userId'], $input['fcmToken'], $input['fcmToken']]);

sendResponse(['success' => true]);