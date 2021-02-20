package pt.upskill.projeto2.financemanager.accounts;

import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.financemanager.date.Date;

public class StatementLine implements Comparable<StatementLine> {
    private Date date;
    private Date valueDate;
    private String description;
    private double draft;
    private double credit;
    private double accountingBalance;
    private double availableBalance;
    private Category category;

    public StatementLine(Date date, Date valueDate, String description, double draft, double credit, double accountingBalance, double availableBalance, Category category) {
        if (date == null) throw new IllegalArgumentException("A data não pode ser nula");
        this.date = date;
        this.valueDate = valueDate;
        if (description == null || description.equals(""))
            throw new IllegalArgumentException("A descrição não pode ser vazia");
        this.description = description;
        if (draft > 0) throw new IllegalArgumentException("O débito não pode ser positivo");
        this.draft = draft;
        if (credit < 0) throw new IllegalArgumentException("O crédito não pode ser negativo");
        this.credit = credit;
        this.accountingBalance = accountingBalance;
        this.availableBalance = availableBalance;
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDraft() {
        return draft;
    }

    public void setDraft(double draft) {
        this.draft = draft;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public double getAccountingBalance() {
        return accountingBalance;
    }

    public void setAccountingBalance(double accountingBalance) {
        this.accountingBalance = accountingBalance;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public int compareTo(StatementLine o) {
        if (equals(o)) {
            return 0;
        }
        if (valueDate.compareTo(o.valueDate) > 0) {
            return valueDate.compareTo(o.valueDate);
        } else if (date.compareTo(o.date) > 0) {
            return date.compareTo(o.date);
        } else if (Math.abs(o.getAvailableBalance() - (getAvailableBalance() + o.credit + o.draft)) < 0.001) {
            return -1;
        }
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StatementLine o = (StatementLine) obj;

            return date.compareTo(o.date) == 0
                && valueDate.compareTo(o.valueDate) == 0
                && description.equals(o.description)
                && draft == o.draft
                && credit == o.credit
                && accountingBalance == o.accountingBalance
                && availableBalance == o.availableBalance;
    }

    @Override
    public String toString() {
        return "StatementLine{" +
                "date=" + date +
                ", valueDate=" + valueDate +
                ", description='" + description + '\'' +
                ", draft=" + draft +
                ", credit=" + credit +
                ", accountingBalance=" + accountingBalance +
                ", availableBalance=" + availableBalance +
                ", category=" + category +
                '}';
    }
}
