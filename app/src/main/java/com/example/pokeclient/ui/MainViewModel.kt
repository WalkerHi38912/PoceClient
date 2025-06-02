package com.example.pokeclient.ui

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeclient.data.api.PokemonDetail
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

    private val limit = 30
    private val maxPokemonCount = 1302
    private var currentOffset = 0
    private val maxOffset = maxPokemonCount - limit
    private var isLoading = false
    private var hasReachedTheEnd = false
    private var initialRandomOffset = 0

    var pokemonList = mutableStateListOf<Pair<String, PokemonDetail>>()
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun loadPokemonList(){
        if(hasReachedTheEnd && currentOffset >= initialRandomOffset) return
        if(isLoading) return

        isLoading = true

        viewModelScope.launch {
            try {
                val list = repository.getPokemonList(limit, currentOffset)
                Log.e("PokemonList", "$list")
                val details = list.map { item ->
                    async {
                        val detail = repository.getPokemonDetail(item.name)
                        item.name to detail
                    }
                }.awaitAll()
                pokemonList.addAll(details)

                currentOffset += limit
                Log.e("CurrentOffset", "$currentOffset")
                if(currentOffset > maxPokemonCount){
                    currentOffset = 0
                    hasReachedTheEnd = true
                }

            } catch (e: Exception){
                errorMessage.value = "Не удалось загрузить покемонов. Проверьте соединение."
                Log.e("loadPokemonList", "Не удалось загрузить покемонов. Проверьте соединение.")
            } finally {
                isLoading = false
            }
        }
    }

    fun loadRandomPokemonList(){
        hasReachedTheEnd = false
        initialRandomOffset = (0..maxOffset).random()

        if(isLoading) return
        isLoading = true

        viewModelScope.launch {
            try {
                val list = repository.getPokemonList(limit, initialRandomOffset)
                Log.e("RandomPokemonList", "$list")
                val details = list.map { item ->
                    async {
                        val detail = repository.getPokemonDetail(item.name)
                        item.name to detail
                    }
                }.awaitAll()
                pokemonList.clear()
                pokemonList.addAll(details)

                currentOffset = (initialRandomOffset + limit)
                Log.e("CurrentOffset", "$currentOffset")
            }catch (e: Exception){
                errorMessage.value = "Не удалось загрузить покемонов. Проверьте соединение."
                Log.e("loadRandomPokemonList", "Не удалось загрузить покемонов. Проверьте соединение.")
            } finally {
                isLoading = false
            }
        }

    }
}