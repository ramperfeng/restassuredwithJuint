@books
Feature: Get book by ISBN

  Scenario: User calls web service to get a book by its ISBN
    Given a book exists with an isbn of 9781451648546
    When a user retrieves the book by isbn
    Then the status code is 200
    And response includes the following
      | totalItems |             1 |
      | kind       | books#volumes |
    And response includes the following in any order
      | items.volumeInfo.title     | Steve Jobs         |
      | items.volumeInfo.publisher | Simon and Schuster |
      | items.volumeInfo.pageCount |                630 |

  Scenario Outline: ISBN Google Base
    Given a book exists with an isbn of <isbn>
    When a user retrieves the book by isbn
    Then the status code is <code>

    Examples: 
      | isbn          | code |
      | 9781451648546 |  200 |
      |        432877 |  404 |

  Scenario: User calls web service to get a non existant book by its ISBN
    Given a book exists with an isbn of 432877
    When a user retrieves the book by isbn
    Then the status code is 404

  Scenario: User ISBN 2 calls
    Given a book exists with an isbn of 9781451648546
    When a user retrieves the book by isbn
    Then the status code is 200
    Given a book exists with an isbn of 432877
    When a user retrieves the book by isbn
    Then the status code is 404
