package service;

import org.junit.Assert;

import java.util.HashMap;
import com.jayway.restassured.response.Response;
public class petService {

    int SUCCESS=200;
    int CREATE=201;
    public HashMap<String,String> setDefaultHeaders()
    {
        HashMap<String,String> headersMap=new HashMap<>();
        headersMap.put("XAPISOURCE","MCA");
        headersMap.put("accept","application/json");
        return headersMap;
    }

    public void verifySuccessResponseStatusCode(Response response)
    {
        Assert.assertEquals("Unexpected response code from server !",SUCCESS,response.getStatusCode());
    }
    public void verifySuccessResponseStatusCodeForCreate(Response response)
    {
        Assert.assertEquals("Unexpected response code from server !", CREATE,response.getStatusCode());
    }
    public String getPetIDDetails(String petId)
    {
        String getPetIDEndpoint=null;
        getPetIDEndpoint=System.getProperty("GET_PET");
        getPetIDEndpoint=getPetIDEndpoint.replace("{petID}",petId);
        return getPetIDEndpoint;
    }
}
