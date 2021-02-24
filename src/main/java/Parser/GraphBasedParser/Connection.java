package Parser.GraphBasedParser;/* Created by oguzkeremyildiz on 11.02.2021 */

import DependencyParser.Universal.UniversalDependencyTreeBankWord;

public class Connection {

    private final UniversalDependencyTreeBankWord from;
    private final UniversalDependencyTreeBankWord to;
    private final double length;

    public Connection(UniversalDependencyTreeBankWord from, UniversalDependencyTreeBankWord to, double length) {
        this.from = from;
        this.to = to;
        this.length = length;
    }

    public UniversalDependencyTreeBankWord getFrom() {
        return from;
    }

    public UniversalDependencyTreeBankWord getTo() {
        return to;
    }

    public double getLength() {
        return length;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof  Connection)) {
            return false;
        }
        Connection second = (Connection) obj;
        return this.from.getName().equals(second.from.getName()) && this.from.getId() == second.from.getId() && this.to.getName().equals(second.to.getName()) && this.to.getId() == second.to.getId() && this.length == second.length;
    }

    @Override
    public String toString() {
        return "[" + from.getId() + " " + from.getName() + ", " + to.getId() + " " + to.getName() + ", " + length + "]";
    }
}
