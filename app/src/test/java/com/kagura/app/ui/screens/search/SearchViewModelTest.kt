package com.kagura.app.ui.screens.search

import app.cash.turbine.test
import com.kagura.app.data.repository.KaguraRepository
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaItem
import com.kagura.app.domain.model.MediaType
import com.kagura.app.domain.model.Raridade
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: KaguraRepository
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testQueryAndResults() = runTest {
        val sampleItem = MediaItem(
            id = 1,
            title = "Test Media",
            japaneseTitle = "Test",
            type = MediaType.ANIME,
            synopsis = "Synopsis",
            coverUrl = "",
            rarity = Raridade.COMUM,
            status = ItemStatus.NONE,
            userRating = null,
            score = 7.0f,
            vibe = "Acao",
            tears = 0,
            units = 1,
            year = 2023,
            accent = 0xFF000000,
            genres = listOf("Action"),
            externalId = "1",
            source = "local",
            popularity = 1000
        )

        coEvery { repository.searchSync("Test") } returns listOf(sampleItem)

        viewModel = SearchViewModel(repository)

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())
            viewModel.onQuery("Test")
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(SearchUiState.Loading, awaitItem())
            val success = awaitItem() as SearchUiState.Success
            assertEquals(1, success.items.size)
            assertEquals("Test Media", success.items[0].title)
        }
    }
}
