package kata;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

/*
##########################################################################################
  No need to read this class, it is not part of this exercise!!!!!!!!!!!!!!!!! (yet)
##########################################################################################
 */
public class SmsGateway {

  private final HttpClient httpClient = HttpClient.newBuilder()
      .version(Version.HTTP_2)
      .connectTimeout(Duration.ofSeconds(2))
      .build();

  public void send(String phoneNumber, String message) {
    var parameters =
        Map.of("To", phoneNumber,
            "From", "+37258141113",
            "Body", message);

    HttpRequest httpRequest = HttpRequest.newBuilder()
        .header("Authorization",
            basicAuth(System.getenv("TWILIO_ACCOUNT_SID"), System.getenv("TWILIO_AUTH_TOKEN")))
        .header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
        .uri(url())
        .POST(ofFormData(parameters))
        .build();

    try {
      HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
      System.out.println(httpResponse.body());
    } catch (IOException | InterruptedException ex) {
      throw new RuntimeException(ex);
    }
  }

  private BodyPublisher ofFormData(Map<String, String> parameters) {
    String result = parameters.entrySet()
        .stream()
        .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
        .collect(Collectors.joining("&"));
    return BodyPublishers.ofString(result);
  }

  private static URI url() {
    return URI.create("https://api.twilio.com/2010-04-01/Accounts/%s/Messages.json".formatted(
        System.getenv("TWILIO_ACCOUNT_SID")));
  }

  private static String basicAuth(String username, String password) {
    return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
  }

}
