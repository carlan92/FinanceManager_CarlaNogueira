package pt.upskill.projeto2.financemanager.gui;

import pt.upskill.projeto2.financemanager.PersonalFinanceManager;
import pt.upskill.projeto2.financemanager.categories.Category;
import pt.upskill.projeto2.utils.Menu;

import java.util.Set;


/**
 * @author upSkill 2020
 * <p>
 * ...
 */

public class PersonalFinanceManagerUserInterface {

    public PersonalFinanceManagerUserInterface(
            PersonalFinanceManager personalFinanceManager) {
        this.personalFinanceManager = personalFinanceManager;
    }

    private static final String OPT_GLOBAL_POSITION = "Posição Global";
    private static final String OPT_ACCOUNT_STATEMENT = "Movimentos Conta";
    private static final String OPT_LIST_CATEGORIES = "Listar categorias";
    private static final String OPT_ANALISE = "Análise";
    private static final String OPT_EXIT = "Sair";

    private static final String OPT_MONTHLY_SUMMARY = "Evolução global por mês";
    private static final String OPT_PREDICTION_PER_CATEGORY = "Previsão gastos totais do mês por categoria";
    private static final String OPT_ANUAL_INTEREST = "Previsão juros anuais";

    private static final String[] OPTIONS_ANALYSIS = {OPT_MONTHLY_SUMMARY, OPT_PREDICTION_PER_CATEGORY, OPT_ANUAL_INTEREST};
    private static final String[] OPTIONS = {OPT_GLOBAL_POSITION,
            OPT_ACCOUNT_STATEMENT, OPT_LIST_CATEGORIES, OPT_ANALISE, OPT_EXIT};

    private final PersonalFinanceManager personalFinanceManager;


    public void execute() {

        Set<String> uncategorizedSet = personalFinanceManager.getUncategorizedDescription();
        for (String description : uncategorizedSet) {
            String input = Menu.requestInput("Escolha a categoria para: \"" + description + "\"");
            while (input == null) {
                input = Menu.requestInput("Escolha a categoria para: \"" + description + "\"");
            }
            Category cat = personalFinanceManager.findCategoryByName(input);
            if (cat == null) {
                cat = new Category(input);
                personalFinanceManager.addCategory(cat);
            }
            cat.addTag(description);
        }
        personalFinanceManager.autoCategorizeAllStatements();

        while (true) {
            String input = Menu.requestSelection("Finance Manager", OPTIONS);
            System.out.println(input);
            if (input == null) {
                if (Menu.yesOrNoInput("Deseja gravar?")) {
                    personalFinanceManager.save();
                }
                break;
            }
            switch (input) {
                case OPT_GLOBAL_POSITION:
                    System.out.println();
                    personalFinanceManager.printGlobalPosition();
                    break;
                case OPT_ACCOUNT_STATEMENT:
                    String[] accountsIDs = personalFinanceManager.getAccountIDs();
                    input = Menu.requestSelection("Escolha a conta:", accountsIDs);
                    System.out.println();
                    personalFinanceManager.printStatements(Long.parseLong(input));
                    break;
                case OPT_LIST_CATEGORIES:
                    System.out.println();
                    personalFinanceManager.printCategories();
                    break;
                case OPT_ANALISE:
                    openAnalysisMenu();
                    break;
                case OPT_EXIT:
                    if (Menu.yesOrNoInput("Deseja gravar?")) {
                        personalFinanceManager.save();
                    }
                    System.exit(0);
            }
        }
    }

    private void openAnalysisMenu() {
        String input = Menu.requestSelection("Finance Manager", OPTIONS_ANALYSIS);
        if (input == null) {
            return;
        }
        String[] accountsIDs = personalFinanceManager.getAccountIDs();
        switch (input) {
            case OPT_MONTHLY_SUMMARY:
                input = Menu.requestSelection("Escolha a conta:", accountsIDs);
                System.out.println();
                personalFinanceManager.printMonthlySummary(Long.parseLong(input));
                break;
            case OPT_PREDICTION_PER_CATEGORY:
                input = Menu.requestSelection("Escolha a conta:", accountsIDs);
                System.out.println();
                personalFinanceManager.printPredictionPerCategory(Long.parseLong(input));
                break;
            case OPT_ANUAL_INTEREST:
                System.out.println();
                personalFinanceManager.printAnualInterest();
                break;
        }
    }






}



