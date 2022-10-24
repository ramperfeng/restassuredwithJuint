package stepdefs;

import com.jayway.restassured.response.Response;
import service.petService;
import utils.ExcelReader;
import utils.Helper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


import java.util.HashMap;

public class samplePetStepDefination {

    String API_NAME = "samplePet";
    String PetID;
    private ThreadLocal<String> resourceReqPayload = new ThreadLocal<>();
    private ThreadLocal<petService> weatherService = new ThreadLocal<petService>() {
        public petService initialValue() {
            return new petService();
        }
    };

    //public static String uri=System.getProperty("CUSTOMER_SERVICE_"+System.getProperty("ENVIRONMENT")+"_URI");
    public static String  end = System.getProperty("URI");
    //public static String  end11 = System.getProperty("RES");
    public Response result;
    public Helper restHelper = new Helper();

    @Given("User provided valid pet details {string}")
    public void petDetails(String petId) {
        PetID = petId;
        System.out.println("pet ID details "+PetID);
    }
    @When("User request the actual server details with pet details {string}")
    public void executeGet(String petId) throws Throwable {
        HashMap<String, String> weatherHeaders = weatherService.get().setDefaultHeaders();
        String endpoint="https://petstore.swagger.io/v2/pet/9989";
        System.out.println("Actual Endpoint requested :>>>>>."+endpoint);
        result = restHelper.GetMesaage(endpoint, weatherHeaders);
        //basePage.injectErrorToCucumberReport("Requested endPoint : " + end+weatherService.get().getPetIDDetails(petId));
        //basePage.injectErrorToCucumberReport("Actual Response From Server" + result.getBody().asString());
    }

    @Then("Return Success response as {string} response")
    public void getPetResponse(String responsePayload) {
        weatherService.get().verifySuccessResponseStatusCode(result);
        //basePage.injectErrorToCucumberReport("Actual Response Status code " + result.getStatusCode());
        //basePage.injectErrorToCucumberReport("Actual Response From Server  " + result.getBody().asString());
        ExcelReader.readMultipleRecordsXlsJSONFile(responsePayload, API_NAME, "RESPONSE", result.getBody().asString());

    }
}
