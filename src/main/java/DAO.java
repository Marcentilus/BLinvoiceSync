import lombok.Getter;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Getter
public class DAO {


    private Connection connection;

    public static List<Payment> findAll (Connection connection, String query) {

        List<Payment> payments = new ArrayList<>();

        try(PreparedStatement preparedRozliczone = connection.prepareStatement(query)){
            ResultSet resultRozliczone = preparedRozliczone.executeQuery();
            while(resultRozliczone.next()){
                Payment payment = new Payment(
                        resultRozliczone.getString("nzf_NumerPelny"),
                        resultRozliczone.getFloat("nzf_WartoscWaluta")
                );
                payments.add(payment);
            }
            return payments;

        }catch (SQLException e){
            e.printStackTrace();
            return payments;
        }


    }

    public static int clearTable (Connection connection, String query){
        int recordCount = 0;
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){


            recordCount = preparedStatement.executeUpdate(query);

        }catch (SQLException e){
            e.printStackTrace();
        }
        return recordCount;
    }

    public static void updateAuxiliaryTable(Connection connection, String query, List<Payment> payments){
        for (Payment payment: payments) {
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setDouble(1, payment.getAmount());
                preparedStatement.setString(2, payment.getSubDocId());
                preparedStatement.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

    }

    public static List<Order> getOrderId(Connection connection, String query, List<Payment> payments){
        List<Order> orderIDs = new ArrayList<>();
        for(Payment payment: payments){
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1, payment.getSubDocId());
                ResultSet rs = preparedStatement.executeQuery();
                while(rs.next()){
                    orderIDs.add(new Order(payment.getSubDocId(), rs.getString("dok_Uwagi"), payment.getAmount()));
                }

            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return orderIDs;
    }




}