package com.chat.chatclient;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Create by Guolianxing on 2018/7/6.
 */
public class Alert extends Stage {

    public Alert() {
    }

    public Alert(StageStyle style) {
        super(style);
    }

    /**
     * @Description: 通用弹窗，一个回调，new Alert().alert(msg, () -> {...});
     * @Author: Guolianxing
     * @Date: 2018/7/7 11:25
     */
    public void alert(String msg, Callback ok) {
        BorderPane borderPane = new BorderPane();
        Label label = new Label(msg);
        borderPane.setCenter(label);
        Button okButton = new Button("确定");
        borderPane.setBottom(okButton);
        borderPane.setAlignment(okButton, Pos.CENTER);
        okButton.setOnAction(event -> {
            this.close();
            ok.doCallback();
        });

        borderPane.setStyle("-fx-padding: 20");

        Scene scene = new Scene(borderPane, 200, 100);

        this.setScene(scene);
        this.setResizable(false);
        this.setOnCloseRequest(event -> ok.doCallback());
        this.setAlwaysOnTop(true);
        this.initModality(Modality.APPLICATION_MODAL);
        this.showAndWait();
    }

    /**
     * @Description: 确定、取消两个回调 new Alert().alert(msg, () -> {...}, () -> {...});
     * @Author: Guolianxing
     * @Date: 2018/7/7 11:26
     */
    public void alert(String msg, Callback ok, Callback cancel) {
        BorderPane borderPane = new BorderPane();
        Label label = new Label(msg);
        borderPane.setCenter(label);
        Button okButton = new Button("确定");
        Button cancelButton = new Button("取消");
        HBox hBox = new HBox();
        hBox.getChildren().addAll(okButton, cancelButton);
        hBox.setSpacing(40);
        hBox.setPadding(new Insets(0, 0, 0, 30));
        borderPane.setBottom(hBox);
        borderPane.setAlignment(hBox, Pos.CENTER);
        okButton.setOnAction(event -> {
            this.close();
            ok.doCallback();
        });

        cancelButton.setOnAction(event -> {
            this.close();
            cancel.doCallback();
        });

        borderPane.setStyle("-fx-padding: 20");

        Scene scene = new Scene(borderPane, 200, 100);

        this.setScene(scene);
        this.setResizable(false);
        this.setOnCloseRequest(event -> cancel.doCallback());
        this.setAlwaysOnTop(true);
        this.initModality(Modality.APPLICATION_MODAL);
        this.showAndWait();
    }
}
