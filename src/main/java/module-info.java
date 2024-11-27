module p.project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens p.project to javafx.fxml;
    exports p.project;
    exports p.project.Controllers;
    opens p.project.Controllers to javafx.fxml;
    exports p.project.DBHandling;
    opens p.project.DBHandling to javafx.fxml;
    exports p.project.Classes;
    opens p.project.Classes to javafx.fxml;
}