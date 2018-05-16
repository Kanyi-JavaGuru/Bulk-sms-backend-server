package co.ke.bigfootke.app.pojos;

//import java.util.List;

public class Report {

	private String label;
	private int[] monthlyExpenditure;
	
	public Report() {
	}

	public Report(String label, int[] monthlyExpenditure) {
		this.label = label;
		this.monthlyExpenditure = monthlyExpenditure;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int[] getMonthlyExpenditure() {
		return monthlyExpenditure;
	}

	public void setMonthlyExpenditure(int[] monthlyExpenditure) {
		this.monthlyExpenditure = monthlyExpenditure;
	}	
}
