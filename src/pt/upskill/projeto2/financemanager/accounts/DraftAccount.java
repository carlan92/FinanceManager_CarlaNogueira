package pt.upskill.projeto2.financemanager.accounts;

import pt.upskill.projeto2.financemanager.date.Date;

import java.util.Iterator;
import java.util.SortedSet;

public class DraftAccount extends Account {

    public DraftAccount(long id, String name) {
        super(id, name);
    }

    public DraftAccount(long id, String name, Date startDate, Date endDate, double balance, SortedSet<StatementLine> statements) {
        super(id, name, startDate, endDate, balance, statements);
    }

    @Override
    public double estimatedAverageBalance() {
        if (getStatements().isEmpty())
            return 0.0;
        double sum = 0.0;

        int dayCount = getStatements().first().getDate()
                .diffInDays(getStatements().last().getDate()) + 1;

        Iterator<StatementLine> sttIterator = getStatements().iterator();
        StatementLine previous = (StatementLine) sttIterator.next();
        StatementLine current = previous;

        while (sttIterator.hasNext()) {
            current = (StatementLine) sttIterator.next();
            sum += sum(previous, current);
        }
        sum += sum(current, current);

        return sum / dayCount;
    }

    private static double sum(StatementLine previous, StatementLine current) {
        int dayDifference;

        if (current.getDate().diffInDays(previous.getDate()) == 0)
            dayDifference = 1;
        else
            dayDifference = current.getDate().diffInDays(previous.getDate());
        return dayDifference * previous.getAvailableBalance();
    }

    @Override
    public double getInterestRate() {
        return BanksConstants.getNormalInterestRate();
    }
}
