module tr.com.yusufgunduz.httpclientfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens tr.com.yusufgunduz.httpclientfx to javafx.fxml;
    exports tr.com.yusufgunduz.httpclientfx;
}