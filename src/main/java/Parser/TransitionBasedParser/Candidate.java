package Parser.TransitionBasedParser;

import DependencyParser.Universal.UniversalDependencyType;

public class Candidate {

    private Command command;
    private UniversalDependencyType universalDependencyType;

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
