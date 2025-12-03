<?php
// api/login/google.php
require '../config.php';
require '../../vendor/autoload.php';
use Firebase\JWT\JWT;
use Firebase\JWT\Key;

$input = json_input();
if (empty($input['idToken'])) sendResponse(['success' => false, 'message' => 'Thiếu idToken'], 400);

try {
    $client = new GuzzleHttp\Client();
    $res = $client->get('https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com');
    $certs = json_decode($res->getBody(), true);

    $decoded = JWT::decode($input['idToken'], ...array_map(fn($c) => new Key($c, 'RS256'), $certs));

    $email = $decoded->email;
    $name  = $decoded->name ?? explode('@', $email)[0];

    $stmt = $pdo->prepare("SELECT userId, name FROM Users WHERE email = ?");
    $stmt->execute([$email]);
    $user = $stmt->fetch();

    if ($user) {
        sendResponse([
            'success' => true,
            'userId'  => $user['userId'],
            'name'    => $user['name'],
            'email'   => $email
        ]);
    }

    // Tạo mới nếu chưa có
    $userId = newId('U');
    $stmt = $pdo->prepare("INSERT INTO Users (userId, email, passwordHash, name) VALUES (?, ?, '', ?)");
    $stmt->execute([$userId, $email, $name]);

    sendResponse([
        'success' => true,
        'userId'  => $userId,
        'name'    => $name,
        'email'   => $email,
        'newUser' => true
    ]);

} catch (Exception $e) {
    sendResponse(['success' => false, 'message' => 'Token Google không hợp lệ'], 401);
}