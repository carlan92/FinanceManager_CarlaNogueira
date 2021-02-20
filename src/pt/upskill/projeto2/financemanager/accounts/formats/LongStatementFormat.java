package pt.upskill.projeto2.financemanager.accounts.formats;

import pt.upskill.projeto2.financemanager.accounts.StatementLine;
import pt.upskill.projeto2.financemanager.date.Date;

public class LongStatementFormat implements StatementLineFormat {

    @Override
    public String fields() {
        return "Date \tValue Date \tDescription \tDraft \tCredit \tAccounting balance \tAvailable balance ";
    }

    @Override
    public String format(StatementLine stLine) {
        return stLine.getDate() + " \t" + stLine.getValueDate() + " \t"
                + stLine.getDescription() + " \t" + stLine.getDraft() + " \t"
                + stLine.getCredit() + " \t" + stLine.getAccountingBalance()
                + " \t" + stLine.getAvailableBalance();
    }
}
