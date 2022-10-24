package stepdefs;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import tech.grasshopper.filter.ExtentRestAssuredFilter;

public class ParametersStepDefinitions {

	private Response response;
	private ValidatableResponse json;
	private RequestSpecification request;
	private String MOCKY_ENDPOINT = "https://run.mocky.io/v3/e2ba1326-1863-40da-9666-ff70661b2111";

	@Given("prepare data for submission")
	public void prepare_data_for_submission() throws Exception {

		File file = new File("src/test/resources/multi.txt");
		InputStream stream = new FileInputStream(file);

		Path path = Paths.get("src/test/resources/multi.txt");
		byte[] data = Files.readAllBytes(path);

		String longStuff = "Hello How are you? What is happening with you? Hello How are you? What is happening with you? Hello How are you? What is happening with you? Hello How are you? What is happening with you? Hello How are you? What is happening with you? Hello How are you? What is happening with you? Hello How are you? What is happening with you? ";

		request = given().contentType(ContentType.MULTIPART).param("hello", "hello").multiPart("name", "Jane Doe")
				.multiPart("occupation", "automation").multiPart("long one", longStuff).multiPart(file)
				.multiPart("stream", "multistream.txt", stream).multiPart("bytes", "multibyte.txt", data)
				.filter(new ExtentRestAssuredFilter());
	}

	@Given("prepare parameter data for submission")
	public void prepare_parameter_data_for_submission() throws Exception {

		request = given().contentType(ContentType.JSON).param("hello", "hello").formParam("form", "form")
				.queryParam("query", "query").filter(new ExtentRestAssuredFilter());
	}

	@When("retrieve the mocked data")
	public void retrieve_the_mocked_data() {
		response = request.when().get(MOCKY_ENDPOINT);
		// System.out.println("response: " + response.prettyPrint());
	}

	@Then("verify status code is {int}")
	public void verify_status_code(int statusCode) {
		json = response.then().statusCode(statusCode);
	}
}
