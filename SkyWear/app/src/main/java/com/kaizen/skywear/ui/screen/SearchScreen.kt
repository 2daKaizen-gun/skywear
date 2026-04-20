package com.kaizen.skywear.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kaizen.skywear.R
import com.kaizen.skywear.data.model.City
import com.kaizen.skywear.ui.viewmodel.CitySearchViewModel
import com.kaizen.skywear.ui.viewmodel.WeatherViewModel

// KR / JP 도시 검색 화면

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    searchViewModel: CitySearchViewModel,
    weatherViewModel: WeatherViewModel
) {
    val krQuery by searchViewModel.krSearchQuery.collectAsState()
    val jpQuery by searchViewModel.jpSearchQuery.collectAsState()
    val krResults by searchViewModel.krSearchResults.collectAsState()
    val jpResults by searchViewModel.jpSearchResults.collectAsState()
    val savedKrCity by searchViewModel.savedKrCity.collectAsState()
    val savedJpCity by searchViewModel.savedJpCity.collectAsState()

    var activeTab by remember { mutableIntStateOf(1) } // 0 = KR, 1 = JP

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.search_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 탭 (KR / JP)
            TabRow(selectedTabIndex = activeTab) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text(stringResource(R.string.search_tab_kr)) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text(stringResource(R.string.search_tab_jp)) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (activeTab == 0) {
                // KR 검색
                CitySearchContent(
                    query = krQuery,
                    onQueryChange = { searchViewModel.searchKrCity(it) },
                    cities = krResults,
                    savedCity = savedKrCity,
                    placeholder = stringResource(R.string.search_hint_kr),
                    onCitySelected = { city ->
                        searchViewModel.selectKrCity(city, weatherViewModel)
                        onBack()
                    }
                )
            } else {
                // JP 검색
                CitySearchContent(
                    query = jpQuery,
                    onQueryChange = { searchViewModel.searchJpCity(it) },
                    cities = jpResults,
                    savedCity = savedJpCity,
                    placeholder = stringResource(R.string.search_hint_jp),
                    onCitySelected = { city ->
                        searchViewModel.selectJpCity(city, weatherViewModel)
                        onBack()
                    }
                )
            }
        }
    }
}

// 도시 검색 콘텐츠
@Composable
private fun CitySearchContent(
    query: String,
    onQueryChange: (String) -> Unit,
    cities: List<City>,
    savedCity: String,
    placeholder: String,
    onCitySelected: (City) -> Unit
) {
    Column(Modifier.fillMaxSize()) {

        // 검색 바
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 현재 선택된 도시 표시
        Text(
            text = stringResource(R.string.search_selected, savedCity),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 도시 목록
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cities) { city ->
                CityItem(
                    city = city,
                    isSelected = city.nameEn == savedCity,
                    onClick = { onCitySelected(city) }
                )
            }
        }

    }
}

// 도시 아이템
@Composable
private fun CityItem(
    city: City,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = city.emoji, style = MaterialTheme.typography.titleLarge)
                Column {
                    Text(
                        text = city.nameKo,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = city.nameEn,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (isSelected) {
                Text(
                    text = stringResource(R.string.search_selected_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}