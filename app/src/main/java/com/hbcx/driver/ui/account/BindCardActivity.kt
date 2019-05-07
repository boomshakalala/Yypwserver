package com.hbcx.driver.ui.account

import android.app.Activity
import android.content.Intent
import cn.sinata.xldutils.rxutils.ResultException
import cn.sinata.xldutils.rxutils.ResultSubscriber
import cn.sinata.xldutils.utils.isValidIdCard
import cn.sinata.xldutils.utils.optInt
import cn.sinata.xldutils.utils.optJsonObj
import cn.sinata.xldutils.utils.optString
import com.google.gson.JsonObject
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_bind_card.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class BindCardActivity: com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_bind_card

    private val name by lazy {
        intent.getStringExtra("name")
    }

    override fun initClick() {
        btn_action.onClick {
            val num = et_car_num.text.toString().trim()
            if (num.isEmpty()){
                toast("请输入银行卡号")
                return@onClick
            }
            val idCard = et_id_card.text.toString()
            if (!idCard.isValidIdCard()){
                toast("请输入正确的身份证号")
                return@onClick
            }
            showDialog()
            HttpManager.getBankInfo(num).subscribe(object :ResultSubscriber<JsonObject>(this@BindCardActivity){
                override fun onNext(t: JsonObject) {
                    dismissDialog()
                    val result = t.optInt("error_code",-1)
                    val data = t.optJsonObj("result")
                    if (result == 0){
                        val bankName = data.optString("bank")
                        startActivityForResult<BindCardConfirmActivity>(1,"name" to name,
                                "carNum" to num,"idCard" to et_id_card.text.toString(),"bankName" to bankName)
                    }else
                        toast(t.optString("reason"))
                }

                override fun onError(t: Throwable) {
                    super.onError(t)
                    dismissDialog()
                    toast("银行卡查询出错")
                }
            })
        }
    }

    override fun initView() {
        title = "绑定银行卡"
        tv_name.text = name
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}