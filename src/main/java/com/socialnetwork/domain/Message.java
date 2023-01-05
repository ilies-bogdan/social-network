package com.socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message implements Entity<Long> {
    private long id;
    private LocalDateTime sentAt;
    private String subject;
    private String text;
    private String sender;
    private String receiver;

    public Message(LocalDateTime sentAt, String subject, String text, String sender, String receiver) {
        this.sentAt = sentAt;
        this.subject = subject;
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id && Objects.equals(sentAt, message.sentAt) && Objects.equals(subject, message.subject) && Objects.equals(text, message.text) && Objects.equals(sender, message.sender) && Objects.equals(receiver, message.receiver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sentAt, subject, text, sender, receiver);
    }

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }
}
