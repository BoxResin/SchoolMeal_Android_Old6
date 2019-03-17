/**
 * 급식 관련 모델 클래스
 * @since 3.0.0
 */
package winapi251.app.schoolmeal.model.meal

import winapi251.app.schoolmeal.datetime.TimePoint

/**
 * 일일 급식 정보
 * @property savedTime [로컬 데이터베이스][MealDatabase]에 저장된 시각
 */
class DailyMeal(
    val savedTime: TimePoint
)

/** 반찬 정보 */
class Dish

/** 영양 정보 */
class Nutrient
