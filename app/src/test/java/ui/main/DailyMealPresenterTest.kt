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
