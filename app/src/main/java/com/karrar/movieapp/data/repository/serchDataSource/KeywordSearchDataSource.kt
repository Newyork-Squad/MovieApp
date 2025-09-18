package com.karrar.movieapp.data.repository.serchDataSource

import com.karrar.movieapp.data.remote.response.search.SearchKeywordDto
import com.karrar.movieapp.data.remote.service.MovieService
import com.karrar.movieapp.data.repository.BasePagingSource
import javax.inject.Inject
import kotlin.properties.Delegates

class KeywordSearchDataSource @Inject constructor(
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
            val pagedResponse = response.body()
            LoadResult.Page(
                data = pagedResponse?.results ?: emptyList(),
                prevKey = null,
                nextKey = if (pagedResponse?.results?.isEmpty() == true) null else pageNumber + 1
            )
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }
}
