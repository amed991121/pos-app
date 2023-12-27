package com.savent.erp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.savent.erp.data.common.model.*
import com.savent.erp.data.local.database.dao.*
import com.savent.erp.data.local.model.*
import com.savent.erp.data.remote.model.Movement
import com.savent.erp.utils.Converters

@Database(
    entities = [
        CompanyEntity::class,
        StoreEntity::class,
        ClientEntity::class,
        ProductEntity::class,
        Discount::class,
        SaleEntity::class,
        IncompletePaymentEntity::class,
        DebtPaymentEntity::class,
        MovementEntity::class,
        MovementReason::class,
        Employee::class,
        Provider::class,
        Purchase::class,
        StatEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun companyDao(): CompanyDao
    abstract fun storeDao(): StoreDao
    abstract fun clientDao(): ClientDao
    abstract fun productDao(): ProductDao
    abstract fun discountDao(): DiscountsDao
    abstract fun saleDao(): SaleDao
    abstract fun incompletePaymentDao(): IncompletePaymentDao
    abstract fun debtPaymentDao(): DebtPaymentDao
    abstract fun movementDao(): MovementDao
    abstract fun movementReasonDao(): MovementReasonDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun providerDao(): ProviderDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun statDao(): StatDao

    /*companion object {
        @Volatile
        private var sINSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return sINSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )   .addCallback(object:Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        }
                    }
                })
                    .build()
                sINSTANCE = instance
                return instance
            }
        }
    }*/
}