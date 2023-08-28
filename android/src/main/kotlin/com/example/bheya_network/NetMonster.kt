package com.example.bheya_network

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.bheya_network.models.*
import com.example.bheya_network.models.cdma.getCdma
import com.example.bheya_network.models.gsm.getGsm
import com.example.bheya_network.models.lte.getLte
import com.example.bheya_network.models.nr.getNr
import com.example.bheya_network.models.tdscdma.getTdscdma
import com.example.bheya_network.models.wcdma.getWcdma
import com.example.bheya_network.core.factory.NetMonsterFactory
import com.example.bheya_network.core.model.cell.*
import com.example.bheya_network.core.model.connection.PrimaryConnection
import com.google.gson.Gson
import io.flutter.plugin.common.MethodChannel
import java.util.*
import kotlin.collections.ArrayList


class NetMonster {

    private val TAG = "NetMonster"

    private val primaryCellList: MutableList<CellType> = ArrayList()
    private val neighboringCellList: MutableList<CellType> = ArrayList()
    private val cellDataList: MutableList<CellData> = ArrayList()

    @SuppressLint("MissingPermission")
    fun requestData(
        context: Context,
        result: MethodChannel.Result? = null
    ): CellsResponse {
        NetMonsterFactory.get(context).apply {
            val merged = getCells()
            merged.forEach { cell ->
                val cellData = CellData()
                cellData.timestamp = System.currentTimeMillis()
                Log.d("timestamptimestamp", "requestData: ${cellData.timestamp}")
                when (cell) {

                    is CellNr -> {
                        Log.d(TAG, "requestData: NR")

                        val cellType = CellType()
                        cellType.nr = getNr(cell, cellData)
                        cellType.type = "NR"
                        when (cell.connectionStatus) {
                            is PrimaryConnection -> {
                                primaryCellList.add(cellType)
                            }
                            else -> {
                                neighboringCellList.add(cellType)
                            }
                        }
                        cellDataList.add(cellData)

                    }
                    is CellLte -> {
                        Log.d(TAG, "requestData: LTE")

                        val cellType = CellType()


                        cellType.lte = getLte(cell, cellData)
                        cellType.type = "LTE"
                        when (cell.connectionStatus) {
                            is PrimaryConnection -> {
                                primaryCellList.add(cellType)
                            }
                            else -> {
                                neighboringCellList.add(cellType)
                            }
                        }
                        cellDataList.add(cellData)
                    }
                    is CellWcdma -> {
                        Log.d(TAG, "requestData: WCDMA")

                        val cellType = CellType()


                        cellType.wcdma = getWcdma(cell, cellData)
                        cellType.type = "WCDMA"
                        when (cell.connectionStatus) {
                            is PrimaryConnection -> {
                                primaryCellList.add(cellType)
                            }
                            else -> {
                                neighboringCellList.add(cellType)
                            }
                        }
                        cellDataList.add(cellData)

                    }
                    is CellCdma -> {
                        Log.d(TAG, "requestData: CDMA")

                        val cellType = CellType()


                        cellType.cdma = getCdma(cell, cellData)
                        cellType.type = "WCDMA"
                        when (cell.connectionStatus) {
                            is PrimaryConnection -> {
                                primaryCellList.add(cellType)
                            }
                            else -> {
                                neighboringCellList.add(cellType)
                            }
                        }

                        cellDataList.add(cellData)
                    }
                    is CellGsm -> {
                        Log.d(TAG, "requestData: GSM")


                        val cellType = CellType()


                        cellType.gsm = getGsm(cell, cellData)
                        cellType.type = "GSM"
                        when (cell.connectionStatus) {
                            is PrimaryConnection -> {
                                primaryCellList.add(cellType)
                            }
                            else -> {
                                neighboringCellList.add(cellType)
                            }
                        }
                        cellDataList.add(cellData)
                    }
                    is CellTdscdma -> {
                        Log.d(TAG, "requestData: TDSCDMA")

                        val cellType = CellType()

                        cellType.tdscdma = getTdscdma(cell, cellData)
                        cellType.type = "TDSCDMA"
                        when (cell.connectionStatus) {
                            is PrimaryConnection -> {
                                primaryCellList.add(cellType)
                            }
                            else -> {
                                neighboringCellList.add(cellType)
                            }
                        }
                        cellDataList.add(cellData)
                    }

                    else -> {
                        Log.d(TAG, "requestData: ")
                    }

                }
            }
            Log.d("NTM-RES", " \n${merged.joinToString(separator = "\n")}")
        }

        val cellsResponse = CellsResponse()
        cellsResponse.neighboringCellList = neighboringCellList
        cellsResponse.primaryCellList = primaryCellList
        cellsResponse.cellDataList = cellDataList

        cellDataList.forEach {
            Log.d("it.subscriptionId", "requestData: ${it.type}")
            Log.d("it.subscriptionId", "requestData: ${it.subscriptionId}")
        }



        Log.d(TAG, "requestData: " + Gson().toJson(cellsResponse))
        result?.success(Gson().toJson(cellsResponse))
        return cellsResponse
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun simsInfo(context: Context, result: MethodChannel.Result? = null): ArrayList<SIMInfo> {

        val simInfoLists = ArrayList<SIMInfo>()
        val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val activeSubscriptionInfoList = subscriptionManager.activeSubscriptionInfoList
        for (subscriptionInfo in activeSubscriptionInfoList) {
            val carrierName = subscriptionInfo.carrierName
            val displayName = subscriptionInfo.displayName
            val mcc = subscriptionInfo.mcc
            val mnc = subscriptionInfo.mnc
            val subscriptionInfoNumber = subscriptionInfo.number
            Log.d(TAG, "carrierName: ${carrierName}")
            simInfoLists.add(SIMInfo(carrierName.toString(), displayName.toString(), mcc, mnc, subscriptionInfoNumber))
        }

        val json = Gson().toJson(SIMInfoResponse(simInfoLists))
        Log.d(TAG, "simsInfo: ${json}")
        result?.success(json)
        return simInfoLists
    }
}


