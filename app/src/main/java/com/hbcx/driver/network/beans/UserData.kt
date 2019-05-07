package com.hbcx.driver.network.beans

data class Appraise(val evaluateScore:Double,val createTime:Long,val content:String,val nickName:String,
                    val licensePlate:String,val carColor:String,val modelName:String,val brandName:String,
                    val score:Int,val remark:String)
data class Evaluate(val attitudeScore:Int,val hygiene:Int,val punctuality:Int,val serviceScore:Int
                    ,val facilities:Int,val totalScore:Double,val evaluateList:ArrayList<Appraise>)
