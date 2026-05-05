package com.kaizen.skywear.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizen.skywear.data.local.TravelJournal
import com.kaizen.skywear.data.repository.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val repository: JournalRepository
) : ViewModel() {

    val journals: StateFlow<List<TravelJournal>> = repository.getAllJournals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addJournal(journal: TravelJournal) {
        viewModelScope.launch { repository.addJournal(journal) }
    }

    fun deleteJournal(journal: TravelJournal) {
        viewModelScope.launch { repository.deleteJournal(journal) }
    }
}