package co.ke.bigfootke.app.pojos;

import java.util.List;

public class LineChartData {

	private String label;
	private List<Integer> monthlyExpenditure;
	
	public LineChartData() {
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Integer> getMonthlyExpenditure() {
		return monthlyExpenditure;
	}

	public void setMonthlyExpenditure(List<Integer> data) {
		this.monthlyExpenditure = data;
	}
	
}
