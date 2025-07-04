package com.example.rushbuy.core.foundation.data.paging




import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rushbuy.core.foundation.data.local.source.IProductLocalDataSource
import com.example.rushbuy.core.foundation.data.remote.source.IProductRemoteDataSource
import com.example.rushbuy.core.foundation.domain.model.Product
import com.example.rushbuy.core.foundation.utils.toDomain
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException
import androidx.paging.PagingSource.LoadResult

// Define the initial page offset and page size.
// pageSize is also defined in PagingConfig, but helps for consistency.
private const val STARTING_PAGE_INDEX = 0
private const val DEFAULT_PAGE_SIZE = 10

class ProductPagingSource(
    private val remoteDataSource: IProductRemoteDataSource,
    private val localDataSource: IProductLocalDataSource,
    private val query: String = "",
    private val category: String = ""
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val position = params.key ?: STARTING_PAGE_INDEX
        val limit = params.loadSize

        return try {
            val products: List<Product>

            if (position == STARTING_PAGE_INDEX && query.isBlank() && category.isBlank()) {
                val localProducts = localDataSource.getAllProducts().first()
                if (localProducts.isNotEmpty()) {
                    products = localProducts
                } else {
                    val remoteResponse = remoteDataSource.getProducts(limit = limit, skip = position * DEFAULT_PAGE_SIZE)
                    val mappedRemoteProducts = remoteResponse.products.map { it.toDomain() } // Map DTOs to Domain Products
                    products = mappedRemoteProducts
                    localDataSource.insertProducts(products)
                }
            } else {
                val fetchedRemoteProducts = remoteDataSource.getProducts(
                    limit = limit,
                    skip = position * DEFAULT_PAGE_SIZE
                )
                // NEW: Map DTOs to Domain Products *before* filtering
                val allRemoteProductsAsDomain = fetchedRemoteProducts.products.map { it.toDomain() }

                val filteredProducts = if (query.isNotBlank()) {
                    // Now 'it' is a Product domain model, which has 'name' and 'description'
                    allRemoteProductsAsDomain.filter {
                        it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
                    }
                } else if (category.isNotBlank()) {
                    // Now 'it' is a Product domain model, which has 'category'
                    allRemoteProductsAsDomain.filter {
                        it.category.equals(category, ignoreCase = true)
                    }
                } else {
                    allRemoteProductsAsDomain
                }
                products = filteredProducts
            }

            val nextKey = if (products.isEmpty() || products.size < limit) {
                null
            } else {
                position + (params.loadSize / DEFAULT_PAGE_SIZE)
            }

            LoadResult.Page(
                data = products,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}