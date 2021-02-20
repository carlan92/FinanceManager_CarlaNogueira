package pt.upskill.projeto2.financemanager;

import pt.upskill.projeto2.financemanager.accounts.Account;
import pt.upskill.projeto2.financemanager.accounts.DraftAccount;
import pt.upskill.projeto2.financemanager.accounts.SavingsAccount;
import pt.upskill.projeto2.financemanager.accounts.StatementLine;
import pt.upskill.projeto2.financemanager.accounts.formats.FileAccountFormat;
import pt.upskill.projeto2.financemanager.accounts.formats.SimpleStatementFormat;
import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.financemanager.date.Date;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class PersonalFinanceManager {

    private List<Account> accounts = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    public PersonalFinanceManager() {
        try {
            categories = Category.readCategories(new File("account_info/categories"));
        } catch (IOException ignored) {
        }
        readAllAccounts();
        readAllStatements();
    }

    public String[] getAccountIDs() {
        String[] accountIDs = new String[accounts.size()];
        for (int i = 0; i < accounts.size(); i++) {
            accountIDs[i] = "" + accounts.get(i).getId();
        }
        return accountIDs;
    }

    public void printStatements(long accountID) {
        Account account = findAccount(accountID);
        SimpleStatementFormat sttF = new SimpleStatementFormat();
        System.out.println();
        System.out.println("Account number: " + account.getId());
        System.out.println(sttF.fields());
        for (StatementLine stt : account.getStatements()) {
            System.out.println(sttF.format(stt));
        }
    }

    private void readAllAccounts() {
        File accountInfoFolder = new File("account_info");
        File[] accountFiles = accountInfoFolder.listFiles((file) -> file.getName().contains(".csv"));

        for (File accountFile : accountFiles) {
            accounts.add(Account.newAccount(accountFile));
        }
    }

    public Set<String> getUncategorizedDescription() {
        Set<String> output = new TreeSet<>();
        Set<String> descriptionSet = new TreeSet<>();

        for (Account a : accounts) {
            for (StatementLine stt : a.getStatements()) {
                descriptionSet.add(stt.getDescription());
            }
        }

        for (String description : descriptionSet) {
            boolean hasCategory = false;
            for (Category category : categories) {
                if (category.hasTag(description)) {
                    hasCategory = true;
                    break;
                }
            }
            if (!hasCategory) {
                output.add(description);
            }
        }
        return output;
    }

    public Category findCategoryByName(String name) {
        for (Category cat : categories) {
            if (cat.getName().equals(name)) {
                return cat;
            }
        }
        return null;
    }

    public void addCategory(Category cat) {
        categories.add(cat);
    }

    public void printCategories() {
        System.out.println(categories);
    }

    public void autoCategorizeAllStatements() {
        for (Account a : accounts) {
            a.autoCategorizeStatements(categories);
        }
    }

    public void printMonthlySummary(long accountID) {
        Account account = findAccount(accountID);
        int startYear = account.getStartDate().getYear();
        int endYear = account.getEndDate().getYear();
        for (int year = startYear; year <= endYear; year++) {
            System.out.println();
            System.out.println("Account number: " + accountID);
            System.out.println("Year: " + year);
            System.out.println("Month \t Total Draft \tTotal Credit");
            for (int month = 1; month <= 12; month++) {
                double totalDraftForMonth = account.totalForMonth(month, year);
                double totalCreditForMonth = account.totalCreditForMonth(month, year);
                System.out.println(Date.intToMonth(month) + " \t" + totalDraftForMonth + " \t" + totalCreditForMonth);
            }
        }
    }

    public void printPredictionPerCategory(long accountID) {
        Account account = findAccount(accountID);
        Date today = new Date();
        Date startOfMonth = Date.firstOfMonth(today);
        System.out.println();
        System.out.println("Account number: " + accountID);
        System.out.println("Category \tCurrent Draft \tPredicted Draft \tTotal expected");
        for (Category category : categories) {
            double draftPerCategory = account.totalDraftsForCategorySince(category, startOfMonth);
            double predictedTotalDraftPerCategory = (Date.lastDayOf(today.getMonth(), today.getYear()) * draftPerCategory) / today.getDay();
            double predictDraftPerCategory = predictedTotalDraftPerCategory - draftPerCategory;
            System.out.println(category + " \t" + draftPerCategory + " \t" + predictDraftPerCategory + " \t" + predictedTotalDraftPerCategory);
        }
    }

    public void printAnualInterest() {
        System.out.println();
        System.out.println("Account number \tAnual interest");
        for (Account account : accounts) {
            double anualInterest = account.getInterestRate() * account.estimatedAverageBalance();
            System.out.println(account.getId() + " \t" + anualInterest);
        }
    }

    private void readAllStatements() {
        File statementsFolder = new File("statements");
        File[] statementFiles = statementsFolder.listFiles((file) -> file.getName().contains(".csv"));
        for (File sttFile : statementFiles) {
            readStatements(sttFile);
        }
    }

    private Account findAccount(long id) {
        for (Account a : accounts) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    private void readStatements(File file) {
        try {
            Scanner fileRead = new Scanner(file);
            String line1 = fileRead.nextLine();
            String line2 = fileRead.nextLine();
            String line3 = fileRead.nextLine();
            String[] line3Split = line3.split(";");
            long accountID = Long.parseLong(line3Split[1].replaceAll("[^\\d.]", ""));
            Account sttAccount = findAccount(accountID);
            if (sttAccount == null) {
                String accountName = line3Split[3].trim();
                String line4 = fileRead.nextLine();
                String[] line4Split = line4.split(";");
                String[] dateSplit = line4Split[1].split("-");
                Date startDate = new Date(Integer.parseInt(dateSplit[0]), Integer.parseInt(dateSplit[1]), Integer.parseInt(dateSplit[2]));

                String line5 = fileRead.nextLine();
                String[] line5Split = line5.split(";");
                String[] dateSplit2 = line5Split[1].split("-");
                Date endDate = new Date(Integer.parseInt(dateSplit2[0]), Integer.parseInt(dateSplit2[1]), Integer.parseInt(dateSplit2[2]));

                switch (line3Split[4].trim()) {
                    case "DraftAccount":
                        sttAccount = new DraftAccount(accountID, accountName, startDate, endDate, 0, null);
                        accounts.add(sttAccount);
                        break;
                    case "SavingsAccount":
                        sttAccount = new SavingsAccount(accountID, accountName, startDate, endDate, 0, null);
                        accounts.add(sttAccount);
                        break;
                }
            } else {
                String line4 = fileRead.nextLine();
                String line5 = fileRead.nextLine();
            }
            String line6 = fileRead.nextLine();
            String line7 = fileRead.nextLine();

            while (fileRead.hasNextLine()) {
                String sttLine = fileRead.nextLine();
                String[] splitLine = sttLine.split(";", -1);
                String[] dateSttSplit = splitLine[0].split("-");
                Date date = new Date(Integer.parseInt(dateSttSplit[0]), Integer.parseInt(dateSttSplit[1]), Integer.parseInt(dateSttSplit[2].trim()));
                dateSttSplit = splitLine[1].split("-");
                Date valueDate = new Date(Integer.parseInt(dateSttSplit[0]), Integer.parseInt(dateSttSplit[1]), Integer.parseInt(dateSttSplit[2].trim()));
                String description = splitLine[2].trim();
                double draft = 0;
                if (!splitLine[3].isEmpty()) {
                    draft = Double.parseDouble(splitLine[3]);
                }
                double credit = Double.parseDouble(splitLine[4]);
                double accountingBalance = Double.parseDouble(splitLine[5]);
                double availableBalance = Double.parseDouble(splitLine[6]);
                Category category = null;

                StatementLine statementLine = new StatementLine(date, valueDate, description, draft, credit, accountingBalance, availableBalance, category);
                sttAccount.addStatementLine(statementLine);
            }
        } catch (
                FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void printGlobalPosition() {
        System.out.println();
        System.out.println("Account Number   Current Balance");
        double totalBalance = 0;
        for (Account a : accounts) {
            System.out.println(a.getId() + "    " + a.getBalance());
            totalBalance += a.getBalance();
        }
        System.out.println();
        System.out.println("Total Balance:   " + totalBalance);
    }

    public void save() {
        FileAccountFormat formatter = new FileAccountFormat();
        for (Account account : accounts) {
            String fileContent = formatter.format(account);
            try {
                File accountFile = new File("account_info/" + account.getId() + "Test.csv");
                PrintWriter printWriter = new PrintWriter(accountFile);
                printWriter.print(fileContent);
                printWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        Category.writeCategories(new File("account_info/categories"), categories);
    }
}