package com.example.medinotify.data.api

import retrofit2.Response

class MedicineRepository(
    private val api: ApiService = ApiClient.api
) {

    /* ------------------ MEDICINE CRUD ------------------ */

    suspend fun getMedicines(userId: String): Response<MedicineListResponse> {
        return api.getMedicines(userId = userId)
    }

    suspend fun getMedicinesByDate(userId: String, date: String): Response<MedicineListResponse> {
        return api.getMedicinesByDate(
            userId = userId,
            date = date
        )
    }

    suspend fun getSchedule(medicineId: String): Response<ScheduleResponse> {
        return api.getSchedule(
            medicineId = medicineId
        )
    }

    suspend fun addMedicine(dto: MedicineDTO): Response<ApiResponse> {
        return api.addMedicine(dto)
    }

    suspend fun deleteMedicine(id: String): Response<ApiResponse> {
        return api.deleteMedicine(
            medicineId = id
        )
    }


    /* ------------------ LOG ENTRY (HomeScreen) ------------------ */

    // ⭐ Lấy tất cả lịch uống trong ngày từ LogEntry
    suspend fun getScheduleByDate(userId: String, date: String): Response<LogEntryResponse> {
        return api.getScheduleByDate(
            userId = userId,
            date = date
        )
    }

    // ⭐ Đánh dấu đã uống → gửi JSON {"logId": "xxx"}
    suspend fun markLogTaken(logId: String): Response<ApiResponse> {
        return api.markLogTaken(
            body = mapOf("logId" to logId)
        )
    }
}
