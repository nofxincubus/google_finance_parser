import java.util.ArrayList;


public class FinancialClass {
	public enum DataType{
		eFinancial,
		eBalanceSheet,
		eCashFlow
	}
	
	String ticker = "";
	DataType eDataType;
	int year;
	int quarter;
	int quarterCount;
	String dateString;
	String quarterCounterRange;
	String quarterHeader;
	ArrayList<Float> financialData = new ArrayList<Float>();
	/*
	0. float Revenue;
	1. float Other_Revenue;
	2. float Total_Revenue;
	3. float Cost_of_Revenue_Total;
	4. float Gross_Profit;
	5. float Selling_General_Admin_Expenses_Total;
	6. float Research_Development;
	7. float Depreciation_Amortization;
	8. float Interest_Expense_Income_Net_Operating;
	9. float Unusual_Expense_Income;
	10. float Other_Operating_Expenses_Total;
	11. float Total_Operating_Expense;
	12. float Operating_Income;
	13. float Interest_Income_Expense_Net_Non_Operating;
	14. float Gain_Loss_on_Sale_of_Assets;
	15. float Other_Net;
	16. float Income_Before_Tax;
	17. float Income_After_Tax;
	18. float Minority_Interest;
	19. float Equity_In_Affiliates;
	20. float Net_Income_Before_Extra_Items;
	21. float Accounting_Change;
	22. float Discontinued_Operations;
	23. float Extraordinary_Item;
	24. float Net_Income;
	25. float Preferred_Dividends;
	26. float Income_Available_to_Common_Excl_Extra_Items;
	27. float Income_Available_to_Common_Incl_Extra_Items;
	28. float Basic_Weighted_Average_Shares;
	29. float Basic_EPS_Excluding_Extraordinary_Items;
	30. float Basic_EPS_Including_Extraordinary_Items;
	31. float Dilution_Adjustment;
	32. float Diluted_Weighted_Average_Shares;
	33. float Diluted_EPS_Excluding_Extraordinary_Items;
	34. float Diluted_EPS_Including_Extraordinary_Items;
	35. float Dividends_per_Share_Common_Stock_Primary_Issue;
	36. float Gross_Dividends_Common_Stock;
	37. float Net_Income_after_Stock_Based_Comp_Expense;
	38. float Basic_EPS_after_Stock_Based_Comp_Expense;
	39. float Diluted_EPS_after_Stock_Based_Comp_Expense;
	40. float Depreciation_Supplemental;
	41. float Total_Special_Items;
	42. float Normalized_Income_Before_Taxes;
	43. float Effect_of_Special_Items_on_Income_Taxes;
	44. float Income_Taxes_Ex_Impact_of_Special_Items;
	45. float Normalized_Income_After_Taxes;
	46. float Normalized_Income_Avail_to_Common;
	47. float Basic_Normalized_EPS;
	48. float Diluted_Normalized_EPS;
	 */

}
