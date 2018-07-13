package com.chat.chatclient;

import com.chat.chatclient.util.HttpUtil;
import com.chat.chatclient.util.MsgDto;
import com.chat.chatclient.util.TimeUtil;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Create by Guolianxing on 2018/7/6.
 */
public class Main extends Application {

    private static Stage loginForm = getLoginForm();
    private static Stage registerForm = getRegisterForm();
    private static Stage mainStage;

    public static VBox msgPane;

    public static VBox usersInRoom;


    @Override
    public void start(Stage primaryStage) throws Exception {
        loginForm.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @Description: 登陆界面
     * @Author: Guolianxing
     * @Date: 2018/7/7 21:41
     */
    public static Stage getLoginForm() {
        Stage loginForm = new Stage();
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(5.5);
        pane.setVgap(5.5);
        pane.add(new Label("用户名"), 0, 0);
        TextField userNameInput = new TextField();
        pane.add(userNameInput, 1, 0);
        pane.add(new Label("密 码"), 0, 1);
        PasswordField passwordInput = new PasswordField();
        pane.add(passwordInput, 1, 1);
        Label errMsg = new Label();
        errMsg.setTextFill(Color.RED);
        pane.add(errMsg, 1, 2);
        Button login = new Button("登录");
        Button register = new Button("注册");
        pane.add(login, 0, 3);
        pane.add(register, 1, 3);

        Scene scene = new Scene(pane, 300, 200);
        loginForm.setScene(scene);
        loginForm.setResizable(false);
        loginForm.setTitle("登录");

        login.setOnAction(event -> {
            String username = userNameInput.getText();
            String password = passwordInput.getText();
            if (username.trim().equals("")) {
                errMsg.setText("请填写用户名");
                return;
            }
            if (password.equals("")) {
                errMsg.setText("请填写密码");
                return;
            }
            Map<String, String> params = new HashMap<>();
            params.put("username", username.trim());
            params.put("password", password);
            JSONObject response;
            login.setDisable(true);
            register.setDisable(true);
            try {
                response = HttpUtil.sendPost(GlobalStore.LOGIN_URL, params);
            } catch (Exception e1) {
                errMsg.setText("服务器错误");
                e1.printStackTrace();
                return;
            }
            login.setDisable(false);
            register.setDisable(false);
            errMsg.setText(response.getString("msg"));
            if (response.getString("status").equals("0")) {
                JSONObject data = response.getJSONObject("data");
                String token = data.getString("token");
                GlobalStore.TOKEN = token;
                User user = (User) JSONObject.toBean(data.getJSONObject("user"), User.class);
                GlobalStore.USER = user;
                // TODO 这里登录成功后要跳转到主界面，同时去连接webSocket
                GlobalStore.SOCKET_URL = "ws://" + GlobalStore.SERVER_IP + ":8080/socket?token=" + token;
                loginForm.close();
                mainStage = getMainStage();
                mainStage.show();
                openSocket();
            } else {
                return;
            }
        });

        register.setOnAction(event -> {
            loginForm.close();
            registerForm.show();

        });

        loginForm.setOnCloseRequest(event -> closeSocket());
        return loginForm;
    }

    /**
     * @Description: 注册界面
     * @Author: Guolianxing
     * @Date: 2018/7/7 21:41
     */
    public static Stage getRegisterForm() {
        Stage registerForm = new Stage();

        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(5.5);
        pane.setVgap(5.5);

        pane.add(new Label("用户名"), 0, 0);
        TextField userNameInput = new TextField();
        pane.add(userNameInput, 1, 0);
        pane.add(new Label("密 码"), 0, 1);
        PasswordField password1 = new PasswordField();
        pane.add(password1, 1, 1);
        pane.add(new Label("确认密码"), 0, 2);
        PasswordField password2 = new PasswordField();
        pane.add(password2, 1, 2);
        Label errMsg = new Label();
        errMsg.setTextFill(Color.RED);
        pane.add(errMsg, 1, 3);
        Button register = new Button("注册");
        Button cancel = new Button("取消");
        pane.add(register, 0, 4);
        pane.add(cancel, 1, 4);

        Scene scene = new Scene(pane, 300, 200);
        registerForm.setScene(scene);
        registerForm.setResizable(false);
        registerForm.setTitle("注册");
        register.setOnAction(event -> {
            String username = userNameInput.getText();
            String password_1 = password1.getText();
            String password_2 = password2.getText();
            if (!checkUsername(username)) {
                errMsg.setText("用户名为中文，字母，数字组合，\n长度为2-10");
                return;
            }
            if (password_1.equals("")) {
                errMsg.setText("请输入密码");
                return;
            }
            if (!password_1.equals(password_2)) {
                errMsg.setText("两次密码不一致");
                return;
            }
            Map<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password_1);
            JSONObject response;
            register.setDisable(true);
            cancel.setDisable(true);
            try {
                response = HttpUtil.sendPost(GlobalStore.REGISTER_URL, params);
            } catch (Exception e) {
                errMsg.setText("服务器错误");
                e.printStackTrace();
                return;
            }
            register.setDisable(false);
            cancel.setDisable(false);
            if (response.getString("status").equals("0")) {
                // TODO: 注册成功弹窗
                new Alert().alert("注册成功，去登陆", () -> {
                    registerForm.close();
                    loginForm.show();
                });
            } else {
                errMsg.setText(response.getString("msg"));
            }
        });
        cancel.setOnAction(event -> {
            registerForm.close();
            loginForm.show();
        });
        registerForm.setOnCloseRequest(event -> loginForm.show());
        return registerForm;
    }



    /**
     * @Description: 主界面
     * @Author: Guolianxing
     * @Date: 2018/7/7 21:42
     */
    public static Stage getMainStage() {
        Stage mainStage = new Stage();

        BorderPane pane = new BorderPane();
        HBox hBox = new HBox();
        Image userPhoto = new Image(GlobalStore.USER.getPhoto());
        ImageView imageView = new ImageView(userPhoto);
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);

        Label userNameLabel = new Label(GlobalStore.USER.getUsername(), imageView);
        userNameLabel.setContentDisplay(ContentDisplay.TOP);
        userNameLabel.setFont(Font.font("Consolas", FontWeight.BLACK, 15));
        userNameLabel.setMinHeight(130);

        Button createBtn = new Button("创建聊天室");
        Button refreshBtn = new Button("刷新列表");
        Button exitBtn = new Button("退出");
        createBtn.setOnAction(event -> getCreateForm());
        exitBtn.setOnAction(event -> new Alert().alert("确定要退出？", () -> {
            mainStage.close();
            logout();
            closeSocket();
        }, () -> {}));
        HBox btns = new HBox();
        btns.setPadding(new Insets(30, 30, 30, 30));
        btns.setSpacing(30);
        btns.getChildren().addAll(createBtn, refreshBtn, exitBtn);
        Label roomNumTip = new Label("当前聊天室共0个", btns);
        roomNumTip.setContentDisplay(ContentDisplay.TOP);
        roomNumTip.setTextAlignment(TextAlignment.LEFT);
        hBox.getChildren().addAll(userNameLabel, roomNumTip);
        hBox.setSpacing(30);
        hBox.setPadding(new Insets(0, 0, 0, 20));
        pane.setTop(hBox);

        pane.setPadding(new Insets(10, 20, 10, 20));

        FlowPane flowPane = new FlowPane();
        flowPane.setMinWidth(760);
        flowPane.setHgap(3);
        flowPane.setVgap(3);
        flowPane.setPadding(new Insets(0, 7, 0, 7));
        List<VBox> labelList = getRoomList();
        flowPane.getChildren().addAll(labelList);
        roomNumTip.setText("当前聊天室共" + labelList.size() + "个");
        refreshBtn.setOnAction(event -> {
            List<VBox> vBoxList = getRoomList();
            if (flowPane.getChildren().size() > 0) {
                flowPane.getChildren().clear();
            }
            flowPane.getChildren().addAll(vBoxList);
            roomNumTip.setText("当前聊天室共" + vBoxList.size() + "个");
        });

        ScrollPane scrollPane = new ScrollPane(flowPane);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setCenter(scrollPane);
        Scene scene = new Scene(pane, 800, 500);

        mainStage.setScene(scene);
        mainStage.setResizable(false);
        mainStage.setTitle("主页--" + GlobalStore.USER.getUsername());
        mainStage.setOnCloseRequest(event -> new Alert().alert("确定要退出？", () -> {
            logout();
            closeSocket();
        }, () -> event.consume()));
        return mainStage;
    }



    /**
     * @Description: 创建聊天室窗口
     * @Author: Guolianxing
     * @Date: 2018/7/7 21:42
     */
    public static void getCreateForm() {
        Stage form = new Stage();
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(5.5);
        pane.setVgap(5.5);
        pane.add(new Label("聊天室名称"), 0, 0);
        TextField roomNameInput = new TextField();
        pane.add(roomNameInput, 1, 0);
        Label errMsg = new Label();
        errMsg.setTextFill(Color.RED);
        pane.add(errMsg, 1, 1);
        Button ok = new Button("确定");
        Button cancel = new Button("取消");
        pane.add(ok, 0, 2);
        pane.add(cancel, 1, 2);

        cancel.setOnAction(event -> form.close());

        ok.setOnAction(event -> {
            String name = roomNameInput.getText();
            if (name.trim().equals("")) {
                errMsg.setText("聊天室名不能为空");
                return;
            }
            if (name.length() > 10) {
                errMsg.setText("聊天室名长度不超过10个字符");
                return;
            }
            JSONObject response = null;
            Map<String, String> params = new HashMap<>();
            params.put("token", GlobalStore.TOKEN);
            params.put("roomName", roomNameInput.getText());
            try {
                response = HttpUtil.sendPost(GlobalStore.CREATE_ROOM, params);
            } catch (Exception e) {
                e.printStackTrace();
                errMsg.setText("服务器错误");
                return;
            }
            if (!response.getString("status").equals("0")) {
                errMsg.setText(response.getString("msg"));
                return;
            }
            form.close();
            getChatArea(roomNameInput.getText());
        });

        Scene scene = new Scene(pane, 300, 150);

        form.setScene(scene);
        form.setTitle("创建聊天室");
        form.setResizable(false);
        form.initModality(Modality.APPLICATION_MODAL);
        form.setAlwaysOnTop(true);
        form.show();
    }


    /**
     * @Description: 聊天界面
     * @Author: Guolianxing
     * @Date: 2018/7/7 21:43
     */
    public static void getChatArea(String roomName) {
        Stage chatArea = new Stage();

        BorderPane pane = new BorderPane();

        // 左侧显示当前聊天室内的用户
        //  TODO 在usersInRoom中动态添加用户节点
        VBox users = new VBox();
        usersInRoom = users;
        ScrollPane side = new ScrollPane(users);
        side.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setLeft(side);


        // 右侧聊天区域
        VBox right = new VBox();
        // 显示聊天消息
        // TODO 在showChat添加消息节点
        VBox showChat = new VBox();
        msgPane = showChat;
        showChat.setSpacing(10);
        showChat.setMaxWidth(630);
        showChat.setMinWidth(630);
        showChat.setPadding(new Insets(0, 7, 10, 5));

        ScrollPane scrollShowChat = new ScrollPane(showChat);
        scrollShowChat.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollShowChat.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollShowChat.setMinSize(640, 300);
        scrollShowChat.setMaxSize(640, 300);
        scrollShowChat.vvalueProperty().bind(showChat.heightProperty());
        right.getChildren().add(scrollShowChat);

        // 输入消息内容
        TextArea textArea = new TextArea();
        textArea.setMaxSize(640, 150);
        textArea.setMinSize(640, 150);
        textArea.setWrapText(true);
        right.getChildren().add(textArea);

        // 底部发送按钮
        Button sendBtn = new Button("发送消息");
        Label sendBtnTip = new Label("Ctrl+Enter发送消息 ", sendBtn);
        sendBtnTip.setContentDisplay(ContentDisplay.RIGHT);
        StackPane btnPane = new StackPane();
        btnPane.getChildren().add(sendBtnTip);
        btnPane.setAlignment(sendBtnTip, Pos.CENTER_RIGHT);
        btnPane.setPadding(new Insets(20, 30, 0, 0));
        right.getChildren().add(btnPane);
        sendBtn.setOnAction(event -> {
            String s = textArea.getText();
            if (s.trim().equals("")) {
                return;
            }
            MsgDto msgDto = new MsgDto();
            msgDto.setMsgType(0);
            msgDto.setRoomName(roomName);
            msgDto.setSendUserName(GlobalStore.USER.getUsername());
            msgDto.setSendUserPhoto(GlobalStore.USER.getPhoto());
            msgDto.setSendTime(TimeUtil.dataTime());
            msgDto.setMsg(s);
            JSONObject msg = JSONObject.fromObject(msgDto);
            SocketClient client = SocketClient.getInstance();
            client.send(msg.toString());
            textArea.setText("");
            textArea.requestFocus();
        });

        textArea.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode().equals(KeyCode.ENTER)) {
                String s = textArea.getText();
                if (s.trim().equals("")) {
                    return;
                }
                MsgDto msgDto = new MsgDto();
                msgDto.setMsgType(0);
                msgDto.setRoomName(roomName);
                msgDto.setSendUserName(GlobalStore.USER.getUsername());
                msgDto.setSendUserPhoto(GlobalStore.USER.getPhoto());
                msgDto.setSendTime(TimeUtil.dataTime());
                msgDto.setMsg(s);
                JSONObject msg = JSONObject.fromObject(msgDto);
                SocketClient client = SocketClient.getInstance();
                client.send(msg.toString());
                textArea.setText("");
            }
        });
        pane.setRight(right);
        Scene scene = new Scene(pane, 800, 500);
        chatArea.setScene(scene);
        chatArea.setTitle(roomName + " [" + GlobalStore.USER.getUsername() + "]");
        chatArea.setResizable(false);
        chatArea.initModality(Modality.APPLICATION_MODAL);
        chatArea.setAlwaysOnTop(true);

        chatArea.setOnCloseRequest(event -> leaveRoom(roomName));

        chatArea.setOnShown(event -> {
            JSONObject response = null;
            Map<String, String> params = new HashMap<>();
            params.put("token", GlobalStore.TOKEN);
            params.put("roomName", roomName);
            try {
                response = HttpUtil.sendPost(GlobalStore.ENTER_ROOM, params);
            } catch (Exception e) {
                e.printStackTrace();
                new Alert().alert("服务器错误", () -> chatArea.close());
            }
            if (!response.getString("status").equals("0")) {
                new Alert().alert(response.getString("msg"), () -> chatArea.close());
            } else {
                System.out.println("进入房间成功");
            }
        });
        chatArea.showAndWait();
    }

    // 退出登录
    public static void logout() {
        Map<String, String> params = new HashMap<>();
        params.put("token", GlobalStore.TOKEN);
        try {
            HttpUtil.sendPost(GlobalStore.LOGOUT_URL, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 离开聊天室
    public static void leaveRoom(String roomName) {
        Map<String, String> params = new HashMap<>();
        params.put("token", GlobalStore.TOKEN);
        params.put("roomName", roomName);
        try {
            HttpUtil.sendPost(GlobalStore.LEAVE_ROOM, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取聊天室内所有用户
    public static List<Label> getUsersInRoom(String roomName) {
        List<Label> list = new LinkedList<>();
        JSONObject response = null;
        Map<String, String> params = new HashMap<>();
        params.put("token", GlobalStore.TOKEN);
        params.put("roomName", roomName);
        try {
            response = HttpUtil.sendPost(GlobalStore.GET_USERS_IN_ROOM, params);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert().alert("服务器异常", () -> {});
        }
        if (!response.getString("status").equals("0")) {
            new Alert().alert(response.getString("msg"), () -> {});
        } else {
            JSONArray jsonArray = response.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                ((LinkedList<Label>) list).addLast(getUserLabel(json.getString("username"), json.getString("photo")));
            }
        }
        return list;
    }

    public static Label getUserLabel(String userName, String photo) {
        Image image = new Image(photo);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        Label label = new Label("  " + userName, imageView);
        label.setContentDisplay(ContentDisplay.LEFT);

        label.setMinSize(160, 43);
        label.setMaxSize(160, 43);
        label.setFont(Font.font("Consolas", FontWeight.BLACK, 14));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setStyle("-fx-border-width: 1; -fx-border-color: black");
        label.setPadding(new Insets(0, 0, 0, 10));
        return label;
    }

    // 注册时正则检查用户名是否合法
    public static boolean checkUsername(String userName) {
        String reg =  "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{2,10}$";
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(userName).matches();
    }


    // 获取聊天室列表
    public static List<VBox> getRoomList() {
        List<VBox> list = new LinkedList<>();
        JSONObject response = null;
        Map<String, String> params = new HashMap<>();
        params.put("token", GlobalStore.TOKEN);
        try {
            response = HttpUtil.sendPost(GlobalStore.GET_ROOMINFO_URL, params);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert().alert("服务器错误", () -> System.exit(1));
        }
        if (response == null) {
            new Alert().alert("服务器错误", () -> System.exit(1));
        }
        if (!response.getString("status").equals("0")) {
            new Alert().alert(response.getString("msg"), () -> {});
        }
        JSONArray jsonArray = response.getJSONArray("data");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            ((LinkedList<VBox>) list).addLast(getRoomBox(json.getString("roomName"), json.getInt("num")));
        }
        return list;
    }

    public static VBox getRoomBox(String roomName, Integer num) {
        VBox vBox = new VBox();
        Label roomNameLabel = new Label(roomName);
        roomNameLabel.setWrapText(true);
        Label numLabel = new Label(num + "人在线");
        roomNameLabel.setTextAlignment(TextAlignment.CENTER);
        roomNameLabel.setFont(Font.font("Consolas", FontWeight.BLACK, 14));
        numLabel.setTextAlignment(TextAlignment.CENTER);

        Button enter = new Button("进入");
        enter.setAlignment(Pos.CENTER);

        enter.setOnAction(event -> getChatArea(roomName));

        Double height = 110.0;
        Double width = 120.0;
        vBox.setMinSize(width, height);
        vBox.setMaxSize(width, height);
        vBox.setStyle("-fx-border-color: black; -fx-border-width: 1");
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(5, 0, 5, 0));
        vBox.getChildren().addAll(roomNameLabel, numLabel, enter);

        return vBox;
    }

    public static HBox getMsgLabel(MsgDto msgDto) {
        Integer msgType = msgDto.getMsgType();
        if (msgType != 0) {
            Label label = new Label("系统消息: " + msgDto.getMsg());
            label.setTextAlignment(TextAlignment.CENTER);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-background-color: #FFEBCD; -fx-background-radius: 5; -fx-border-radius: 5");
            label.setMinHeight(30);
            label.setMaxHeight(30);
            label.setPadding(new Insets(0, 10, 0, 10));
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.getChildren().add(label);
            return hBox;
        }
        HBox hBox = new HBox();
        Image image = new Image(msgDto.getSendUserPhoto());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);
        VBox vBox = new VBox();
        Label usernameLabel = new Label(msgDto.getSendUserName());
        usernameLabel.setFont(Font.font("Consolas", FontWeight.BLACK, 14));
        Label timeLabel = new Label(msgDto.getSendTime(), usernameLabel);
        Label msgArea = new Label(msgDto.getMsg());
        msgArea.setStyle("-fx-background-color: #87CEEB; -fx-border-radius: 5; -fx-background-radius: 5");
        msgArea.setWrapText(true);
        msgArea.setPadding(new Insets(5, 5, 5, 5));
        msgArea.setFont(Font.font(14));
        hBox.setSpacing(10);
        vBox.setSpacing(10);
        if (msgDto.getSendUserName().equals(GlobalStore.USER.getUsername())) {
            vBox.setAlignment(Pos.TOP_RIGHT);
            timeLabel.setContentDisplay(ContentDisplay.RIGHT);
            vBox.getChildren().addAll(timeLabel, msgArea);
            hBox.setAlignment(Pos.TOP_RIGHT);
            hBox.getChildren().addAll(vBox, imageView);
        } else {
            vBox.setAlignment(Pos.TOP_LEFT);
            timeLabel.setContentDisplay(ContentDisplay.LEFT);
            vBox.getChildren().addAll(timeLabel, msgArea);
            hBox.setAlignment(Pos.TOP_LEFT);
            hBox.getChildren().addAll(imageView, vBox);
        }
        return hBox;
    }

    // 关闭websocket连接
    public static void closeSocket() {
        SocketClient client = SocketClient.getInstance();
        if (client != null && (client.isOpen() || client.isConnecting())) {
            client.close();
        }
    }

    // 打开websocket连接
    public static void openSocket() {
        SocketClient client = SocketClient.getInstance();
        client.connect();
    }

}
