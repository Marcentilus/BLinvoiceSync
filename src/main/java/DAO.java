import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
public class DAO {



    public static List<Payment> findAllFromAuxiliary (Connection connection, String query) {

        List<Payment> payments = new ArrayList<>();

        try(PreparedStatement preparedAuxiliary = connection.prepareStatement(query)){
            ResultSet resultAuxiliary = preparedAuxiliary.executeQuery();
            while(resultAuxiliary.next()){
                LocalDateTime localDateTime = Payment.convertToLocalDateTime(resultAuxiliary.getDate("nzf_Data"));
                Payment payment = new Payment(
                        resultAuxiliary.getInt("id"),
                        resultAuxiliary.getString("nzf_NumerPelny"),
                        resultAuxiliary.getString("nzf_BLID"),
                        resultAuxiliary.getFloat("nzf_WartoscPierwotnaWaluta"),
                        resultAuxiliary.getFloat("nzf_WartoscWaluta"),
                        localDateTime,
                        resultAuxiliary.getInt("nzf_updated")
                );
                payments.add(payment);
            }
            return payments;

        }catch (SQLException e){
            e.printStackTrace();
            return payments;
        }


    }

    public static List<Payment> findAllFromMain (Connection connection, String query){
        List<Payment> payments = new ArrayList<>();



        try(PreparedStatement prepareSettled = connection.prepareStatement(query)){
            ResultSet resultSettled = prepareSettled.executeQuery();
            while(resultSettled.next()){

                float amountDue = resultSettled.getFloat("nzf_WartoscPierwotnaWaluta");
                LocalDateTime localDateTime = Payment.convertToLocalDateTime(resultSettled.getDate("nzf_Data"));
                float amountPaid = amountDue - resultSettled.getFloat("nzf_WartoscWaluta");

                if(amountDue - amountPaid == amountDue) {

                    amountPaid = 0;
                }

                Payment payment = new Payment(
                        0,
                        resultSettled.getString("nzf_NumerPelny"),
                        "",
                        amountDue,
                        amountPaid,
                        localDateTime,
                        0
                );

                        payments.add(payment);
            }
            return payments;
        }catch (SQLException e){
            e.printStackTrace();
            return payments;
        }
    }



    public static void updateAuxiliaryTable(Connection connection, String query, Payment payment){

            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1, payment.getSubDocId());
                preparedStatement.setString(2, payment.getBlId());
                preparedStatement.setDouble(3, payment.getAmount());
                preparedStatement.setDouble(4, payment.getOriginalAmount());
                preparedStatement.setDate(5,Payment.convertToSqlDate(payment.getDate()));
                preparedStatement.setInt(6, payment.getUpdated());
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }


    }

    public static List<Payment> getOrderId(Connection connection, String query, List<Payment> payments){

        IdParser idParser = new BLIdParser();
        for(Payment payment: payments){
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1, payment.getSubDocId());
                ResultSet rs = preparedStatement.executeQuery();
                while(rs.next()){
                    payment.setBlId(idParser.parseId(rs.getString("dok_Uwagi")));
                }

            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return payments;
    }

    public static void updatePayment(Connection connection, String query, List<Payment> payments){
        for (Payment payment: payments) {
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){

                preparedStatement.setFloat(1,payment.getAmount());
                preparedStatement.setString(2, payment.getSubDocId());

              preparedStatement.executeUpdate();

            }catch (SQLException e){
                e.printStackTrace();
            }

        }

    }

    public static void setPaymentUpdated(Connection connection, String query, int paymentID){

            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){

                preparedStatement.setInt(1,0);
                preparedStatement.setInt(2, paymentID);

                preparedStatement.executeUpdate();

            }catch (SQLException e){
                e.printStackTrace();
            }


    }




}