package com.example.pokeclient.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.MutableSnapshot
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.pokeclient.data.api.PokemonDetail
import com.example.pokeclient.data.api.PokemonListItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


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
    val pokemons = viewModel.pokemonDetails

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Pokemon", color = Color(0xFF4E342E))},
                colors = TopAppBarColors(
                    containerColor = Color(0xFFFFB74D),
                    actionIconContentColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    scrolledContainerColor = Color.Unspecified,
                    titleContentColor = Color(0xFF4E342E)
                )
            )
        },
        floatingActionButton = {

        },
        content = { paddingValues ->
            PokemonContent(pokemons = pokemons, paddingValues)
        }
    )
}

@ExperimentalMaterial3Api
@Composable
fun PokemonContent(pokemons: SnapshotStateMap<String, PokemonDetail>, paddingValues: PaddingValues){

    LazyVerticalGrid (
        columns = GridCells.Fixed(2),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(horizontal = 10.dp)

    ){
        items(pokemons.toList()) { pokemon ->
            PokemonCard(
                pokemon,

            ){

            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun PokemonCard(pokemon: Pair<String, PokemonDetail>, onClick: () -> Unit){
    val gradientColors = listOf(Color(0xFFFFF176), Color(0xFFFFB74D))

    Card (
        modifier = Modifier
            .height(250.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            }
        ,
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardColors(
            containerColor = Color.Unspecified,
            contentColor = Color.Unspecified,
            disabledContentColor = Color.Unspecified,
            disabledContainerColor = Color.Unspecified
        )
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradientColors)),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Box(
                modifier = Modifier
                    .size(200.dp)
            ){
                AsyncImage(
                    model = pokemon.second.sprites.frontShiny,
                    contentDescription = "Pokemon's Image",
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
            Text(
                text = pokemon.first.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF4E342E)
            )
        }
    }
}
