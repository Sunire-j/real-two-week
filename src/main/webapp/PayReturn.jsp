<%@ page contentType="text/html; charset=EUC-KR" %>
<%@ page import="com.galaxia.api.util.*"%>
<%@ page import="com.galaxia.api.merchant.* "%>
<%@ page import="com.galaxia.api.crypto.* "%>
<%@ page import="com.galaxia.api.*"%>
<%@ page import="java.sql.* "%>
<%@ page import="java.util.* "%> 
<%!
	//================================
	// static ���� �� �Լ� �����
	//================================
	public static final String VERSION ="0100";
	public static final String CONF_PATH ="C:/project/real-two-week/src/main/webapp/WEB-INF/classes/config.ini"; //*������ ���� �ʼ�
	
	// ���� ��û
		public Message MessageAuthProcess(Map<String,String> authInfo) throws Exception {
			String serviceId = authInfo.get("serviceId");
			String serviceCode = authInfo.get("serviceCode");
			String msg = authInfo.get("message");

			//�޽��� Length ����
			byte[] b = new byte[msg.getBytes().length - 4] ;
			System.arraycopy(msg.getBytes(), 4, b, 0, b.length);

			Message requestMsg = new Message(b, getCipher(serviceId,serviceCode)) ;
			
			Message responseMsg = null ;

			ServiceBroker sb = new ServiceBroker(CONF_PATH, serviceCode);

			responseMsg = sb.invoke(requestMsg);
			
			return responseMsg;
		}
	
	//���� ������ ���� key, iv�� ������
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
	�ش� �������� ������Ʈ ������ ���� "������� ���� �� ���ο�û/���� "�׽�Ʈ ������ �Դϴ�.
	------------------------------------------------------------------------------------- 
	*/	
	/* ���� ��� ���� */
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
	String serviceType = null;		//���� ����(�Ϲ�:0000/���ڵ�:1000)
	String confType = null;			//ƾĳ��_���� Ÿ�� ����(0000:ID ����/1000:PIN ����)

	String message = null;			//���� ���� MESSAGE

	//�������
	String accountNumber = null;		//������¹�ȣ
	String bankCode = null;				//�߱� ���� �ڵ�
	String mixType = null;				//�ŷ� ����(�Ϲ�:0000/����ũ��:1000)
	String expireDate = null;				//�Աݸ�������(YYYYMMDD)
	String expireTime = null;			//�Աݸ����ð�(HH24MISS)
	String amount = null;					//�Աݿ����ݾ�

	/* ���� ��� ���� */
	String outTransactionId = null;
	String outResponseCode = null;
	String outResponseMessage = null;
	String outDetailResponseCode = null;
	String outDetailResponseMessage = null;

	String authAmount = null; 		// �������� �߰� �Ķ����	_���αݾ�
	String authNumber = null;		// �������� �߰� �Ķ����_���ι�ȣ
	String authDate = null;			// �������� �߰� �Ķ����_�����Ͻ�

	String quota = null;					//�ſ�ī�� �������� �߰� �Ķ����_�Һΰ��� �� 
	String cardCompanyCode = null; //�ſ�ī�� �������� �߰� �Ķ����_�߱޻� �ڵ� 
	
	String balance = null;					//ĳ�ð���Ʈ �������� �߰� �Ķ����_�ܾ�
	String dealAmount = null;			//ĳ�ð���Ʈ �������� �߰� �Ķ����_���αݾ�
	
	String usingType = null;				//������ü �������� �߰� �Ķ����_���ݿ����� �뵵
	String identifier = null;				//������ü �������� �߰� �Ķ����_���ݿ����� ���ι�ȣ
	String identifierType = null;		//������ü �������� �߰� �Ķ����_���ݿ����� �����߱� ����
	String inputBankCode = null;		//������ü  �������� �߰� �Ķ����_���� �ڵ� 
	String inputAccountName = null;	//������ü  �������� �߰� �Ķ����_�����

	String partCancelType = null;		//�޴��� �������� �߰� �Ķ����_�κ� ��� Ÿ��(�Ϲ� �����ÿ��� ����)

	Map<String,String> authInfo = null;	 //���ο�û ���� ����

	Message respMsg = null;			

	try{
			
		//================================================
		// 1. ���� ��� �Ķ���� ����
		//================================================
		request.setCharacterEncoding("euc-kr");
		
		serviceType = request.getParameter("SERVICE_TYPE");						//���� Ÿ��(�Ϲ� :0000 , ���ڵ�:1000)
		confType = request.getParameter("CONF_TYPE");								//���� ���� Ÿ��(ID����: 0000, PIN����: 1000) *ƾĳ�� 
		serviceId = request.getParameter("SERVICE_ID");								//������ ���� ���̵�
		serviceCode = request.getParameter("SERVICE_CODE");						//���� ���� �� �����ڵ�
		orderId = request.getParameter("ORDER_ID");										//�ֹ� ��ȣ
		orderDate = request.getParameter("ORDER_DATE");							//�ֹ� ����
		transactionId = request.getParameter("TRANSACTION_ID");					//�ŷ���ȣ
		responseCode = request.getParameter("RESPONSE_CODE");								//�����ڵ�
		responseMessage = request.getParameter("RESPONSE_MESSAGE");					//����޽���
		detailResponseCode = request.getParameter("DETAIL_RESPONSE_CODE");		//�� �����ڵ�
		detailResponseMessage = request.getParameter("DETAIL_RESPONSE_MESSAGE");//�� ���� �޽���

		message = request.getParameter("MESSAGE");								//���� ���� ���� �޽���
	
		reserved1 = request.getParameter("RESERVED1");							//���񺯼�1
		reserved2 = request.getParameter("RESERVED2");							//���񺯼�2
		reserved3 = request.getParameter("RESERVED3");							//���񺯼�3

		/*������� ä�� ����*/		
		accountNumber =request.getParameter("ACCOUNT_NUMBER");			//������¹�ȣ
		bankCode =request.getParameter("BANK_CODE");							//�߱� ���� �ڵ�
		mixType = request.getParameter("MIX_TYPE");								//�ŷ� ����(�Ϲ�:0000/����ũ��:1000)
		expireDate = request.getParameter("EXPIRE_DATE");						//�Աݸ�������(YYYYMMDD)
		expireTime = request.getParameter("EXPIRE_TIME");						//�Աݸ����ð�(HH24MISS)
		amount = request.getParameter("AMOUNT");									//�Աݿ����ݾ�
	
		//================================================
		// 2. ���� ������ ��쿡�� ���� ��û ����
		//================================================
		if(("0000").equals(responseCode)&&!("1800".equals(serviceCode))) { //������� ����

			//���� ���� Map�� ����
			authInfo = new HashMap<String, String>();

			authInfo.put("serviceId", serviceId);
			authInfo.put("serviceCode", serviceCode);
			authInfo.put("message", message);
			System.out.println(serviceId);
			System.out.println(serviceCode);
			System.out.println(message);

			//================================
			// 4. ���� ��û & ���� ���� ��� ����
			//================================
			//���� ��û(Message)
			respMsg = MessageAuthProcess(authInfo);
			System.out.println(respMsg.toString());

			//���� ����
			outResponseCode = respMsg.get("1002");
			outResponseMessage = respMsg.get("1003");
			outDetailResponseCode = respMsg.get("1009");
			outDetailResponseMessage = respMsg.get("1010");
			outTransactionId = respMsg.get("1001");                //�ŷ���ȣ
			authNumber = respMsg.get("1004");                    //���ι�ȣ
			System.out.println(authNumber);
			authDate = respMsg.get("1005");                        //�����Ͻ�
			authAmount = respMsg.get("1007");                    //���αݾ�
			quota = respMsg.get("0031");                                //�Һΰ��� ��
			cardCompanyCode = respMsg.get("0034");            //ī��߱޻� �ڵ�
		}else{
%>				
			<script type="text/javascript">
				alert(<%=serviceCode%>+"RETURN ������ ����\n���� �޽��� : ���������� ���� �ڵ带 Ȯ�����ּ���!/ ");
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
	body, tr, td {font-size:9pt; font-family:�������,verdana; }
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
				//������ ��
				//status 1�� �����, ������ �߰��ϰ� ���â���� �ű��
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
						console.log(orderNum+"������ success");
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
				alert("�� �� ���� �����߻�!");
				//db���� ��������.
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