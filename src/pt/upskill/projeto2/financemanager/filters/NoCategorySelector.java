package pt.upskill.projeto2.financemanager.filters;

import pt.upskill.projeto2.financemanager.accounts.StatementLine;

public class NoCategorySelector {

    public boolean isSelected(StatementLine item) {
        return item.getCategory() == null;
    }
}

