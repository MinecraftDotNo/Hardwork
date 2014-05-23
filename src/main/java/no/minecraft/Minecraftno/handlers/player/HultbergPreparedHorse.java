package no.minecraft.Minecraftno.handlers.player;

import org.bukkit.entity.Horse;

public class HultbergPreparedHorse {

    private Horse.Variant type;
    private Horse.Color color;
    private Horse.Style style;

    public HultbergPreparedHorse() {

    }

    public Horse.Variant getType() {
        return type;
    }

    public void setType(Horse.Variant type) {
        this.type = type;
    }

    public Horse.Color getColor() {
        return color;
    }

    public void setColor(Horse.Color color) {
        this.color = color;
    }

    public Horse.Style getStyle() {
        return style;
    }

    public void setStyle(Horse.Style style) {
        this.style = style;
    }
}
