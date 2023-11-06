import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebLogAnalyzer {
    private static final Logger LOGGER = Logger.getLogger(WebLogAnalyzer.class.getName());

    public static void main(String[] args) {
        String logFilePath = "src/access.txt";
        String reportFilePath = "src/web_traffic_report.txt";

        int totalRequests = 0;
        Map<String, Integer> requestsPerIP = new HashMap<>();
        int requestsWith404Status = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(reportFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                totalRequests++;

                // Используйте регулярное выражение для извлечения соответствующей информации из записи журнала.
                String regex = "\\[(.*?)\\] - \\[(.*?)\\] \\[(.*?)\\] \"(.*?)\" (\\d+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(line);


                if (matcher.find()) {
                    String ipAddress = matcher.group(1);
                    String username = matcher.group(2);
                    String timestamp = matcher.group(3);
                    String request = matcher.group(4);
                    int statusCode = Integer.parseInt(matcher.group(5));

                    // Подсчет запросов на IP-адрес
                    requestsPerIP.put(ipAddress, requestsPerIP.getOrDefault(ipAddress, 0) + 1);

                    // Подсчет запросов с кодом состояния HTTP 404
                    if (statusCode == 404) {
                        requestsWith404Status++;
                    }
                }
            }

            //Пишем сводный отчет
            writer.write("Всего запросов: " + totalRequests);
            writer.newLine();
            writer.write("Запросы на IP-адрес: " + requestsPerIP);
            writer.newLine();
            writer.write("Запросы с кодом состояния HTTP 404: " + requestsWith404Status);

            LOGGER.info("Всего запросов: " + totalRequests);
            LOGGER.info("Запросы на IP-адрес: " + requestsPerIP);
            LOGGER.info("Запросы с кодом состояния HTTP 404: " + requestsWith404Status);
            LOGGER.info("Отчет о веб-трафике успешно создан.");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,"Возникло исключение при обработке файла журнала.", e);
        }
    }
}
