

@updatePet
@Automation
@SmokeTest
Feature: update pet details
  @updatePet
  Scenario Outline: <TCNO> user update valid pet details to the server <scenario>
    Given  create a pet based on as per details provided by user "<payload>" for petCreation
    And create endpoint api return the success as "<createResponse>" as response
    And  User able to retrieve the pet details using petId
    And get endpoint api return the "<getResponse>" response based on petID
    And user able to update pet details for as per requirement "<updatePayload>" for update
    When user request the pet update details endpoint with "<updatePayload>"
    Then update details endpoint api return expected "<updateResponse>" as response
    Then get endpoint api return the updated response based on petID
    Examples:
      |TCNO                 | payload          |createResponse    | getResponse        | updatePayload  |updateResponse   |updateGetResponse     |scenario                                                 |
      |PUD_TS1_TC01         | createPayload1   | createResponse1  | getResponse1       | updatePayload1 |updateResponse1  | updateGetResponse1   |Creating the pet details based on user provided details  |

