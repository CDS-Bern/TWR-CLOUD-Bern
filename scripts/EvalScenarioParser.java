import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;


public class EvalScenarioParser {

    static class EvaluationScenario {
        public List<Integer> Agents;
        public List<Object> Anchors;

        @JsonProperty("True Positions of Agents")
        public Map<String, Position> truePositionsOfAgents;

        @JsonProperty("True Positions of Anchors")
        public Map<String, Position> truePositionsOfAnchors;

        public List<Iteration> Iterations;

        public static class Position {
            public double x;
            public double y;
            public double z;
        }

        public static class Iteration {
            public int ID;

            @JsonProperty("Initial positions of Agents")
            public Map<String, Position> initialPositionsOfAgents;

            @JsonProperty("Ranging samples between Agents")
            public Map<String, Map<String, Object>> rangingSamplesBetweenAgents;

            @JsonProperty("Ranging samples to Anchors")
            public Map<String, Map<String, Object>> rangingSamplesToAnchors;
        }

        // A method to get an Iteration by ID
        public Iteration getIteration(int iteration_id){
            for (Iteration iteration: Iterations){
                if (iteration.ID == iteration_id){
                    return iteration;
                }
            }
            return null; // Return null if the iteration with the given ID is not found
        }
    }

    // Path to the JSON file containing the evaluation scenario data.
    static String dbFilePath = "/Users/geogouz/Desktop/eval scenarios/Local/TWR-CLOUD Bern Eval Scenarios/6905.json";

    /**
     * Parses the evaluation scenario JSON file and outputs the node configuration details.
     * <p>
     * This method reads a JSON file containing information about nodes (agents and anchors) in an evaluation scenario.
     * It parses the file to extract and print:
     * <ul>
     *     <li>Lists of agents and anchors in the scenario</li>
     *     <li>True positions of agents and anchors</li>
     *     <li>Initial positions and ranging samples for each iteration in the scenario</li>
     * </ul>
     * </p>
     * <p>
     * Each parsed entry is displayed in the console, with structured data printed for each entity and iteration.
     * This allows for easy verification of node setup details, including positions and sample data.
     * </p>
     *
     * @throws Exception if there is an error during file reading or parsing.
     */
    static void getNodeSetupFromJsonDB() {
        try {
            System.out.println("Parsing Node JSON DB");

            // Read the entire content of the file into a String
            String jsonString = Files.readString(Path.of(dbFilePath));

            ObjectMapper mapper = new ObjectMapper();
            EvaluationScenario evaluationScenarioDatabase = mapper.readValue(jsonString, EvaluationScenario.class);

            System.out.println("\nNodes in scenario:");
            System.out.println(evaluationScenarioDatabase.Agents);
            System.out.println(evaluationScenarioDatabase.Anchors);

            System.out.println("\nTrue Positions of Agents");
            for (Map.Entry<String, EvaluationScenario.Position> entry :
                    evaluationScenarioDatabase.truePositionsOfAgents.entrySet()) {
                String nodeId = entry.getKey();
                EvaluationScenario.Position nodePos = entry.getValue();
                System.out.println(
                        "Agent: " + nodeId + " x: " + nodePos.x + ", y: " + nodePos.y + ", z: " + nodePos.z
                );
            }

            System.out.println("\nTrue Positions of Anchors");
            for (Map.Entry<String, EvaluationScenario.Position> entry :
                    evaluationScenarioDatabase.truePositionsOfAnchors.entrySet()) {
                String nodeId = entry.getKey();
                EvaluationScenario.Position nodePos = entry.getValue();
                System.out.println(
                        "Anchor: " + nodeId + " x: " + nodePos.x + ", y: " + nodePos.y + ", z: " + nodePos.z
                );
            }

            // Get the iteration data, holding all measurements and initializations.
            for (int iteration = 1; iteration <= 2; iteration++) {
                EvaluationScenario.Iteration evaluationScenarioIteration = evaluationScenarioDatabase.getIteration(iteration);
                System.out.println("\n====================");
                System.out.println("Data for iteration: " + evaluationScenarioIteration.ID);

                System.out.println("\nInitial positions of Agents");
                for (Map.Entry<String, EvaluationScenario.Position> entry : evaluationScenarioIteration.initialPositionsOfAgents.entrySet()) {
                    String agentId = entry.getKey();
                    EvaluationScenario.Position agentInitPos = entry.getValue();
                    System.out.println(
                            "Agent: " + agentId + " x: " + agentInitPos.x + ", y: " + agentInitPos.y + ", z: " + agentInitPos.z
                    );
                }

                System.out.println("\nRanging samples between Agents");
                System.out.println(evaluationScenarioIteration.rangingSamplesBetweenAgents);

                System.out.println("\nRanging samples to Anchors");
                System.out.println(evaluationScenarioIteration.rangingSamplesToAnchors);
            }

        } catch (Exception e) {
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            for (StackTraceElement ste : stackTraceElements) {
                System.out.println(ste.toString());
            }
            System.out.println(e.getLocalizedMessage() + "\nExiting from 1");
        }
    }

    public static void main(String[] args) {
        getNodeSetupFromJsonDB();
    }
}