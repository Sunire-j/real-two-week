<%@ page contentType="text/html; charset=EUC-KR" %>
<%@ page import="com.galaxia.api.util.*"%>
<%@ page import="com.galaxia.api.merchant.* "%>
<%@ page import="com.galaxia.api.crypto.* "%>
<%@ page import="com.galaxia.api.*"%>
<%@ page import="java.sql.* "%>
<%@ page import="java.util.* "%> 
<%!
	//================================
	// static 변수 및 함수 선언부
	//================================
	public static final String VERSION ="0100";
	public static final String CONF_PATH ="C:/project/real-two-week/src/main/webapp/WEB-INF/classes/config.ini"; //*가맹점 수정 필수
	
	// 승인 요청
		public Message MessageAuthProcess(Map<String,String> authInfo) throws Exception {
			String serviceId = authInfo.get("serviceId");
			String serviceCode = authInfo.get("serviceCode");
			String msg = authInfo.get("message");

			//메시지 Length 제거
			byte[] b = new byte[msg.getBytes().length - 4] ;
			System.arraycopy(msg.getBytes(), 4, b, 0, b.length);

			Message requestMsg = new Message(b, getCipher(serviceId,serviceCode)) ;
			
			Message responseMsg = null ;

			ServiceBroker sb = new ServiceBroker(CONF_PATH, serviceCode);

			responseMsg = sb.invoke(requestMsg);
			
			return responseMsg;
		}
	
	//설정 파일을 통해 key, iv값 가져옴
		private GalaxiaCipher getCipher(String serviceId, String serviceCode) throws Exception {
			GalaxiaCipher cipher = null ;

			String key = null ;
			String iv = null ;
			
			try {
				ConfigInfo config = new ConfigInfo(CONF_PATH, serviceCode);
				key = config.getKey();
				iv = config.getIv();
				
				cipher = new Seed();
				cipher.setKey(key.getBytes());
				cipher.setIV(iv.getBytes());
			} catch(Exception e) {
				throw e ;
			}
			return cipher;
		}
				
%>

<% 
	/*
	------------------------------------------------------------------------------------- 
	해당 페이지는 빌게이트 결제를 위한 "인증결과 리턴 및 승인요청/응답 "테스트 페이지 입니다.
	------------------------------------------------------------------------------------- 
	*/	
	/* 인증 결과 변수 */
	String serviceId = null;
	String serviceCode = null; 
	String orderId = null;
	String orderDate = null;
	String transactionId = null;
	String responseCode = null;	
	String responseMessage = null;
	String detailResponseCode = null;
	String detailResponseMessage = null;
	String reserved1 = null;
	String reserved2 = null;
	String reserved3 = null;
	String serviceType = null;		//서비스 구분(일반:0000/월자동:1000)
	String confType = null;			//틴캐시_인증 타입 구분(0000:ID 인증/1000:PIN 인증)

	String message = null;			//인증 응답 MESSAGE

	//가상계좌
	String accountNumber = null;		//가상계좌번호
	String bankCode = null;				//발급 은행 코드
	String mixType = null;				//거래 구분(일반:0000/에스크로:1000)
	String expireDate = null;				//입금마감일자(YYYYMMDD)
	String expireTime = null;			//입금마감시간(HH24MISS)
	String amount = null;					//입금예정금액

	/* 승인 결과 변수 */
	String outTransactionId = null;
	String outResponseCode = null;
	String outResponseMessage = null;
	String outDetailResponseCode = null;
	String outDetailResponseMessage = null;

	String authAmount = null; 		// 승인응답 추가 파라미터	_승인금액
	String authNumber = null;		// 승인응답 추가 파라미터_승인번호
	String authDate = null;			// 승인응답 추가 파라미터_승인일시

	String quota = null;					//신용카드 승인응답 추가 파라미터_할부개월 수 
	String cardCompanyCode = null; //신용카드 승인응답 추가 파라미터_발급사 코드 
	
	String balance = null;					//캐시게이트 승인응답 추가 파라미터_잔액
	String dealAmount = null;			//캐시게이트 승인응답 추가 파라미터_승인금액
	
	String usingType = null;				//계좌이체 승인응답 추가 파라미터_현금영수증 용도
	String identifier = null;				//계좌이체 승인응답 추가 파라미터_현금영수증 승인번호
	String identifierType = null;		//계좌이체 승인응답 추가 파라미터_현금영수증 자진발급 유무
	String inputBankCode = null;		//계좌이체  승인응답 추가 파라미터_은행 코드 
	String inputAccountName = null;	//계좌이체  승인응답 추가 파라미터_은행명

	String partCancelType = null;		//휴대폰 승인응답 추가 파라미터_부분 취소 타입(일반 결제시에만 전달)

	Map<String,String> authInfo = null;	 //승인요청 정보 저장

	Message respMsg = null;			

	try{
			
		//================================================
		// 1. 인증 결과 파라미터 수신
		//================================================
		request.setCharacterEncoding("euc-kr");
		
		serviceType = request.getParameter("SERVICE_TYPE");						//서비스 타입(일반 :0000 , 월자동:1000)
		confType = request.getParameter("CONF_TYPE");								//결제 인증 타입(ID인증: 0000, PIN인증: 1000) *틴캐시 
		serviceId = request.getParameter("SERVICE_ID");								//가맹점 서비스 아이디
		serviceCode = request.getParameter("SERVICE_CODE");						//결제 수단 별 서비스코드
		orderId = request.getParameter("ORDER_ID");										//주문 번호
		orderDate = request.getParameter("ORDER_DATE");							//주문 일자
		transactionId = request.getParameter("TRANSACTION_ID");					//거래번호
		responseCode = request.getParameter("RESPONSE_CODE");								//응답코드
		responseMessage = request.getParameter("RESPONSE_MESSAGE");					//응답메시지
		detailResponseCode = request.getParameter("DETAIL_RESPONSE_CODE");		//상세 응답코드
		detailResponseMessage = request.getParameter("DETAIL_RESPONSE_MESSAGE");//상세 응답 메시지

		message = request.getParameter("MESSAGE");								//인증 응답 전문 메시지
	
		reserved1 = request.getParameter("RESERVED1");							//예비변수1
		reserved2 = request.getParameter("RESERVED2");							//예비변수2
		reserved3 = request.getParameter("RESERVED3");							//예비변수3

		/*가상계좌 채번 응답*/		
		accountNumber =request.getParameter("ACCOUNT_NUMBER");			//가상계좌번호
		bankCode =request.getParameter("BANK_CODE");							//발급 은행 코드
		mixType = request.getParameter("MIX_TYPE");								//거래 구분(일반:0000/에스크로:1000)
		expireDate = request.getParameter("EXPIRE_DATE");						//입금마감일자(YYYYMMDD)
		expireTime = request.getParameter("EXPIRE_TIME");						//입금마감시간(HH24MISS)
		amount = request.getParameter("AMOUNT");									//입금예정금액
	
		//================================================
		// 2. 인증 성공일 경우에만 승인 요청 진행
		//================================================
		if(("0000").equals(responseCode)&&!("1800".equals(serviceCode))) { //가상계좌 제외

			//결제 정보 Map에 저장
			authInfo = new HashMap<String, String>();

			authInfo.put("serviceId", serviceId);
			authInfo.put("serviceCode", serviceCode);
			authInfo.put("message", message);
			System.out.println(serviceId);
			System.out.println(serviceCode);
			System.out.println(message);

			//================================
			// 4. 승인 요청 & 승인 응답 결과 설정
			//================================
			//승인 요청(Message)
			respMsg = MessageAuthProcess(authInfo);
			System.out.println(respMsg.toString());

			//승인 응답
			outResponseCode = respMsg.get("1002");
			outResponseMessage = respMsg.get("1003");
			outDetailResponseCode = respMsg.get("1009");
			outDetailResponseMessage = respMsg.get("1010");
			outTransactionId = respMsg.get("1001");                //거래번호
			authNumber = respMsg.get("1004");                    //승인번호
			System.out.println(authNumber);
			authDate = respMsg.get("1005");                        //승인일시
			authAmount = respMsg.get("1007");                    //승인금액
			quota = respMsg.get("0031");                                //할부개월 수
			cardCompanyCode = respMsg.get("0034");            //카드발급사 코드
		}else{
%>				
			<script type="text/javascript">
				alert(<%=serviceCode%>+"RETURN 페이지 오류\n에러 메시지 : 결제수단의 서비스 코드를 확인해주세요!/ ");
				window.close();
			</script>
<%
		}
	} catch (Exception e) {
        throw new RuntimeException(e);
    }
%>
<html>
<head>
<title></title>
<style>
	body, tr, td {font-size:9pt; font-family:맑은고딕,verdana; }
	div {width: 98%; height:100%; overflow-y: auto; overflow-x:hidden;}
</style>
<meta charset="EUC-KR">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
<title>Insert title here</title>
	<script src="https://pay.billgate.net/paygate/plugin/gx_web_client.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js" integrity="sha512-v2CJ7UaYy4JwqLDIrZUI/4hqeoQieOmAZNXBeQyjo21dadnwR+8ZaIJVT8EE2iyI61OV8e6M8PP2/4hpQINQ/g==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
	<script>
		$(function() {
			var responseCode = "<%=responseCode%>";
			console.log(responseCode);
			if (responseCode === "0000") {
				//정상일 때
				//status 1로 만들고, 아이템 추가하고 결과창으로 옮기기
				var orderNum = "<%=orderId%>"
				var cardNum = <%=authNumber%>;
				console.log(orderNum);
				console.log(cardNum);
				$.ajax({
					url:"/purchase/success",
					data: {
						orderNum:orderNum,
						cardNum:<%=authNumber%>
					},
					async:false,
					type:'post',
					success:function(result){

						var orderid;
						console.log(orderNum+"마지막 success");
						$.ajax({
							async: 'false',
							url:'/order/getOrderId',
							data:{
								orderNum:orderNum
							},
							type: 'post',
							success:function(result){
								orderid=result;
								window.parent.location.href = "/basket/complete?orderid="+orderid;
							}
						});
					},
					error:function(error){
						console.log(error.responseText);
					}
				});

			} else {
				alert("알 수 없는 오류발생!");
				//db에서 날려야함.
				var orderNum = "<%=orderId%>"
				$.post({
					url:"/order/cancle",
					type:'post',
					async:'false',
					data:{
						orderNum:orderNum
					},
					success:function(result){
						window.parent.location.href="/";
					}
				});
			}
		});
	</script>
</head>
<body>
</body>

</html>