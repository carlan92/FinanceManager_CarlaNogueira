package pt.upskill.projeto2.financemanager.accounts.formats;

import pt.upskill.projeto2.financemanager.accounts.Account;
import pt.upskill.projeto2.financemanager.accounts.StatementLine;
import pt.upskill.projeto2.financemanager.date.Date;

public class FileAccountFormat implements Format<Account> {
    private static final String NL = System.getProperty("line.separator");

    public String format(Account a) {
        String FileOutput;
        FileOutput = "Account Info - " + new Date() + NL;
        FileOutput += "Account  ;" + a.getId() + " ; " + a.getCurrency() + "  ;" + a.getName() + " ;" + a.getClass().getSimpleName() + " ;" + NL;
        FileOutput += "Start Date ;" + a.getStartDate() + NL;
        FileOutput += "End Date ;" + a.getEndDate() + NL;
        FileOutput += statements(a.getStatements());

        return FileOutput;
    }

    private static String statements(Iterable<StatementLine> stt) {
        StringBuilder s = new StringBuilder();
        s.append("Date ;Value Date ;Description ;Draft ;Credit ;Accounting balance ;Available balance").append(NL);
        for (StatementLine statementLine : stt) {
            s.append(statement(statementLine)).append(NL);
        }
        return s.toString();
    }

    private static String statement(StatementLine st) {
        return st.getDate() + " ;" + st.getValueDate() + " ;"
                + st.getDescription() + " ;"
                + st.getDraft() + " ;"
                + st.getCredit() + " ;"
                + st.getAccountingBalance() + " ;"
                + st.getAvailableBalance();
    }
}
