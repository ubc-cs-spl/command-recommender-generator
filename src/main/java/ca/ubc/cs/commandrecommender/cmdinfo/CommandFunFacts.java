package ca.ubc.cs.commandrecommender.cmdinfo;

/**
 * Encapsulate fun facts related to a command
 * Created by KeEr on 2014-08-26.
 */
public class CommandFunFacts {

    private final String cmdId;
    private final int useCount;
    private final int userCount;

    public CommandFunFacts(String cmdId, int useCount, int userCount) {
        this.cmdId = cmdId;
        this.useCount = useCount;
        this.userCount = userCount;
    }

    public int getUserCount() {
        return userCount;
    }

    public int getUseCount() {
        return useCount;
    }

    public String getCmdId() {
        return cmdId;
    }
}
