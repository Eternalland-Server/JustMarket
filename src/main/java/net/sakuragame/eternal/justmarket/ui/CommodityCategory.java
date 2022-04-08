package net.sakuragame.eternal.justmarket.ui;

import com.taylorswiftcn.megumi.uifactory.generate.function.Statements;
import com.taylorswiftcn.megumi.uifactory.generate.function.SubmitParams;
import com.taylorswiftcn.megumi.uifactory.generate.type.ActionType;
import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.TextureComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import lombok.Getter;
import net.sakuragame.eternal.dragoncore.config.FolderType;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justmarket.core.TradeType;
import net.sakuragame.eternal.justmarket.core.commodity.CommodityType;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public class CommodityCategory {

    private final ScreenUI UI;
    private final TradeType tradeType;
    private final List<CommodityType> types;

    public CommodityCategory(TradeType tradeType) {
        this.UI = new ScreenUI("market_left_category");
        this.tradeType = tradeType;
        this.types = CommodityType.getCategory(tradeType);
    }

    public void send(Player player) {
        for (int i = 0; i < types.size(); i++) {
            CommodityType type = this.types.get(i);
            this.UI.addComponent(new TextureComp(type.name())
                    .setText(type.getName())
                    .setTexture("(global.market_commodity_category == " + type.getID() + ") ? 'ui/market/c_s.png':'ui/market/c.png'")
                    .setXY("body.x+14", this.getY(i))
                    .setCompSize(66, 18)
                    .addAction(ActionType.Left_Click, new Statements()
                            .add("func.Sound_Play();")
                            .add("global.market_commodity_category = " + type.getID() + ";")
                            .build()
                    )
                    .addAction(ActionType.Left_Click, new SubmitParams()
                            .addValue(OperateCode.LeftCategory.getID())
                            .addValue(type.getID())
                    )
            );
        }

        PacketSender.sendRunFunction(player, "default", "global.market_commodity_category = " + types.get(0).getID() + ";", false);
        PacketSender.sendYaml(player, FolderType.Gui, this.UI.getID(), this.UI.build(null));
    }

    private String getY(int i) {
        return "body.y+58+" + 20 * i;
    }
}
