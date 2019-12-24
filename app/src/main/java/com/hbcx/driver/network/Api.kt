package com.hbcx.driver.network

import com.hbcx.driver.BuildConfig

object Api {
        const val BASE_URL = "http://106.116.167.23:80/YunYou/" //外网地址
//    const val BASE_URL = "http:/192.168.1.188:8080/YunYou/" //本地地址

    const val URL = "$BASE_URL/app/public/advertInfo?url="
    const val URL1 = BASE_URL + "app/user/getAppText?url="
    const val URL2 = "$BASE_URL/app/public/bannerInfo?url="
    const val INFO_URL = BASE_URL + "sys/apptext/getAppText?type=%s"
    const val SHARE_URL = "http://106.116.167.23/YunYou/resources/share/down.html?userId="
    const val OSS_END_POINT = "http://oss-cn-beijing.aliyuncs.com"
    const val CALCEL_RULE = "$BASE_URL"
    const val JUHE_URL = "http://v.juhe.cn/bankcardinfo/query" //聚合
    private const val BASE_RULE = BASE_URL + "app/public/getAppText?type="
    const val NOTICE_INFO = BASE_URL + "app/public/noticeInfo?url="


    const val ABOUT = "${BASE_RULE}11" //关于我们
    const val PLATFORM_RULE = "${BASE_RULE}12" //平台协议



    //    const val SOCKET_SERVER = "192.168.3.228"
//    const val SOCKET_SERVER = "106.116.167.23"
    const val SOCKET_SERVER = "192.168.1.4"
    const val SOCKET_PORT = 8888


    const val UP_LOAD_IMG = "/app/public/uplaodImg"
    const val SEND_MSM = "/app/public/sendSms"
    const val CHECK_CODE = "/app/public/checkSms"
    const val GET_AD = "/app/public/getAdvert"
    const val GET_BANNER = "/app/public/getBanner"
    const val LOGIN = "/app/driver/login"
    const val FORGET = "/app/driver/forgetPassword"
    const val UPDATE_PWD = "/app/driver/updatePassword"
    const val CAR_TYPE_LIST = "/app/driver/getCarModelList"
    const val DRIVER_REGISTER = "/app/driver/driverRegist"
    const val PERSON_REGISTER = "/app/driver/personalOperatorRegist" //个人经营者入驻
    const val COMPANY_REGISTER = "/app/driver/companyRegist" //公司入驻
    const val CHANGE_WORK_STATE = "/app/driver/startAndStop"
    const val ROB_ORDER = "/app/fastCar/robOrder"
    const val DRIVER_MAIN = "/app/driver/getFirstPageInfo"
    const val GO_START_ADDRESS = "/app/fastCar/driverDepart" //出发接乘客
    const val ARRIVE_START_ADDRESS = "/app/fastCar/driverArrival" //到达预约地点
    const val START_TRIP = "/app/fastCar/startTrip" //到达预约地点
    const val END_TRIP = "/app/fastCar/endTrip" //到达预约地点
    const val ORDER_DETAIL = "/app/fastCar/getDriverOrderDetail" //订单详情
    const val CANLEL_MONEY = "/app/fastCar/getReassigMoney" //改派金额
    const val CANLEL_ORDER = "/app/fastCar/sendOrderReassig" //改派金额
    const val CAR_ORDER_LIST = "/app/fastCar/getDriverOrderList" //快专车订单列表
    const val DRIVER_HOME = "/app/driver/getPersonalCenterInfo" //司机个人中心
    const val DRIVER_EVALUATE = "/app/fastCar/getDriverInfo" //评价
    const val TICKET_DRIVER_HOME = "/app/driver/getDriverPageInfo" //票务司机个人中心
    const val GET_DRIVER_LIST = "/app/driverTicketing/getPersonDriverList" //司机列表
    const val GET_IDEL_DRIVER_LIST = "/app/driverTicketing/getPersonUnrelatedDriverList" //空闲司机列表
    const val GET_EVALUATE_LIST = "/app/driverTicketing/getDriverEvaluateList" //评价列表
    const val GET_DRIVER_DETAIL = "/app/driverTicketing/getPersonDriverDetail" //司机详情
    const val GET_CAR_DETAIL = "/app/driverTicketing/getPersonCarDetail" //车详情
    const val ADD_DRIVER = "/app/driverTicketing/addPersonDriver" //添加司机
    const val ADD_CAR = "/app/driverTicketing/addPersonCar" //添加车
    const val BIND_DRIVER = "/app/driverTicketing/relationDriver" //关联司机
    const val DEL_DRIVER = "/app/driverTicketing/delPersonDriver" //删除司机
    const val DEL_CAR = "/app/driverTicketing/delPersonCar" //删除车
    const val CAR_LIST = "/app/driverTicketing/getPersonCarList" //车辆列表
    const val WALLET = "/app/driver/getDriverWallet" //我的钱包
    const val BIND_CARD = "/app/driver/bindingBank" //绑银行卡
    const val WITHDRAW_DATA = "/app/driver/getWithdrawalsPageInfo" //提现页面数据
    const val WITHDRAW = "/app/driver/driverWithdrawals" //提现
    const val WITHDRAW_HISTORY = "/app/driver/withdrawList" //提现记录
    const val INCOME_HISTORY = "/app/driver/getIncomeDetailsList" //收入明细
    const val UPDATE_PHONE = "/app/driver/upPhone" //更换手机
    const val FEEDBACK = "/app/user/addFeedBack"
    const val GET_OPEN_CITY = "/app/public/getOpenCity"  //获取开通城市
    const val ENABLE_DAY = "/app/ticketing/getUserCanBuyDay" //可预约天数
    const val TICKET_LIST = "/app/driverTicketing/getDriverShiftList" //服务列表
    const val TICKET_HISTORY_LIST = "/app/driverTicketing/getServiceRecordList" //服务历史列表
    const val DELETE_TICKET_HISTORY = "/app/driverTicketing/delServiceRecord" //删除服务历史
    const val BUS_ARRIVE = "/app/driverTicketing/safeArrival" //到达
    const val BUS_START = "/app/driverTicketing/startImmediately" //发车
    const val BUS_PASSENGERS = "/app/driverTicketing/driverGetPassengerList" //班次乘客列表
    const val TICKET_DETAIL = "/app/driverTicketing/getOrderDetail" //车票详情
    const val CONFIRM_RIDE = "/app/driverTicketing/confirmRide" //乘坐确认
    const val TICKET_LINE_DETAIL = "/app/driverTicketing/getDriverShiftDetail" //班线详情
    const val TICKETLINE_DETAIL = "/app/ticketing/getTicketLineDetail" //站外售票班线详情
    const val LINE_TYPE_LIST = "/app/ticketing/getLineTypeList"
    const val REGION_LIST = "/app/driverTicketing/getRegionList" //省市区三级
    const val STATION_LIST = "/app/driverTicketing/getStationist" //站点列表
    const val LINE_MANAGE_LIST = "/app/driverTicketing/getTicketList" //线路管理列表
    const val ADD_LINE = "/app/driverTicketing/addLine" //线路添加
    const val HALF_STATIONS = "/app/driverTicketing/getAlongWayList" //沿途站点
    const val LINE_MANAGE_DETAIL = "/app/driverTicketing/getLineDetail" //线路详情
    const val UP_DOWN_LINE = "/app/driverTicketing/upperOrLowerFrames"
    const val UP_DOWN_CLASS = "/app/driverTicketing/upperOrLowerFramesShift"
    const val PRICE_LIST = "/app/driverTicketing/getLineFare"
    const val SET_PRICE_LIST = "/app/driverTicketing/addLineFare"
    const val CLASS_LIST = "/app/driverTicketing/getTicketShiftList"
    const val LINE_LIST = "/app/driverTicketing/getShiftLineList"
    const val ENABLE_CAR_LIST = "/app/driverTicketing/getShiftCarList"
    const val ADD_CLASS = "/app/driverTicketing/addShift"
    const val STATION_TIME_LIST = "/app/driverTicketing/getShiftAlongWayList"
    const val CLASS_DETAIL = "/app/driverTicketing/getShiftDetail"
    const val HAS_NEW_MSG = "/app/public/getIsMess"
    const val CLEAR_MSG = "/app/public/clearMessAll"
    const val GET_MSG_LIST = "/app/public/getMessList"
    const val GET_SERVICE = "/app/public/getServerPhone"
    const val GET_TIME_PAGE = "/app/driverTicketing/gettimepage"
    const val GET_LOCATION_PAGE = "/app/driverTicketing/getpage"
    const val COMMIT_TIME = "/app/driverTicketing/updatetime"
    const val COMMIT_LOCATION = "/app/driverTicketing/addlongitudeandlatitude"
    const val EDIT_TICKET_COUNT = "/app/driverTicketing/updatepoll"

}