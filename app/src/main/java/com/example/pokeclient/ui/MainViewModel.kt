package com.example.pokeclient.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeclient.data.api.PokemonListItem
import com.example.pokeclient.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    init {
        loadPokemonList()
    }

    val pokemonList = mutableStateListOf<PokemonListItem>()

    fun loadPokemonList(limit: Int = 30, offset: Int = 0){
        viewModelScope.launch {
            val result = repository.getPokemonList(limit, offset)
            pokemonList.clear()
            pokemonList.addAll(result)
        }
    }
}