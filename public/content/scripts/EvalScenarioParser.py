import json
from dataclasses import dataclass, field
from typing import List, Dict, Optional, Any
from pathlib import Path


@dataclass
class Position:
    x: float
    y: float
    z: float


@dataclass
class Iteration:
    ID: int
    initial_positions_of_agents: Dict[str, Position] = field(default_factory=dict)
    ranging_samples_between_agents: Dict[str, Dict[str, Any]] = field(default_factory=dict)
    ranging_samples_to_anchors: Dict[str, Dict[str, Any]] = field(default_factory=dict)


@dataclass
class EvaluationScenario:
    Agents: List[int]
    Anchors: List[Any]
    true_positions_of_agents: Dict[str, Position] = field(default_factory=dict)
    true_positions_of_anchors: Dict[str, Position] = field(default_factory=dict)
    Iterations: List[Iteration] = field(default_factory=list)

    def get_iteration(self, iteration_id: int) -> Optional[Iteration]:
        """Retrieve an iteration by ID."""
        for iteration in self.Iterations:
            if iteration.ID == iteration_id:
                return iteration
        return None


def get_node_setup_from_json_db(db_file_path: str) -> None:
    """Parse and display node setup information from JSON database."""
    try:
        print("Parsing Node JSON DB")

        # Read the JSON file
        json_string = Path(db_file_path).read_text()
        data = json.loads(json_string)

        # Deserialize JSON into EvaluationScenario
        evaluation_scenario = EvaluationScenario(
            Agents=data["Agents"],
            Anchors=data["Anchors"],
            true_positions_of_agents={
                k: Position(**v) for k, v in data["True Positions of Agents"].items()
            },
            true_positions_of_anchors={
                k: Position(**v) for k, v in data["True Positions of Anchors"].items()
            },
            Iterations=[
                Iteration(
                    ID=iteration["ID"],
                    initial_positions_of_agents={
                        k: Position(**v) for k, v in iteration["Initial positions of Agents"].items()
                    },
                    ranging_samples_between_agents=iteration.get("Ranging samples between Agents", {}),
                    ranging_samples_to_anchors=iteration.get("Ranging samples to Anchors", {})
                )
                for iteration in data["Iterations"]
            ]
        )

        print("\nNodes in scenario:")
        print(evaluation_scenario.Agents)
        print(evaluation_scenario.Anchors)

        print("\nTrue Positions of Agents")
        for node_id, node_pos in evaluation_scenario.true_positions_of_agents.items():
            print(f"Agent: {node_id} x: {node_pos.x}, y: {node_pos.y}, z: {node_pos.z}")

        print("\nTrue Positions of Anchors")
        for node_id, node_pos in evaluation_scenario.true_positions_of_anchors.items():
            print(f"Anchor: {node_id} x: {node_pos.x}, y: {node_pos.y}, z: {node_pos.z}")

        # Display iteration data
        for iteration_id in range(1, 3):
            iteration = evaluation_scenario.get_iteration(iteration_id)
            if iteration:
                print("\n====================")
                print(f"Data for iteration: {iteration.ID}")

                print("\nInitial positions of Agents")
                for agent_id, agent_init_pos in iteration.initial_positions_of_agents.items():
                    print(f"Agent: {agent_id} x: {agent_init_pos.x}, y: {agent_init_pos.y}, z: {agent_init_pos.z}")

                print("\nRanging samples between Agents")
                print(iteration.ranging_samples_between_agents)

                print("\nRanging samples to Anchors")
                print(iteration.ranging_samples_to_anchors)
            else:
                print(f"Iteration with ID {iteration_id} not found")

    except Exception as e:
        print("An error occurred:", e)
        print("Exiting due to an exception")


if __name__ == "__main__":
    db_file_path = "/Users/geogouz/Desktop/eval scenarios/Local/TWR-CLOUD Bern Eval Scenarios/6905.json"
    get_node_setup_from_json_db(db_file_path)
