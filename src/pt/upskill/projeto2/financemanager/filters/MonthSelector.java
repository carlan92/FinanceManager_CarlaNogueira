package pt.upskill.projeto2.financemanager.filters;


import pt.upskill.projeto2.financemanager.accounts.StatementLine;

public class MonthSelector implements Selector<StatementLine> {
    private int month;
    private int year;

    public MonthSelector(int month, int year) {
        this.month = month;
        this.year = year;
    }

    @Override
    public boolean isSelected(StatementLine item) {
        return item.getDate().getMonth().ordinal()==month && item.getDate().getYear()==year;
    }
}
