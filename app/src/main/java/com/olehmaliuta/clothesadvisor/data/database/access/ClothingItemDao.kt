package com.olehmaliuta.clothesadvisor.data.database.access

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.olehmaliuta.clothesadvisor.data.database.entities.ClothingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingItemDao {
    @Insert
    suspend fun insertEntity(entity: ClothingItem): Long

    @Insert
    suspend fun insertEntities(entities: List<ClothingItem>)

    @Update
    suspend fun updateEntity(entity: ClothingItem)

    @Query("""
        UPDATE clothing_items
        SET is_favorite = NOT is_favorite
        WHERE id = :id
        """)
    suspend fun updateIsFavoriteValue(id: Long)

    @Query("DELETE FROM clothing_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("DELETE FROM clothing_items")
    suspend fun deleteAllRows()

    @Query("SELECT * FROM clothing_items")
    suspend fun getAllClothingItems(): List<ClothingItem>

    @Query("SELECT COUNT(*) FROM clothing_items")
    fun countClothingItems(): Flow<Long>

    @Query("SELECT * FROM clothing_items WHERE id = :id LIMIT 1")
    fun getItemById(id: Long?): Flow<ClothingItem?>

    @Query("SELECT * FROM clothing_items WHERE id IN (:ids)")
    fun getItemsByIds(ids: List<Long>): Flow<List<ClothingItem>>

    @Query("""
        SELECT DISTINCT category 
        FROM clothing_items
        WHERE id IN (:itemIds)
        ORDER BY category ASC
    """)
    fun getUniqueCategoriesByIds(itemIds: List<Long>): Flow<List<String>>

    @RawQuery(observedEntities = [ClothingItem::class])
    fun searchAllFieldsRaw(
        query: SupportSQLiteQuery
    ): Flow<List<ClothingItem>>

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