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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.pokeclient.R
import com.example.pokeclient.data.api.PokemonDetail
import dagger.hilt.android.AndroidEntryPoint


@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokemonMainScreen()
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun PokemonMainScreen(
    viewModel: MainViewModel = hiltViewModel()
){
    val pokemonList = viewModel.pokemonList
    val errorMessage by viewModel.errorMessage
    val firstLoading by viewModel.firstLoading
    val randomLoading by viewModel.randomLoading
    val strongestPokemon by viewModel.strongestPokemon

    var useAttack by remember { mutableStateOf(false) }
    var useDefense by remember { mutableStateOf(false) }
    var useHP by remember { mutableStateOf(false) }


    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                   VersionMenu("v1.0.0")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFB74D)
                ),
                actions = {
                    Spacer(Modifier.width(10.dp))
                    Button(
                        modifier = Modifier.height(50.dp),
                        onClick = {viewModel.loadRandomPokemonList()},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFB74D)
                        ),
                        elevation = ButtonDefaults.buttonElevation(2.dp)
                    ) {
                        Text(
                            text = "\uD83C\uDFB2",
                            fontSize = 30.sp
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    StatFilterMenu(
                        useAttack = useAttack,
                        useDefense = useDefense,
                        useHP = useHP,
                        onUseAttackChange = { useAttack = it},
                        onUseDefenseChange = {useDefense = it},
                        onUseHPChange = {useHP = it},
                        onFindStrongest = {
                            viewModel.findStrongestPokemon(
                                list = pokemonList,
                                useAttack = useAttack,
                                useDefense = useDefense,
                                useHP = useHP
                            )
                        }
                    )
                }
            )
        },
        floatingActionButton = {
        },
        content = { paddingValues ->
            if(errorMessage == null && !randomLoading && !firstLoading){
                PokemonContent(
                    pokemonList = pokemonList,
                    strongest = strongestPokemon,
                    paddingValues = paddingValues
                ) {
                    viewModel.loadPokemonList()
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if(errorMessage != null){
                        Text(
                            text = "$errorMessage",
                            color = Color.Red,
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            modifier = Modifier.height(50.dp),
                            onClick = {
                                viewModel.loadPokemonList()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFB74D)
                            )
                        ) {
                            Text(
                                text = "Retry",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    } else {
                        Log.e("Loading", "Loading")
                        CircularProgressIndicator(
                            color = Color(0xFFFFB74D)
                        )
                    }

                }
            }
        }
    )
}

@ExperimentalMaterial3Api
@Composable
fun PokemonContent(pokemonList: List<Pair<String, PokemonDetail>>, strongest: Pair<String, PokemonDetail>?, paddingValues: PaddingValues, onLoadMore: () -> Unit){
    val sheetState = rememberModalBottomSheetState()
    val lazyGridState = rememberLazyGridState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedPokemon by remember { mutableStateOf<Pair<String, PokemonDetail>?>(null) }

    LaunchedEffect(strongest) {
        if (strongest != null){
            lazyGridState.animateScrollToItem(0)
        }
    }

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
        items(pokemonList) { pokemon ->
            val isStrongest = strongest?.first == pokemon.first

            PokemonCard(
                pokemon = pokemon,
                isStrongest = isStrongest,
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
    isStrongest: Boolean = false,
    cardModifier: Modifier,
    showBottomSheet: (Boolean) -> Unit,
) {
    val gradientColors = listOf(Color(0xFFFFF176), Color(0xFFFFB74D))
    val strongestGradientColors = listOf(Color(0xFFFF8A80), Color(0xFFD32F2F))

    val name = pokemon.first.replaceFirstChar { it.uppercase() }
    val isNameLong = name.length > 10

    val imageSize = if (isNameLong) 150.dp else 200.dp
    val fontSize = if (isNameLong) 16.sp else 20.sp

    Card(
        modifier = cardModifier
            .clickable {
                showBottomSheet(true)
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(if (isStrongest) 32.dp else 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Unspecified)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        if (isStrongest) strongestGradientColors else gradientColors
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(imageSize)
            ) {
                AsyncImage(
                    model = pokemon.second.sprites.frontShiny,
                    contentDescription = "Pokemon's Image",
                    modifier = Modifier.fillMaxSize()
                )
            }
            if(showName){
                Text(
                    text = pokemon.first.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = fontSize),
                    color = Color(0xFF4E342E),
                    maxLines = 2
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
        Spacer(Modifier.width(16.dp))
        Column(
            Modifier
                .fillMaxWidth()

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

@Composable
fun StatFilterMenu(
    useAttack: Boolean,
    useDefense: Boolean,
    useHP: Boolean,
    onUseAttackChange: (Boolean) -> Unit,
    onUseDefenseChange: (Boolean) -> Unit,
    onUseHPChange: (Boolean) -> Unit,
    onFindStrongest: () -> Unit
){
    var isExpended by remember { mutableStateOf(false) }

    Button(
        modifier = Modifier.height(50.dp),
        onClick = {isExpended = true},
        colors = ButtonDefaults.buttonColors(Color(0xFFFFB74D)),
        elevation = ButtonDefaults.buttonElevation(2.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.sliders),
            contentDescription = "PokemonFilterButton"
        )
    }

    DropdownMenu(
        expanded = isExpended,
        onDismissRequest = {isExpended = false}
    ) {
        DropdownMenuItem(
            text = {
                Row {
                    Checkbox(
                        checked = useAttack,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFFFB74D)
                        )
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Attack")
                }
            },
            onClick = {onUseAttackChange(!useAttack)}
        )
        DropdownMenuItem(
            text = {
                Row {
                    Checkbox(
                        checked = useDefense,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFFFB74D)
                        )
                        )
                    Spacer(Modifier.width(4.dp))
                    Text("Defense")
                }
            },
            onClick = {onUseDefenseChange(!useDefense)}
        )
        DropdownMenuItem(
            text = {
                Row {
                    Checkbox(
                        checked = useHP,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFFFB74D)
                        )
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("HP")
                }
            },
            onClick = {onUseHPChange(!useHP)}
        )
        HorizontalDivider()
        DropdownMenuItem(
            text = {
                Button(
                    onClick = {
                        isExpended = false
                        onFindStrongest()
                              },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFB74D)
                    ),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Text(
                        text = "Find Strongest"
                    )
                }
            },
            onClick = {

            }
        )
    }
}

@Composable
fun VersionMenu(version: String){
    var isExpended by remember { mutableStateOf(false) }
    Button(
        modifier = Modifier.height(50.dp),
        onClick = {
            isExpended = true
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFB74D)
        )
    ) {
        Text(
            text = "PokeClient",
            fontSize = 30.sp,
            fontWeight = FontWeight.Thin
        )
    }

    DropdownMenu(
        expanded = isExpended,
        onDismissRequest = {isExpended = false}
    ) {
        DropdownMenuItem(
            text = {
                Text(version)
            },
            onClick = {}
        )
    }
}