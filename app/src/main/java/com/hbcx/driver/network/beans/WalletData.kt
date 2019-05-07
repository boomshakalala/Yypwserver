package com.hbcx.driver.network.beans

import com.hbcx.driver.R

data class IncomeData(val totalMoney:Double,val incomeDetailsList:ArrayList<IncomeDetail>)
data class IncomeDetail(val id:Int,val remark:String,val money:Double,val createTime:Long)
data class WithdrawHistory(val money:Double,val createTime:Long,val status:Int){
    fun getStatusStr() = when(status){
        1->"提现中"
        2->"已完成"
        3->"已驳回"
        else->""
    }
    fun getStatusColorRes() = if (status == 1) R.color.colorOrange else R.color.grey
}