package com.example.stride

import androidx.paging.PagingSource
import androidx.paging.PagingState

class MockPagingSource<T : Any>(private val data: List<T>) :
    PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val pageNumber = params.key ?: 1
        val startIndex = (pageNumber-1)*params.loadSize
        return LoadResult.Page(data.subList(startIndex, startIndex+params.loadSize), pageNumber, pageNumber.plus(1))
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int{
        return 1
    }
}