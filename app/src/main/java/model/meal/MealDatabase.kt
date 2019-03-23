package winapi251.app.schoolmeal.model.meal

import winapi251.app.schoolmeal.datetime.TimePoint
import winapi251.app.schoolmeal.model.school.School

/**
 * 로컬 급식 데이터베이스
 * @since 3.0.0
 */
object MealDatabase {
    /**
     * 지정한 [학교][school]와 [날짜][date]에 해당하는 [일일 급식 정보][DailyMeal]를 반환한다.
     * @return 지정한 조건에 해당하는 급식 정보를 찾을 수 없으면 null
     */
    fun load(school: School, date: TimePoint): DailyMeal? = TODO()

    /**
     * 로컬 급식 데이터베이스에 급식 정보를 저장한다.
     * @param school 어떤 학교의 급식 정보인지
     * @param data 저장할 급식 정보
     */
    fun save(school: School, data: List<DailyMeal>): Unit = TODO()
}
