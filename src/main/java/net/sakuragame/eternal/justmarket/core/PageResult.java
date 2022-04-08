package net.sakuragame.eternal.justmarket.core;

import net.sakuragame.eternal.justmarket.core.commodity.Commodity;
import lombok.Getter;

import java.util.List;

@Getter
public class PageResult {

    private final int current;
    private final int total;
    private final List<Commodity> commodities;

    public PageResult(int current, int total, List<Commodity> commodities) {
        this.current = current;
        this.total = total;
        this.commodities = commodities;
    }
}
