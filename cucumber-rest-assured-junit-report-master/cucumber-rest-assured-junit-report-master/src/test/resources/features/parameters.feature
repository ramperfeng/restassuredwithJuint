@parameter
Feature: Parameters and multi part form data

  Scenario: Multi part form data display
    Given prepare data for submission
    When retrieve the mocked data
    Then verify status code is 200

  Scenario: Parameter data display
    Given prepare parameter data for submission
    When retrieve the mocked data
    Then verify status code is 200
