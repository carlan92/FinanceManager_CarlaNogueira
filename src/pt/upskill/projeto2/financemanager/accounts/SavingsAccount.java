package pt.upskill.projeto2.financemanager.accounts;

import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.financemanager.date.Date;

import java.util.SortedSet;

public class SavingsAccount extends Account{
    public static Category savingsCategory= new Category("SAVINGS");

    public SavingsAccount(long id, String name) {
        super(id, name);
    }

    public SavingsAccount(long id, String name, Date startDate, Date endDate, double balance, SortedSet<StatementLine> statements) {
        super(id, name, startDate, endDate, balance, statements);
    }

    @Override
    public void addStatementLine(StatementLine statementLine) {
        statementLine.setCategory(savingsCategory);
        super.addStatementLine(statementLine);
    }

    @Override
    public double estimatedAverageBalance() {
        return getStatements().last().getAvailableBalance();
    }

    @Override
    public double getInterestRate() {
        return BanksConstants.getSavingsInterestRate();
    }
}
