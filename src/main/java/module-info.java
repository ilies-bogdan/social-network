module com.socialnetwork {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.socialnetwork to javafx.fxml;
    exports com.socialnetwork;

    opens com.socialnetwork.controller to javafx.fxml;
    exports com.socialnetwork.controller;

    opens com.socialnetwork.domain to javafx.fxml;
    exports com.socialnetwork.domain;

    opens com.socialnetwork.domain.dto to javafx.fxml;
    exports com.socialnetwork.domain.dto;
}