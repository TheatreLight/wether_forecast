import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class WetherForecast {
    public static void main(String[] args) {
        double lattitude;
        double longitude;
        int days;

        System.out.println("Enter the coordinates: ");
        Scanner scanner = new Scanner(System.in);
        lattitude = scanner.nextDouble();
        longitude = scanner.nextDouble();
        System.out.println("Enter the days number: ");
        days = scanner.nextInt();

        HttpClient client = HttpClient.newHttpClient();
        String url = "https://api.weather.yandex.ru/v2/forecast?lat=" +
                Double.toString(lattitude) + "&lon=" + Double.toString(longitude) +
                "&limit=" + Integer.toString(days);

        URI uri = URI.create(url);
        HttpRequest request = HttpRequest.newBuilder().
                uri(uri).GET().
                header("X-Yandex-Weather-Key", "84afae18-9016-4989-8da9-61a1fde84f89").build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status code: " + response.statusCode());
            String responseBody = response.body();
            System.out.println("Response body: " + responseBody);

            double currentTemp;
            double avgTemp = 0;
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject)parser.parse(responseBody);
            var fact = (JSONObject)obj.get("fact");
            var temp = fact.get("temp");
            System.out.println("Current temperature is " + temp.toString() + " degrees.");

            var forecasts = (JSONArray)obj.get("forecasts");
            int count = 0;
            for (Object forecast : forecasts) {
                count++;
                Long t_temp;
                var part = ((JSONObject)forecast).get("parts");
                var day = ((JSONObject)part).get("day");
                t_temp = (Long)((JSONObject)day).get("temp_avg");
                avgTemp += t_temp.doubleValue();
            }
            avgTemp /= count;
            avgTemp = Math.round(avgTemp * 10.0)/10.0;

            System.out.println("The average temperature for " + Integer.toString(days) +
                    " days is " + Double.toString(avgTemp) + " degrees.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
