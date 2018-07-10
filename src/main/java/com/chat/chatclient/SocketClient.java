package com.chat.chatclient;

import com.chat.chatclient.util.MsgDto;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import net.sf.json.JSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.MalformedURLException;
import java.net.URI;

/**
 * Create by Guolianxing on 2018/7/6.
 */
public class SocketClient extends WebSocketClient {

    private SocketClient(URI serverUri) {
        super(serverUri);
    }

    private SocketClient(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("建立连接");
    }

    @Override
    public void onMessage(String s) {
        if (Main.msgPane == null || Main.usersInRoom == null) {
            return;
        }
        MsgDto msgDto = (MsgDto) JSONObject.toBean(JSONObject.fromObject(s), MsgDto.class);

        // 更新聊天消息界面
        Platform.runLater(() -> {
            HBox node = Main.getMsgLabel(msgDto);
            Main.msgPane.getChildren().add(node);

            if (msgDto.getMsgType() != 0) {
                Main.usersInRoom.getChildren().clear();
                Main.usersInRoom.getChildren().addAll(Main.getUsersInRoom(msgDto.getRoomName()));
            }
        });
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("连接断开");
    }

    @Override
    public void onError(Exception e) {
        System.out.println("socket 出错了");
        System.out.println(e.getMessage());
        e.printStackTrace();
    }

    private static SocketClient socketClient = null;

    // 不存在多个线程同时请求这个单例，可以不加锁
    public static SocketClient getInstance() {
        if (GlobalStore.SOCKET_URL == null) {
            return null;
        }
        if (socketClient == null) {
            try {
                socketClient = new SocketClient(new URI(GlobalStore.SOCKET_URL), new Draft_6455());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return socketClient;
    }
}
