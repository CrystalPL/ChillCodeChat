package pl.chillcode.chillcodechat.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class User {
    int breakStone;
    int slowDownTime;
    long lastMessage;

    public User(final int breakStone, final int slowDownTime) {
        this.breakStone = breakStone;
        this.slowDownTime = slowDownTime;
    }

    public void addStone() {
        this.breakStone++;
    }
}
