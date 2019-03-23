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
import winapi251.app.schoolmeal.model.meal.MealDatabase
import winapi251.app.schoolmeal.model.meal.MealRemoteSource
import winapi251.app.schoolmeal.model.school.School

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

    /** 올바른 보기 모드를 선택하는지 검사한다. */
    @Test
    fun selectMode(): Unit = runBlocking {
        val view: DailyMealView = mockk()

        val mockDailyMeal = DailyMeal(
            lunch = Meal(
                dishes = emptyList(),
                nutrient = mockk(),
                origin = emptyMap()
            ),
            savedTime = timePoint
        )
        every { MealDatabase.load(Config.school.value!!, timePoint) } returns mockDailyMeal

        // 현재 선택된 보기 모드가 급식 메뉴 보기 모드이면 급식 메뉴 보기 모드를 선택해야 한다.
        Config.dailyMealViewMode.send(DailyMealView.Mode.MEAL_MENU)
        DailyMealPresenter(view, timePoint)
        verify { view.selectMealMenuMode(mockDailyMeal.lunch!!.dishes) }

        clearMocks(view)

        // 현재 선택된 보기 모드가 영양 정보 보기 모드이면 영양 정보 보기 모드를 선택해야 한다.
        Config.dailyMealViewMode.send(DailyMealView.Mode.NUTRIENT)
        DailyMealPresenter(view, timePoint)
        verify { view.selectNutrientMode(mockDailyMeal.lunch!!.nutrient!!) }

        clearMocks(view)

        // 현재 선택된 보기 모드가 원산지 정보 보기 모드이면 원산지 정보 보기 모드를 선택해야 한다.
        Config.dailyMealViewMode.send(DailyMealView.Mode.ORIGIN)
        DailyMealPresenter(view, timePoint)
        verify { view.selectOriginMode(mockDailyMeal.lunch!!.origin!!) }
    }

    /** 식사 시간 탭을 올바르게 보여주는지 검사한다. */
    @Test
    fun showMealTimeTab() {
        val view: DailyMealView = mockk()

        // 점심 정보만 존재할 때
        every { MealDatabase.load(Config.school.value!!, timePoint) } returns DailyMeal(
            lunch = mockk(),
            savedTime = timePoint
        )

        DailyMealPresenter(view, timePoint)

        // 점심 탭만 표시되어야 한다.
        verify { view.showMealTimeTab(MealTime.BREAKFAST, visible = false) }
        verify { view.showMealTimeTab(MealTime.LUNCH, visible = true) }
        verify { view.showMealTimeTab(MealTime.DINNER, visible = false) }

        clearMocks(view)

        // 점심, 저녁 정보만 존재할 때
        every { MealDatabase.load(Config.school.value!!, timePoint) } returns DailyMeal(
            lunch = mockk(),
            dinner = mockk(),
            savedTime = timePoint
        )

        DailyMealPresenter(view, timePoint)

        // 점심, 저녁 탭만 표시되어야 한다.
        verify { view.showMealTimeTab(MealTime.BREAKFAST, visible = false) }
        verify { view.showMealTimeTab(MealTime.LUNCH, visible = true) }
        verify { view.showMealTimeTab(MealTime.DINNER, visible = true) }

        clearMocks(view)

        // 아침, 점심, 저녁 정보 모두 존재할 때
        every { MealDatabase.load(Config.school.value!!, timePoint) } returns DailyMeal(
            breakfast = mockk(),
            lunch = mockk(),
            dinner = mockk(),
            savedTime = timePoint
        )

        DailyMealPresenter(view, timePoint)

        // 모든 탭이 표시되어야 한다.
        verify { view.showMealTimeTab(MealTime.BREAKFAST, visible = true) }
        verify { view.showMealTimeTab(MealTime.LUNCH, visible = true) }
        verify { view.showMealTimeTab(MealTime.DINNER, visible = true) }
    }

    /** 올바른 식사 시간 탭을 선택하는지 검사한다. */
    @Test
    fun selectMealTimeTab(): Unit = runBlocking {
        val view: DailyMealView = mockk()

        every { MealDatabase.load(Config.school.value!!, timePoint) } returns DailyMeal(
            breakfast = mockk(),
            lunch = mockk(),
            dinner = mockk(),
            savedTime = timePoint
        )

        // 현재 선택된 탭이 아침 탭이면 아침 탭을 선택해야 한다.
        Config.mealTime.send(MealTime.BREAKFAST)
        DailyMealPresenter(view, timePoint)
        verify { view.selectMealTimeTab(MealTime.BREAKFAST) }
        verify(exactly = 0) { view.selectMealTimeTab(MealTime.LUNCH) }
        verify(exactly = 0) { view.selectMealTimeTab(MealTime.DINNER) }

        clearMocks(view)

        // 현재 선택된 탭이 점심 탭이면 점심 탭을 선택해야 한다.
        Config.mealTime.send(MealTime.LUNCH)
        DailyMealPresenter(view, timePoint)
        verify(exactly = 0) { view.selectMealTimeTab(MealTime.BREAKFAST) }
        verify { view.selectMealTimeTab(MealTime.LUNCH) }
        verify(exactly = 0) { view.selectMealTimeTab(MealTime.DINNER) }

        clearMocks(view)

        // 현재 선택된 탭이 저녁 탭이면 저녁 탭을 선택해야 한다.
        Config.mealTime.send(MealTime.DINNER)
        DailyMealPresenter(view, timePoint)
        verify(exactly = 0) { view.selectMealTimeTab(MealTime.BREAKFAST) }
        verify(exactly = 0) { view.selectMealTimeTab(MealTime.LUNCH) }
        verify { view.selectMealTimeTab(MealTime.DINNER) }
    }

    /** 보기 모드가 선택될 때 올바른 동작을 하는지 검사한다. */
    @Test
    fun onSelectMode() {
        val mockDailyMeal = DailyMeal(
            lunch = Meal(
                dishes = emptyList(),
                nutrient = mockk(),
                origin = emptyMap()
            ),
            savedTime = timePoint
        )
        every { MealDatabase.load(Config.school.value!!, timePoint) } returns mockDailyMeal

        val view: DailyMealView = mockk()
        val presenter = DailyMealPresenter(view, timePoint)

        // 영양 정보 보기 모드가 선택되면 해당 정보를 보여주고 설정을 동기화해야 한다.
        presenter.onSelectMode(DailyMealView.Mode.NUTRIENT)
        verify { view.selectNutrientMode(mockDailyMeal.lunch!!.nutrient!!) }
        coVerify { Config.dailyMealViewMode.send(DailyMealView.Mode.NUTRIENT) }

        // 원산지 정보 보기 모드가 선택되면 해당 정보를 보여주고 설정을 동기화해야 한다.
        presenter.onSelectMode(DailyMealView.Mode.ORIGIN)
        verify { view.selectOriginMode(mockDailyMeal.lunch!!.origin!!) }
        coVerify { Config.dailyMealViewMode.send(DailyMealView.Mode.ORIGIN) }
    }

    /** 식사 시간 탭이 선택될 때 올바른 동작을 하는지 검사한다. */
    @Test
    fun onSelectMealTimeTab() {
        val mockDailyMeal = DailyMeal(
            breakfast = Meal(
                dishes = emptyList()
            ),
            lunch = Meal(
                dishes = emptyList()
            ),
            dinner = Meal(
                dishes = emptyList()
            ),
            savedTime = timePoint
        )
        every { MealDatabase.load(Config.school.value!!, timePoint) } returns mockDailyMeal

        val view: DailyMealView = mockk()
        val presenter = DailyMealPresenter(view, timePoint)

        // 아침 탭이 선택되면 해당 정보를 보여주고 설정을 동기화해야 한다.
        presenter.onSelectMealTimeTab(MealTime.BREAKFAST)
        verify { view.selectMealMenuMode(mockDailyMeal.breakfast!!.dishes) }
        coVerify { Config.mealTime.send(MealTime.BREAKFAST) }

        // 저녁 탭이 선택되면 해당 정보를 보여주고 설정을 동기화해야 한다.
        presenter.onSelectMealTimeTab(MealTime.DINNER)
        verify { view.selectMealMenuMode(mockDailyMeal.dinner!!.dishes) }
        coVerify { Config.mealTime.send(MealTime.DINNER) }
    }

    /** 오류창에서 다운로드 버튼을 클릭할 때 올바른 동작을 하는지 검사한다. */
    @Test
    fun onClickDownloadInError() {
        val view: DailyMealView = mockk()
        val presenter = DailyMealPresenter(view, timePoint)

        // 다운로드 중 에러가 발생하면 오류창을 보여줘야 한다.
        coEvery { MealRemoteSource.download(Config.school.value!!, timePoint) } throws Exception()
        presenter.onClickDownloadInError()
        verify {
            view.showError(
                message = "오류 발생",
                buttonText = "다시 시도",
                onClick = presenter::onClickDownloadInError
            )
        }

        // 다운로드 중 에러가 발생하지 않았으면 로컬 데이터베이스에 저장하고 급식 정보를 보여줘야 한다.
        val mockDailyMeals: List<DailyMeal> = listOf(
            DailyMeal(
                lunch = Meal(
                    dishes = emptyList()
                ),
                savedTime = timePoint
            )
        )
        coEvery { MealRemoteSource.download(Config.school.value!!, timePoint) } returns mockDailyMeals
        presenter.onClickDownloadInError()
        verify { MealDatabase.save(Config.school.value!!, mockDailyMeals) }
        verify { view.selectMealMenuMode(mockDailyMeals[0].lunch!!.dishes) }
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

    /** 학교가 변경되었을 때 */
    @Test
    fun onSchoolChanged(): Unit = runBlocking {
        val view: DailyMealView = mockk()
        DailyMealPresenter(view, timePoint)

        val newSchool: School = mockk()
        Config.school.send(newSchool)

        // 급식 정보를 새로 로딩해야 한다.
        verify { MealDatabase.load(newSchool, timePoint) }
    }

    /** 보기 모드 설정이 변경되었을 때 */
    @Test
    fun onViewModeConfigChanged(): Unit = runBlocking {
        val mockDailyMeal = DailyMeal(
            lunch = Meal(
                dishes = emptyList(),
                nutrient = mockk(),
                origin = emptyMap()
            ),
            savedTime = timePoint
        )
        every { MealDatabase.load(Config.school.value!!, timePoint) } returns mockDailyMeal

        val view: DailyMealView = mockk()
        DailyMealPresenter(view, timePoint)

        // 보기 모드 설정이 영양 정보 보기 모드로 바뀌면
        Config.dailyMealViewMode.send(DailyMealView.Mode.NUTRIENT)

        // 해당 보기 모드를 선택해야 한다.
        verify { view.selectNutrientMode(mockDailyMeal.lunch!!.nutrient!!) }

        // 보기 모드 설정이 원산지 정보 보기 모드로 바뀌면
        Config.dailyMealViewMode.send(DailyMealView.Mode.ORIGIN)

        // 해당 보기 모드를 선택해야 한다.
        verify { view.selectOriginMode(mockDailyMeal.lunch!!.origin!!) }
    }

    /** 식사 시간 설정이 변경되었을 때 */
    @Test
    fun onMealTimeConfigChanged(): Unit = runBlocking {
        val mockDailyMeal = DailyMeal(
            breakfast = Meal(
                dishes = emptyList()
            ),
            lunch = Meal(
                dishes = emptyList()
            ),
            dinner = Meal(
                dishes = emptyList()
            ),
            savedTime = timePoint
        )
        every { MealDatabase.load(Config.school.value!!, timePoint) } returns mockDailyMeal

        val view: DailyMealView = mockk()
        DailyMealPresenter(view, timePoint)

        // 식사 시간이 아침으로 바뀌면
        Config.mealTime.send(MealTime.BREAKFAST)

        // 해당 식사 시간 탭을 선택해야 한다.
        verify { view.selectMealTimeTab(MealTime.BREAKFAST) }

        // 식사 시간이 저녁으로 바뀌면
        Config.mealTime.send(MealTime.DINNER)

        // 해당 식사 시간 탭을 선택해야 한다.
        verify { view.selectMealTimeTab(MealTime.DINNER) }
    }
}
