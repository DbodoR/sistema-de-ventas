module org.dbodor.sistemadeventas {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens org.dbodor.sistemadeventas to javafx.fxml;
    exports org.dbodor.sistemadeventas;
}