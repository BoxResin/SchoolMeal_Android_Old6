/**
 * 일일 급식 UI
 * @since 3.0.0
 */
package winapi251.app.schoolmeal.ui.main

import winapi251.app.schoolmeal.model.meal.Dish
import winapi251.app.schoolmeal.model.meal.Nutrient

/** 일일 급식 뷰 */
class DailyMealView {
    /** 날짜 바 텍스트를 [text]로 설정한다. */
    fun setDateText(text: String): Unit = TODO()

    /** 지정한 [mode] 버튼을 숨기거나 보여준다. */
    fun showModeButton(mode: Mode, visible: Boolean): Unit = TODO()

    /** 보기 모드 */
    enum class Mode {
        /** 급식 메뉴 보기 모드 */
        MEAL_MENU,

        /** 영양 정보 보기 모드 */
        NUTRIENT,

        /** 원산지 정보 보기 모드 */
        ORIGIN
    }

    /**
     * 급식 메뉴 보기 모드로 전환한다.
     * @param dishes 보여줄 급식 메뉴 목록
     */
    fun selectMealMenuMode(dishes: List<Dish>): Unit = TODO()

    /**
     * 영양 정보 보기 모드로 전환한다.
     * @param nutrient 보여줄 영양 정보
     */
    fun selectNutrientMode(nutrient: Nutrient): Unit = TODO()

    /**
     * 원산지 정보 보기 모드로 전환한다.
     * @param table 보여줄 원산지 정보(키: 식자재 이름, 값: 원산지 이름)
     */
    fun selectOriginMode(table: Map<String, String>): Unit = TODO()

    /** 지정한 [mealTime] 탭을 숨기거나 보여준다. */
    fun showMealTimeTab(mealTime: MealTime, visible: Boolean): Unit = TODO()

    /** 지정한 [mealTime] 탭을 선택한다. */
    fun selectMealTimeTab(mealTime: MealTime): Unit = TODO()

    /**
     * 오류창을 보여준다.
     * @param message 오류 메시지
     * @param buttonText 버튼 텍스트. 빈 문자열을 지정하면 버튼을 보여주지 않는다.
     * @param onClick 버튼 클릭시 호출할 함수
     */
    fun showError(
        message: String,
        buttonText: String = "",
        onClick: () -> Unit = {}
    ): Unit = TODO()

    /**
     * 스낵 바를 보여준다.
     * @param message 메시지
     * @param buttonText 버튼 텍스트. 빈 문자열을 지정하면 버튼을 보여주지 않는다.
     * @param onClick 버튼 클릭시 호출할 함수
     */
    fun showSnackBar(
        message: String,
        buttonText: String = "",
        onClick: () -> Unit = {}
    ): Unit = TODO()

    /** 로딩을 숨기거나 보여준다. */
    fun showLoading(visible: Boolean): Unit = TODO()
}

/** 식사 시간 */
enum class MealTime {
    /** 아침 */
    BREAKFAST,

    /** 점심 */
    LUNCH,

    /** 저녁 */
    DINNER
}
