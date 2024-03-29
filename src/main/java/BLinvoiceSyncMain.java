import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BLinvoiceSyncMain {

    private static HttpRequestHandler httpRequestHandler;

    private static Configuration configuration;

    private static DBUtil dbUtil;
    private static final Logger LOGGER = LogManager.getLogger(BLinvoiceSyncMain.class);

    public static void main(String[] args) {

        try {
            LOGGER.info("Getting config...");
            AppConfig appConfig = new AppConfig("C:\\BLinvoiceSync\\config.xml");
            configuration = appConfig.getConfiguration();

        }catch (NullPointerException e){
            LOGGER.error("NullPointerException occurred, make sure that config file exists " + e.getMessage());
        }

        dbUtil = DBUtil.configBuilder(configuration);


        while (true) { // Pętla nieskończona

            List<Payment> orderIDs;
            List<Payment> paymentsNew;
            List<Payment> paymentsOld;


            LOGGER.info("Connecting to database...");
            try (Connection connection = dbUtil.getConnection()) {

                LOGGER.info("Fetching old payments list...");

                paymentsOld = DAO.findAllFromAuxiliary(connection, Queries.SELECT_FROM_ROZLICZONE.getQuery());

                LOGGER.info("Fetched " + paymentsOld.size() + " records");

                LOGGER.info("Fetching new payments list...");

                paymentsNew = DAO.findAllFromMain(connection, Queries.SELECT_FROM_ROZLICZONENEW.getQuery());
                LOGGER.info("Fetched " + paymentsNew.size() + " records");


                LOGGER.info("Checking payments to add...");

                if (!isRecordsLengthEqual(paymentsOld, paymentsNew)) {

                    orderIDs = paymentsToAdd(connection, paymentsOld, paymentsNew);


                    if (orderIDs.size() > 0) {

                        List<Payment> filteredIDs = orderIDs.stream()
                                .filter(o -> !o.getBlId().equals(""))
                                .toList();

                        LOGGER.info("Found " + filteredIDs.size() + " payments to add");


                        addNewPayments(connection, filteredIDs);

                    }
                }else {
                    LOGGER.info("No new payments found");
                }
                    LOGGER.info("Checking payments to update...");

                List<Payment> paymentsToUpdate = paymentsToUpdate(connection,paymentsNew, paymentsOld);

                if(paymentsToUpdate.size() > 0){
                    LOGGER.info("Found " + paymentsToUpdate.size() + " payments to update");

                    updatePayments(connection, paymentsToUpdate);
                } else {
                    LOGGER.info("No payments to update found");
                }
                LOGGER.info("Sleep...");
            } catch (SQLException e) {
                LOGGER.error("SQLException occurred: " + e.getMessage());
            }

            try {
                // Zawieszenie bieżącego wątku na 5 minut (300000 milisekund)
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                LOGGER.error("InterruptedException occurred: " + e.getMessage());
            }

        }
    }

    public static boolean isRecordsLengthEqual(List<Payment> oldRecordSet, List<Payment> newRecordSet) {
        if(Objects.isNull(oldRecordSet) || Objects.isNull(newRecordSet)){

            return true;
        }
        if(oldRecordSet.size() == newRecordSet.size()){
            return true;
        }



        return false;
    }

    public static List<Payment> paymentsToAdd(Connection connection, List<Payment> paymentsOld, List<Payment> paymentsNew){

            List<Payment> paymentsToAdd = paymentsNew.stream()
                    .filter(p -> !paymentsOld.contains(p))
                    .collect(Collectors.toList());

            return DAO.getOrderId(connection, Queries.SELECT_BL_ID.getQuery(), paymentsToAdd);


        }

        public static List<Payment> paymentsToUpdate(Connection connection,List<Payment> paymentsNew, List<Payment> paymentsOld){
        List<Payment> paymentsUpdated = new ArrayList<>();
        List<Payment> paymentsToUpdate;

          paymentsToUpdate = paymentsOld.stream()
                    .filter(p -> p.getUpdated() == 1)
                    .toList();
            for(Payment paymentNew: paymentsNew){
                for (Payment payment: paymentsToUpdate) {
                    if(payment.isValidForUpdate(paymentNew,payment )){
                        payment.setAmount(paymentNew.getAmount());
                        if(paymentNew.getOriginalAmount() - paymentNew.getAmount() == 0){
                            payment.setUpdated(0);
                            DAO.setPaymentUpdated(connection, Queries.SET_TO_UPDATED.getQuery(), payment.getId() );
                            LOGGER.info("Setting payment with id: " + payment.getId() + "to paid");
                        }
                        paymentsUpdated.add(payment);
                    }
                }
            }

            return paymentsUpdated;
        }

        public static void addNewPayments(Connection connection, List<Payment> filteredIDs){
            for (Payment payment : filteredIDs) {
                httpRequestHandler = new HttpRequestHandler(configuration.getToken());

                Map<String, String> httpResponse = httpRequestHandler.sendRequest(payment);

                if (httpResponse.containsKey("info")) {

                    LOGGER.info("Adding payment: " + payment.toString());

                    LOGGER.info(httpResponse.get("info"));

                    if(payment.getOriginalAmount() - payment.getAmount() != 0){
                        payment.setUpdated(1);
                    }

                        DAO.updateAuxiliaryTable(connection, Queries.INSERT_INTO_AUXILIARY.getQuery(), payment);
                        LOGGER.info("Invoice: " + payment.getSubDocId() + " entered into auxiliary table");



                } else if (httpResponse.containsKey("error")) {

                    LOGGER.error(httpResponse.get("error"));
                }
            }
        }

        public static void updatePayments(Connection connection, List<Payment> paymentsToUpdate){
            for (Payment payment: paymentsToUpdate) {

                httpRequestHandler = new HttpRequestHandler(configuration.getToken());

                Map<String, String> httpResponse = httpRequestHandler.sendRequest(payment);

                if (httpResponse.containsKey("info")) {

                    LOGGER.info("Updating payment: " + payment.toString());

                    LOGGER.info(httpResponse.get("info"));

                    DAO.updatePayment(connection, Queries.UPDATE_PAYMENTS.getQuery(), paymentsToUpdate);
                    LOGGER.info("Payment for Invoice: " + payment.getSubDocId() + " updated");



                } else if (httpResponse.containsKey("error")) {

                    LOGGER.error(httpResponse.get("error"));
                }
            }
        }

    }



