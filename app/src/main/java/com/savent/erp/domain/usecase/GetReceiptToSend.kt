package com.savent.erp.domain.usecase

import com.savent.erp.R
import com.savent.erp.domain.repository.BusinessBasicsRepository
import com.savent.erp.domain.repository.ClientsRepository
import com.savent.erp.domain.repository.ProductsRepository
import com.savent.erp.domain.repository.SalesRepository
import com.savent.erp.presentation.ui.model.Contact
import com.savent.erp.presentation.ui.model.SharedReceipt
import com.savent.erp.utils.*
import kotlinx.coroutines.flow.first


class GetReceiptToSend(
    private val businessBasicsRepository: BusinessBasicsRepository,
    private val clientsRepository: ClientsRepository,
    private val salesRepository: SalesRepository,
    private val productsRepository: ProductsRepository,
) {
    suspend operator fun invoke(saleId: Int): Resource<SharedReceipt> {

        val businessBasics = businessBasicsRepository.getBusinessBasics().first()
        if (businessBasics is Resource.Error)
            return Resource.Error(resId = R.string.get_business_error)

        val sale = salesRepository.getSale(saleId)
        if (sale is Resource.Error || sale.data == null)
            return Resource.Error(resId = R.string.get_sales_error)

        val products = sale.data.selectedProducts

        val client = clientsRepository.getClientByRemoteId(sale.data.clientId)


        if (client is Resource.Error || client.data == null)
            return Resource.Error(resId = R.string.get_clients_error)

        var clientName = client.data.tradeName?.run {
            if (this.isNotEmpty()) NameFormat.format(this)
            else ""
        }
        if (clientName.isNullOrEmpty())
            clientName = client.data.name?.let {
                NameFormat.format(
                    "${it.trim()} ${(client.data.paternalName ?: "")}"
                )
            } ?: ""


        val subtotal = sale.data.subtotal
        val taxes = sale.data.IVA.plus(sale.data.IEPS)
        val collected = sale.data.collected
        val total = sale.data.total
        val change = if (collected < total) 0F else collected - total
        val isCreditSale = collected < total
        val discounts = sale.data.discounts

        val payMethod = when (sale.data.paymentMethod) {
            PaymentMethod.Credit -> "Tarjeta de Crédito"
            PaymentMethod.Cash -> "Efectivo"
            PaymentMethod.Debit -> "Tarjeta de Débito"
            PaymentMethod.Transfer -> "Transferencia Electrónica"
        }

        val descriptionLength = 22
        val priceLength = 10
        val totalLength = descriptionLength + priceLength
        var totalUnits = 0
        val productsStrBuilder = StringBuilder()
        products.forEach {
            val product = productsRepository.getProductByRemoteId(it.key.toLong()).data
            val name = product?.name ?: ""
            val price = product?.price ?: 0F
            totalUnits += it.value
            val productStr = "${it.value.toString().padEnd(3)}${name}"
            var count = 0
            while (count * descriptionLength < productStr.length) {
                val start = count * descriptionLength
                val end = start + descriptionLength
                val str = try {
                    productStr.subSequence(start, end)
                } catch (e: Exception) {
                    productStr.subSequence(start, productStr.length).padEnd(descriptionLength)
                }
                count++
                if (count * descriptionLength >= productStr.length) {
                    productsStrBuilder.append(str)
                    continue
                }
                productsStrBuilder.append("$str \n")

            }
            productsStrBuilder.append("$${price * it.value}".padStart(priceLength) + "\n")

        }

        val subtotalStrBuilder = StringBuilder("")
        if (subtotal != total) {
            subtotalStrBuilder.append("Subtotal:".padEnd(descriptionLength))
            subtotalStrBuilder.append(
                "$${DecimalFormat.format(subtotal)}".padStart(priceLength) + "\n"
            )
            if (taxes > 0) {
                subtotalStrBuilder.append("Impuestos:".padEnd(descriptionLength))
                subtotalStrBuilder.append(
                    "+$${DecimalFormat.format(taxes)}".padStart(priceLength) + "\n"
                )
            }
            if (discounts > 0) {
                subtotalStrBuilder.append("Descuentos:".padEnd(descriptionLength))
                subtotalStrBuilder.append(
                    "-$${DecimalFormat.format(discounts)}".padStart(priceLength) + "\n"
                )
            }
            subtotalStrBuilder.append("******************************** \n")
        }

        var receiptFooter =
            businessBasics.data?.receiptFooter?.replace("\r", "")
        if (receiptFooter.isNullOrBlank())
            receiptFooter = "Gracias por su compra".alignCenter(totalLength) + " \n" +
                    "Vuelva Pronto \uD83D\uDC4B".alignCenter(totalLength)

        val note = ("\uD83E\uDDFE Recibo de Compra \n".alignCenter(totalLength) +
                "\uD83C\uDFDB ${NameFormat.format(businessBasics.data?.name ?: "")}".alignCenter(
                    totalLength
                ) + " \n" +
                "\uD83D\uDCCD ${NameFormat.format(businessBasics.data?.address ?: "")}".alignCenter(
                    totalLength
                ) + " \n" +
                "\uD83C\uDFE0 ${NameFormat.format(businessBasics.data?.storeName ?: "")} ".alignCenter(
                    totalLength
                ) + " \n" +
                "\uD83D\uDDD3 ${
                    DateFormat.format(
                        System.currentTimeMillis(),
                        "dd/MM/yyyy hh:mm a"
                    )
                } ".alignCenter(totalLength) + " \n" +
                "******************************** \n" +
                "Cant. Descripcion        Importe \n" +
                "******************************** \n" +
                productsStrBuilder.toString() +
                "******************************** \n" +
                "$totalUnits".padEnd(3) + "Arcticulos \n" +
                "******************************** \n" +
                subtotalStrBuilder.toString() +
                "Total:".padEnd(descriptionLength) +
                "$${DecimalFormat.format(total)}".padStart(priceLength) + "\n" +
                "Su Pago:".padEnd(descriptionLength) +
                "$${DecimalFormat.format(collected)}".padStart(priceLength) + "\n" +
                "Su Cambio:".padEnd(descriptionLength) +
                "$${DecimalFormat.format(change)}".padStart(priceLength) + "\n" +
                "******************************** \n" +
                "Método de Pago:".alignCenter(totalLength) + " \n" +
                payMethod.alignCenter(totalLength) + " \n" +
                "******************************** \n" +
                if (isCreditSale) " * Venta a crédito * \n".alignCenter(totalLength)
                        + "******************************** \n"
                else {
                    ""
                } +
                "Firma del Cliente:".alignCenter(totalLength) + " \n \n \n" +
                //"******************** \n" +
                clientName.alignCenter(totalLength).truncate(totalLength)+ " \n" +
                "******************************** \n" +
                receiptFooter).uppercase()



        client.data.let {
            val contact = Contact(it.phoneNumber, it.email)
            return Resource.Success(SharedReceipt(note, contact))
        }

    }

    private fun String.alignCenter(totalLength: Int): String {
        val lenStart = (totalLength - length) / 2
        return if (lenStart > 0) padStart(lenStart + length)
        else this
    }

    private fun String.truncate(totalLength: Int): String{
        return if(length <= totalLength) this
        else substring(0,totalLength-1)
    }
}