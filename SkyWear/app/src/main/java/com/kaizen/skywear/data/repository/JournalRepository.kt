package com.kaizen.skywear.data.repository

import com.kaizen.skywear.data.local.TravelJournal
import com.kaizen.skywear.data.local.TravelJournalDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class JournalRepository @Inject constructor(
    private val dao: TravelJournalDao
) {
    fun getAllJournals(): Flow<List<TravelJournal>> = dao.getAllJournals()
    suspend fun addJournal(journal: TravelJournal) = dao.insertJournal(journal)
    suspend fun deleteJournal(journal: TravelJournal) = dao.deleteJournal(journal)
}