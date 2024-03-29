import lombok.AllArgsConstructor;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
public class HttpRequestHandler {


    private final String token;

    public Map<String, String> sendRequest(Payment payment) {

        Map<String, String> httpResponse = new HashMap<>();
        StringBuilder response = new StringBuilder();

        if (Objects.isNull(payment)) {
            httpResponse.put("error", "order was null");
            return httpResponse;
        }

        String orderId = payment.getBlId();
        float amount = payment.getAmount();

        try {
            String methodParams = "{\"order_id\": " + orderId +
                    ",\"payment_done\": " + amount + "," +
                    "\"payment_date\": " + Instant.now().getEpochSecond() + "," +
                    "\"payment_comment\": \"payment processed\"}";

            Map<String, String> apiParams = new HashMap<>();
            apiParams.put("method", "setOrderPayment");
            apiParams.put("parameters", methodParams);

            URL url = new URL("https://api.baselinker.com/connector.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-BLToken", token);
            connection.setDoOutput(true);

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

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                httpResponse.put("info", response.toString());
            } else {
                httpResponse.put("error", "HTTP error: " + responseCode);
            }

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            httpResponse.put("error", "IOException occurred: " + e.getMessage());
        }

        return httpResponse;
    }
}

