
@INT-006
@PetCreation
@Automation
@SmokeTest
Feature: Get Pet Details from PetStore
  @petCreate
  Scenario Outline: <TCNO> Request valid pet details to the server <scenario>
    Given  User provided valid pet details "<payload>" for petCreation
    When  User request the petCreation endpoint with "<payload>"
    Then petCreation end point expected "<responsePayload>" as success response

    Examples:
      |TCNO                 | payload             |responsePayload     |scenario                                                 |
      |API_006_TS1_TC01     | createPayload1      | createResponse1    |Creating the pet details based on user provided details  |

