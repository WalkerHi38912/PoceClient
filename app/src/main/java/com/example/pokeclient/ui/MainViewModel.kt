package com.example.pokeclient.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeclient.data.api.PokemonDetail
import com.example.pokeclient.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val limit = 30
    private val maxPokemonCount = 1302
    private var currentOffset = 0
    private val maxOffset = maxPokemonCount - limit

    private var hasReachedTheEnd = false
    private var initialRandomOffset = 0

    var pokemonList = mutableStateListOf<Pair<String, PokemonDetail>>()
        private set

    var strongestPokemon = mutableStateOf<Pair<String, PokemonDetail>?>(null)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    var firstLoading = mutableStateOf(false)
        private set

    var randomLoading = mutableStateOf(false)
        private set

    init {
        loadPokemonList()
        firstLoading.value = true
    }

    fun loadPokemonList(){
        if(hasReachedTheEnd && currentOffset >= initialRandomOffset) return
        if(isLoading.value) return

        isLoading.value = true

        viewModelScope.launch {
            try {
                val list = repository.getRemotePokemonList(limit, currentOffset)
                Log.e("PokemonList", "$list")
                val details = list.map { item ->
                    async {
                        val detail = repository.getRemotePokemonDetail(item.name)
                        item.name to detail
                    }
                }.awaitAll()
                errorMessage.value = null
                strongestPokemon.value = null
                pokemonList.addAll(details)

                currentOffset += limit
                Log.e("CurrentOffset", "$currentOffset")
                if(currentOffset > maxPokemonCount){
                    currentOffset = 0
                    hasReachedTheEnd = true
                }

            } catch (e: java.io.IOException){
                errorMessage.value = "Check network connection!"
                Log.e("loadPokemonList", "Не удалось загрузить покемонов. Проверьте соединение.")
            } finally {
                isLoading.value = false
                firstLoading.value = false
            }
        }
    }

    fun loadRandomPokemonList(){
        hasReachedTheEnd = false
        initialRandomOffset = (0..maxOffset).random()
        errorMessage.value = null

        if(isLoading.value) return
        isLoading.value = true
        randomLoading.value = true

        viewModelScope.launch {
            try {
                val list = repository.getRemotePokemonList(limit, initialRandomOffset)
                Log.e("RandomPokemonList", "$list")
                val details = list.map { item ->
                    async {
                        val detail = repository.getRemotePokemonDetail(item.name)
                        item.name to detail
                    }
                }.awaitAll()
                pokemonList.clear()
                strongestPokemon.value = null
                pokemonList.addAll(details)

                currentOffset = (initialRandomOffset + limit)
                Log.e("CurrentOffset", "$currentOffset")
            }catch (e: java.io.IOException){
                errorMessage.value = "Check network connection!"
                Log.e("loadRandomPokemonList", "Не удалось загрузить покемонов. Проверьте соединение.")
            } finally {
                isLoading.value = false
                randomLoading.value = false
            }
        }
    }

    fun findStrongestPokemon(
        list: List<Pair<String, PokemonDetail>>,
        useAttack: Boolean = false,
        useDefense: Boolean = false,
        useHP: Boolean = false
    ){
        if (!useAttack && !useDefense && !useHP) return

         val strongest = list.maxByOrNull { (_, detail) ->
            var score = 0
            detail.stats.forEach{
                when (it.stat.name){
                    "attack" -> if(useAttack) score += it.baseStat
                    "defense" -> if (useDefense) score += it.baseStat
                    "hp" -> if (useHP) score += it.baseStat
                }
            }
           score
        }

        strongest?.let {
            strongestPokemon.value = strongest
            pokemonList.remove(strongest)
            pokemonList.add(0, strongest)
        }
    }
}