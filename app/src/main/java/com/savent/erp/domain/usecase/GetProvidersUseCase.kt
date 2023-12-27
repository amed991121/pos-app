package com.savent.erp.domain.usecase

import com.savent.erp.data.common.model.Provider
import com.savent.erp.domain.repository.ProvidersRepository
import com.savent.erp.presentation.ui.model.ProviderItem
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class GetProvidersUseCase(
    private val providersRepository: ProvidersRepository,
    private val providerUiMapper: (Provider) -> ProviderItem
) {
    operator fun invoke(query: String): Flow<Resource<List<ProviderItem>>> = flow{
        providersRepository.getProviders(query).onEach { result ->
            if (result is Resource.Error || result.data == null) {
                emit(
                    Resource.Error(
                        result.resId,
                        result.message
                    )
                )
                return@onEach
            }
            emit(Resource.Success(result.data.map(providerUiMapper)))
        }.collect()
    }

}