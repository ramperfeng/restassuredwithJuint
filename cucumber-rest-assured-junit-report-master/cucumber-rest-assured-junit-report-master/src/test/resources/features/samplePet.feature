
@INT-006
@PurchaseOrder
@EPRFI
@PetStoreGet
@SmokeTest
Feature: Get Pet Details from PetStore
  @getPet
  Scenario Outline: <TCNO> Request valid pet details to the server
    Given  User provided valid pet details "<petId>"
    When  User request the actual server details with pet details "<petId>"
    Then Return Success response as "<responsePayload>" response

    Examples:
      |TCNO                 | petId  |responsePayload      |scenario                                   |
      |API_006_TS1_TC01     | 9989   | pet9989             |Retrieve the Pet details based on pet id   |

