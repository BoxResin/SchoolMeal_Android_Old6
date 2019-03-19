package winapi251.app.schoolmeal.ui.main

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import winapi251.app.schoolmeal.Config
import winapi251.app.schoolmeal.datetime.TimePoint
import winapi251.app.schoolmeal.model.meal.DailyMeal
import winapi251.app.schoolmeal.model.meal.Meal

@ExperimentalCoroutinesApi
class DailyMealPresenterTest {
    private val timePoint = TimePoint(2019, 3, 5)

    @Before
    fun setup(): Unit = runBlocking {
        // 필요 데이터 stubbing
        Config.school.send(mockk())
        Config.dailyMealViewMode.send(DailyMealView.Mode.MEAL_MENU)
        Config.mealTime.send(MealTime.LUNCH)

        mockkObject(MealDatabase)
    }

    @After
    fun release() {
        unmockkAll()
    }

    /** 날짜 바 텍스트가 올바른지 검사한다. */
    @Test
    fun dateText() {
        val view: DailyMealView = mockk()
        DailyMealPresenter(view, timePoint)

        verify { view.setDateText("3월 5일 (화)") }
    }

    /** 보기 모드 버튼을 올바르게 보여주는지 검사한다. */
    @Test
    fun showModeButton() {
        val view: DailyMealView = mockk()

        // 급식 메뉴 정보만 존재할 때
        every { MealDatabase.load(Config.school.value!!, timePoint) } returns DailyMeal(
            lunch = Meal(
                dishes = emptyList()
            ),
            savedTime = timePoint
        )

        DailyMealPresenter(view, timePoint)

        // 급식 메뉴 보기 모드 버튼만 표시되어야 한다.
        verify { view.showModeButton(DailyMealView.Mode.MEAL_MENU, visible = true) }
        verify { view.showModeButton(DailyMealView.Mode.NUTRIENT, visible = false) }
        verify { view.showModeButton(DailyMealView.Mode.ORIGIN, visible = false) }

        clearMocks(view)

        // 급식 메뉴 정보와 영양 정보만 존재할 때
        every { MealDatabase.load(Config.school.value!!, timePoint) } returns DailyMeal(
            lunch = Meal(
                dishes = emptyList(),
                nutrient = mockk()
            ),
            savedTime = timePoint
        )

        DailyMealPresenter(view, timePoint)

        // 급식 메뉴 및 영양 정보 보기 모드 버튼만 표시되어야 한다.
        verify { view.showModeButton(DailyMealView.Mode.MEAL_MENU, visible = true) }
        verify { view.showModeButton(DailyMealView.Mode.NUTRIENT, visible = true) }
        verify { view.showModeButton(DailyMealView.Mode.ORIGIN, visible = false) }

        clearMocks(view)

        // 급식 메뉴 정보와 원산지 정보만 존재할 때
        every { MealDatabase.load(Config.school.value!!, timePoint) } returns DailyMeal(
            lunch = Meal(
                dishes = emptyList(),
                origin = emptyMap()
            ),
            savedTime = timePoint
        )

        DailyMealPresenter(view, timePoint)

        // 급식 메뉴 및 원산지 정보 보기 모드 버튼만 표시되어야 한다.
        verify { view.showModeButton(DailyMealView.Mode.MEAL_MENU, visible = true) }
        verify { view.showModeButton(DailyMealView.Mode.NUTRIENT, visible = false) }
        verify { view.showModeButton(DailyMealView.Mode.ORIGIN, visible = true) }

        clearMocks(view)

        // 모든 정보가 존재할 때
        every { MealDatabase.load(Config.school.value!!, timePoint) } returns DailyMeal(
            lunch = Meal(
                dishes = emptyList(),
                nutrient = mockk(),
                origin = emptyMap()
            ),
            savedTime = timePoint
        )

        DailyMealPresenter(view, timePoint)

        // 모든 보기 모드 버튼이 표시되어야 한다.
        verify { view.showModeButton(DailyMealView.Mode.MEAL_MENU, visible = true) }
        verify { view.showModeButton(DailyMealView.Mode.NUTRIENT, visible = true) }
        verify { view.showModeButton(DailyMealView.Mode.ORIGIN, visible = true) }
    }

    /** 설정된 학교가 없을 때 */
    @Test
    fun noSchool(): Unit = runBlocking {
        Config.school.send(null)

        val view: DailyMealView = mockk()
        val presenter = DailyMealPresenter(view, timePoint)

        verify {
            view.showError(
                message = "학교를 설정해주세요",
                buttonText = "학교 설정",
                onClick = presenter::onClickConfigSchoolInError
            )
        }
    }

    /** 저장된 일일 급식 정보가 없을 때 */
    @Test
    fun noDailyMeal() {
        every { MealDatabase.load(Config.school.value!!, timePoint) } returns null

        val view: DailyMealView = mockk()
        val presenter = DailyMealPresenter(view, timePoint)

        verify {
            view.showError(
                message = "급식 정보를 다운로드하세요.",
                buttonText = "다운로드",
                onClick = presenter::onClickDownloadInError
            )
        }
    }

    /** 저장된 일일 급식 정보가 오래되었을 때 */
    @Test
    fun oldDailyMeal() {
        val mockDailyMeal = DailyMeal(savedTime = TimePoint(2019, 2, 15))
        every { MealDatabase.load(Config.school.value!!, timePoint) } returns mockDailyMeal

        val view: DailyMealView = mockk()
        val presenter = DailyMealPresenter(view, timePoint)

        verify {
            view.showSnackBar(
                message = "급식 정보가 오래되었습니다.\n마지막 다운로드 18일 전",
                buttonText = "새로고침",
                onClick = presenter::onClickDownloadInSnackBar
            )
        }
    }
}
