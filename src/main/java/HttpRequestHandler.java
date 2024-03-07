import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class HttpRequestHandler {


    private final String token = "5001659-5007177-60CT8KE3KQPS8OOQYW4FMDG7I95PQ2WCAYVS91BPWFVZ40SI0JBI7136K90DU6OK";

    public Map<String, String> sendRequest (Order order){


        if(Objects.isNull(order)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "object order was null");
            return error;
        }

        String orderId = order.getOrderId();

        float amount = order.getAmount();

        StringBuilder response = new StringBuilder();

        try {
            // Parametry metody w formacie JSON
            String methodParams = "{\"order_id\": " + orderId +
                    ",\"payment_done\": " + amount + "," +
                    "\"payment_date\": " + Instant.now().getEpochSecond() + "," +
                    "\"payment_comment\": \"payment processed\"}";

            // Parametry do przekazania w zapytaniu
            Map<String, String> apiParams = new HashMap<>();
            apiParams.put("method", "setOrderPayment");
            apiParams.put("parameters", methodParams);

            // Adres URL docelowego API
            URL url = new URL("https://api.baselinker.com/connector.php");

            // Otwieranie połączenia
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-BLToken", token);
            connection.setDoOutput(true);

            // Tworzenie zapytania POST z parametrami
            try (OutputStream outputStream = connection.getOutputStream();
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, String> param : apiParams.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(param.getKey());
                    postData.append('=');
                    postData.append(param.getValue());
                }
                writer.write(postData.toString());
                writer.flush();
            }

            // Odczytywanie odpowiedzi
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            // Zamykanie połączenia
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> httpResponse = new HashMap<>();
        httpResponse.put("info", response.toString());

        return httpResponse;
    }

}

