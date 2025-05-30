package com.example.pokeclient.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeclient.data.api.PokemonDetail
import com.example.pokeclient.data.api.PokemonListItem
import com.example.pokeclient.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    init {
        loadPokemonList()
    }

    var pokemonDetails = mutableStateMapOf<String, PokemonDetail>()
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun loadPokemonList(limit: Int = 30, offset: Int = 0){
        viewModelScope.launch {
            try {
                val list = repository.getPokemonList(limit, offset)
                val details = list.map { item ->
                    async {
                        val detail = repository.getPokemonDetail(item.name)
                        item.name to detail
                    }
                }.awaitAll()

                pokemonDetails.clear()
                pokemonDetails.putAll(details.toMap())
            } catch (e: Exception){
                errorMessage.value = "Не удалось загрузить покемонов. Проверьте соединение."
            }
        }
    }
}