package org.kts.tazmin.feature.course_details.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.course_details.data.local.LessonEntity
import org.kts.tazmin.feature.course_details.data.local.SectionEntity
import org.kts.tazmin.feature.course_details.data.local.StepEntity
import org.kts.tazmin.feature.course_details.data.local.UnitEntity
import org.kts.tazmin.feature.course_details.data.local.fullEntity.SectionFull

@Dao
interface CourseStructureDao {

    // observe
    @Transaction
    @Query(
        """
        SELECT * FROM sections 
        WHERE courseId = :courseId 
        ORDER BY position
        """
    )
    fun observeCourseStructure(
        courseId: Int
    ): Flow<List<SectionFull>>


    // insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSections(sections: List<SectionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnits(units: List<UnitEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: List<StepEntity>)

    // delete
    @Query(
        """
        DELETE FROM steps 
        WHERE lessonId IN (
            SELECT id FROM lessons 
            WHERE id IN (
                SELECT lessonId FROM units 
                WHERE sectionId IN (
                    SELECT id FROM sections WHERE courseId = :courseId
                )
            )
        )
        """
    )
    suspend fun clearStepsByCourse(courseId: Int)

    @Query(
        """
        DELETE FROM lessons 
        WHERE id IN (
            SELECT lessonId FROM units 
            WHERE sectionId IN (
                SELECT id FROM sections WHERE courseId = :courseId
            )
        )
        """
    )
    suspend fun clearLessonsByCourse(courseId: Int)

    @Query(
        """
        DELETE FROM units 
        WHERE sectionId IN (
            SELECT id FROM sections WHERE courseId = :courseId
        )
        """
    )
    suspend fun clearUnitsByCourse(courseId: Int)

    @Query("DELETE FROM sections WHERE courseId = :courseId")
    suspend fun clearSectionsByCourse(courseId: Int)


    @Transaction
    suspend fun replaceCourseData(
        courseId: Int,
        sections: List<SectionEntity>,
        units: List<UnitEntity>,
        lessons: List<LessonEntity>,
        steps: List<StepEntity> = emptyList()
    ) {
        clearStepsByCourse(courseId)
        clearLessonsByCourse(courseId)
        clearUnitsByCourse(courseId)
        clearSectionsByCourse(courseId)

        insertSections(sections)
        insertUnits(units)
        insertLessons(lessons)
        insertSteps(steps)
    }
}
