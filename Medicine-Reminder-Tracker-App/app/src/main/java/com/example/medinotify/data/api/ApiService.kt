package com.example.medinotify.data.api

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    /* ------------------ MEDICINE ------------------ */

    @GET("api.php")
    suspend fun getMedicines(
        @Query("action") action: String = "getMedicines",
        @Query("userId") userId: String
    ): Response<MedicineListResponse>

    @GET("api.php")
    suspend fun getMedicinesByDate(
        @Query("action") action: String = "getMedicinesByDate",
        @Query("userId") userId: String,
        @Query("date") date: String
    ): Response<MedicineListResponse>

    @POST("api.php?action=addMedicine")
    suspend fun addMedicine(
        @Body body: MedicineDTO
    ): Response<ApiResponse>

    @DELETE("api.php")
    suspend fun deleteMedicine(
        @Query("action") action: String = "deleteMedicine",
        @Query("medicineId") medicineId: String
    ): Response<ApiResponse>


    /* ------------------ SCHEDULE (not used much) ------------------ */

    @GET("api.php")
    suspend fun getSchedule(
        @Query("action") action: String = "getSchedule",
        @Query("medicineId") medicineId: String
    ): Response<ScheduleResponse>


    /* ------------------ LOG ENTRY (HomeScreen dùng) ------------------ */

    // ⭐ Get all schedule for selected date (LogEntry + Medicine JOIN)
    @GET("api.php")
    suspend fun getScheduleByDate(
        @Query("action") action: String = "getScheduleByDate",
        @Query("userId") userId: String,
        @Query("date") date: String
    ): Response<LogEntryResponse>

    // ⭐ Mark log taken
    @POST("api.php?action=markTaken")
    suspend fun markLogTaken(
        @Body body: Map<String, String>   // JSON → PHP nhận được
    ): Response<ApiResponse>
}
