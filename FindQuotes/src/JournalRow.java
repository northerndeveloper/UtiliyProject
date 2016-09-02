import java.math.BigDecimal;

public class JournalRow {
	
	private String accNo;
	private String accDesc;
	private String voucherDesc;
	private BigDecimal crd;
	private BigDecimal dbt;
	private Long offIdNo;
	private String voucherDate;
	private String voucherId;
	private String yon;
	private String accName;
	private Long seqNo;
	private String voucherType;
	private String documentType;
	private String documentDate;
	private String documentNo;
	private String documentOtherDesc;
	private String paymentType;
	private String paymentTypeOtherDesc;
	private String voucherCreationDate;
	private String fisTuru; // Açýlýþ : A, Kapanýþ : K
	
	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	// accNo
	public void setNo(String accNo) {
		this.accNo = accNo;
	}
	public String getAccDesc() {
		return accDesc;
	}
	public void setAccDesc(String accDesc) {
		this.accDesc = accDesc;
	}
	public String getVoucherDesc() {
		return voucherDesc;
	}
	public void setVoucherDesc(String voucherDesc) {
		this.voucherDesc = voucherDesc;
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
	public Long getOffIdNo() {
		return offIdNo;
	}
	public void setOffIdNo(Long offIdNo) {
		this.offIdNo = offIdNo;
	}
	// offIdNo
	public void setIdNo(Long offIdNo) {
		this.offIdNo = offIdNo;
	}
	public String getVoucherDate() {
		return voucherDate;
	}
	public void setVoucherDate(String voucherDate) {
		this.voucherDate = voucherDate;
	}
	public String getVoucherId() {
		return voucherId;
	}
	public void setVoucherId(String voucherId) {
		this.voucherId = voucherId;
	}
	public String getYon() {
		return yon;
	}
	public void setYon(String yon) {
		this.yon = yon;
	}
	public String getAccName() {
		return accName;
	}
	public void setAccName(String accName) {
		this.accName = accName;
	}
	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}
	public Long getSeqNo() {
		return seqNo;
	}
	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}
	public String getVoucherType() {
		return voucherType;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getDocumentDate() {
		return documentDate;
	}
	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}
	public String getDocumentNo() {
		return documentNo;
	}
	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}
	public void setDocumentOtherDesc(String documentOtherDesc) {
		this.documentOtherDesc = documentOtherDesc;
	}
	public String getDocumentOtherDesc() {
		return documentOtherDesc;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentTypeOtherDesc(String paymentTypeOtherDesc) {
		this.paymentTypeOtherDesc = paymentTypeOtherDesc;
	}
	public String getPaymentTypeOtherDesc() {
		return paymentTypeOtherDesc;
	}
	public String getVoucherCreationDate() {
		return voucherCreationDate;
	}
	public void setVoucherCreationDate(String voucherCreationDate) {
		this.voucherCreationDate = voucherCreationDate;
	}
	/*
	 * Açýlýþ : A, Kapanýþ : K
	 */
	public String getFisTuru() {
		return fisTuru;
	}
	public void setFisTuru(String fisTuru) {
		this.fisTuru = fisTuru;
	}
	public boolean isAllNull() {
		if (this.accNo == null && this.accDesc == null
				&& this.voucherDesc == null && this.crd == null
				&& this.dbt == null && this.offIdNo == null
				&& this.voucherDate == null && this.voucherId == null
				&& this.yon == null && this.accName == null) {
			return true;
		}
		return false;
	}
	/*
	 * dd/MM/yyyy -> yyyy-MM-dd
	 * 31/01/2013 -> 2013-01-31
	 */
	public String getVoucherDateAsSQLiteDateFormatted() {
		return voucherDate.substring(6,10) + "-" + voucherDate.substring(3,5) + "-" + voucherDate.substring(0,2);
	}
	/*
	 * yyyy-MM-dd -> dd/MM/yyyy
	 * 2013-01-31 -> 31/01/2013
	 */
	public void setVoucherDateFromSQLiteFormattedStr(String voucherDateStr) {
		this.voucherDate = voucherDateStr.substring(8,10) + "/" + voucherDateStr.substring(5,7) + "/" + voucherDateStr.substring(0,4);
	}
	/*
	 * dd/MM/yyyy -> yyyy-MM-dd
	 * 31/01/2013 -> 2013-01-31
	 */
	public String getDocumentDateAsSQLiteDateFormatted() {
		if (documentDate == null) {
			return null;
		}
		return documentDate.substring(6,10) + "-" + documentDate.substring(3,5) + "-" + documentDate.substring(0,2);
	}
	/*
	 * yyyy-MM-dd -> dd/MM/yyyy
	 * 2013-01-31 -> 31/01/2013
	 */
	public void setDocumentDateFromSQLiteFormattedStr(String documentDateStr) {
		if (documentDateStr == null) {
			return;
		}
		this.documentDate = documentDateStr.substring(8,10) + "/" + documentDateStr.substring(5,7) + "/" + documentDateStr.substring(0,4);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JournalRow [accNo=");
		builder.append(accNo);
		builder.append(", accDesc=");
		builder.append(accDesc);
		builder.append(", voucherDesc=");
		builder.append(voucherDesc);
		builder.append(", crd=");
		builder.append(crd);
		builder.append(", dbt=");
		builder.append(dbt);
		builder.append(", offIdNo=");
		builder.append(offIdNo);
		builder.append(", voucherDate=");
		builder.append(voucherDate);
		builder.append(", voucherId=");
		builder.append(voucherId);
		builder.append(", yon=");
		builder.append(yon);
		builder.append(", accName=");
		builder.append(accName);
		builder.append(", seqNo=");
		builder.append(seqNo);
		builder.append(", voucherType=");
		builder.append(voucherType);
		builder.append(", documentType=");
		builder.append(documentType);
		builder.append(", documentDate=");
		builder.append(documentDate);
		builder.append(", documentNo=");
		builder.append(documentNo);
		builder.append(", documentOtherDesc=");
		builder.append(documentOtherDesc);
		builder.append(", paymentType=");
		builder.append(paymentType);
		builder.append(", paymentTypeOtherDesc=");
		builder.append(paymentTypeOtherDesc);
		builder.append(", voucherCreationDate=");
		builder.append(voucherCreationDate);
		builder.append(", fisTuru=");
		builder.append(fisTuru);
		builder.append("]");
		return builder.toString();
	}
}
