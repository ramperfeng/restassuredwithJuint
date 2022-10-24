package stepdefs;

import com.jayway.restassured.response.Response;
import org.junit.Assert;
import service.petService;
import utils.ExcelReader;
import utils.Helper;
import java.util.HashMap;
import java.util.Random;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class petCreationStepDefination {
    String API_NAME = "petCreation";
    private ThreadLocal<String> PetID = new ThreadLocal<>();
    private ThreadLocal<String> petCreationPayload = new ThreadLocal<>();
    private ThreadLocal<petService> weatherService = new ThreadLocal<petService>() {
        public petService initialValue() {
            return new petService();
        }
    };
    public Helper restHelper = new Helper();

    public static String  end = System.getProperty("URI");


    public Response result;


    @Given("User provided valid pet details {string} for petCreation")
    public void user_provided_valid_pet_details_for_pet_creation(String payload) {
       String  inputPayload= ExcelReader.readXlsJONFile(payload,API_NAME,"DATA");
        inputPayload=ExcelReader.jsonFormat(inputPayload);
        Random rand = new Random();
        String id = String.format("%04d", rand.nextInt(10000));
        PetID.set(id);
        petCreationPayload.set(inputPayload.replace("creatid",PetID.get()));
        System.out.println("Request payload ---------"+petCreationPayload.get());

        if((payload.contains("FieldMissing"))==true)
        {
            ExcelReader.mandetroryorEmptyFieldValidate.set("N");
        }
    }
    @When("User request the petCreation endpoint with {string}")
    public void user_request_the_pet_creation_endpoint_with(String payload) throws Throwable {
        HashMap<String, String> weatherHeaders = weatherService.get().setDefaultHeaders();
        String endpoint="https://petstore.swagger.io/v2/pet";
        System.out.println("Actual Endpoint requested :>>>>>."+endpoint);
        result = restHelper.PostMessageByMessageBody(endpoint, petCreationPayload.get(),weatherHeaders);
        //basePage.injectErrorToCucumberReport("Requested endPoint : " + end+weatherService.get().getPetIDDetails(petId));
        //basePage.injectErrorToCucumberReport("Actual Response From Server" + result.getBody().asString());
    }

    @Then("petCreation end point expected {string} as success response")
    public void pet_creation_end_point_expected_as_success_response(String responsePayload) {
        weatherService.get().verifySuccessResponseStatusCode(result);
        System.out.println("Actual Response from the Server :"+result.getBody().asString());
        System.out.println("Actual Pet from the Request>>>>>>>:"+PetID.get());
        Assert.assertTrue(result.getBody().asString().contains(PetID.get()));
        //ExcelReader.readMultipleRecordsXlsJSONFile(responsePayload, API_NAME, "RESPONSE", result.getBody().asString());

    }
}
