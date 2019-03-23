package winapi251.app.schoolmeal.model.meal

import winapi251.app.schoolmeal.datetime.TimePoint
import winapi251.app.schoolmeal.model.school.School

/**
 * 원격 급식 정보 저장소
 * @since 3.0.0
 */
object MealRemoteSource {
    /**
     * 지정한 [학교][school]와 [날짜][date]에 해당하는 급식 정보를 다운로드한다.
     * @return 지정한 [날짜][date]를 포함하는 일일 급식 정보 목록
     */
    suspend fun download(school: School, date: TimePoint): List<DailyMeal> = TODO()
}
