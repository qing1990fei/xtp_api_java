package com.zts.xtp.trade;


import com.zts.xtp.common.enums.*;
import com.zts.xtp.common.jni.JNILoadLibrary;
import com.zts.xtp.trade.api.TradeApi;
import com.zts.xtp.trade.model.request.*;
import com.zts.xtp.trade.spi.TradeSpi;
import org.junit.*;
import org.junit.runners.MethodSorters;
import com.zts.xtp.userInfo.User;

@FixMethodOrder(MethodSorters.JVM)
public class TradeApiTest {
    private static TradeApi tradeApi;
    private static String sessionId;




    static {
        User.readPropToInit();
    }
    private static final String XTP_TRADE_SERVER_IP = User.tradeServerIP;//xtp交易server的ip
    private static final int XTP_TRADE_SERVER_PORT = User.tradeServerPort;//xtp交易server的端口
    private static final String TRADE_KEY = User.serverKey;//xtp交易serverkey
    private static final short CLIENT_ID = User.clientId;//xtp允许的clientid
    private static final String XTP_QUOTE_SERVER_IP = User.quoteServerIP;//xtp行情server的ip
    private static final int XTP_QUOTE_SERVER_PORT = User.quoteServerPort;//xtp行情server的端口
    private static final String ACCOUNT = User.userName;//xtp资金账号
    private static final String PASSWORD = User.userPwd;//xtp密码
    private static final String DATA_FOLDER = User.logFolder;//java api输出日志的本地目录



    /**
     * 初始化xtp api，并登录，在所有case执行前执行一次
     */
    @BeforeClass
    public static void setUp() {
        JNILoadLibrary.loadLibrary();
        TradeSpi tradeSpi = new TradeSpiImpl();
        tradeApi = new TradeApi(tradeSpi);

        tradeApi.init(CLIENT_ID, TRADE_KEY,
                DATA_FOLDER, XtpLogLevel.XTP_LOG_LEVEL_TRACE, JniLogLevel.JNI_LOG_LEVEL_INFO, XtpTeResumeType.XTP_TERT_RESTART);
//        tradeApi.subscribePublicTopic(XtpTeResumeType.XTP_TERT_RESTART);
//        tradeApi.setHeartBeatInterval(30);
        sessionId = tradeApi.login(XTP_TRADE_SERVER_IP, XTP_TRADE_SERVER_PORT,
                ACCOUNT, PASSWORD, TransferProtocol.XTP_PROTOCOL_TCP);


        Assert.assertNotNull("login fail", sessionId);
        Assert.assertNotEquals("login fail", sessionId, "0");
    }

    @After
    public void waitSomeTime() throws InterruptedException {
        Thread.sleep(5000);
    }

    /**
     * 登出，并等待1秒，保证xtp api资源释放完全
     */
    @AfterClass
    public static void tearDown() {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ex) {
//            System.out.println("sleep error");
//        }
        tradeApi.logout(sessionId);
        tradeApi.disconnect();
    }

    /**
     * 下单
     */
    @Test
    public void testInsertOrder() {
        System.out.println("testInsertOrder");
        String orderXtpId = insertOrder();
        System.out.println("testInsertOrder orderXtpId: " + orderXtpId);
        Assert.assertNotNull(orderXtpId);
    }

    /**
     * 撤单
     */
    @Test
    public void testCancelOrder() {
        System.out.println("testCancelOrder");

        String orderXtpId = insertOrder();
        System.out.println("testCancelOrder orderXtpId: " + orderXtpId);
        Assert.assertNotNull(orderXtpId);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException exception) {
            System.out.println("sleep error");
        }
        String cancelOrderXtpId = tradeApi.cancelOrder(orderXtpId, sessionId);
        System.out.println("testCancelOrder cancelOrderXtpId: " + cancelOrderXtpId);
        Assert.assertNotNull(cancelOrderXtpId);
    }

    /**
     * 委托查询
     */
    @Test
    public void testQueryOrders() {
        System.out.println("testQueryOrders");
        OrderQueryRequest req = OrderQueryRequest.builder().beginTime(0)
            .endTime(0).build();

        int result = tradeApi.queryOrders(req, sessionId, 13);
        Assert.assertEquals(result, 0);
    }

    /**
     * 成交查询
     */
    @Test
    public void testQueryTrades() {
        System.out.println("testQueryTrades");
        TraderQueryRequest req = TraderQueryRequest.builder().beginTime(0)
            .endTime(0).build();

        int result = tradeApi.queryTrades(req, sessionId, 13);
        Assert.assertEquals(result, 0);
    }

    /**
     * 根据报单ID请求查询报单
     */
    @Test
    public void testQueryOrderByXtpId() {
        System.out.println("testQueryOrderByXtpId");
        String orderXtpId = insertOrder();
        Assert.assertNotNull(orderXtpId);

        int result = tradeApi.queryOrderByXtpId(orderXtpId, sessionId, 13);
        Assert.assertEquals(result, 0);
    }

    /**
     * 根据委托编号请求查询相关成交
     */
    @Test
    public void testQueryTradesByXtpId() {
        System.out.println("testQueryTradesByXtpId");
        String orderXtpId = insertOrder();
        Assert.assertNotNull(orderXtpId);

        int result = tradeApi.queryTradesByXtpId(orderXtpId, sessionId, 13);
        Assert.assertEquals(result, 0);
    }

    /**
     * 查询投资者持仓
     */
    @Test
    public void testQueryPosition() {
        System.out.println("testQueryPosition");
        int result = tradeApi.queryPosition(null, sessionId, 13);
        Assert.assertEquals(result, 0);
    }

    /**
     * 查询资产
     */
    @Test
    public void testQueryAsset() {
        System.out.println("testQueryAsset");

        int result = tradeApi.queryAsset(sessionId, 13);
        Assert.assertEquals(result, 0);
    }

    /**
     * 查询分级基金
     */
    @Test
    public void testQueryStructuredFund() {
        System.out.println("testQueryStructuredFund");
        StructuredFundInfoQueryRequest req = StructuredFundInfoQueryRequest.builder()
            .exchangeType(ExchangeType.SZ).build();

        int result = tradeApi.queryStructuredFund(req, sessionId, 13);
        Assert.assertEquals(result, 0);
    }

    /**
     * 资金划拨请求
     */
    @Test
    public void testFundTransfer() {
        System.out.println("testFundTransfer");
        String serialId = fundTransfer();
        System.out.println("testFundTransfer serialId: " + serialId);
        Assert.assertNotNull(serialId);
    }

    /**
     * 查询资金划拨
     */
    @Test
    public void testQueryFundTransfer() {
        System.out.println("testQueryFundTransfer");
        FundTransferLogQueryRequest req = FundTransferLogQueryRequest.builder()
            .serialId("0").build();
        int result = tradeApi.queryFundTransfer(req, sessionId, 13);
        Assert.assertEquals(result, 0);
    }

    /**
     * 查询ETF清单文件
     */
    @Test
    public void testQueryETF() {
        System.out.println("testQueryETF");
        ETFBaseQueryRequest req = ETFBaseQueryRequest.builder().marketType(MarketType.XTP_MKT_SH_A).build();

        int result = tradeApi.queryETF(req, sessionId, 13);
        Assert.assertEquals(result, 0);
    }

    /**
     * 请求查询ETF股票篮
     */
    @Test
    public void testQueryETFTickerBasket(){
        System.out.println("testQueryETFTickerBasket");
        ETFComponentQueryRequest queryETFBaseReq = ETFComponentQueryRequest.builder()
            .marketType(MarketType.XTP_MKT_SH_A).ticker("510050").build();
        int result = tradeApi.queryETFTickerBasket(queryETFBaseReq, sessionId, 13);
        Assert.assertEquals(result, 0);
    }

    /**
     * 请求查询今日新股申购信息列表
     */
    @Test
    public void testQueryIPOInfoList(){
        System.out.println("testQueryIPOInfoList");
        int result = tradeApi.queryIPOInfoList(sessionId, 13);
        Assert.assertEquals(result, 0);
    }

    /**
     * 请求查询用户新股申购额度信息
     */
    @Test
    public void testQueryIPOQuotaInfo(){
        System.out.println("testQueryIPOQuotaInfo");
        int result = tradeApi.queryIPOQuotaInfo(sessionId, 13);
        Assert.assertEquals(result, 0);
    }

    /**
     * 查询qiquan信息
     */
    @Test
    public void testQueryOptionAuctionInfo(){
        System.out.println("testQueryOptionAuctionInfo");
        int result = tradeApi.queryOptionAuctionInfo(null, sessionId, 13);
        Assert.assertEquals(result, 0);
    }

	@Test
	public void testGetTradingDay() {
		System.out.println("testGetTradingDay");
		String tradingDay = tradeApi.getTradingDay();
		System.out.println("testGetTradingDay tradingDay: " + tradingDay);
		Assert.assertNotNull(tradingDay);
	}

    @Test
    public void testGetApiVersion() {
        System.out.println("testGetApiVersion");
        String version = tradeApi.getApiVersion();
        System.out.println("testGetApiVersion version: " + version);
        Assert.assertEquals("1.1.18.19", version);
    }

    @Test
    public void testGetClientIDByXTPID() {
        System.out.println("testGetClientIDByXTPID");
        short clientId = tradeApi.getClientIDByXTPID("1009073519666951445");
        System.out.println("testGetClientIDByXTPID clientId: " + clientId);
        Assert.assertNotNull(clientId);
    }

	@Test
	public void testGetAccountByXTPID() {
		System.out.println("testGetAccountByXTPID");
		String account = tradeApi.getAccountByXTPID("396628509490944857");
		System.out.println("testGetAccountByXTPID account: " + account);
		Assert.assertNotNull(account);
	}

    @Test
    public void testIsServerRestart() {
        System.out.println("testIsServerRestart");
        boolean result = tradeApi.isServerRestart(sessionId);
        System.out.println("testIsServerRestart: " + result);
    }

    @Test
    public void testQueryOrdersByPage() {
        System.out.println("testQueryOrdersByPage");
        OrderQueryByPageReq req = OrderQueryByPageReq.builder().reqCount(10).reserved(0).build();
        tradeApi.queryOrdersByPage(req, sessionId,13);
    }

    @Test
    public void testQueryTradesByPage() {
        System.out.println("testQueryTradesByPage");
        TradeQueryByPageReq req = TradeQueryByPageReq.builder().reqCount(10).reserved(0).build();
        tradeApi.queryTradesByPage(req, sessionId,13);
    }

    //==============Common Functions=========
    private String insertOrder() {
        //期权
//        OrderInsertRequest req = OrderInsertRequest.builder()
//                .orderXtpId("0").orderClientId(2).ticker("10001033").marketType(MarketType.XTP_MKT_SH_A)
//                .price(26).stopPrice(0).quantity(1).priceType(PriceType.XTP_PRICE_ALL_OR_CANCEL)
//                .sideType(SideType.XTP_SIDE_SELL).businessType(BusinessType.XTP_BUSINESS_TYPE_OPTION)
//                    .positionEffectType(PositionEffectType.XTP_POSITION_EFFECT_OPEN).build();

        //现货
        OrderInsertRequest req = OrderInsertRequest.builder()
                .orderXtpId("0").orderClientId(2).ticker("600000").marketType(MarketType.XTP_MKT_SH_A)
                .price(26).stopPrice(0).quantity(1).priceType(PriceType.XTP_PRICE_ALL_OR_CANCEL)
                .sideType(SideType.XTP_SIDE_SELL).businessType(BusinessType.XTP_BUSINESS_TYPE_CASH)
                .positionEffectType(PositionEffectType.XTP_POSITION_EFFECT_OPEN).build();

        return tradeApi.insertOrder(req, sessionId);
    }

    private String fundTransfer() {
        FundTransferRequest req = FundTransferRequest.builder()
            .fundAccount("53300025659").password("123456").amount(100.0)
            .fundTransferType(FundTransferType.XTP_FUND_TRANSFER_IN).build();

        return tradeApi.fundTransfer(req, sessionId);
    }


}
