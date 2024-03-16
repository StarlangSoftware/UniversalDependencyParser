package Parser.TransitionBasedParser;

import DependencyParser.Universal.UniversalDependencyType;

public class Candidate {

    private final Command command;
    private final UniversalDependencyType universalDependencyType;

    public Candidate(Command command, UniversalDependencyType universalDependencyType){
        this.command = command;
        this.universalDependencyType = universalDependencyType;
    }

    public Command getCommand() {
        return command;
    }

    public UniversalDependencyType getUniversalDependencyType() {
        return universalDependencyType;
    }

}
