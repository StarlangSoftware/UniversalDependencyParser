package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 5.12.2020 */

import DependencyParser.Universal.UniversalDependencyType;

public class Decision extends Candidate{

    private double point;

    public Decision(Command command, UniversalDependencyType relation, double point) {
        super(command, relation);
        this.point = point;
    }

    public double getPoint() {
        return point;
    }
}
