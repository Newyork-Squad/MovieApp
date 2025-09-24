package com.karrar.movieapp.data.repository.serchDataSource

import com.karrar.movieapp.data.remote.response.search.SearchKeywordDto
import com.karrar.movieapp.data.remote.service.MovieService
import com.karrar.movieapp.data.repository.BasePagingSource
import javax.inject.Inject
import kotlin.properties.Delegates

class SearchSuggestedDataSource @Inject constructor(
    private val service: MovieService,
) : BasePagingSource<SearchKeywordDto>() {

    private var keywordSearchText by Delegates.notNull<String>()

    fun setSearchText(searchText: String) {
        keywordSearchText = searchText
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchKeywordDto> {
        val pageNumber = params.key ?: 1
        return try {
            val response = service.getSearchKeywords(keywordSearchText, pageNumber)
            val results = response.body()?.results ?: emptyList()

            val limitedResults = if (results.size > 20) results.take(20) else results

            LoadResult.Page(
                data = limitedResults,
                prevKey = null,
                nextKey = if (results.isEmpty() || pageNumber >= 1) null else pageNumber + 1
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }

}
