package pt.upskill.projeto2.financemanager.filters;

import pt.upskill.projeto2.financemanager.accounts.StatementLine;
import pt.upskill.projeto2.financemanager.categories.Category;

public class CategorySelector implements Selector<StatementLine>{
    private Category category;

    public CategorySelector(Category category) {
        this.category = category;
    }

    @Override
    public boolean isSelected(StatementLine item) {
        if(item.getCategory()==null){
            return false;
        }
        return item.getCategory().equals(category);


    }
}
