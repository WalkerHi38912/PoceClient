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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.pokeclient.data.api.PokemonDetail
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
    val errorMessage by viewModel.errorMessage
    val pokemonList = viewModel.pokemonList

    LaunchedEffect (Unit){
        viewModel.loadPokemonList()
    }

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
            Button(
                onClick = {
                    viewModel.loadRandomPokemonList()
                }
            ) {

            }
        },
        content = { paddingValues ->
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxWidth()
                )
            }
            PokemonContent(
                pokemons = pokemonList,
                paddingValues = paddingValues
            ) {
                viewModel.loadPokemonList()
            }
        }
    )
}

@ExperimentalMaterial3Api
@Composable
fun PokemonContent(pokemons: List<Pair<String, PokemonDetail>>, paddingValues: PaddingValues, onLoadMore: () -> Unit){
    val sheetState = rememberModalBottomSheetState()
    val lazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedPokemon by remember { mutableStateOf<Pair<String, PokemonDetail>?>(null) }

    LazyVerticalGrid (
        columns = GridCells.Fixed(2),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        state = lazyGridState,
        modifier = Modifier
            .padding(top = 10.dp)
            .padding(horizontal = 10.dp)

    ){
        items(pokemons) { pokemon ->
            PokemonCard(
                pokemon = pokemon,
                cardModifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ){ isShowBottomSheet ->
                showBottomSheet = isShowBottomSheet
                selectedPokemon = pokemon
            }
        }
    }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = lazyGridState.layoutInfo.totalItemsCount
            lastVisibleItem != null && lastVisibleItem.index >= totalItems - 15
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if(shouldLoadMore.value){
            onLoadMore()
            Log.e("ShouldLoadMore", "${shouldLoadMore.value}")
        }
    }

    if(showBottomSheet){
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            selectedPokemon?.let { pokemon ->
                PokemonDetailCard(pokemon)
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun PokemonCard(
    pokemon: Pair<String, PokemonDetail>,
    showName: Boolean = true,
    cardModifier: Modifier,
    showBottomSheet: (Boolean) -> Unit,
    ){
    val gradientColors = listOf(Color(0xFFFFF176), Color(0xFFFFB74D))

    Card (
        modifier = cardModifier
            .clickable {
                showBottomSheet(true)
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
            if(showName){
                Text(
                    text = pokemon.first.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF4E342E)
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun PokemonDetailCard(pokemon: Pair<String, PokemonDetail>){
    val (name, detail) = pokemon


    Row(
        modifier = Modifier
            .padding(10.dp)
    ){
        PokemonCard(
            pokemon = pokemon,
            showName = false,
            cardModifier = Modifier
                .size(200.dp)
        ) { }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(8.dp))
            Text("Weight: ${detail.weight / 10.00} kg")
            Text("Height: ${detail.height / 10.00} m")
            val types = detail.types.joinToString("/") { it.type.name }
            Text("Type: $types")
            detail.stats.forEach {
                Text("${it.stat.name}: ${it.baseStat}")
            }
        }
    }
}
