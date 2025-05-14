package com.olehmaliuta.clothesadvisor.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.olehmaliuta.clothesadvisor.R
import com.olehmaliuta.clothesadvisor.data.http.HttpServiceManager
import com.olehmaliuta.clothesadvisor.data.http.responses.BaseResponse
import com.olehmaliuta.clothesadvisor.data.http.security.AuthState
import com.olehmaliuta.clothesadvisor.data.http.services.UserApiService
import com.olehmaliuta.clothesadvisor.data.database.repositories.ClothingItemDaoRepository
import com.olehmaliuta.clothesadvisor.data.database.repositories.OutfitDaoRepository
import com.olehmaliuta.clothesadvisor.snackbar.SnackbarManager
import com.olehmaliuta.clothesadvisor.utils.LocaleConstants
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class AuthViewModel(
    private val clothingItemDaoRepository: ClothingItemDaoRepository,
    private val outfitDaoRepository: OutfitDaoRepository,
    context: Context
) : ViewModel() {
    class Factory(
        private val clothingItemDaoRepository: ClothingItemDaoRepository,
        private val outfitDaoRepository: OutfitDaoRepository,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(
                    clothingItemDaoRepository,
                    outfitDaoRepository,
                    context
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val service = HttpServiceManager.buildService(UserApiService::class.java)
    private val snackbarManager = SnackbarManager.getInstance()
    private val sharedPref = context.getSharedPreferences("user", Context.MODE_PRIVATE)

    var authState = mutableStateOf<AuthState>(AuthState.Loading)

    fun profile(
        context: Context
    ) {
        viewModelScope.launch {
            authState.value = AuthState.Loading

            try {
                val token = sharedPref.getString("token", null)
                val tokenType = sharedPref.getString("token_type", null)
                val synchronizedAt = sharedPref.getString("synchronized_at", null)

                if (token == null) {
                    authState.value = AuthState.Unauthenticated
                    return@launch
                }

                val profileResponse = service.profile(
                    "${tokenType ?: "bearer"} $token")

                if (profileResponse.isSuccessful) {
                    val profileBody = profileResponse.body()

                    if (
                        synchronizedAt == null ||
                        synchronizedAt != profileBody?.synchronizedAt
                    ) {
                        val synchronizeResponse = service.synchronize(
                            token = "${tokenType ?: "bearer"} $token",
                            clothingItems = ""
                                .toRequestBody("text/plain".toMediaTypeOrNull()),
                            clothingCombinations = ""
                                .toRequestBody("text/plain".toMediaTypeOrNull()),
                            files = null,
                            isServerToLocal = true.toString()
                                .toRequestBody("text/plain".toMediaTypeOrNull())
                        )

                        if (synchronizeResponse.isSuccessful) {
                            val synchronizedBody = synchronizeResponse.body()

                            clothingItemDaoRepository.deleteAllRows()
                            outfitDaoRepository.deleteAllRows()

                            clothingItemDaoRepository.insertEntities(
                                synchronizedBody?.data?.items?.map {
                                        el -> return@map el.toClothingItemDbEntity()
                                } ?: emptyList()
                            )

                            outfitDaoRepository.insertOutfitsWithItemsByHttpResponse(
                                synchronizedBody?.data?.combinations ?:
                                emptyList()
                            )

                            sharedPref.edit {
                                putString("synchronized_at",
                                    synchronizedBody?.synchronizedAt)
                            }
                        } else {
                            val errorBody = Gson().fromJson(
                                synchronizeResponse.errorBody()?.string(),
                                BaseResponse::class.java)
                            authState.value = AuthState.Error(
                                LocaleConstants.getString(
                                    errorBody.detail.toString()))
                            return@launch
                        }
                    }

                    authState.value =
                        AuthState.Authenticated(profileBody?.data)
                } else if (profileResponse.code() == 401) {
                    logOut()
                    snackbarManager.queueMessage(
                        SnackbarManager.SnackbarMessage(
                            context.getString(R.string.error__session_expired_message)
                        )
                    )
                } else {
                    val errorBody = Gson().fromJson(
                        profileResponse.errorBody()?.string(),
                        BaseResponse::class.java)
                    authState.value = AuthState.Error(
                        errorBody.detail.toString())
                }
            } catch (_: IOException) {
                authState.value = AuthState.Error(
                    context.getString(R.string.error__io_message)
                )
            } catch (e: Exception) {
                authState.value = AuthState.Error("Error: ${e.message}")
            }
        }
    }

    fun logOut() {
        sharedPref.edit {
            remove("token")
            remove("token_type")
            remove("synchronized_at")
        }
        authState.value = AuthState.Unauthenticated
    }
}