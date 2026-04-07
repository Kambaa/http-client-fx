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
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
  private Button cancelButton;

  @FXML
  private CheckBox sslCheckBox;

  @FXML
  private CheckBox followRedirectCheckBox;

  @FXML
  private TextArea requestHeaderTextarea;

  @FXML
  private Label responseCodeLabel;
  @FXML
  private TextArea responseHeaderTextarea;

  @FXML
  private TextArea responseBodyTextarea;

  private FutureTask task;

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

  private boolean isCancellable = true;

  @FXML
  protected void onSendButtonClick() {

    sendButton.setVisible(false);
    cancelButton.setVisible(true);

    task = new Task<>() {
      @Override
      protected Void call() throws Exception {
        System.out.println("Task called");
        for (int i = 0; i < 6; i++) {
          if (isCancelled()) {
            return null;
          }

          try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
          } catch (InterruptedException e) {
            if (isCancelled()) {
              System.out.println("Task Cancelled(in call method)");
              return null;
            }
            System.out.println("Task interrupted");
            Thread.currentThread().interrupt();
            throw e;
          }
        }
        System.out.println("Task done");
        return null;
      }

      @Override
      protected void succeeded() {
        System.out.println("Task succeeded");

        super.succeeded();
        sendButton.setVisible(true);
        cancelButton.setVisible(false);
      }

      @Override
      protected void cancelled() {
        System.out.println("Task cancelled(inside cancelled method)");

        super.cancelled();
        sendButton.setVisible(true);
        cancelButton.setVisible(false);
      }

      @Override
      protected void failed() {
        System.out.println("Task failed");

        super.failed();
        sendButton.setVisible(true);
        cancelButton.setVisible(false);
        Throwable ex = getException();
        if (ex != null) {
          System.out.println(" failed exception is:");
          ex.printStackTrace();
        }
      }
    };

    Thread thread = new Thread(task);
    thread.setDaemon(true);
    thread.start();

    // try {
    //   HttpClient httpClient = prepareHttpClient();
    //   System.out.println("Selected HTTP Method: " + httpMethod.getValue());
    //   System.out.println("Given URL: " + url.getText());
    //   // welcomeText.setText("Welcome to JavaFX Application!");
    //   HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
    //   requestBuilder.uri(new URI(url.getText()));
    //   requestBuilder.method(httpMethod.getValue(), HttpRequest.BodyPublishers.noBody());
    //   HttpRequest request = requestBuilder.build();
    //   HttpResponse<String> response =
    //       httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    //   StringBuilder sb = new StringBuilder();
    //   response.headers().map().forEach((n, v) -> sb.append(n + ": " + String.join("", v) + "\n"));
    //   responseHeaderTextarea.setText(sb.toString());
    //   responseCodeLabel.setText(String.valueOf(response.statusCode()));
    //   responseBodyTextarea.setText(response.body());
    //   System.out.println(response.statusCode());
    //   System.out.println(response.body());
    // } catch (Exception ex) {
    //   System.out.println(ex.getMessage());
    // } finally {
    //   // cancelButton.setVisible(false);
    //
    //   // sendButton.setVisible(true);
    // }
  }

  @FXML
  protected void onCancelButtonClick() {
    if (task != null ) {
      task.cancel(true);
      System.out.println("Task cancelled (onCancelButtonClick) end");
    }
  }

}