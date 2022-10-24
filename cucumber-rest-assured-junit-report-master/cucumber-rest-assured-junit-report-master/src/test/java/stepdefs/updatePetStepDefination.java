package stepdefs;

import com.jayway.restassured.response.Response;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import service.petService;
import utils.ExcelReader;
import utils.Helper;

import java.util.HashMap;
import java.util.Random;

public class updatePetStepDefination {

    String API_NAME = "petCreation";
    String API_NAME1 = "samplePet";
    String API_NAME2 = "updatePet";
    private ThreadLocal<String> PetID = new ThreadLocal<>();
    private ThreadLocal<String> updatedPetName = new ThreadLocal<>();
    private ThreadLocal<String> petCreationPayload = new ThreadLocal<>();
    private ThreadLocal<String> petUpdatePayload = new ThreadLocal<>();
    private ThreadLocal<petService> weatherService = new ThreadLocal<petService>() {
        public petService initialValue() {
            return new petService();
        }
    };
    private ThreadLocal<String> resourceReqPayload = new ThreadLocal<>();
    public Response result;
    public Helper restHelper = new Helper();
    public static String  end = System.getProperty("URI");

    private io.restassured.response.Response response;
    private ValidatableResponse json;
    private RequestSpecification request;

    @Given("create a pet based on as per details provided by user {string} for petCreation")
    public void userProvidedPetDetails(String payload) {
       String  inputPayload=ExcelReader.readXlsJONFile(payload,API_NAME,"DATA");
        inputPayload=ExcelReader.jsonFormat(inputPayload);
        Random rand = new Random();
        String id = String.format("%04d", rand.nextInt(10000));
        PetID.set(id);
        petCreationPayload.set(inputPayload.replace("creatid",PetID.get()));
        System.out.println("Request payload ---------"+petCreationPayload.get());
       System.out.println("Resource Request Payload : "+petCreationPayload.get());
        if((payload.contains("FieldMissing"))==true)
        {
            ExcelReader.mandetroryorEmptyFieldValidate.set("N");
        }
        HashMap<String, String> weatherHeaders = weatherService.get().setDefaultHeaders();
        String createEndPoint="https://petstore.swagger.io/v2/pet";
        System.out.println("Actual Endpoint requested :>>>>>."+createEndPoint);
        result = restHelper.PostMessageByMessageBody(createEndPoint, petCreationPayload.get(),weatherHeaders);
       // response = restHelper.PostMessageByMessageBody(createEndPoint, petCreationPayload.get(),weatherHeaders);
    }
    @Then("create endpoint api return the success as {string} as response")
    public void petCreationResponse(String createResponse) {
        weatherService.get().verifySuccessResponseStatusCode(result);
        System.out.println("Actual Response from the Server :"+result.getBody().asString());
        System.out.println("Actual Pet from the Request>>>>>>>:"+PetID.get());
        Assert.assertTrue(result.getBody().asString().contains(PetID.get()));
        System.out.println("Actual Response Status code " + result.getStatusCode());
        System.out.println("Actual Response From Server  " + result.getBody().asString());
        //ExcelReader.readMultipleRecordsXlsJSONFile(responsePayload, API_NAME, "RESPONSE", result.getBody().asString());

    }
    @Given("User able to retrieve the pet details using petId")
    public void executeGet() throws Throwable {
        System.out.println("pet ID details "+PetID.get());
        HashMap<String, String> weatherHeaders = weatherService.get().setDefaultHeaders();
        String getEndPoint="https://petstore.swagger.io/v2/pet/"+PetID.get();
        result = restHelper.GetMesaage(getEndPoint, weatherHeaders);
        System.out.println("Actual response for get endpoint :>>>>>."+ result.getBody().asString());
    }
    @Given("get endpoint api return the {string} response based on petID")
    public void getPetResponse(String responsePayload) {
        weatherService.get().verifySuccessResponseStatusCode(result);
        Assert.assertTrue(result.getBody().asString().contains(PetID.get()));

    }
    @Given("user able to update pet details for as per requirement {string} for update")
    public void petUpdatePayload(String payload) {
        String inputPayload = ExcelReader.readXlsJONFile(payload, API_NAME2, "DATA");
        inputPayload = ExcelReader.jsonFormat(inputPayload);
        petUpdatePayload.set(inputPayload.replace("creatid", PetID.get()));
        System.out.println("Request payload ---------" + petUpdatePayload.get());
        System.out.println("Resource Request Payload : " + petUpdatePayload.get());
        if ((payload.contains("FieldMissing")) == true) {
            ExcelReader.mandetroryorEmptyFieldValidate.set("N");
        }
    }

    @When("user request the pet update details endpoint with {string}")
    public void executePostForUpdatePetEndPoint(String payload)    {
        HashMap<String, String> weatherHeaders = weatherService.get().setDefaultHeaders();
        String updateEndpoint="https://petstore.swagger.io/v2/pet";
        System.out.println("Actual Endpoint requested :>>>>>."+updateEndpoint);
        result = restHelper.PUTMessageByMessageBody(updateEndpoint, petUpdatePayload.get(),weatherHeaders);

    }
        @Then("update details endpoint api return expected {string} as response")
       public void petUpdateResponse(String responsePayload) {

        weatherService.get().verifySuccessResponseStatusCode(result);
        Assert.assertTrue(result.getBody().asString().contains(PetID.get()));

    }
    @Then("get endpoint api return the updated response based on petID")
    public void UpdatePetGetResponse() {
        weatherService.get().verifySuccessResponseStatusCode(result);
        try{
            Assert.assertTrue(result.getBody().asString().contains(PetID.get()));
            System.out.println("petDetails returned by server :" + PetID.get());
        }catch(AssertionError e){
            System.out.println("petDetails not returned by server :" + PetID.get());
            throw e;
        }

        try{
            Assert.assertTrue(result.getBody().asString().contains("UpdatedPetDetails"));
            System.out.println("updated petDetails returned by server :" + "UpdatedPetDetails");
        }catch(AssertionError e){
            System.out.println("updated petDetails not returned by server :" + "UpdatedPetDetails");
            throw e;
        }

    }
}
