package com.example.medinotify.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medinotify.data.api.*
import kotlinx.coroutines.launch

class MedicineViewModel(
    private val repo: MedicineRepository = MedicineRepository()
) : ViewModel() {

    /* -----------------------------------------------------------
       LOG ENTRY (HomeScreen)
       ----------------------------------------------------------- */

    var daySchedule by mutableStateOf(listOf<LogEntryDTO>())
        private set

    var loadingSchedule by mutableStateOf(false)
        private set

    fun loadScheduleByDate(userId: String = "U001", date: String) {
        viewModelScope.launch {
            loadingSchedule = true

            try {
                val res = repo.getScheduleByDate(userId, date)
                daySchedule = res.body()?.schedule ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                daySchedule = emptyList()
            }

            loadingSchedule = false
        }
    }

    fun markAsTaken(logId: String, date: String, userId: String = "U001") {
        viewModelScope.launch {
            try {
                val result = repo.markLogTaken(logId)
                if (result.body()?.success == true) {
                    loadScheduleByDate(userId, date)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    /* -----------------------------------------------------------
       MEDICINE CRUD
       ----------------------------------------------------------- */

    var medicines by mutableStateOf(listOf<MedicineDTO>())
        private set

    var loadingMedicines by mutableStateOf(false)
        private set

    fun loadMedicines(uid: String = "U001") {
        viewModelScope.launch {
            loadingMedicines = true
            try {
                val res = repo.getMedicines(uid)
                medicines = res.body()?.medicines ?: emptyList()
            } catch (_: Exception) {}
            loadingMedicines = false
        }
    }

    fun addMedicine(dto: MedicineDTO, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repo.addMedicine(dto)
                loadMedicines()
                onDone()
            } catch (_: Exception) {}
        }
    }

    fun deleteMedicine(id: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repo.deleteMedicine(id)
                loadMedicines()
                onDone()
            } catch (_: Exception) {}
        }
    }
}
