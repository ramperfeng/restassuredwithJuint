package stepdefs;


import hooks.BasePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import service.DummypetService;
import service.petService;
import utils.DummyExcelReader;
import utils.DummyHelper;
import utils.ExcelReader;
import io.restassured.response.Response;
import utils.configReader;

import java.util.HashMap;
import java.util.Random;

public class UPStepDefination  {
    public BasePage basePage = new BasePage();
    configReader configR = new configReader();
    String API_NAME = "petCreation";
    String API_NAME1 = "samplePet";
    String API_NAME2 = "updatePet";
    private ThreadLocal<String> PetID = new ThreadLocal<>();
    private ThreadLocal<String> updatedPetName = new ThreadLocal<>();
    private ThreadLocal<String> petCreationPayload = new ThreadLocal<>();
    private ThreadLocal<String> petUpdatePayload = new ThreadLocal<>();
    private ThreadLocal<DummypetService> petsrvice = new ThreadLocal<DummypetService>() {
        public DummypetService initialValue() {
            return new DummypetService();
        }
    };
    private ThreadLocal<String> resourceReqPayload = new ThreadLocal<>();
    public Response result;
    public DummyHelper dmRestHelper = new DummyHelper();
    public static String  end = System.getProperty("URI");

    private io.restassured.response.Response response;
    private ValidatableResponse json;
    private RequestSpecification request;

    @Given("create a pet based on as per details provided by user {string} for petCreations")
    public void userProvidedPetDetails(String payload) {
        System.out.println("Value from Config file ----:"+configR.getENVIRONMENT());

       String  inputPayload= DummyExcelReader.readXlsJONFile(payload,API_NAME,"DATA");
        inputPayload=ExcelReader.jsonFormat(inputPayload);
        Random rand = new Random();
        String id = String.format("%04d", rand.nextInt(10000));
        PetID.set(id);
        petCreationPayload.set(inputPayload.replace("creatid",PetID.get()));
        System.out.println("Request payload ---------"+petCreationPayload.get());
        basePage.injectErrorToCucumberReport("Resource Request Payload : "+petCreationPayload.get());
       System.out.println("Resource Request Payload : "+petCreationPayload.get());
        if((payload.contains("FieldMissing"))==true)
        {
            ExcelReader.mandetroryorEmptyFieldValidate.set("N");
        }
        HashMap<String, String> weatherHeaders = petsrvice.get().setDefaultHeaders();
        String createEndPoint="https://petstore.swagger.io/v2/pet";
        System.out.println("Actual Endpoint requested :>>>>>."+createEndPoint);
        response = dmRestHelper.PostMessageByMessageBody(createEndPoint, petCreationPayload.get(),weatherHeaders);
    }
    @Then("create endpoint api return the success as {string} as responses")
    public void petCreationResponse(String createResponse) {
       // petsrvice.get().verifySuccessResponseStatusCode(result);
        json = response.then().statusCode(200);

        Assert.assertTrue(response.getBody().asString().contains(PetID.get()));

    }
    @Given("User able to retrieve the pet details using petIds")
    public void executeGet() throws Throwable {
        System.out.println("pet ID details "+PetID.get());
        HashMap<String, String> weatherHeaders = petsrvice.get().setDefaultHeaders();
        String getEndPoint="https://petstore.swagger.io/v2/pet/"+PetID.get();
        result = dmRestHelper.GetMesaage(getEndPoint, weatherHeaders);
        System.out.println("Actual response for get endpoint :>>>>>."+ result.getBody().asString());
    }
    @Given("get endpoint api return the {string} response based on petIDs")
    public void getPetResponse(String responsePayload) {
        petsrvice.get().verifySuccessResponseStatusCode(result);
        Assert.assertTrue(result.getBody().asString().contains(PetID.get()));

    }
    @Given("user able to update pet details for as per requirement {string} for updates")
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

    @When("user request the pet update details endpoint withs {string}")
    public void executePostForUpdatePetEndPoint(String payload)    {
        HashMap<String, String> weatherHeaders = petsrvice.get().setDefaultHeaders();
        String updateEndpoint="https://petstore.swagger.io/v2/pet";
        System.out.println("Actual Endpoint requested :>>>>>."+updateEndpoint);
        result = dmRestHelper.PUTMessageByMessageBody(updateEndpoint, petUpdatePayload.get(),weatherHeaders);

    }
        @Then("update details endpoint api return expected {string} as responses")
       public void petUpdateResponse(String responsePayload) {

            petsrvice.get().verifySuccessResponseStatusCode(result);
        Assert.assertTrue(result.getBody().asString().contains(PetID.get()));

    }
    @Then("get endpoint api return the updated response based on petIDs")
    public void UpdatePetGetResponse() {
        petsrvice.get().verifySuccessResponseStatusCode(result);
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
