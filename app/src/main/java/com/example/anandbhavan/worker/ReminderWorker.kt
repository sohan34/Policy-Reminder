package com.example.anandbhavan.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.anandbhavan.data.PolicyRepository
import com.example.anandbhavan.util.NotificationHelper
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val policies = PolicyRepository.getAllPolicies()
            val today = Calendar.getInstance()

            // Normalize "today" to beginning of the day
            today.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val daysToCheck = listOf(30, 15, 10, 5, 3, 2, 1, 0) // 🔹 include expiry day

            policies.forEach { policy ->
                val status = policy.status?.lowercase() ?: ""

                // Notify only if policy is Active (case-insensitive)
                if (status == "active" && policy.endDate != null) {

                    val expiry = Calendar.getInstance().apply {
                        time = policy.endDate!!
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    val diff = expiry.timeInMillis - today.timeInMillis
                    val days = TimeUnit.MILLISECONDS.toDays(diff).toInt()

                    if (days in daysToCheck) {
                        val title = if (days == 0)
                            "Policy Expires Today!"
                        else
                            "Policy Expiring Soon!"

                        val message = if (days == 0)
                            "${policy.name} expires today — renew immediately!"
                        else
                            "${policy.name} expires in $days days. Renew soon!"

                        NotificationHelper.sendNotification(
                            applicationContext,
                            title,
                            message,
                            policy.hashCode() // Unique ID
                        )
                    }
                }
            }

            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }
}
