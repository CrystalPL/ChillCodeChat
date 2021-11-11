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

    public void addStone() {
        this.breakStone++;
    }
}
