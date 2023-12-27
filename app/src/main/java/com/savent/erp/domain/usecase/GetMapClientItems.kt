package com.savent.erp.domain.usecase

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.local.model.ClientEntity
import com.savent.erp.domain.repository.ClientsRepository
import com.savent.erp.presentation.ui.model.MapClientItem
import com.savent.erp.utils.NameFormat
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class GetMapClientItems(private val clientsRepository: ClientsRepository) {

    operator fun invoke(): Flow<Resource<List<MapClientItem>>> = flow {
        clientsRepository.getClients("").onEach {
            if (it is Resource.Success) it.data?.let { it1 ->

                emit(Resource.Success(it1.filter { clientEntity ->
                    !((clientEntity.location?.latitude == null
                            && clientEntity.location?.longitude == null) ||
                            (clientEntity.location.latitude == 0.0
                                    && clientEntity.location.longitude == 0.0))

                }.map { clientFilter -> mapToUiItem(clientFilter) }))
            } ?: emit(Resource.Error<List<MapClientItem>>(resId = R.string.get_clients_error))
            else emit(Resource.Error<List<MapClientItem>>(resId = R.string.get_clients_error))
        }.collect()
    }

    fun mapToUiItem(clientEntity: ClientEntity): MapClientItem {
        return MapClientItem(
            clientEntity.tradeName?.let { NameFormat.format(it) }
                ?: clientEntity.name?.let { NameFormat.format(it) } ?: "",
            clientEntity.location ?: LatLng(0.0, 0.0)
        )
    }
}