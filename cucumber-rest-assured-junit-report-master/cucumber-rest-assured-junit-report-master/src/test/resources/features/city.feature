@city
Feature: Get city airdata

  Scenario: Simple cuke test
    Given step one
    When step two
    Then step three

  Scenario: User calls web service to get london air data
    Given air data for london exists
    When retrieve the air data
    Then the air data status code is 200

  Scenario Outline: User calls web service to get <city> air data
    Given air data for <city> exists
    When retrieve the air data
    Then the air data status code is <code>

    Examples: 
      | city   | code |
      | munich |  200 |
      | paris  |  200 |
      | rome   |  200 |
