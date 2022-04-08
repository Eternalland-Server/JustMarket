package net.sakuragame.eternal.justmarket.ui.shelf;

import com.taylorswiftcn.megumi.uifactory.generate.function.Statements;
import com.taylorswiftcn.megumi.uifactory.generate.function.SubmitParams;
import com.taylorswiftcn.megumi.uifactory.generate.type.ActionType;
import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.TextureComp;
import net.sakuragame.eternal.dragoncore.config.FolderType;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justmarket.core.TradeType;
import net.sakuragame.eternal.justmarket.core.commodity.Commodity;
import net.sakuragame.eternal.justmarket.ui.OperateCode;
import net.sakuragame.eternal.justmarket.util.Utils;
import org.bukkit.entity.Player;

import java.util.List;

public class TradeShelf extends CommodityShelf {

    private final TradeType type;

    public TradeShelf(List<Commodity> commodities, TradeType type) {
        super(commodities);
        this.type = type;
    }

    @Override
    public void send(Player player) {
        for (int i = 0; i < 6; i++) {
            int index = i + 1;
            if (i >= this.getCommodities().size()) {
                this.clearLine(player, index);
                continue;
            }

            Commodity commodity = this.getCommodities().get(i);

            String code = Utils.getUUIDPart(commodity.getUUID());
            this.getUI()
                    .addComponent(new TextureComp("c_" + i)
                            .setTexture("ui/market/l.png")
                            .setCompSize(393, 26)
                            .setExtend("g_" + index)
                    )
                    .addComponent(new TextureComp("c_" + index + "_f")
                            .setTexture("ui/pack/slot_bg.png")
                            .setXY("g_" + index + ".x+5", "g_" + index + ".y+3")
                            .setCompSize(20, 20)
                            .setVisible("area.visible")
                    )
                    .addComponent(new TextureComp("c_" + code)
                            .setText(type == TradeType.Sell ? "购买" : "出售")
                            .setTexture("ui/common/button_normal_f.png")
                            .setXY("h_4.x+84", "g_" + index + ".y+3")
                            .setCompSize(46, 20)
                            .setVisible("area.visible")
                            .addAction(ActionType.Left_Click, new Statements()
                                    .add("func.Sound_Play();")
                                    .add("c_" + code + ".texture = 'ui/common/button_normal_f_press.png';")
                                    .build()
                            )
                            .addAction(ActionType.Left_Release, "c_" + code + ".texture = 'ui/common/button_normal_f.png';")
                            .addAction(ActionType.Left_Release,
                                    new SubmitParams()
                                            .addValue(type == TradeType.Sell ? OperateCode.SellTrade.getID() : OperateCode.BuyTrade.getID())
                                            .addValue(commodity.getUUID().toString())
                            )
                    );

            this.putName(index, commodity.getItemName());
            this.putAmount(index, commodity.getAmount() + "");
            this.putUnitPrice(index, Utils.formatPrice(commodity.getUnitPrice()));
            this.putTotalPrice(index, Utils.formatPrice(commodity.getPrice()));
            this.putItem(player, index, commodity.getItemStack());
        }

        PacketSender.sendSyncPlaceholder(player, this.getPlaceholder());
        PacketSender.sendYaml(player, FolderType.Gui, this.getUI().getID(), this.getUI().build(null));
    }
}
