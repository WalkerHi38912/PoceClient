package com.example.pokeclient.data.api

import com.google.gson.annotations.SerializedName

data class PokemonListResponse(
    val results: List<PokemonListItem>
)

data class PokemonListItem(
    val name: String,
    val url: String
)

data class PokemonDetail(
    val sprites: Sprites, //Изображение поекмона, выбрал недефолтное
    val height: Int,
    val weight: Int,
    val types: List<TypeSlot>,
    val stats: List<Stat>
)

data class Sprites(
    @SerializedName("front_shiny") val frontShiny: String?
)

data class TypeSlot(
    val type: Type
)

data class Type(
    val name: String
)

data class Stat(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: StatName
)

data class StatName(
    val name: String
)