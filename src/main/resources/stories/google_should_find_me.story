
Scenario: Google should search find my profile

Given I am visiting the Googles
When I search for 'Chris Kozak'
Then I should see my profile
