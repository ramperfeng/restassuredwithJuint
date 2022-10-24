package stepdefs;

import static io.restassured.RestAssured.given;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import tech.grasshopper.filter.ExtentRestAssuredFilter;

public class CityDataStepDefinitions {

	private Response response;
	private ValidatableResponse json;
	private RequestSpecification request;
	private static String ENDPOINT = "https://api.waqi.info/feed/";
	private String cityName;

	@Given("air data for {word} exists")
	public void city_air_data_for_london_exists(String city) {
		cityName = city;
		request = given().param("token", "1a5d8f027e344fa191b88e966bdb5d4aa4853d4a").cookie("Cookie 1", "cookie one")
				.cookie("Cookie 2", "cookie two").filter(new ExtentRestAssuredFilter());
	}

	@When("retrieve the air data")
	public void retrieve_the_city_air_data() {
		response = request.when().get(ENDPOINT + cityName + "/");
		// System.out.println("response: " + response.prettyPrint());
	}

	@Then("the air data status code is {int}")
	public void verify_status_code(int statusCode) {
		json = response.then().statusCode(statusCode);
	}

	@Given("step one")
	@When("step two")
	@Then("step three")
	public void commonStep() {

	}
}