package pt.upskill.projeto2.financemanager.accounts;

import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.financemanager.date.Date;
import pt.upskill.projeto2.financemanager.filters.*;

import java.io.File;

import java.io.FileNotFoundException;
import java.util.*;

public abstract class Account {
    private long id;
    private String name;
    private Date startDate;
    private Date endDate;
    private String currency = "EUR";
    private String additionalInfo = "";
    private double balance;
    private SortedSet<StatementLine> statements = new TreeSet<>();

    public Account(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Account(long id, String name, Date startDate, Date endDate, double balance, SortedSet<StatementLine> statements) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.balance = balance;
        if (statements != null)
            this.statements = statements;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public static Account newAccount(File file) {
        Account account = null;

        try {
            Scanner fileRead = new Scanner(file);
            String line1 = fileRead.nextLine();

            String line2 = fileRead.nextLine();
            String[] line2Split = line2.split(";");
            long accountID = Long.parseLong(line2Split[1].replaceAll("[^\\d.]", ""));
            String accountName = line2Split[3].trim();

            String line3 = fileRead.nextLine();
            String[] line3Split = line3.split(";");
            String[] dateSplit = line3Split[1].split("-");
            Date startDate = new Date(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));

            String line4 = fileRead.nextLine();
            String[] line4Split = line4.split(";");
            String[] dateSplit2 = line4Split[1].split("-");
            Date endDate = new Date(Integer.parseInt(dateSplit2[0]), Integer.parseInt(dateSplit2[1]), Integer.parseInt(dateSplit2[2]));

            String line5 = fileRead.nextLine();

            SortedSet<StatementLine> sttAux = new TreeSet<>();
            double balance = 0;

            while (fileRead.hasNextLine()) {
                String sttLine = fileRead.nextLine();
                String[] splitLine = sttLine.split(";");

                String[] dateSttSplit = splitLine[0].split("-");
                Date date = new Date(Integer.parseInt(dateSttSplit[0]), Integer.parseInt(dateSttSplit[1]), Integer.parseInt(dateSttSplit[2].trim()));
                dateSttSplit = splitLine[1].split("-");
                Date valueDate = new Date(Integer.parseInt(dateSttSplit[0]), Integer.parseInt(dateSttSplit[1]), Integer.parseInt(dateSttSplit[2].trim()));

                String description = splitLine[2].trim();
                double draft = Double.parseDouble(splitLine[3]);
                double credit = Double.parseDouble(splitLine[4]);
                double accountingBalance = Double.parseDouble(splitLine[5]);
                double availableBalance = Double.parseDouble(splitLine[6]);
                Category category = null;

                StatementLine statementLine = new StatementLine(date, valueDate, description, draft, credit, accountingBalance, availableBalance, category);
                sttAux.add(statementLine);

                balance = Double.parseDouble(splitLine[6]);
            }

            switch (line2Split[4].trim()) {
                case "DraftAccount":
                    account = new DraftAccount(accountID, accountName, startDate, endDate, balance, sttAux);
                    break;
                case "SavingsAccount":
                    account = new SavingsAccount(accountID, accountName, startDate, endDate, balance, sttAux);
                    break;
            }
            account.setBalance(balance);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return account;
    }

    public double currentBalance() {
        return balance;
    }

    public Date getStartDate() {
        if (hasStatements()) {
            return statements.first().getDate();
        }
        return startDate;
    }

    public Date getEndDate() {
        if (hasStatements()) {
            return statements.last().getDate();
        }
        return endDate;
    }

    public abstract double estimatedAverageBalance();

    public abstract double getInterestRate();

    public String additionalInfo() {
        return additionalInfo;
    }

    public SortedSet<StatementLine> getStatements() {
        return statements;
    }

    public boolean hasStatements() {
        return !statements.isEmpty();
    }

    public void addStatementLine(StatementLine statementLine) {
        statements.add(statementLine);
        balance = statementLine.getAvailableBalance();
    }

    public void removeStatementLinesBefore(Date date) {
        BeforeDateSelector selector = new BeforeDateSelector(date);
        Filter<StatementLine, BeforeDateSelector> filter = new Filter<>(selector);
        statements = (SortedSet<StatementLine>) filter.apply(statements);
    }

    public double totalDraftsForCategorySince(Category category, Date date) {
        if (!hasStatements()) {
            return 0.0;
        }
        AfterDateSelector selector = new AfterDateSelector(date);
        Filter<StatementLine, AfterDateSelector> filter = new Filter<>(selector);
        CategorySelector selector1 = new CategorySelector(category);
        Filter<StatementLine, CategorySelector> filter1 = new Filter<>(selector1);
        SortedSet<StatementLine> filterStatements = (SortedSet<StatementLine>) filter.apply(filter1.apply(statements));
        double draftSum = 0.0;

        for (StatementLine stt : filterStatements) {
            draftSum += stt.getDraft();
        }
        return draftSum;
    }

    public double totalForMonth(int month, int year) {
        if (!hasStatements()) {
            return 0.0;
        }
        MonthSelector selector = new MonthSelector(month, year);
        Filter<StatementLine, MonthSelector> filter = new Filter<>(selector);
        SortedSet<StatementLine> filterStatements = (SortedSet<StatementLine>) filter.apply(statements);
        double totalDraftMonth = 0.0;

        for (StatementLine stt : filterStatements) {
            totalDraftMonth += stt.getDraft();
        }
        return totalDraftMonth;
    }

    public double totalCreditForMonth(int month, int year) {
        if (!hasStatements()) {
            return 0.0;
        }
        MonthSelector selector = new MonthSelector(month, year);
        Filter<StatementLine, MonthSelector> filter = new Filter<>(selector);
        SortedSet<StatementLine> filterStatements = (SortedSet<StatementLine>) filter.apply(statements);
        double totalCreditMonth = 0.0;
        for (StatementLine stt : filterStatements) {
            totalCreditMonth += stt.getCredit();
        }
        return totalCreditMonth;
    }

    public void autoCategorizeStatements(List<Category> categories) {
        if (hasStatements()) {
            for (StatementLine stt : statements) {
                for (Category c : categories) {
                    if (c.hasTag(stt.getDescription())) {
                        stt.setCategory(c);
                        break;
                    }
                }
            }
        }
    }
}
