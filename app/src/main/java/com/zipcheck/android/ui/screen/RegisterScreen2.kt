package com.zipcheck.android.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.zipcheck.android.R
import com.zipcheck.android.ui.component.CustomTopBar
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.MainBlue
import com.zipcheck.android.ui.theme.PlaceholderGray
import com.zipcheck.android.ui.theme.TextFieldBorderGray
import com.zipcheck.android.ui.theme.Gray
import com.zipcheck.android.ui.theme.White
import com.zipcheck.android.ui.theme.ZipcheckfrontTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.io.path.name

// 재사용 가능한 클릭 가능한 입력 필드 (Text + Dropdown/Calendar Icon)
@Composable
fun ClickableField(
    label: String,
    value: String,
    placeholderText: String,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null, // 아이콘을 위한 파라미터 추가
    onClick: () -> Unit
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (isRequired) {
                Text(
                    text = " *",
                    color = MainBlue,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, TextFieldBorderGray, RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon?.invoke() // 아이콘 렌더링
                if (leadingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 텍스트 사이 간격
                }
                Text(
                    text = if (value.isNotEmpty()) value else placeholderText,
                    color = if (value.isNotEmpty()) Color.Black else PlaceholderGray,
                    fontSize = 16.sp
                )
                // 드롭다운 아이콘 제거 (캘린더 아이콘이 대신함)
            }
        }
    }
}

// 사기 분류/계약 형태 선택용 BottomSheet
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TypeSelectorBottomSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    initialSelection: String = ""
) {
    if (isVisible) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val coroutineScope = rememberCoroutineScope()
        val itemHeight = 50.dp // 각 항목의 높이
        val visibleItems = 5 // 화면에 보이는 항목 수 (홀수 권장)
        val halfVisibleItems = visibleItems / 2

        // 초기 선택된 항목의 인덱스를 찾아 스크롤 위치 초기화
        val initialIndex = if (initialSelection.isNotEmpty()) {
            options.indexOf(initialSelection).coerceAtLeast(0)
        } else {
            0
        }

        val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex.minus(halfVisibleItems).coerceAtLeast(0))

        var selectedItem by remember { mutableStateOf(initialSelection.ifEmpty { options.firstOrNull() ?: "" }) }

        LaunchedEffect(listState) {
            snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
                .debounce(100L) // 스크롤 멈춘 후 약간의 딜레이
                .collect { (index, offset) ->
                    // 스크롤이 멈췄을 때 중앙에 가장 가까운 아이템을 선택
                    val centerIndex = index + halfVisibleItems
                    if (centerIndex >= 0 && centerIndex < options.size) {
                        selectedItem = options[centerIndex]
                    }
                }
        }


        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight * visibleItems)
                ) {

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // 상단 패딩용 빈 아이템
                        items(halfVisibleItems) {
                            Spacer(modifier = Modifier.height(itemHeight))
                        }

                        items(options) { option ->
                            val isSelected = (option == selectedItem)
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) Black else PlaceholderGray, label = ""
                            )
                            val textFontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(itemHeight)
                                    .clickable {
                                        selectedItem = option
                                        val index = options.indexOf(option)
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(index.minus(halfVisibleItems).coerceAtLeast(0))
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = textFontWeight,
                                    color = textColor
                                )
                            }
                        }

                        // 하단 패딩용 빈 아이템
                        items(halfVisibleItems) {
                            Spacer(modifier = Modifier.height(itemHeight))
                        }
                    }
                }


                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("취소", color = Black, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSelect(selectedItem)
                            onDismissRequest()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("확인", color = White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DatePickerBottomSheet(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    initialDate: LocalDate? = null
) {
    if (isVisible) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val coroutineScope = rememberCoroutineScope()
        val today = LocalDate.now()
        val defaultDate = initialDate ?: today

        val itemHeight = 50.dp // 각 항목의 높이
        val visibleItems = 5 // 화면에 보이는 항목 수 (홀수 권장)
        val halfVisibleItems = visibleItems / 2

        val years = (1900..today.year + 5).toList() // 1900년부터 현재 +5년까지
        val months = (1..12).toList()
        val days = (1..31).toList() // 임시로 31일, 실제로는 월에 따라 조정

        val yearListState = rememberLazyListState(initialFirstVisibleItemIndex = years.indexOf(defaultDate.year).minus(halfVisibleItems).coerceAtLeast(0))
        val monthListState = rememberLazyListState(initialFirstVisibleItemIndex = months.indexOf(defaultDate.monthValue).minus(halfVisibleItems).coerceAtLeast(0))
        val dayListState = rememberLazyListState(initialFirstVisibleItemIndex = days.indexOf(defaultDate.dayOfMonth).minus(halfVisibleItems).coerceAtLeast(0))

        var selectedYear by remember { mutableStateOf(defaultDate.year) }
        var selectedMonth by remember { mutableStateOf(defaultDate.monthValue) }
        var selectedDay by remember { mutableStateOf(defaultDate.dayOfMonth) }

        // 스크롤 시 선택된 값 업데이트
        LaunchedEffect(yearListState) {
            snapshotFlow { yearListState.firstVisibleItemIndex to yearListState.firstVisibleItemScrollOffset }
                .debounce(100L)
                .collect { (index, offset) ->
                    val centerIndex = index + halfVisibleItems
                    if (centerIndex >= 0 && centerIndex < years.size) {
                        selectedYear = years[centerIndex]
                    }
                }
        }
        LaunchedEffect(monthListState) {
            snapshotFlow { monthListState.firstVisibleItemIndex to monthListState.firstVisibleItemScrollOffset }
                .debounce(100L)
                .collect { (index, offset) ->
                    val centerIndex = index + halfVisibleItems
                    if (centerIndex >= 0 && centerIndex < months.size) {
                        selectedMonth = months[centerIndex]
                    }
                }
        }
        LaunchedEffect(dayListState) {
            snapshotFlow { dayListState.firstVisibleItemIndex to dayListState.firstVisibleItemScrollOffset }
                .debounce(100L)
                .collect { (index, offset) ->
                    val centerIndex = index + halfVisibleItems
                    if (centerIndex >= 0 && centerIndex < days.size) { // TODO: 월에 따른 일자 범위 조정 필요
                        selectedDay = days[centerIndex]
                    }
                }
        }


        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 년/월/일 휠 피커 UI
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight * visibleItems) // 보이는 항목 수에 따라 높이 조정
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 년도 피커
                        WheelPicker(
                            listState = yearListState,
                            items = years,
                            coroutineScope = coroutineScope,
                            itemHeight = itemHeight,
                            visibleItems = visibleItems,
                            onItemSelected = { selectedYear = it }
                        ) { year, isSelected ->
                            Text(
                                text = "${year}년",
                                fontSize = 18.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Black else PlaceholderGray
                            )
                        }

                        // 월 피커
                        WheelPicker(
                            listState = monthListState,
                            items = months,
                            coroutineScope = coroutineScope,
                            itemHeight = itemHeight,
                            visibleItems = visibleItems,
                            onItemSelected = { selectedMonth = it }
                        ) { month, isSelected ->
                            Text(
                                text = "${month}월",
                                fontSize = 18.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Black else PlaceholderGray
                            )
                        }

                        // 일 피커
                        // TODO: 월에 따라 일자 수 동적으로 변경
                        val currentDaysInMonth = try {
                            LocalDate.of(selectedYear, selectedMonth, 1).lengthOfMonth()
                        } catch (e: Exception) {
                            31 // 기본값
                        }
                        val currentDays = (1..currentDaysInMonth).toList()

                        LaunchedEffect(selectedYear, selectedMonth) {
                            // 월이 바뀌면 선택된 '일'이 유효한지 확인하고, 필요하면 조정 (예: 2월 30일 -> 2월 28일)
                            if (selectedDay > currentDaysInMonth) {
                                selectedDay = currentDaysInMonth
                                coroutineScope.launch {
                                    dayListState.animateScrollToItem(currentDaysInMonth.minus(1 + halfVisibleItems).coerceAtLeast(0))
                                }
                            } else {
                                coroutineScope.launch {
                                    dayListState.animateScrollToItem(selectedDay.minus(1 + halfVisibleItems).coerceAtLeast(0))
                                }
                            }
                        }

                        WheelPicker(
                            listState = dayListState,
                            items = currentDays,
                            coroutineScope = coroutineScope,
                            itemHeight = itemHeight,
                            visibleItems = visibleItems,
                            onItemSelected = { selectedDay = it }
                        ) { day, isSelected ->
                            Text(
                                text = "${day}일",
                                fontSize = 18.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Black else PlaceholderGray
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("취소", color = Black, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val finalDate = LocalDate.of(selectedYear, selectedMonth, selectedDay)
                            onDateSelected(finalDate)
                            onDismissRequest()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("확인", color = White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

// 휠 피커 제네릭 컴포넌트
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> WheelPicker(
    listState: LazyListState,
    items: List<T>,
    coroutineScope: CoroutineScope,
    itemHeight: Dp,
    visibleItems: Int,
    onItemSelected: (T) -> Unit,
    content: @Composable (item: T, isSelected: Boolean) -> Unit
) {
    val halfVisibleItems = visibleItems / 2
    val density = LocalDensity.current

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .debounce(100L) // 스크롤 멈춘 후 약간의 딜레이
            .collect { (index, offset) ->
                val centerIndex = index + halfVisibleItems
                if (centerIndex >= 0 && centerIndex < items.size) {
                    onItemSelected(items[centerIndex])
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .width(100.dp) // 너비는 적절히 조정
            .height(itemHeight * visibleItems),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 상단 패딩용 빈 아이템
        items(halfVisibleItems) {
            Spacer(modifier = Modifier.height(itemHeight))
        }

        items(items) { item ->
            val isSelected by remember {
                derivedStateOf {
                    val layoutInfo = listState.layoutInfo
                    val itemIndex = items.indexOf(item)
                    val visibleItemIndices = layoutInfo.visibleItemsInfo.map { it.index }
                    val currentCenterItem = if (visibleItemIndices.isNotEmpty()) {
                        val firstVisible = listState.firstVisibleItemIndex
                        val offset = listState.firstVisibleItemScrollOffset
                        val scrolledPx = with(density) { itemHeight.toPx() }
                        val scrolledItems = offset / scrolledPx

                        // 중앙에 가장 가까운 아이템 인덱스 계산
                        (firstVisible + scrolledItems + halfVisibleItems).toInt()
                    } else {
                        -1
                    }
                    itemIndex == currentCenterItem
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .clickable {
                        val index = items.indexOf(item)
                        coroutineScope.launch {
                            listState.animateScrollToItem(index.minus(halfVisibleItems).coerceAtLeast(0))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                content(item, isSelected)
            }
        }

        // 하단 패딩용 빈 아이템
        items(halfVisibleItems) {
            Spacer(modifier = Modifier.height(itemHeight))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen2(
    navController: NavHostController,
    // 이전 페이지에서 넘어온 주소 정보
    address: String,
    detailAddress: String
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // 상태 변수
    var fraudType by remember { mutableStateOf("") } // 사기 분류
    var contractType by remember { mutableStateOf("") } // 계약 형태
    var contractDate by remember { mutableStateOf<LocalDate?>(null) } // 계약 일자
    var recognitionDate by remember { mutableStateOf<LocalDate?>(null) } // 사기 인지 일자

    // BottomSheet 상태
    var showFraudTypeSheet by remember { mutableStateOf(false) }
    var showContractTypeSheet by remember { mutableStateOf(false) }
    var showContractDateSheet by remember { mutableStateOf(false) }
    var showRecognitionDateSheet by remember { mutableStateOf(false) }

    // 사기 분류 및 계약 형태 옵션 (두 번째 이미지 참고)
    val propertyOptions = listOf("아파트/다세대", "빌라", "투룸+", "원룸", "오피스텔", "상가")
    // 계약 일자 포맷터
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.KOREA) }

    // 다음 버튼 활성화 조건: 사기 분류, 계약 형태, 계약 일자 3가지가 모두 입력되어야 함
    val isNextButtonEnabled = fraudType.isNotEmpty() && contractType.isNotEmpty() && contractDate != null

    Scaffold(
        containerColor = White,
        topBar = {
            CustomTopBar("사기 등록", navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
        ) {
            // LinearProgressIndicator (2/3 진행률)
            LinearProgressIndicator(
                progress = 2f / 3f, // 두 번째 화면이므로 66% 진행률
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp),
                color = MainBlue,
                trackColor = Gray
            )

            // 사기 정보 입력 텍스트
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = "사기 정보를 입력해주세요.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "멘트가 있으면 좋겠는데 기억이 안나네", // 이미지의 힌트 텍스트
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 입력 필드 섹션
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    // 1. 사기 분류 입력
                    ClickableField(
                        label = "사기 분류",
                        value = fraudType,
                        placeholderText = "사기 분류를 선택하세요",
                        isRequired = true,
                        onClick = { showFraudTypeSheet = true }
                    )
                }
                item {
                    // 2. 계약 형태 입력
                    ClickableField(
                        label = "계약 형태",
                        value = contractType,
                        placeholderText = "계약 형태를 선택하세요",
                        isRequired = true,
                        onClick = { showContractTypeSheet = true }
                    )
                }
                item {
                    // 3. 계약 일자 입력 (캘린더 아이콘 추가)
                    ClickableField(
                        label = "계약 일자",
                        value = contractDate?.format(dateFormatter) ?: "",
                        placeholderText = "계약 일자를 선택하세요",
                        isRequired = true,
                        leadingIcon = {
                            Icon(
                                painterResource(id = R.drawable.ic_calendar), // 캘린더 아이콘
                                contentDescription = null,
                                tint = PlaceholderGray,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        onClick = { showContractDateSheet = true }
                    )
                }
                item {
                    // 4. 사기 인지 일자 입력 (캘린더 아이콘 추가)
                    ClickableField(
                        label = "사기 인지 일자",
                        value = recognitionDate?.format(dateFormatter) ?: "",
                        placeholderText = "사기 인지 일자를 선택하세요",
                        isRequired = false,
                        leadingIcon = {
                            Icon(
                                painterResource(id = R.drawable.ic_calendar), // 캘린더 아이콘
                                contentDescription = null,
                                tint = PlaceholderGray,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        onClick = { showRecognitionDateSheet = true }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) } // 하단 여백 확보
            }

            // 다음 버튼
            Button(
                onClick = {
                    if (isNextButtonEnabled) {
                        // API 연결을 위해 모든 정보 (주소 + 사기 정보)를 다음 페이지로 넘깁니다.
                        val allData = mapOf(
                            "address" to address,
                            "detailAddress" to detailAddress,
                            "fraudType" to fraudType,
                            "contractType" to contractType,
                            "contractDate" to contractDate?.format(dateFormatter),
                            "recognitionDate" to recognitionDate?.format(dateFormatter)
                        )
                        println("Data for API: $allData")
                        // 1. Map 객체를 JSON 문자열로 변환합니다.
                        val gson = Gson()
                        val jsonString = gson.toJson(allData)

                        // 2. JSON 문자열을 URL 경로에 사용하기 위해 인코딩합니다.
                        val encodedJson = URLEncoder.encode(jsonString, StandardCharsets.UTF_8.name())

                        // 3. 인코딩된 JSON을 인자로 하여 다음 화면으로 이동합니다.
                        // "register_screen_3"이 아니라 "register3"일 수 있으니, NavHost에 정의된 라우트 이름을 정확히 사용해야 합니다.
                        navController.navigate("register_screen_3/$encodedJson")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(bottom = 16.dp),
                enabled = isNextButtonEnabled, // 활성화 조건 적용
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isNextButtonEnabled) MainBlue else Gray,
                    disabledContainerColor = Gray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "다음",
                    color = if (isNextButtonEnabled) White else Black, // 활성화 시 화이트, 비활성화 시 블랙
                    fontSize = 18.sp
                )
            }
        }
    }

    // 사기 분류 BottomSheet
    TypeSelectorBottomSheet(
        isVisible = showFraudTypeSheet,
        onDismissRequest = { showFraudTypeSheet = false },
        title = "사기 분류",
        options = propertyOptions,
        onSelect = { fraudType = it },
        initialSelection = fraudType
    )

    // 계약 형태 BottomSheet
    TypeSelectorBottomSheet(
        isVisible = showContractTypeSheet,
        onDismissRequest = { showContractTypeSheet = false },
        title = "계약 형태",
        options = propertyOptions, // 사기 분류와 동일한 옵션을 사용하도록 요청됨
        onSelect = { contractType = it },
        initialSelection = contractType
    )

    // 계약 일자 DatePicker BottomSheet
    DatePickerBottomSheet(
        isVisible = showContractDateSheet,
        onDismissRequest = { showContractDateSheet = false },
        onDateSelected = { contractDate = it },
        initialDate = contractDate
    )

    // 사기 인지 일자 DatePicker BottomSheet
    DatePickerBottomSheet(
        isVisible = showRecognitionDateSheet,
        onDismissRequest = { showRecognitionDateSheet = false },
        onDateSelected = { recognitionDate = it },
        initialDate = recognitionDate
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterInfoScreen() {
    ZipcheckfrontTheme {
        // 프리뷰를 위해 임시 NavController를 사용하며, 주소 정보는 가짜로 전달
        RegisterScreen2(
            navController = rememberNavController(),
            address = "서울시 강남구 테헤란로",
            detailAddress = "123-456"
        )
    }
}