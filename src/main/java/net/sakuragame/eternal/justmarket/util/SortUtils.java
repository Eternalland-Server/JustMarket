package net.sakuragame.eternal.justmarket.util;

import net.sakuragame.eternal.justmarket.core.PageResult;
import net.sakuragame.eternal.justmarket.core.commodity.Commodity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortUtils {

    private final static int COUNT_OF_PAGE = 6;

    public static List<Commodity> orderByPrice(List<Commodity> commodities) {
        return orderByPrice(commodities, false);
    }

    public static List<Commodity> orderByPrice(List<Commodity> commodities, boolean reverse) {
        Collections.sort(commodities);
        if (reverse) Collections.reverse(commodities);
        return commodities;
    }

    public static PageResult orderByPrice(List<Commodity> commodities, int page) {
        return orderByPrice(commodities, page, false);
    }

    public static PageResult orderByPrice(List<Commodity> commodities, int page, boolean reverse) {
        return getPagePart(orderByPrice(commodities, reverse), page);
    }

    public static List<Commodity> orderByUnitPrice(List<Commodity> commodities) {
        return orderByUnitPrice(commodities, false);
    }

    public static List<Commodity> orderByUnitPrice(List<Commodity> commodities, boolean reverse) {
        commodities.sort((o1, o2) -> (int) (o1.getUnitPrice() - o2.getUnitPrice()));
        if (reverse) Collections.reverse(commodities);
        return commodities;
    }

    public static PageResult orderByUnitPrice(List<Commodity> commodities, int page) {
        return orderByUnitPrice(commodities, page, false);
    }

    public static PageResult orderByUnitPrice(List<Commodity> commodities, int page, boolean reverse) {
        return getPagePart(orderByUnitPrice(commodities, reverse), page);
    }

    public static PageResult getPagePart(List<Commodity> commodities, int page) {
        int size = commodities.size();
        int total = size / COUNT_OF_PAGE;
        
        int current = Math.min(page, total);
        int from = current * COUNT_OF_PAGE;
        int to = (page > total) ? size : Math.min((page + 1) * COUNT_OF_PAGE, size);

        return new PageResult(current + 1, total, new ArrayList<>(commodities.subList(from, to)));
    }
}
