package com.olehmaliuta.clothesadvisor.database.access

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.olehmaliuta.clothesadvisor.database.entities.ClothingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingItemDao {
    @Insert
    suspend fun insertEntity(entity: ClothingItem)

    @Insert
    suspend fun insertEntities(entities: List<ClothingItem>)

    @Query("DELETE FROM clothing_items")
    suspend fun deleteAllRows()

    @Query("SELECT * FROM clothing_items")
    suspend fun getAllClothingItems(): List<ClothingItem>

    @Query("SELECT COUNT(*) FROM clothing_items")
    fun countClothingItems(): Flow<Int>

    @RawQuery(observedEntities = [ClothingItem::class])
    fun searchAllFieldsRaw(query: SupportSQLiteQuery): Flow<List<ClothingItem>>

    fun searchAllFields(
        query: String,
        sortBy: String,
        ascSort: Boolean,
        categories: List<String>,
        seasons: List<String>
    ): Flow<List<ClothingItem>> {
        val sortDirection = if (ascSort) "ASC" else "DESC"
        val safeSortBy = when (sortBy.lowercase()) {
            in listOf("name", "purchase_date") -> sortBy
            else -> "name"
        }

        val whereClauses = mutableListOf<String>()
        val queryArgs = mutableListOf<Any>()

        if (query.isNotBlank()) {
            whereClauses.add("""
                (LOWER(name) LIKE '%' || LOWER(?) || '%'
                OR LOWER(material) LIKE '%' || LOWER(?) || '%'
                OR LOWER(brand) LIKE '%' || LOWER(?) || '%')
            """.trimIndent())
            queryArgs.addAll(listOf(query, query, query))
        }

        if (categories.isNotEmpty()) {
            whereClauses.add("category IN (${categories.joinToString(", ") { "?" }})")
            queryArgs.addAll(categories)
        }

        if (seasons.isNotEmpty()) {
            whereClauses.add("season IN (${seasons.joinToString(", ") { "?" }})")
            queryArgs.addAll(seasons)
        }

        val whereStatement = if (whereClauses.isNotEmpty()) {
            "WHERE ${whereClauses.joinToString(" AND ")}"
        } else {
            ""
        }

        val sqlQuery = """
            SELECT * FROM clothing_items
            $whereStatement
            ORDER BY $safeSortBy $sortDirection
        """.trimIndent()

        return searchAllFieldsRaw(
            SimpleSQLiteQuery(sqlQuery, queryArgs.toTypedArray()))
    }
}