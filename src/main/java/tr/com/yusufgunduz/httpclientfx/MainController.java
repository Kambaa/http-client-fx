package tr.com.yusufgunduz.httpclientfx;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainController {

  @FXML
  private TabPane tabPane;

  private int tabCounter = 1;

  @FXML
  private void initialize() throws IOException {
    addRequestTab();
  }

  @FXML
  private void onNewTab() throws IOException {
    addRequestTab();
  }

  private void addRequestTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(
        getClass().getResource("request-tab.fxml")
    );
    Parent content = loader.load();

    RequestTabController controller = loader.getController();
    controller.setTabName("Request " + tabCounter);

    Tab tab = new Tab("Request " + tabCounter);
    tab.setContent(content);
    tab.setClosable(true);

    tab.setOnClosed(event -> controller.dispose());

    tabPane.getTabs().add(tab);
    tabPane.getSelectionModel().select(tab);

    tabCounter++;
  }
}