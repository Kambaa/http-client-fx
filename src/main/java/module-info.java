module tr.com.yusufgunduz.httpclientfx {
    requires javafx.controls;
    requires javafx.fxml;
  requires java.net.http;

  opens tr.com.yusufgunduz.httpclientfx to javafx.fxml;
    exports tr.com.yusufgunduz.httpclientfx;
}