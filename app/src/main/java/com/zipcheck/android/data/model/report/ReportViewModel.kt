package com.zipcheck.android.data.model.report

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate

sealed interface SubmitState {
    data object Idle : SubmitState
    data object Loading : SubmitState
    data class Success(val page: ReportPage) : SubmitState
    data class Error(val message: String) : SubmitState
}

class ReportViewModel(app: Application) : AndroidViewModel(app) {

    // 간단한 Retrofit 빌드(프로젝트 기존 클라이언트가 있으면 그걸 주입)
    private val api: ReportApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://YOUR.BASE.URL/") // TODO: 교체
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReportApi::class.java)
    }
    private val repo = ReportRepository(api, app.applicationContext)

    private val _form = MutableStateFlow(ReportForm())
    val form: StateFlow<ReportForm> = _form

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState

    // ---- setters (각 화면에서 호출) ----
    fun setAddress(addr: String)      { _form.value = _form.value.copy(addr = addr) }
    fun setAddrDetail(detail: String) { _form.value = _form.value.copy(addrDetail = detail) }
    fun setClassification(v: Int)     { _form.value = _form.value.copy(classification = v) }
    fun setContractType(v: Int)       { _form.value = _form.value.copy(contractType = v) }
    fun setContractAt(d: LocalDate)   { _form.value = _form.value.copy(contractAt = d) }
    fun setRecognizedAt(d: LocalDate) { _form.value = _form.value.copy(recognizedAt = d) }
    fun setContent(c: String)         { _form.value = _form.value.copy(content = c) }
    fun setEvidence(uri: Uri?)        { _form.value = _form.value.copy(evidencePdf = uri) }

    fun canProceedFirst(): Boolean = with(_form.value) {
        addr.isNotBlank() && addrDetail.isNotBlank()
        // + SearchFirstScreen에서 요구하는 추가 항목이 있다면 여기에 조건 추가
    }

    fun submit(page: Int = 0, size: Int = 10) {
        val f = _form.value
        val missing = when {
            f.addr.isBlank() -> "주소"
            f.classification == null -> "사기 분류"
            f.contractType == null -> "계약 형태"
            f.contractAt == null -> "계약 일자"
            f.recognizedAt == null -> "사기 인지 일자"
            f.content.isBlank() -> "피해 상황"
            else -> null
        }
        if (missing != null) {
            _submitState.value = SubmitState.Error("$missing 를 입력해주세요.")
            return
        }

        viewModelScope.launch {
            _submitState.value = SubmitState.Loading
            runCatching {
                repo.submitReport(
                    addr = f.addr,
                    addrDetail = f.addrDetail,
                    classification = f.classification!!,
                    contractType = f.contractType!!,
                    content = f.content,
                    contractAtEpochDay = f.contractAt!!.toEpochDay(),
                    recognizedAtEpochDay = f.recognizedAt!!.toEpochDay(),
                    evidencePdf = f.evidencePdf,
                    page = page,
                    size = size
                )
            }.onSuccess { pageData ->
                _submitState.value = SubmitState.Success(pageData)
            }.onFailure {
                _submitState.value = SubmitState.Error(it.message ?: "신고 접수 실패")
            }
        }
    }
}