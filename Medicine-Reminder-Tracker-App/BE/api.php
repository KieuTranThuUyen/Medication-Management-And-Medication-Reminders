<?php
header("Content-Type: application/json; charset=utf-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Access-Control-Allow-Methods: GET, POST, DELETE, OPTIONS");

require_once "config.php";

if ($_SERVER["REQUEST_METHOD"] === "OPTIONS") exit();

// action + JSON body
$action = $_GET["action"] ?? "";
$input  = json_decode(file_get_contents("php://input"), true) ?? [];

// JSON format
function respond($data, $code = 200) {
    http_response_code($code);
    echo json_encode($data, JSON_UNESCAPED_UNICODE);
    exit;
}

/* ============================================================
   1) GET MEDICINES (list thuốc – không phải lịch)
   ============================================================ */
if ($action === "getMedicines") {

    if (!isset($_GET["userId"]))
        respond(["error" => "userId required"], 400);

    $stmt = $pdo->prepare("
        SELECT * FROM Medicine
        WHERE userId = :uid AND isActive = 1
    ");
    $stmt->execute([":uid" => $_GET["userId"]]);

    respond(["medicines" => $stmt->fetchAll(PDO::FETCH_ASSOC)]);
}


/* ============================================================
   2) GET SCHEDULE BY DATE (Dùng LogEntry để hiển thị HomeScreen)
   ============================================================ */
if ($action === "getScheduleByDate") {

    if (!isset($_GET["userId"], $_GET["date"])) {
        respond(["error" => "Missing userId or date"], 400);
    }

    $stmt = $pdo->prepare("
        SELECT 
            l.logId,
            l.medicineId,

            -- ⭐ Format thời gian thành HH:mm để hiển thị
            DATE_FORMAT(l.scheduledTime, '%H:%i') AS time,

            -- actualTime có thể null
            DATE_FORMAT(l.actualTime, '%Y-%m-%d %H:%i:%s') AS actualTime,

            l.status,

            -- ⭐ Thông tin thuốc
            m.name,
            m.medicineType,
            m.dosage

        FROM LogEntry l
        JOIN Medicine m ON m.medicineId = l.medicineId

        WHERE m.userId = :uid
          AND m.isActive = 1
          AND DATE(l.scheduledTime) = :day

        ORDER BY l.scheduledTime
    ");

    $stmt->execute([
        ":uid" => $_GET["userId"],
        ":day" => $_GET["date"]
    ]);

    respond(["schedule" => $stmt->fetchAll(PDO::FETCH_ASSOC)]);
}


/* ============================================================
   3) MARK AS TAKEN — cập nhật LogEntry
   ============================================================ */
if ($action === "markTaken" && $_SERVER["REQUEST_METHOD"] === "POST") {

    if (!isset($input["logId"]))
        respond(["error" => "Missing logId"], 400);

    $stmt = $pdo->prepare("
        UPDATE LogEntry
        SET status = 'Taken',
            actualTime = NOW()
        WHERE logId = ?
    ");

    respond(["success" => $stmt->execute([$input["logId"]])]);
}


/* ============================================================
   4) ADD MEDICINE + AUTO SCHEDULE + AUTO LOGENTRY (trigger)
   ============================================================ */
if ($action === "addMedicine" && $_SERVER["REQUEST_METHOD"] === "POST") {

    if (!isset($input["userId"], $input["name"], $input["scheduleTimes"]))
        respond(["error" => "Missing fields"], 400);

    try {
        $pdo->beginTransaction();

        $medId = newId("M");
        $timesCsv = implode(",", $input["scheduleTimes"]);

        /* Insert Medicine */
        $stmt = $pdo->prepare("
            INSERT INTO Medicine (
                medicineId, userId, name, medicineType, dosage,
                timesPerDay, specificTimes, notes, isActive
            ) VALUES (
                :id, :uid, :name, :type, :dos,
                :tpd, :st, :nt, 1
            )
        ");
        $stmt->execute([
            ":id" => $medId,
            ":uid" => $input["userId"],
            ":name" => $input["name"],
            ":type" => $input["medicineType"] ?? null,
            ":dos" => $input["dosage"] ?? null,
            ":tpd" => count($input["scheduleTimes"]),
            ":st"  => $timesCsv,
            ":nt"  => $input["notes"] ?? null
        ]);

        /* Insert Schedule → trigger tự tạo LogEntry */
        $stmt2 = $pdo->prepare("
            INSERT INTO Schedule (scheduleId, medicineId, scheduleDate, specificTime)
            VALUES (:sid, :mid, :date, :time)
        ");

        $start = $input["startDate"];
        $freq  = $input["frequency"];

        if ($freq === "Once") {
            foreach ($input["scheduleTimes"] as $time) {
                $stmt2->execute([
                    ":sid" => newId("S"),
                    ":mid" => $medId,
                    ":date" => $start,
                    ":time" => $time
                ]);
            }
        } 
        else { // Daily 30 ngày
            for ($i = 0; $i < 30; $i++) {
                $d = date("Y-m-d", strtotime("$start +$i day"));
                foreach ($input["scheduleTimes"] as $time) {
                    $stmt2->execute([
                        ":sid" => newId("S"),
                        ":mid" => $medId,
                        ":date" => $d,
                        ":time" => $time
                    ]);
                }
            }
        }

        $pdo->commit();
        respond(["success" => true, "medicineId" => $medId]);

    } catch (Exception $e) {
        $pdo->rollBack();
        respond(["error" => $e->getMessage()], 500);
    }
}


/* ============================================================
   5) DELETE MEDICINE
   ============================================================ */
if ($action === "deleteMedicine") {

    if (!isset($_GET["medicineId"]))
        respond(["error" => "Missing medicineId"], 400);

    $stmt = $pdo->prepare("
        UPDATE Medicine SET isActive = 0 WHERE medicineId = ?
    ");

    respond(["success" => $stmt->execute([$_GET["medicineId"]])]);
}


// DEFAULT
respond(["error" => "Unknown action"], 400);
