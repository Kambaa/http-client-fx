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
import javafx.concurrent.Task;
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

    // httpMethod->url tab change
    httpMethod.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.TAB) {
        url.requestFocus();
        event.consume();
      }
    });

    // url->sendButton tab change
    url.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.TAB) {
        sendButton.requestFocus();
        event.consume();
      }
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

    builder.followRedirects(followRedirectCheckBox.isSelected() ? HttpClient.Redirect.ALWAYS
        : HttpClient.Redirect.NEVER);
    return builder.build();
  }

  private boolean isCancellable = true;

  private record ResponseData(int statusCode, String headers, String body) {
  }

  private Task<ResponseData> task;
  private Thread workerThread;

  @FXML
  protected void onSendButtonClick() {

    sendButton.setVisible(false);
    cancelButton.setVisible(true);

    task = new Task<>() {
      @Override
      protected ResponseData call() throws Exception {
        if (isCancelled()) {
          return null;
        }
        return sendRequest();
      }

      @Override
      protected void succeeded() {
        super.succeeded();

        ResponseData data = getValue();
        if (data != null) {
          responseHeaderTextarea.setText(data.headers());
          responseCodeLabel.setText(String.valueOf(data.statusCode()));
          responseBodyTextarea.setText(data.body());
        }

        sendButton.setVisible(true);
        cancelButton.setVisible(false);
      }

      @Override
      protected void cancelled() {
        super.cancelled();
        sendButton.setVisible(true);
        cancelButton.setVisible(false);
        System.out.println("Task cancelled");
      }

      @Override
      protected void failed() {
        super.failed();
        sendButton.setVisible(true);
        cancelButton.setVisible(false);

        Throwable ex = getException();
        if (ex != null) {
          ex.printStackTrace();
        }
      }
    };

    workerThread = new Thread(task, "http-request-thread");
    workerThread.setDaemon(true);
    workerThread.start();
  }

  @FXML
  protected void onCancelButtonClick() {
    if (task != null && task.isRunning()) {
      task.cancel(true);
    }
    if (workerThread != null) {
      workerThread.interrupt();
    }
  }

  private ResponseData sendRequest()
      throws IOException, InterruptedException, NoSuchAlgorithmException,
      KeyManagementException, URISyntaxException {

    HttpClient httpClient = prepareHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(new URI(url.getText()))
        .timeout(java.time.Duration.ofSeconds(30))
        .method(httpMethod.getValue(), HttpRequest.BodyPublishers.noBody())
        .build();

    HttpResponse<String> response =
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    if (Thread.currentThread().isInterrupted()) {
      throw new InterruptedException("Request thread interrupted");
    }

    StringBuilder sb = new StringBuilder();
    response.headers().map()
        .forEach((n, v) -> sb.append(n)
            .append(": ")
            .append(String.join("", v))
            .append("\n"));

    return new ResponseData(
        response.statusCode(),
        sb.toString(),
        response.body()
    );
  }

}