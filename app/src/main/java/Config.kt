package winapi251.app.schoolmeal

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import winapi251.app.schoolmeal.model.school.School
import winapi251.app.schoolmeal.ui.main.DailyMealView
import winapi251.app.schoolmeal.ui.main.MealTime

/**
 * 환경 설정
 * @since 3.0.0
 */
@ExperimentalCoroutinesApi
object Config {
    /** 현재 설정된 학교 정보 */
    val school = ConflatedBroadcastChannel<School?>(value = null)

    /** 현재 설정된 일일 급식 보기 모드 */
    val dailyMealViewMode = ConflatedBroadcastChannel(DailyMealView.Mode.MEAL_MENU)

    /** 현재 설정된 식사 시간 */
    val mealTime = ConflatedBroadcastChannel(MealTime.LUNCH)
}
