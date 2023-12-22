package com.bangkit.annaapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bangkit.annaapp.data.pref.UserPreference
import com.bangkit.annaapp.data.remote.response.ListChatItem
import com.bangkit.annaapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first

class ChatPagingSource(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) : PagingSource<Int, ListChatItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListChatItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val user = userPreference.getSession().first()
            val authorization = "Bearer ${user.accessToken}"
            val responseData = apiService.getChatRooms(authorization).data

            val sortedData = responseData.sortedByDescending { it.updatedAt }

            val nextKey = if (sortedData.size < params.loadSize) null else position + 1

            LoadResult.Page(
                data = sortedData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListChatItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}