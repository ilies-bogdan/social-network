package com.socialnetwork.domain.dto;

import com.socialnetwork.domain.FriendshipStatus;
import com.socialnetwork.utils.Constants;

import java.time.LocalDateTime;

public class FriendshipDto {
    private String friendUsername;
    private String friendsFrom;
    private String status;

    public FriendshipDto(String friendUsername, LocalDateTime friendsFrom, FriendshipStatus status) {
        this.friendUsername = friendUsername;
        this.friendsFrom = friendsFrom.format(Constants.DATE_TIME_FORMATTER);
        this.status = status.name();
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getFriendsFrom() {
        return friendsFrom;
    }

    public void setFriendsFrom(LocalDateTime friendsFrom) {
        this.friendsFrom = friendsFrom.format(Constants.DATE_TIME_FORMATTER);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status.name();
    }
}
