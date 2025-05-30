package com.example.pokeclient.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pokeclient.data.api.PokemonListItem
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokemonsMainScreen()
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun PokemonsMainScreen(
    viewModel: MainViewModel = hiltViewModel()
){
    val pokemons = viewModel.pokemonList.toList()

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Pokemon")},
                colors = TopAppBarColors(
                    containerColor = Color.Yellow,
                    actionIconContentColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    scrolledContainerColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified
                )
            )
        },
        floatingActionButton = {

        },
        content = { paddingValues ->
            LazyColumn (
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 10.dp)

            ){
                items(pokemons) { pokemon ->
                    PokemonCard(pokemon)
                }
            }
        }
    )
}

@Composable
fun PokemonCard(pokemon: PokemonListItem){
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(color = Color.LightGray)
            .clip(RoundedCornerShape(8.dp))
    ){
        Text(
            text = pokemon.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
