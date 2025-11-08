package com.nerver.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: String = System.currentTimeMillis().toString(),
    val photoPath: String? = null,
    val fio: String = "",
    val description: String = "",
    val answers: List<Boolean> = List(12) { false } // 12 бинарных вопросов
) : Parcelable {
    
    // Блоки вопросов
    private val block1: List<Boolean> get() = answers.take(3) // Resources (1-3)
    private val block2: List<Boolean> get() = answers.drop(3).take(3) // Reciprocity (4-6)
    private val block3: List<Boolean> get() = answers.drop(6).take(3) // ConditionalSupport (7-9)
    private val block4: List<Boolean> get() = answers.drop(9).take(3) // RedFlags (10-12)
    
    // Подсчет баллов по блокам
    private val block1Score: Int get() = block1.count { it }
    private val block2Score: Int get() = block2.count { it }
    private val block3Score: Int get() = block3.count { it }
    private val block4Score: Int get() = block4.count { it }
    
    // Проверка на красные флаги
    val hasRedFlags: Boolean get() = block4.any { it }
    
    // Общий счет
    val score: Int get() {
        val baseScore = block1Score + block2Score + block3Score
        return if (hasRedFlags) baseScore - 10 else baseScore
    }
    
    // Определение категории
    enum class Category {
        CRITICAL,    // 🔴 Критично / Исключить
        ON_HOLD,     // 🟡 На удержании
        SAFE         // 🟢 Можно общаться
    }
    
    val category: Category
        get() {
            // Если есть красные флаги или отрицательный баланс -> Критично
            if (hasRedFlags || score < 3) {
                return Category.CRITICAL
            }
            // Если score >= 7 -> Можно общаться
            if (score >= 7) {
                return Category.SAFE
            }
            // Иначе -> На удержании
            return Category.ON_HOLD
        }
    
    // Нормализация ФИО (до 50 символов)
    fun normalizeFio(): String {
        return if (fio.length > 50) fio.take(50) else fio
    }
    
    // Нормализация описания (ровно 50 символов)
    fun normalizeDescription(): String {
        val normalized = if (description.length > 50) {
            description.take(50)
        } else {
            description
        }
        return normalized.padEnd(50, ' ')
    }
}

