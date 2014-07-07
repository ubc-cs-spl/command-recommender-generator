package ca.ubc.cs.commandrecommender.model;

import org.bson.BasicBSONObject;

/**
 * Rationale for a recommendations
 * //TODO this is mostly a stub.
 *
 * Created by KeEr on 2014-07-07.
 */
public class Rationale extends BasicBSONObject {

    public static final String USER_BASED_CF_INFO = "user based CF";
    public static final String LINTON_PERCENT_USAGE = "linton percent";
    double value;

    public Rationale() {
    }

    public Rationale(double value) {
        setValue(value);
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

}
