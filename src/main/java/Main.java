import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main {

    private static List<Payment> paymentsOld;
    private static List<Payment> paymentsNew;

    private static List<Order> orderIDs = new ArrayList<>();

    private static HttpRequestHandler httpRequestHandler;


    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        int recordCount = 0;
        try(Connection connection = DBUtil.getConnection()){
            LOGGER.info("Fetching old payments list...");
            paymentsOld = DAO.findAll(connection, Queries.SELECT_FROM_ROZLICZONE.getQuery());
            LOGGER.info("Fetched " + paymentsOld.size() + " records");
            LOGGER.info("Fetching new payments list...");
            paymentsNew = DAO.findAll(connection, Queries.SELECT_FROM_ROZLICZONENEW.getQuery());
            LOGGER.info("Fetched " + paymentsNew.size() + " records");
            LOGGER.info("Checking payments to update...");
            if(!isRecordsLengthEqual(paymentsOld, paymentsNew)){
                List<Payment> paymentsToUpdate = paymentsNew.stream()
                        .filter(p -> !paymentsOld.contains(p))
                        .collect(Collectors.toList());
                recordCount = DAO.clearTable(connection, Queries.DELETE_ALL.getQuery());
                LOGGER.info(recordCount + " records deleted from auxiliary table");
                DAO.updateAuxiliaryTable(connection, Queries.INSERT_INTO_AUXILIARY.getQuery(), paymentsNew);



                orderIDs = DAO.getOrderId(connection, Queries.SELECT_BL_ID.getQuery(), paymentsToUpdate);



            }
            if (orderIDs.size() > 0) {
                LOGGER.info("Found " + orderIDs.size() + " payments to update");

                for (Order order: orderIDs ) {

                    httpRequestHandler = new HttpRequestHandler();
                    Map<String,String> httpResponse = httpRequestHandler.sendRequest(order);



                    if(httpResponse.containsKey("info")){
                        LOGGER.info(httpResponse.get("info"));
                    } else if (httpResponse.containsKey("error")){
                        LOGGER.error(httpResponse.get("error"));
                    }
                }

            } else {
                LOGGER.info("No payments to update found");
            }


        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static boolean isRecordsLengthEqual (List<Payment> oldRecordSet, List<Payment> newRecordSet){
        if(Objects.isNull(oldRecordSet) || Objects.isNull(newRecordSet)){

            return true;
        }
        if(oldRecordSet.size() == newRecordSet.size()){
            return true;
        }



        return false;
    }


}
