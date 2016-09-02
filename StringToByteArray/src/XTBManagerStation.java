
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.net.ssl.SSLSocketFactory;


/**
 * XTBManager.
 * 
 * @author ugureli
 *
 */
public class XTBManagerStation {

	private static final String FIELD_ID = "id";
	private static final String FIELD_CID = "cid";
	private static final String FIELD_ADDRESS = "address";
	private static final String FIELD_COUNTRY = "country";
	private static final String FIELD_CITY = "city";
	private static final String FIELD_ZIP_CODE = "zip code";
	private static final String FIELD_STATE = "state";
	private static final String FIELD_PHONE = "phone";
	private static final String FIELD_EMAIL = "email";
	private static final String FIELD_COMMENT = "comment";
	private static final String FIELD_ENABLED = "enabled";
	private static final String FIELD_ACC_GROUP = "group";
	private static final String FIELD_SERIAL = "serial";
	private static final String FIELD_PHONE_PASSWORD = "phone password";
	private static final String FIELD_BALANCE = "balance";
	private static final String FIELD_CREDIT = "credit";
	private static final String FIELD_LEVARAGE = "leverage";
	private static final String FIELD_REGDATE = "regdate";
	private static final String FIELD_AGENT_ID = "agent id";
	private static final String FIELD_SEND_REPORTS = "send reports";
	private static final String RESULT = "result";
	private static final String DATA = "data";
	private static final String HEADER = "header";
	private static final String TYPE = "type";

	private String host;
	private Integer port;
	private String loginId;
	private String pass;

	enum ReqType {
		LOGIN, GROUP_LIST, ACCOUNT_LIST, ACCOUNT_DETAILS, OPERATION
	}
	
	public static void main(String[] args) {
		XTBManagerStation x= new XTBManagerStation();
		try {
			x.getAccountList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Default Constructor
	 */
	public XTBManagerStation() {
		readSettings(new Properties());
	}

	/**
	 * this needs host, port, loginId, pass
	 * 
	 * @param prop
	 */
	public XTBManagerStation(Properties prop) {
		readSettings(prop);
	}

	/**
	 * @param prop
	 */
	private void readSettings(Properties prop) {
//		xm-demo.corp.xtb.com
		this.setHost(prop.getProperty("host", "xm-demo.xopenhub.pro"));
		this.setPort(Integer.valueOf(prop.getProperty("port", "9443")));
		this.setLoginId(prop.getProperty("loginId", "InfleksTR"));
		this.setPass(prop.getProperty("pass","Infleks123"));
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> ping() throws Exception {
		Socket socket = null;
		OutputStream os = null;
		DataInputStream is;
		List<Map<String, Object>> sonuc = null;
		try {
			socket = SSLSocketFactory.getDefault().createSocket(this.host, this.port.intValue());
			is = new DataInputStream(socket.getInputStream());
			os = socket.getOutputStream();
			read(is);

			os.write(getReqBytes(ReqType.LOGIN, this.loginId, this.pass));
			boolean loginSuccess = getResult(read(is));
			if (loginSuccess) {
				os.write(getReqBytes(ReqType.ACCOUNT_LIST));
				JsonArray accounts = getAccountList(read(is));

				os.write(getReqBytes(ReqType.ACCOUNT_DETAILS, accounts));
				JsonObject str = read(is);
				sonuc = getAccountDetails(str);
			}

			os.flush();
			return sonuc;
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					throw new Exception(e);
				}
			}
		}
	}

	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getGroupList() throws Exception {
		Socket socket = null;
		OutputStream os = null;
		DataInputStream is;
		List<Map<String, Object>> sonuc = null;
		try {
			socket = SSLSocketFactory.getDefault().createSocket(this.host, this.port.intValue());
			is = new DataInputStream(socket.getInputStream());
			os = socket.getOutputStream();
			read(is);

			os.write(getReqBytes(ReqType.LOGIN, this.loginId, this.pass));
			boolean loginSuccess = getResult(read(is));
			if (loginSuccess) {

				os.write(getReqBytes(ReqType.GROUP_LIST));
				JsonObject o = read(is);
				sonuc = getGroupList(o);
			}
			os.flush();
			return sonuc;
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					throw new Exception(e);
				}
			}
		}
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getAccountList() throws Exception {
		Socket socket = null;
		OutputStream os = null;
		DataInputStream is;
		List<Map<String, Object>> sonuc = null;
		try {
			socket = SSLSocketFactory.getDefault().createSocket(this.host, this.port.intValue());
			is = new DataInputStream(socket.getInputStream());
			os = socket.getOutputStream();
			read(is);

			os.write(getReqBytes(ReqType.LOGIN, this.loginId, this.pass));
			boolean loginSuccess = getResult(read(is));
			if (loginSuccess) {
				os.write(getReqBytes(ReqType.ACCOUNT_LIST));
				JsonArray accounts = getAccountList(read(is));

				os.write(getReqBytes(ReqType.ACCOUNT_DETAILS, accounts));
				JsonObject str = read(is);
				sonuc = getAccountDetails(str);
			}
			System.out.println(sonuc);
			os.flush();
			return sonuc;
		} catch (Exception e) {
			throw new Exception( e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					throw new Exception( e);
				}
			}
		}
	}

	/**
	 * @param accId
	 * @param amount
	 * @param explanation
	 * @return
	 * @throws Exception
	 */
	public String sendDeposit(Integer accId, Double amount, String explanation) throws Exception {
		Socket socket = null;
		OutputStream os = null;
		DataInputStream is;
		String sonuc = null;
		try {
			socket = SSLSocketFactory.getDefault().createSocket(this.host, this.port.intValue());
			is = new DataInputStream(socket.getInputStream());
			os = socket.getOutputStream();
			read(is);

			os.write(getReqBytes(ReqType.LOGIN, this.loginId, this.pass));
			boolean loginSuccess = getResult(read(is));

			os.flush();
			return sonuc;
		} catch (Exception e) {
			throw new Exception( e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					throw new Exception( e);
				}
			}
		}
	}

	/**
	 * @param is
	 * @return
	 */
	private static JsonObject read(InputStream is) {
		JsonReader r = Json.createReader(is);
		JsonObject o = r.readObject();
		return o;
	}

	/**
	 * @param reqType
	 * @param jsonParam
	 * @return
	 */
	private byte[] getReqBytes(ReqType reqType, Object... jsonParam) {
		return getJSON(reqType, jsonParam).toString().getBytes(Charset.forName("UTF-8"));
	}

	/**
	 * @param reqType
	 * @param jsonParam
	 * @return
	 */
	public JsonObject getJSON(ReqType reqType, Object... jsonParam) {
		JsonObject object = null;
		switch (reqType) {
		case LOGIN:
			object = Json.createObjectBuilder().add(HEADER, Json.createObjectBuilder().add(TYPE, "login").build())
					.add("data", Json.createObjectBuilder().add("login", (String) jsonParam[0]).add("password", (String) jsonParam[1]).build()).build();
			break;
		case GROUP_LIST:
			object = Json.createObjectBuilder().add(HEADER, Json.createObjectBuilder().add(TYPE, "group list").build()).add("data", Json.createObjectBuilder().add("", "").build())
					.build();
			break;
		case ACCOUNT_LIST:
			object = Json.createObjectBuilder().add(HEADER, Json.createObjectBuilder().add(TYPE, "account list").build())
					.add("data", Json.createObjectBuilder().add("", "").build()).build();
			Json.createObjectBuilder().add(HEADER, Json.createObjectBuilder().add(TYPE, "account list").build()).build();
			break;
		case ACCOUNT_DETAILS:
			object = Json.createObjectBuilder().add(HEADER, Json.createObjectBuilder().add(TYPE, "account info list").build())
					.add("data", Json.createObjectBuilder().add("accounts", (JsonArray) jsonParam[0]).build()).build();
			break;
		case OPERATION:
			object = Json
					.createObjectBuilder().add(HEADER, Json.createObjectBuilder().add(TYPE, "account operation").build()).add("data", Json.createObjectBuilder()
							.add("id", (JsonValue) jsonParam[0]).add("operation", "deposit").add("amount", (JsonValue) jsonParam[1]).add("comment", (String) jsonParam[2]).build())
					.build();
			break;
		// $CASES-OMITTED$
		default:
			break;
		}
		return object;
	}

	/**
	 * @param jsonObject
	 * @return
	 */
	public boolean getResult(JsonObject jsonObject) {
		JsonObject data = jsonObject.getJsonObject(DATA);
		System.out.println(data.getString(RESULT));
		return data.getString(RESULT) != null && "success".equals(data.getString(RESULT));
	}

	/**
	 * @param jsonObject
	 * @return
	 */
	public JsonArray getAccountList(JsonObject jsonObject) {
		JsonObject data = jsonObject.getJsonObject(DATA);
		JsonArray accounts = data.getJsonArray("accounts");
		return accounts;
	}

	/**
	 * @param jsonObject
	 * @return
	 */
	public List<Map<String, Object>> getGroupList(JsonObject jsonObject) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		JsonObject data = (JsonObject) jsonObject.get(DATA);
		JsonArray groups = data.getJsonArray("groups");
		Iterator<JsonValue> iterator = groups.iterator();
		while (iterator.hasNext()) {
			JsonObject o = (JsonObject) iterator.next();
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("name", o.get("name"));
			m.put("currency", o.get("currency"));
			list.add(m);
		}
		return list;
	}

	/**
	 * @param jsonObject
	 * @return
	 */
	public List<Map<String, Object>> getAccountDetails(JsonObject jsonObject) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		JsonObject data = (JsonObject) jsonObject.get(DATA);
		JsonArray accounts = data.getJsonArray("accounts");
		Iterator<JsonValue> iterator = accounts.iterator();
		while (iterator.hasNext()) {
			JsonObject o = (JsonObject) iterator.next();
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("id", o.get(FIELD_ID));
			m.put("cid", o.get(FIELD_CID));
			m.put("address", o.get(FIELD_ADDRESS));
			m.put("country", o.get(FIELD_COUNTRY));
			m.put("city", o.get(FIELD_CITY));
			m.put("zipCode", o.get(FIELD_ZIP_CODE));
			m.put("state", o.get(FIELD_STATE));
			m.put("phone", o.get(FIELD_PHONE));
			m.put("email", o.get(FIELD_EMAIL));
			m.put("comment", o.get(FIELD_COMMENT));
			m.put("enabled", o.get(FIELD_ENABLED));
			m.put("accGroup", o.get(FIELD_ACC_GROUP));
			m.put("serial", o.get(FIELD_SERIAL));
			m.put("phonePassword", o.get(FIELD_PHONE_PASSWORD));
			m.put("balance", o.get(FIELD_BALANCE));
			m.put("credit", o.get(FIELD_CREDIT));
			m.put("leverage", o.get(FIELD_LEVARAGE));
			m.put("regdate", new Date(o.getInt(FIELD_REGDATE) * 1000L)); // unix
			m.put("agentId", o.get(FIELD_AGENT_ID));
			m.put("sendReports", o.get(FIELD_SEND_REPORTS));
			list.add(m);
		}
		return list;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String value) {
		this.host = value;
	}

	public Integer getPort() {
		return this.port;
	}

	public String getLoginId() {
		return this.loginId;
	}

	public void setLoginId(String value) {
		this.loginId = value;
	}

	public void setPort(Integer value) {
		this.port = value;
	}

	public String getPass() {
		return this.pass;
	}

	public void setPass(String value) {
		this.pass = value;
	}

	// public static void main(String[] args) throws Exception {
	// Integer accId = 555555;
	// Double amount = 1000.0;
	// String explanation = "test!";
	// sendDeposit("wlgwa.xtb.com", 9443, 969, "5wFjifo", accId, amount,
	// explanation);
	// }

}