package pt.upskill.projeto2.financemanager.accounts.formats;

import pt.upskill.projeto2.financemanager.accounts.StatementLine;

public class SimpleStatementFormat implements StatementLineFormat {

    @Override
    public String fields() {
        return "Date \tDescription \tDraft \tCredit \tAvailable balance ";
    }

    @Override
    public String format(StatementLine stLine) {
        return stLine.getDate() + " \t" + stLine.getDescription() + " \t"
                + stLine.getDraft() + " \t" + stLine.getCredit() + " \t"
                + stLine.getAvailableBalance();
    }
}
