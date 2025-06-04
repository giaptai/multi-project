package crypto_crawler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CC {
    public String getWebContent(String link) {
        try {
            // 1. Tạo HTTP Client
            HttpClient client = HttpClient.newHttpClient();

            // 2. Tạo HTTP Request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(link))
                    .GET()
                    .build();

            // 3. Gửi request và nhận response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. Lấy body HTML
            String html = response.body();

            // 5. Dùng regex để tìm <title>

            Pattern coinNamePattern = Pattern.compile("<p class=\"sc-65e7f566-0 iPbTJf coin-item-name\">(.*?)</p>");
            Pattern coinPricePattern = Pattern.compile("<div class=\"sc-142c02c-0 lmjbLF\"><span>(.*?)</span></div>");

            Matcher coinNameMatcher = coinNamePattern.matcher(html);
            Matcher coinPriceMatcher = coinPricePattern.matcher(html);
            // Print table header
            System.out.println("+-----------------+-------------+");
            System.out.println("| Coin Name       | Price ($)   |");
            System.out.println("+-----------------+-------------+");
            int countNames = 0;
            while (coinNameMatcher.find() && coinPriceMatcher.find()) {
                System.out.printf("| %-15s | %-11s |\n", coinNameMatcher.group(1).trim(),
                        coinPriceMatcher.group(1).trim());
                countNames++;
            }

            System.out.println("+-----------------+-------------+");
            System.out.println(countNames);
            return "";
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        new CC().getWebContent("https://coinmarketcap.com/");
    }
}
