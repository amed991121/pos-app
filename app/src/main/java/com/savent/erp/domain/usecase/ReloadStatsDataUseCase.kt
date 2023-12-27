package com.savent.erp.domain.usecase

import com.savent.erp.domain.repository.StatsRepository
import com.savent.erp.utils.Resource
import kotlinx.coroutines.flow.first

class ReloadStatsDataUseCase(private val statsRepository: StatsRepository) {

    suspend operator fun invoke(businessId: Int): Resource<Int> =
        statsRepository.fetchStats(businessId)



}