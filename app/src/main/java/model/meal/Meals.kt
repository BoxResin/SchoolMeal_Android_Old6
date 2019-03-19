/**
 * 급식 관련 모델 클래스
 * @since 3.0.0
 */
package winapi251.app.schoolmeal.model.meal

import winapi251.app.schoolmeal.datetime.TimePoint

/**
 * 일일 급식 정보
 * @property breakfast 아침 정보
 * @property lunch 점심 정보
 * @property dinner 저녁 정보
 * @property savedTime [로컬 데이터베이스][MealDatabase]에 저장된 시각
 */
class DailyMeal(
    val breakfast: Meal? = null,
    val lunch: Meal? = null,
    val dinner: Meal? = null,
    val savedTime: TimePoint
)

/**
 * 한 끼 급식 정보
 * @property dishes 반찬 목록
 * @property nutrient 영양 정보
 * @property origin 원산지 정보 (ex. `mapOf("쌀" to "국내산", "돼지고기" to "국내산")`)
 */
data class Meal(
    val dishes: List<Dish>,
    val nutrient: Nutrient? = null,
    val origin: Map<String, String>? = null
)

/** 반찬 정보 */
class Dish

/** 영양 정보 */
class Nutrient
