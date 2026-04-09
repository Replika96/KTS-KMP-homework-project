package org.kts.tazmin.feature.courses.data.local


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogDao {

    @Transaction
    @Query("SELECT * FROM catalog_sections ORDER BY position ASC")
    fun observeSectionsWithItems(): Flow<List<SectionWithItems>>

    @Query("SELECT * FROM catalog_section_items")
    suspend fun getAllItems(): List<CatalogSectionItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSections(sections: List<CatalogSectionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<CatalogSectionItemEntity>)

    @Query("DELETE FROM catalog_section_items")
    suspend fun clearItems()

    @Query("DELETE FROM catalog_sections")
    suspend fun clearSections()

    @Transaction
    suspend fun replaceCatalog(
        sections: List<CatalogSectionEntity>,
        items: List<CatalogSectionItemEntity>
    ) {
        clearItems()
        clearSections()
        insertSections(sections)
        insertItems(items)
    }
}
