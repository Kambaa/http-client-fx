package tr.com.yusufgunduz.httpclientfx;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HelloController {
  @FXML
  private AnchorPane rootPane;

  @FXML
  private ComboBox<String> httpMethod;

  @FXML
  private TextField url;

  @FXML
  private Button sendButton;

  @FXML
  private CheckBox sslCheckBox;

  @FXML
  private CheckBox followRedirectCheckBox;

  @FXML
  private TextArea requestHeaderTextarea;

  @FXML
  private TextArea responseHeaderTextarea;

  @FXML
  private void initialize() {
    httpMethod.getSelectionModel().selectFirst();
    rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
      if (newScene == null) {
        return;
      }
      var ctrlW = new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN);
      newScene.getAccelerators().put(ctrlW, Platform::exit);
    });
  }

  private HttpClient prepareHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
    var builder = HttpClient.newBuilder();

    if (sslCheckBox.isSelected()) {
      TrustManager[] trustAll = new TrustManager[] {
          new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] c, String a) {
            }

            public void checkServerTrusted(X509Certificate[] c, String a) {
            }

            public X509Certificate[] getAcceptedIssuers() {
              return new X509Certificate[0];
            }
          }
      };
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustAll, new SecureRandom());
      SSLParameters sslParameters = new SSLParameters();
      sslParameters.setEndpointIdentificationAlgorithm(null); // disables hostname verification
      builder.sslContext(sslContext);
      builder.sslParameters(sslParameters);
    }

    builder.followRedirects(followRedirectCheckBox.isSelected() ? HttpClient.Redirect.NEVER
        : HttpClient.Redirect.ALWAYS);
    return builder.build();
  }

  @FXML
  protected void onSendButtonClick()
      throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException, IOException,
      InterruptedException {
    sendButton.setDisable(true);
    HttpClient httpClient = prepareHttpClient();
    System.out.println("Selected HTTP Method: " + httpMethod.getValue());
    System.out.println("Given URL: " + url.getText());
    // welcomeText.setText("Welcome to JavaFX Application!");
    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
    requestBuilder.uri(new URI(url.getText()));
    requestBuilder.method(httpMethod.getValue(), HttpRequest.BodyPublishers.noBody());
    HttpRequest request = requestBuilder.build();
    HttpResponse<String> response =
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    StringBuilder sb = new StringBuilder();
    response.headers().map().forEach((n, v) -> sb.append(n + ": " + String.join("", v) + "\n"));
    responseHeaderTextarea.setText(sb.toString());
    sendButton.setDisable(false);
  }

}