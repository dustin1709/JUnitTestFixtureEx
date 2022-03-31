import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SavingsAccountTestFixture {
    public static Logger logger = LogManager.getLogger(SavingsAccountTestFixture.class);

    static final String TEST_FILE = "src/test/resources/SavingsAccountTest.csv";

    record TestScenario(double initBalance,
                        List<Double> withdrawals,
                        List<Double> deposits,
                        double interest,
                        boolean runMonthEnd,
                        double endBalance
    ) { }

    private static List<TestScenario> testScenarios;

    @Test
    public void runTestScenarios() throws Exception {
        assertThat("[SavingsAccountTestFixture] testScenarios object must be populated, is this running from main()?",
                testScenarios, notNullValue());
        for (int testNum = 0; testNum < testScenarios.size(); testNum++) {
            TestScenario scenario = testScenarios.get(testNum);
            logger.info("**** Running test for {}", scenario);

            SavingsAccount sa = new SavingsAccount(
                "test "+testNum, scenario.initBalance, scenario.interest, new Owner("TEST_"+testNum));
            
            // now process withdrawals, deposits
            for (double withdrawalAmount : scenario.withdrawals) {
                sa.withdraw(withdrawalAmount);
            }
            for (double depositAmount : scenario.deposits) {
                sa.deposit(depositAmount);
            }

            // run month-end if desired and output register
            if (scenario.runMonthEnd) {
                sa.monthEnd();
                for (Map.Entry<String, Double> entry : sa.getRegisterEntries()) {
                    logger.info("Register Entry -- {}: {}", entry.getKey(), entry.getValue());

                }
            }

            // make sure the balance is correct
            assertThat("Test #" + testNum + ":" + scenario, sa.getBalance(), is(scenario.endBalance));
        }
    }

    private static void runJunitTests() {
        JUnitCore jc = new JUnitCore();
        jc.addListener(new TextListener(System.out));
        Result r = jc.run(SavingsAccountTestFixture.class);
        System.out.printf("[SavingsAccountTestFixture] Tests run: %d Passed: %d Failed: %d\n",
                r.getRunCount(), r.getRunCount() - r.getFailureCount(), r.getFailureCount());
        System.out.println("Failures:");
        for (Failure f : r.getFailures()) {
            System.out.println("\t"+f);
        }
    }

    // TODO this could be added to TestScenario class
    private static List<Double> parseListOfAmounts(String amounts) {
        if (amounts.trim().isEmpty()) {
            return List.of();
        }
        List<Double> ret = new ArrayList<>();
        logger.debug("Amounts to split: {}", amounts);
        for (String amtStr : amounts.trim().split("\\|")) {
            logger.debug("An Amount: {}", amtStr);
            ret.add(Double.parseDouble(amtStr));
        }
        return ret;
    }

    // TODO this could be added to TestScenario class
    private static TestScenario parseScenarioString(String scenarioAsString) {
        String [] scenarioValues = scenarioAsString.split(",");
        // should probably validate length here
        double initialBalance = Double.parseDouble(scenarioValues[0]);
        List<Double> wds = parseListOfAmounts(scenarioValues[1]);
        List<Double> deps = parseListOfAmounts(scenarioValues[2]);
        double intrst = Double.parseDouble(scenarioValues[3]);
        double finalBalance = Double.parseDouble(scenarioValues[4]);
        TestScenario scenario = new TestScenario(
                initialBalance, wds, deps, intrst, false, finalBalance
        );
        return scenario;
    }

    private static List<TestScenario> parseScenarioStrings(String ... scenarioStrings) {
        logger.info("[SavingsAccountTestFixture] Running test scenarios...");
        List<TestScenario> scenarios = new ArrayList<>();
        for (String scenarioAsString : scenarioStrings) {
            if (scenarioAsString.trim().isEmpty()) {
                continue;
            }
            TestScenario scenario = parseScenarioString(scenarioAsString);
            scenarios.add(scenario);
        }
        return scenarios;
    }

    public static void main(String [] args) throws IOException {
        System.out.println("[SavingsAccountTestFixture] START");

        if (args.length == 0) {
            System.out.println("\n\n****** FROM FILE [SavingsAccountTestFixture] ******\n");
            // We could get the filename from the cmdline, e.g. "-f CheckingAccountScenarios.csv"
            List<String> scenarioStringsFromFile2 = Files.readAllLines(Paths.get(TEST_FILE));
            testScenarios = parseScenarioStrings(scenarioStringsFromFile2.toArray(String[]::new));
            runJunitTests();
        }
        
        /*
         * Read from cmd line - for a file and single scenario
         * If args.length == 1 means it is a file path
         * If args.length > 1 means it is an input string
         */
        if (args.length == 1) {
            System.out.println("\n\n*** Accepting a file from command line ***\n");
            System.out.println("File path: " + args[0]);
            List<String> scenarioStringsFromFile = Files.readAllLines(Paths.get(args[0]));
            testScenarios = parseScenarioStrings(scenarioStringsFromFile.toArray(String[]::new));
            runJunitTests();
        } else if (args.length > 1) {   
            System.out.println("\n\n*** Accepting a string from command line ***\n");
            System.out.println("(start_balance,check_amt|...,withdraw_amt|...,deposit_amt|...,end_balance): " +
                java.util.Arrays.asList(args));
            String scenarioString = Arrays.toString(args);
            scenarioString = scenarioString.replace("[","");
            scenarioString = scenarioString.replace("]","");
            String [] scenario_strings = {scenarioString};
            testScenarios = parseScenarioStrings(scenario_strings);
            runJunitTests();
        }

        System.out.println("Command-line arguments passed in: " + java.util.Arrays.asList(args));

        System.out.println("[SavingsAccountTestFixture] DONE");
    }
}
