Narrative:
As a user
I want to sign up on pcgarage.ro
So I can buy stuff later

Scenario: trying to sign up with an already existing user
Given I go to PCGarage home page
When I go to My account
When I sign up
Then I get an error message