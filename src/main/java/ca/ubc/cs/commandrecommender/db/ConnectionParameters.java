package ca.ubc.cs.commandrecommender.db;

public class ConnectionParameters {
    private final String dbUrl;
    private final String dbUser;
    private final int dbPort;
    private final String dbPassword;
    private final String dBName;

    public ConnectionParameters(String dbUrl, String dbUser, int dbPort, String dbPassword, String cmdDbName) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPort = dbPort;
        this.dbPassword = dbPassword;
        this.dBName = cmdDbName;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public int getDbPort() {
        return dbPort;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getdBName() {
        return dBName;
    }
}
