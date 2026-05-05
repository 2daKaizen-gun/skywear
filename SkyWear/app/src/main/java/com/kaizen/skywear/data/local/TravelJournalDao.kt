package com.kaizen.skywear.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelJournalDao {

    @Query("SELECT * FROM travel_journals ORDER BY createdAt DESC")
    fun getAllJournals(): Flow<List<TravelJournal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: TravelJournal)

    @Delete
    suspend fun deleteJournal(journal: TravelJournal)

    @Query("SELECT COUNT(*) FROM travel_journals")
    suspend fun count(): Int
}