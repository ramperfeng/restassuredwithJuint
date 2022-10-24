package utils;

import static com.jayway.restassured.RestAssured.given;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.ssl.SSLSocketFactory;

import org.junit.Assert;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.SSLConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.slf4j.Logger;

public class Helper {
    public Map<String, String> authenticationHeaders;
    public Logger logman = null;
   String env="ST";


    public Response PostMessageByMessageBody(String ServiceURLPart, String messagebody,
                                             HashMap<String, String> authenticationDetails) {
        Response response = null;
        try {

            if(env.equals("SIT"))
            {
                setKeyStoreAndTrusStoreforHTTPS();
            }
                System.out.println("Before Requesting the API Request Payload : " + messagebody);
                response = given().contentType("application/json\r\n").body(messagebody).post(ServiceURLPart);
                System.out.println("Response from Server inside Post Message Body : ----" + response.getBody().asString());


        } catch (Throwable t) {
            System.out.println("Error Occured inside PostmessageBody fuction while posting at URL= " + ServiceURLPart
                    + ", Messagebody=" + messagebody);
            Assert.assertTrue("Unable to connect to server or error occured inside postmessageByMessageBody function",
                    0 > 1);
        }
        return response;

    }
    public Response PUTMessageByMessageBody(String ServiceURLPart, String messagebody,
                                            HashMap<String, String> authenticationDetails) {
        Response response = null;
        try {
            if(env.equals("SIT")) {
                setKeyStoreAndTrusStoreforHTTPS();
            }
                System.out.println("Before Requesting the API Request Payload : " + messagebody);
                response = given().contentType("application/json\r\n").body(messagebody).put(ServiceURLPart);
                System.out.println("Response from Server inside Post Message Body : ----" + response.getBody().asString());

        } catch (Throwable t) {
            System.out.println("Error Occured inside PostmessageBody fuction while posting at URL= " + ServiceURLPart
                    + ", Messagebody=" + messagebody);
            Assert.assertTrue("Unable to connect to server or error occured inside postmessageByMessageBody function",
                    0 > 1);
        }
        return response;

    }

    public Response GetMesaage(String ServiceURLPart, HashMap<String, String> authenticationDetails) {
        Response response = null;
        try {

             if(env.equals("SIT"))
             {
             setKeyStoreAndTrusStoreforHTTPS();
             }
           /*logman.info("------------------------------- GET URL Start -------------------------"
                    ); logman.debug("GET URL *****************************************="
                    +ServiceURLPart); logman.
                    info("------------------------------- GET URL END -------------------------"
                    );*/
            response = given().contentType(ContentType.JSON).when().relaxedHTTPSValidation().get(ServiceURLPart);
            System.out.println("Response Generated successfully and value =\n\n" + response.asString());

        } catch (Throwable t) {
            System.out.println("Error Occured inside Get Mesage  fuction while posting at URL=" + ServiceURLPart
                    + " ,Error=" + t.getMessage());
            Assert.assertTrue("Unable to connect to server or error occured inside GET Message Function function",
                    0 > 1);
        }
        return response;

    }

    public String returnFileASASingleString(String FilePath) {
        BufferedReader br;
        StringBuilder sb;
        String line;
        String everything = "";
        try {
            br = new BufferedReader(new FileReader(FilePath));
            sb = new StringBuilder();
            line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();
            br.close();
            // logman.info("File Read successfully ,file path="+FilePath);
        } catch (Throwable t) {
            System.out.println("Error occured inside returning FileASAString function for ,file path=" + FilePath);
        }
        return everything;
    }

    public void setKeyStoreAndTrusStoreforHTTPS() {
        KeyStore keyStore = null;
        KeyStore trustStore = null;
        SSLConfig config = null;
        String keyStorecertpath = System.getProperty("user.dir") + "\\src\\test\\resources\\certs\\keyStore.jks";
        String password = "changeit";
        String trustStorepath = System.getProperty("user.dir") + "\\src\\test\\resources\\certs\\trustStore.jks";

        try {
            keyStore = keyStore.getInstance(keyStore.getDefaultType());
            trustStore = keyStore.getInstance(keyStore.getDefaultType());
            keyStore.load(new FileInputStream(keyStorecertpath), password.toCharArray());
            trustStore.load(new FileInputStream(trustStorepath), password.toCharArray());
            org.apache.http.conn.ssl.SSLSocketFactory clientAuthFactory= new org.apache.http.conn.ssl.SSLSocketFactory(keyStore,password,trustStore);
            clientAuthFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            config = new SSLConfig().with().sslSocketFactory(clientAuthFactory).and().allowAllHostnames();
            RestAssured.config = RestAssured.config.sslConfig(config);

        } catch (Exception ex) {
            System.out.println("Error while loading Keystore or truststore >>>>>>>>>>");
            ex.printStackTrace();
        }
    }

}
