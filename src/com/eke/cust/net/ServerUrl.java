package com.eke.cust.net;

public class ServerUrl {
    public static String BASE_URL = "https://api.ekeae.com/eke";


    public static String BASE_SHARE_URL = "https://api.ekeae.com/";

    public static String METHOD_REQUEST_HX_TOKEN = "https://a1.easemob.com/";

    // 图片上传
    public static final String METHOD_image_upload = "https://api.ekeae.com/eke/Upload?";

    // 动态切屏
    public static final String METHOD_getLinkextSwitch = getAbsoluteUrl("/Cust/linkext/unlogin/getLinkextSwitch.do");
    // [登录]获取验证码
    public static final String METHOD_sendPhoneCode = "/Cust/public/sendPhoneCode.do";

    // [注册登录]提交验证码（注册/登录）<访客及注册用户使用>
    public static final String METHOD_checkPhoneCode = "/Cust/public/checkPhoneCode.do";

    //忘记密码
    public static final String METHOD_findCustPwd = "/Cust/cust/unlogin/findCustPwd.do";

    // [登录]登陆
    public static final String METHOD_login = "/Cust/public/unlogin/login.do";
    //[注册登录]根据手机号判断访客/注册用户/优质用户
    public static final String METHOD_checkCustType = "/Cust/public/unlogin/checkCustType.do";
    //[注册登录]完善用户资料
    public static final String METHOD_perfectedPwdEmail = "/Cust/public/perfectedPwdEmail.do";
    // 心跳包
    public static final String METHOD_heartbeats = "/Cust/heartbeat/unlogin/updateHeartbeat.do";
    // [新助理注册]
    public static final String METHOD_register = "/Cust/employee/register.do";
    // [查看助理名片]
    public static final String METHOD_getEmpCard = "/Cust/employee/getEmpCard.do";
    //查询助理是否在线
    public static final String METHOD_getHXUserSttus = "/Cust/employee/getHXUserSttus.do";

    // []查询城市列表
    public static final String METHOD_queryListCity = "/Cust/city/unlogin/queryListCity.do";
    // []查询城市是够开通
    public static final String METHOD_queryCityByName = "/Cust/city/unlogin/queryCityByName.do";
    // []根据所在的城市，返回区县
    public static final String METHOD_queryDistrictByCity = "/Cust/city/queryDistrictByCity.do";

    // []历史私盘记录
    public static final String METHOD_getSPHistory = "/Cust/property/getSPHistory.do";
    // [增补楼盘]提交
    public static final String METHOD_addnewloupan = "/Cust/estatefind/insertEstatefind.do";
    //[租售委托]帮我登记-是的
    public static final String METHOD_addOwnerapplyreg = "/Cust/ownerapplyreg/addOwnerapplyreg.do";

    //是否注册助理
    public static final String METHOD_getUserIsEmp = "/Cust/employee/getUserIsEmp.do";

    //[自助登记]根据楼盘获取栋座
    public static final String METHOD_queryListBuilding = "/WebMobile/building/unlogin/queryListBuilding.do";
    //查询单元
    public static final String METHOD_queryListEkecell= "/WebMobile/ekecell/unlogin/queryListEkecell.do";



    // [新助理注册]-根据当前用户所在城市判断是否允许注册
    // public static final String METHOD_queryCityByName =
    // "/Emp/city/queryCityByName.do";
    // public static final String METHOD_queryCityByName =
    // "/Cust/sysset/getSettledCity.do";
    // []根据定位码获取图片
    public static final String METHOD_queryListByLinkNOs = "/Cust/ekelinkpic/unlogin/queryListByLinkNOs.do";

    // []一次性返回指定文件夹下所有图片
    public static final String METHOD_getListByForeignId = "/Cust/ekepic/getListByForeignId.do";

    // []图片删除
    public static final String METHOD_deletePicByForgin = "/Cust/ekepic/deletePicByForgin.do";

    // []移动端信息采集
    public static final String METHOD_addEkemobilmes = "/Cust/ekemobilmes/addEkemobilmes.do";

    // []根据条件获取楼盘信息
    public static final String METHOD_queryListPage = "/Cust/estate/unlogin/queryListPage.do";
    // []根据名字获取
    public static final String METHOD_queryListByName = "/Cust/estate/unlogin/queryListByName.do";

    // 资讯
    public static final String METHOD_getIndexNews = "/Cust/news/unlogin/getIndexNews.do";

    // [近期成交]
    public static final String METHOD_getMyDeptContract = "/Cust/contract/getMyDeptContract.do";

    // [跟进列表]
    public static final String METHOD_getMyFollow = "/Cust/follow/getMyFollow.do";

    // [新闻公告]
    public static final String METHOD_getListNewsNotice = "/Cust/news/unlogin/getListNewsNotice.do";

    // [新闻公告]新闻详情
    public static final String METHOD_getNewsById = "/Cust/news/unlogin/getNewsById.do";

    // [同事列表]楼盘-详情
    public static final String METHOD_selectEstateDL = "/Cust/estate/selectEstateDL.do";

    // [资讯]根据助理id获取昵称等基本情况
    public static final String METHOD_findEmployeeByIds = "/Cust/employee/getEmployeeNikeName.do";
    // [房源]
    public static final String METHOD_getListProperty = "/Cust/property/unlogin/getListProperty.do";

    // [房源]缩略图上传
    public static final String METHOD_PropertyHeadPic = "https://api.ekeae.com:8080/eke/Cust/servlet/PropertyHeadPic.do";

    // [房源]条件查询
    public static final String METHOD_getListPropertyPage = "/Cust/property/unlogin/getListPropertyPage.do";
    // [租售委托]根据交易类型获取全部房源特点
    public static final String METHOD_getFeatureByProperty = "/Cust/ekefeature/unlogin/getFeatureByProperty.do";

    //检测重复房源
    public static final String METHOD_getPropertyInvalid = "/WebMobile/property/unlogin/getPropertyInvalid.do";

    // [私盘登记]提交
    public static final String METHOD_addSpdjContract = "/Cust/contract/addSpdjContract.do";

    // [私盘登记]提交作废
    public static final String METHOD_insertProperty = "/Cust/property/insertProperty.do";

    // [房源]房源菜单-卖点
    public static final String METHOD_getFeatureByPropertyID = "/Cust/ekefeature/getFeatureByPropertyID.do";

    // [房源]房源菜单-卖点-提交
    public static final String METHOD_updateEKEFeature = "/Cust/property/updateEKEFeature.do";

    // [房源]房源菜单-申请跟房
    public static final String METHOD_applyGenfang = "/Cust/property/applyGenfang.do";

    // [写跟进]-提交
    public static final String METHOD_addFollow = "/Cust/follow/addFollow.do";
    // 添加收藏
    public static final String METHOD_insertEkecustcollect = "/Cust/ekecustcollect/insertEkecustcollect.do";
    // 助理
    public static final String METHOD_getEstateEmp = "/Cust/estate/getEstateEmp.do";
    //签约助理
    public static final String METHOD_updateMyContractEmp = "/Cust/contract/updateMyContractEmp.do";
    // 助理
    public static final String METHOD_addCustFollow = "/Cust/custfollow/addCustFollow.do";
    // 助理
    public static final String METHOD_getEmpAppointResp = "/Cust/custfollow/getEmpAppointResp.do";
    //助理详情-坚持预约
    public static final String METHOD_addCustFollowInsist = "/Cust/custfollow/addCustFollowInsist.do";
    //详情-取助理列表
    public static final String METHOD_findListByEstate = "/Cust/employee/unlogin/findListByEstate.do";
    //搜索助理
    public static final String METHOD_getEstateEmpByTel = "/Cust/estate/getEstateEmpByTel.do";
    //获取业主联系方式
    public static final String METHOD_getOwerMess = "/Cust/property/getOwerMess.do";


    // 提交订单
    public static final String METHOD_insertContract = "/Cust/contract/insertContract.do";
    // [订单及支付]页面加载
    public static final String METHOD_initPropertyOrder = "/Cust/property/initPropertyOrder.do";
    // [订单及支付]页面加载
    public static final String METHOD_getOrderData = "/Cust/contract/getOrderData.do";
    public static final String METHOD_getPayNo = "/Cust/custpay/getPayNo.do";

    // []统一下单
    public static final String METHOD_startPay = "/Cust/custpay/startPay.do";
    // []申请退款
    public static final String METHOD_updateContractTK = "/Cust/contract/updateContractTK.do";

    // [写跟进]
    public static final String METHOD_getPropertyFollow = "/Cust/follow/getPropertyFollow.do";

    // [上传]-上传房源图
    public static final String METHOD_addDataPic = "/Cust/ekepic/addDataPic.do";

    // [我客成交]根据编号获取房源信息
    public static final String METHOD_getPropertyByNo = "/Cust/property/getPropertyByNo.do";

    // []纠错
    public static final String METHOD_insertErrorCorrection = "/Cust/ekesuggest/unlogin/insertErrorCorrection.do";

    // []启动带看
    public static final String METHOD_startGuide = "/Cust/ekeguide/startGuide.do";

    // []结束带看
    public static final String METHOD_endGuide = "/Cust/ekeguide/endGuide.do";

    // [我客成交]
    public static final String METHOD_addContract = "/Cust/contract/addContract.do";
    // [我的账户]
    public static final String METHOD_queryContractact = "/Cust/contractact/queryContractact.do";
    // [我的账户明细]
    public static final String METHOD_queryMXPage = "/Cust/contractact/queryMXPage.do";
    // [提现]
    public static final String METHOD_contractactTX = "/Cust/contractact/contractactTX.do";

    // 楼盘首推助理
    public static final String METHOD_queryRandDL = "/Cust/employee/unlogin/queryRandDL.do";

    // 房源详情
    public static final String METHOD_getPropertyById = "/Cust/property/unlogin/getPropertyById.do";

    // [钥匙管理]
    public static final String METHOD_queryListByProperty = "/Cust/ekekeymanage/queryListByProperty.do";

    // [钥匙管理]提交
    public static final String METHOD_insertEkekeymanage = "/Cust/ekekeymanage/insertEkekeymanage.do";

    // [房源]右上角菜单-根据房源编号精确查找
    public static final String METHOD_getListPropertyNo = "/Cust/property/getListPropertyNo.do";

    // [房源]右上角菜单--条件过滤-确定
    public static final String METHOD_queryPropertyCondition = "/Cust/property/queryPropertyCondition.do";

    // [看图]
    public static final String METHOD_getListPicForeignId = "/Cust/ekepic/unlogin/getListByProperty.do";

    // [写跟进]删除跟进记录
    public static final String METHOD_deleteFollow = "/Cust/follow/deleteFollow.do";


    //[我的]
    public static final String METHOD_initCustCenter = "/Cust/ekecust/initCustCenter.do";

    // [个人资料]--头像设置
    public static final String METHOD_EmployeeIcon = "/Cust/employeeicon";

    // [个人资料]--头像读取
    public static final String METHOD_EmployeeIconShow = "/Cust/employeeiconshow?custid=";

    // [个人资料]昵称/常用邮箱/个性签名--保存
    public static final String METHOD_updateEkecust = "/Cust/ekecust/updateEkecust.do";
    // [个人资料]昵称/常用邮箱/个性签名--保存
    // [我的合同]
    public static final String METHOD_getMyContract = "/Cust/contract/getMyContract.do";
    //[房源收藏]
    public static final String METHOD_getMyCollectPropertyPage = "/Cust/property/getMyCollectPropertyPage.do";

    // [限时反馈]
    public static final String METHOD_getListSchedule = "/Cust/schedule/getListSchedule.do";

    // [限时反馈]提交
    public static final String METHOD_updateSchedule = "/Cust/schedule/updateSchedule.do";

    // [房源]主菜单-添加/取消关注
    public static final String METHOD_updateEkeempCollect = "/Cust/ekeempcollect/updateEkeempCollect.do";

    // [房源]右上角菜单-关注房源
    public static final String METHOD_getMyCollectProperty = "/Cust/property/getMyCollectProperty.do";

    // [房源]右上角菜单-我的跟房
    public static final String METHOD_getMyFollowProperty = "/Cust/property/getMyFollowProperty.do";

    // [关于]
    public static final String METHOD_getAboutUs = "/Cust/sysset/unlogin/getAboutUs.do";

    // [关于]
    public static final String METHOD_getAboutUs1 = "/Emp/sysset/unlogin/getAboutUs.do";

    // [关于]版本检测
    public static final String METHOD_checkNewVersion = "/Cust/sysset//unlogin/checkNewVersion.do";

    // [二维码]
    public static final String METHOD_getAboutUsUrl = "/Cust/sysset/unlogin/getAboutUsUrl.do";

    // [二维码]入口
    public static final String METHOD_checkEKEMaper = "/Cust/employee/checkEKEMaper.do";

    // [后台查询]提交-房源编号
    public static final String METHOD_queryHTByNo = "/Cust/property/queryHTByNo.do";

    // [后台查询]提交-手机号
    public static final String METHOD_queryHTByTel = "/Cust/blacklist/queryHTByTel.do";

    // [测绘助手]提交
    public static final String METHOD_insertEkeempmaprecord = "/Cust/ekeempmaprecord/insertEkeempmaprecord.do";

    // [历史测绘]
    public static final String METHOD_LisEmpMaprecord = "/Cust/ekeempmaprecord/LisEmpMaprecord.do";

    // [帮助中心]
    public static final String METHOD_getAboutUsHelp = "/Cust/sysset/getAboutUsHelp.do";

    // [定位采集]
    public static final String METHOD_getSystemTime = "/Cust/public/getSystemTime.do";

    // [定位采集]上传
    public static final String METHOD_insertLocatCollect = "/Cust/ekelocatcollect/insertLocatCollect.do";

    // [定位采集]图片上传
    public static final String METHOD_EkeLocatCollectPic = "https://api.ekeae.com:8080/eke/Emp/servlet/EkeLocatCollectPic.do";

    // [房源登记]选择楼盘
    public static final String METHOD_queryCityListPage = "/Cust/estate/queryCityListPage.do";
    // [房源登记]找房
    public static final String METHOD_getListEstateMap = "/Cust/estate/unlogin/getListEstateMap.do";
    //我是业主
    public static final String METHOD_getMyAllProperty = "/WebMobile/property/getMyAllProperty.do";

    // [房源登记]提交
    public static final String METHOD_insertPropertyByWT = "/Cust/property/insertPropertyByWT.do";
    // [房源登记]（暂存）
    public static final String METHOD_insertPropertyDj = "/Cust/property/insertPropertyByWTZC.do";
    // 历史委托登记
    public static final String METHOD_queryListMyHistory = "/Cust/property/queryListMyHistory.do";
    // 删除
    public static final String METHOD_updatePropertyDJ = "/Cust/property/updatePropertyDJ.do";
    // 继续登记
    public static final String METHOD_findPropertyDJ = "/Cust/property/findPropertyDJ.do";

    // [房源登记]图片上传
    public static final String METHOD_PropertyDJPic = "/Cust/servlet/PropertyDJPic.do";
    // 房间图
    public static final String METHOD_initPropertyWTImg = "/Cust/property/initPropertyWTImg.do";

    // 房间图上传
    public static final String METHOD_addDataPicBase = "/Cust/ekepic/addDataPicBase.do";

    // [意见反馈]
    public static final String METHOD_insert = "/Cust/proposal/unlogin/insert.do";


    // [代理重置]
    public static final String METHOD_initAgent = "/Cust/estate/initAgent.do";
    // [注册获取代理楼盘]
    public static final String METHOD_UNLOGIN_PAGE_LOUPAN = "/Cust/estate/unlogin/queryEstateByCity.do";
    // [注册筛选楼盘]
    public static final String METHOD_UNLOGIN_FILTER_LOUPAN = "/Cust/estate/unlogin/queryListPageUn.do";
    // [代理重置]提交
    public static final String METHOD_insertEkeempestatemodi = "/Cust/ekeempestatemodi/insertEkeempestatemodi.do";


    // [已成交]菜单-业主收款-选择银行
    public static final String METHOD_queryListBank = "/Cust/reference/queryListBank.do";


    // [安全设置]返佣账户
    public static final String METHOD_getSafeCount = "/Cust/employee/getSafeCount.do";

    // [安全设置]返佣账户-提交
    public static final String METHOD_updateBankCount = "/Cust/employee/updateBankCount.do";

    // [安全设置]密码设置-确认
    public static final String METHOD_updatePassword = "/Cust/employee/updatePassword.do";

    // [我的任务]
    public static final String METHOD_initMissionExp = "/Cust/ekeempmissionexp/initMissionExp.do";

    // [我的任务]明细
    public static final String METHOD_queryTypeList = "/Cust/ekeempmissionexp/queryTypeList.do";

    // [已成交]菜单-业主收款-提交
    public static final String METHOD_updateContractBySK = "/Cust/contract/updateContractBySK.do";

    // [已成交]上传代理合同
    public static final String METHOD_upload_daili_hetong = "/Cust/ekepic/addContractPicBase.do";

    //环信转微信
    public static final String METHOD_sendWXTempToEmp = "/Cust/wx/sendWXTempToEmp.do";


    public static final String METHOD_alipaynotify = ServerUrl.BASE_URL + "/Cust/custpay/unlogin/alipaynotify.do";


    //委托记录分享url
    public static final String METHOD_fangyuan_detaile = "/mobile/fangyuan-detaile/?propertyid=";
    //查询配置
    public static final String METHOD_queryListReference = "/Emp/reference/unlogin/queryListReference.do";
    public static final String METHOD_getTableEnumOne = "Cust/public/unlogin/getTableEnumOne.do";

    public static String buildHeadUrl(String empid) {
        return BASE_URL + ServerUrl.METHOD_EmployeeIconShow + empid;

    }

    public static String buildShareHouseHistoryUrl(String propertyid) {
        return BASE_SHARE_URL + METHOD_fangyuan_detaile + propertyid;
    }
    public static String getAbsoluteUrl(String relativeUrl) {
        if (relativeUrl.startsWith(BASE_URL)) {
            return relativeUrl;
        }
        return BASE_URL + "/" + relativeUrl;

    }
    public static String buildImageUrl(String url) {
        if (url.startsWith("http") || url.startsWith("https")) {
            return url;
        }
        return BASE_SHARE_URL + url;
    }
}
