package com.wtsystems.hallothere.Model;

public class NotificationData {

    /*Estrutura de Dados para enviarmos a notificação para o Firebase

        {
            "to": "token",
            "notification" : {
                    "title" : "título da notificação"
                    "body" : "corpo da notificação
                    }
        }
     */


    private String to;
    private Notification notification;

    public NotificationData(String to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
