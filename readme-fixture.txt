Running checking account tests (Windows):
    Using File: gradlew runCheckingFixture --args "src/test/resources/CheckingAccountTest.csv"
    Single Scenario: gradlew runCheckingFixture --args "100 10 10 50 130"

Running savings account tests (Windows):
    Using File: gradlew runSavingsFixture --args "src/test/resources/SavingsAccountTest.csv"
    Single Scenario: gradlew runSavingsFixture --args "0 0 10|50 0.01 560"

Command language -- uses argument separation to determine either a file or single scenario.
    One argument implies a file path, more than one argument implies a single scenario.

Files modified/created for assignment:
    - Owner.java, could not use records
    - Any file required to be changed for the assignment
        MODIFIED CheckingAccountTestFixture.java
        MODIFIED CheckingAccountTest.csv
        NEW SavingsAccountTestFixture.java
        NEW SavingsAccountTest.csv
        MODIFIED build.gradle
        NEW readme-fixture.txt (if not uploaded to MyCourses)

What kinds of scenarios would fixture not be good for testing?
    Any file format that is not a csv or similar text file. Moreover,
    any scenario where a text file is not strictly able to adhere
    to the acceptable input format (ie, too large/complex to change).
    Though, in some cases I suppose this would be more of a class issue
    than a test fixture one.