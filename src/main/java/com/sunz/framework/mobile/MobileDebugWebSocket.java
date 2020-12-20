//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.mobile;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/mobile/onlinedebug")
public class MobileDebugWebSocket {
    private static CopyOnWriteArraySet<MobileDebugWebSocket> webSocketSet = new CopyOnWriteArraySet();
    private Session session;

    public MobileDebugWebSocket() {
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("新用户连接\t");
        System.out.print(webSocketSet.size());
        this.session = session;
        webSocketSet.add(this);
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        System.out.println("用户连接断开");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        Iterator var3 = webSocketSet.iterator();

        while(var3.hasNext()) {
            MobileDebugWebSocket item = (MobileDebugWebSocket)var3.next();

            try {
                item.sendMessage(message);
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
