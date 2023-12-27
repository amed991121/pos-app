package com.savent.erp.utils

import com.savent.erp.data.common.model.Employee
import com.savent.erp.data.common.model.MovementReason
import com.savent.erp.data.common.model.Provider
import com.savent.erp.data.local.model.CompanyEntity
import com.savent.erp.data.local.model.MovementEntity
import com.savent.erp.data.local.model.StoreEntity
import com.savent.erp.data.remote.model.Company
import com.savent.erp.data.remote.model.Movement
import com.savent.erp.data.remote.model.Store
import com.savent.erp.presentation.ui.model.EmployeeItem
import com.savent.erp.presentation.ui.model.MovementReasonItem
import com.savent.erp.presentation.ui.model.ProviderItem
import java.text.SimpleDateFormat

class Mappers {
    class Repos {
        companion object {
            val company = fun(company: Company) =
                CompanyEntity(
                    company.id,
                    company.name
                )

            val store = fun(store: Store) =
                StoreEntity(
                    store.id,
                    store.name,
                    store.companyId
                )

            val movement: RepoMapper<MovementEntity, Movement> =
                RepoMapper(
                    local = fun(movement: Movement) =
                        MovementEntity(
                            0,
                            movement.id,
                            movement.type,
                            movement.employeeId,
                            movement.reasonId,
                            movement.providerId,
                            movement.purchaseId,
                            movement.selectedProducts,
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                                .parse("${movement.dateTimestamp.date} ${movement.dateTimestamp.time}").time,
                            movement.inputStoreId,
                            movement.outputStoreId,
                            movement.inputStoreKeeperId,
                            movement.outputStoreKeeperId,
                            movement.units,
                            PendingRemoteAction.COMPLETED
                        ),
                    network = fun(entity: MovementEntity) =
                        Movement(
                            entity.id,
                            entity.type,
                            entity.employeeId,
                            entity.reasonId,
                            entity.providerId,
                            entity.purchaseId,
                            entity.selectedProducts,
                            DateTimeObj(
                                DateFormat.format(entity.dateTimestamp, "yyyy-MM-dd"),
                                DateFormat.format(entity.dateTimestamp, "HH:mm:ss.SSS")
                            ),
                            entity.storeInputId,
                            entity.storeOutputId,
                            entity.inputStoreKeeperId,
                            entity.outputStoreKeeperId,
                            entity.units
                        )
                )
        }
    }

    class Ui {
        companion object {
            val employee = fun(employee: Employee) =
                EmployeeItem(
                    employee.id,
                    NameFormat.format(
                        "${employee.name} ${employee.paternalName} ${employee.maternalName}"
                    )
                )
            val provider = fun(provider: Provider) =
                ProviderItem(
                    provider.id,
                    NameFormat.format(
                        provider.tradeName
                    )
                )
            val reason = fun(reason: MovementReason) =
                MovementReasonItem(
                    reason.id,
                    NameFormat.format(
                        reason.name
                    ),
                    reason.type
                )
        }
    }
}