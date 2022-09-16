package net.sakuragame.eternal.justmarket.api.event;

import lombok.Getter;
import net.sakuragame.eternal.justmarket.core.commodity.Commodity;
import org.bukkit.entity.Player;

@Getter
public class MarketUnshelveEvent extends JustEvent {

    private final Commodity commodity;

    public MarketUnshelveEvent(Player who, Commodity commodity) {
        super(who);
        this.commodity = commodity;
    }

}
