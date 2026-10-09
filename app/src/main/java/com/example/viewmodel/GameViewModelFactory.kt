package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.GameRepository
import com.example.data.FirestoreRepository
import com.example.utils.SoundManager

class GameViewModelFactory(
    private val repository: GameRepository,
    private val soundManager: SoundManager,
    private val firestoreRepository: FirestoreRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository, soundManager, firestoreRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
