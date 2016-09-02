package csv.pre.control;

import java.math.BigDecimal;

public class TotalJournalRow {
	
	private String voucherId;
	
	private BigDecimal crd;
	
	private BigDecimal dbt;

	public String getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(String voucherId) {
		this.voucherId = voucherId;
	}

	public BigDecimal getCrd() {
		return crd;
	}

	public void setCrd(BigDecimal crd) {
		this.crd = crd;
	}

	public BigDecimal getDbt() {
		return dbt;
	}

	public void setDbt(BigDecimal dbt) {
		this.dbt = dbt;
	}
	
	

}
