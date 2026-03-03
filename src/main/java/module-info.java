module org.dbodor.sistemadeventas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    requires org.kordamp.bootstrapfx.core;

    opens org.dbodor.sistemadeventas to javafx.fxml;
    exports org.dbodor.sistemadeventas;
    exports org.dbodor.sistemadeventas.Controllers;
    opens org.dbodor.sistemadeventas.Controllers to javafx.fxml;
}