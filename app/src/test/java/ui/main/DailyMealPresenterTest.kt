package winapi251.app.schoolmeal.ui.main

import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import winapi251.app.schoolmeal.Config
import winapi251.app.schoolmeal.datetime.TimePoint

@ExperimentalCoroutinesApi
class DailyMealPresenterTest {
    private val timePoint = TimePoint(2019, 3, 5)

    @Before
    fun setup(): Unit = runBlocking {
        // 필요 데이터 stubbing
        Config.school.send(mockk())
        Config.dailyMealViewMode.send(DailyMealView.Mode.MEAL_MENU)
        Config.mealTime.send(MealTime.LUNCH)
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
}
