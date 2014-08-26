package ca.ubc.cs.commandrecommender.generator;

import ca.ubc.cs.commandrecommender.model.ToolUseCollection;
import ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance;
import ca.ubc.cs.commandrecommender.model.learning.SequenceSet;
import ca.ubc.cs.commandrecommender.model.learning.Transaction;

import java.util.List;

/**
 * The base class for algorithms where we take into account of the order in
 * which tools or commands are used or "discovered." Filtering of data with the
 * use of {@link ca.ubc.cs.commandrecommender.model.acceptance.AbstractLearningAcceptance}
 * is involved to specify when counts as a discovery sequence in the usage data collected
 */
public abstract class AbstractFilteredLearningRecGen extends AbstractRecGen {

    protected SequenceSet trainer = new SequenceSet();

    private AbstractLearningAcceptance acceptance;

    public AbstractFilteredLearningRecGen(String algorithm,
                                          AbstractLearningAcceptance acceptance) {
        super(algorithm);
        this.acceptance = acceptance;
    }

    /**
     * Parse data usage into {@link ca.ubc.cs.commandrecommender.generator.AbstractFilteredLearningRecGen#trainer} and
     * filter data according to {@link ca.ubc.cs.commandrecommender.generator.AbstractFilteredLearningRecGen#acceptance}
     * @param uses
     */
    @Override
    public void trainWith(ToolUseCollection uses) {
        uses.sort();
        List<Transaction> ts = uses.divideIntoTransactions();
        Transaction last = ts.get(ts.size()-1);

        int count = 0;
        acceptance.filterTransactions(ts,uses.toolsUsedBag());
        for(Transaction t : ts){
            trainer.process(t, count, t==last);
            count++;
        }
    }

    // This method could be useful if we want to get more specific rationale
    protected String getAcceptanceName() {
        String name = acceptance.getClass().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

}
