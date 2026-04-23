package tr.com.yusufgunduz.httpclientfx;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RequestTabController {

  @FXML
  private ComboBox<String> httpMethod;

  @FXML
  private TextField urlField;

  @FXML
  private Button sendButton;

  @FXML
  private Button cancelButton;

  @FXML
  private TextArea responseBodyArea;

  private Task<String> task;
  private Thread workerThread;
  private String tabName;

  @FXML
  private void initialize() {
    httpMethod.getItems().addAll("GET", "POST", "PUT", "DELETE", "PATCH");
    httpMethod.setValue("GET");
  }

  public void setTabName(String tabName) {
    this.tabName = tabName;
  }

  @FXML
  private void onSend() {
    sendButton.setVisible(false);
    cancelButton.setVisible(true);

    task = new Task<>() {
      @Override
      protected String call() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(urlField.getText()))
            .method(httpMethod.getValue(), HttpRequest.BodyPublishers.noBody())
            .build();

        HttpResponse<String> response =
            client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
      }

      @Override
      protected void succeeded() {
        responseBodyArea.setText(getValue());
        resetButtons();
      }

      @Override
      protected void failed() {
        Throwable ex = getException();
        responseBodyArea.setText(ex == null ? "Request failed" : ex.getMessage());
        resetButtons();
      }

      @Override
      protected void cancelled() {
        responseBodyArea.setText("Cancelled");
        resetButtons();
      }
    };

    workerThread = new Thread(task, tabName + "-thread");
    workerThread.setDaemon(true);
    workerThread.start();
  }

  @FXML
  private void onCancel() {
    if (task != null) {
      task.cancel(true);
    }
    if (workerThread != null) {
      workerThread.interrupt();
    }
  }

  public void dispose() {
    onCancel();
  }

  private void resetButtons() {
    sendButton.setVisible(true);
    cancelButton.setVisible(false);
  }
}