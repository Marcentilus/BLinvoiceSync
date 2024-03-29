import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@Getter
@ToString


public class Payment {

    private int id;
    private String subDocId;
    private String BlId;
    private float originalAmount;
    private float amount;
    private LocalDateTime date;
    private int updated;



    @Override
    public int hashCode() {
        return Objects.hash(subDocId, originalAmount);
    }

    public void setBlId(String blId) {
        BlId = blId;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public static LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toLocalDate().atStartOfDay();
    }

    public static Date convertToSqlDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.valueOf(localDateTime.toLocalDate());
    }

    public boolean isValidForUpdate(Payment paymentNew, Payment paymentOld){
        return paymentOld.getSubDocId().equals(paymentNew.getSubDocId()) && paymentOld.getAmount() != paymentNew.getAmount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Float.compare(payment.originalAmount, originalAmount) == 0 && Objects.equals(subDocId, payment.subDocId);
    }
}
