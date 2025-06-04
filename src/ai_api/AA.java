package ai_api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;
import common.Constants;

public class AA {
    private class Content {
        private String role;
        private List<PartItem> parts;

        private Content() {
        }

        public Content(String role, List<PartItem> parts) {
            this.role = role;
            this.parts = parts;
        }

        @Override
        public String toString() {
            return "{\"role\":\"" + role + "\", \"parts\":" + parts + "}";
        }

    }

    private class PartItem {
        private String text;

        private PartItem() {
        }

        public PartItem(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "{\"text\":\"" + text + "\"}";
        }

    }

    private void GeminiAPI() {
        Content contentUser = new Content("user", List.of(new PartItem("Xin chào ! Tôi tên là Long")));
        List<Content> contents = new ArrayList<>();
        contents.add(contentUser);
        HttpClient httpClient = HttpClient.newHttpClient();
        String jsonRBody = "{\"contents\":" + contents + "}";
        BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(jsonRBody);
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine();
            if (input.equals("exit")) {
                sc.close();
                break;
            }
            Content contentUser2 = new Content("user", List.of(new PartItem(input)));
            contents.add(contentUser2);
            jsonRBody = "{\"contents\":" + contents + "}";
            bodyPublisher = HttpRequest.BodyPublishers.ofString(jsonRBody);
            try {
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(Constants.GEMINI_URL))
                        .header("Content-Type", "application/json; charset=UTF-8")
                        // .headers("User-Agent", "PureJavaGeminiCaller/1.0")
                        .POST(bodyPublisher)
                        .build();
                HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                System.out.println("HTTP Status Code: " + httpResponse.statusCode());
                System.out.println("Gemini API Response...");
                System.out.println(httpResponse.body());
                JSONObject obj = new JSONObject(httpResponse.body());
                String ww = obj.getJSONArray("candidates").getJSONObject(0).getJSONObject("content")
                        .getJSONArray("parts").getJSONObject(0).getString("text");
                Content contentModel = new Content("model", List.of(new PartItem(ww)));
                contents.add(contentModel);
                System.out.println(contents);
            } catch (IOException | InterruptedException | NullPointerException e) {
                System.out.println("API quá lâu, hủy yêu cầu.");
                e.printStackTrace();
            }
        }
    }

    private void DeepseekAPI() {
        HttpClient httpClient = HttpClient.newHttpClient();
        String jsonRBody = "{\"model\":\"deepseek-chat\",\"messages\":[{\"role\":\"user\",\"content\":\"Hello !\"}]}";
        BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString(jsonRBody);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Constants.DEEPSEEK_URL))
                    .headers("Content-Type", "application/json; charset=UTF-8",
                            "Authorization", "Bearer ".concat(Constants.DEEPSEEK_KEY))
                    .POST(bodyPublisher)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.printf("HTTP Status Code: %d\n", response.statusCode());
            System.out.println(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void OpenAIAPI() {
        HttpClient httpClient = HttpClient.newHttpClient();
        String bodyReq = "{\"model\":\"omni-moderation-latest\",\"input\":[{\"role\":\"user\",\"content\":\"Hello !\"}]}";
        BodyPublisher bodyPub = HttpRequest.BodyPublishers.ofString(bodyReq);
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(Constants.OPENAI_URL))
                    .headers("Content-Type", "application/json; charset=UTF-8",
                            "Authorization", "Bearer ".concat(Constants.OPENAI_KEY))
                    .POST(bodyPub)
                    .build();

            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.printf("HTTP Status Code: %d\n", res.statusCode());
            System.out.println(res.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AA aa = new AA();
        aa.GeminiAPI();
        aa.DeepseekAPI();
        aa.OpenAIAPI();
    }
}
