package com.example.pokeclient.data.repository

import com.example.pokeclient.data.api.ApiClient
import com.example.pokeclient.data.api.PokeApiService
import com.example.pokeclient.data.api.PokemonDetail
import com.example.pokeclient.data.api.PokemonListItem
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val api: PokeApiService
){

    suspend fun getPokemonList(limit: Int, offset: Int): List<PokemonListItem>{
        return api.getPokemonList(limit, offset).results
    }

    suspend fun getPokemonDetail(name: String): PokemonDetail{
        return api.getPokemonDetail(name)
    }
}