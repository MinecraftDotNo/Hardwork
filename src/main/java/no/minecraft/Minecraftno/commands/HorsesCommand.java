package no.minecraft.Minecraftno.commands;

import no.minecraft.Minecraftno.Minecraftno;
import no.minecraft.Minecraftno.handlers.BankHandler;
import no.minecraft.Minecraftno.handlers.player.HultbergPreparedHorse;
import no.minecraft.Minecraftno.handlers.player.UserHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;

public class HorsesCommand extends MinecraftnoCommand {

    // Maps
    private final HashMap<Player, Integer> buyers = new HashMap<Player, Integer>();
    private final HashMap<String, HultbergPreparedHorse> plHorse = new HashMap<String, HultbergPreparedHorse>();
    private final HashMap<String, Horse.Variant> types = new HashMap<String, Horse.Variant>();
    private final HashMap<String, Horse.Color> colors = new HashMap<String, Horse.Color>();
    private final HashMap<String, Horse.Style> styles = new HashMap<String, Horse.Style>();

    private BankHandler bankHandler;

    public HorsesCommand(Minecraftno instance) {
        super(instance);
        setAccessLevel(1);
        this.bankHandler = instance.getBankHandler();

        // Fill maps.
        for (Horse.Variant var : Horse.Variant.values()) {
            if (var != null) {
                types.put(var.toString().toLowerCase().replaceAll("_", "").trim(), var);
            }
        }

        for (Horse.Color var : Horse.Color.values()) {
            if (var != null) {
                colors.put(var.toString().toLowerCase().replaceAll("_", "").trim(), var);
            }
        }

        for (Horse.Style var : Horse.Style.values()) {
            if (var != null) {
                styles.put(var.toString().toLowerCase().replaceAll("_", "").trim(), var);
            }
        }
    }

    @Override
    public boolean onPlayerCommand(Player player, Command command, String label, String[] args) {
        if (args.length == 0) {
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "hest start" + getVarChatColor() + " - Starter prosessen for kjøp av en hest, start-pris er 100g.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "hest set [type/farge/stil] <var>" + getVarChatColor() + " - Sett type/farge/stil.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "hest list [typer/farger/stiler]" + getVarChatColor() + " - Lister alle typer/farger/stiler.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "hest kjøp/kjop (antall)" + getVarChatColor() + " - Spawner og betaler for hesten, antall er ikke påkrevd.");
            player.sendMessage(getDefaultChatColor() + "/" + getCommandChatColor() + "hest avbryt" + getVarChatColor() + " - Avbryter prosessen.");
        } else {
            handleCommand(player, args);
        }
        return true;
    }

    private void handleCommand(Player player, String[] args) {
        final String uname = player.getName();
        if (args.length >= 1 && args[0].equalsIgnoreCase("list")) {
            if (args.length > 1 && args[1].equalsIgnoreCase("typer")) {
                this.listsCommand(player, available("typer"), makeListString(types.keySet()));
                player.sendMessage(ChatColor.GRAY + "Mules og donkeys koster 50g ekstra mens skeleton og undead koster 100g ekstra.");
            } else if (args.length > 1 && args[1].equalsIgnoreCase("farger")) {
                this.listsCommand(player, available("farger"), makeListString(colors.keySet()));
                player.sendMessage(ChatColor.GRAY + "All farge untatt hvit koster 25g ekstra.");
            } else if (args.length > 1 && args[1].equalsIgnoreCase("stiler")) {
                this.listsCommand(player, available("stiler"), makeListString(styles.keySet()));
                player.sendMessage(ChatColor.GRAY + "All stil untatt 'none' koster 25g ekstra.");
            }
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("start")) {
            if (this.buyers.containsKey(player)) {
                player.sendMessage(this.getErrorChatColor() + "Du har allerede startet en prosess, skriv " + this.getCommandChatColor() + "/hest avbryt" + this.getErrorChatColor() + " for å avbryte");
                return;
            }
            int amount = this.bankHandler.getAmount(player);
            if (amount >= 100) {
                player.sendMessage(this.getInfoChatColor() + "Du har startet prosessen av å kjøpe en hest, du hadde nok gull på konto.");
                player.sendMessage(this.getVarChatColor() + " Nå må du sette hvilken type hest du vil ha. For å sette en type skriv " + this.getCommandChatColor() + "/hest set type [type]" + this.getVarChatColor() + ". For å se en liste over tilgjenelige typer se " + this.getCommandChatColor() + "/hest list typer" + this.getOkChatColor() + ".");
                player.sendMessage(STATUS_MESSAGE);
                this.buyers.put(player, 100);
                this.plHorse.put(uname, new HultbergPreparedHorse());
            } else {
                player.sendMessage(this.getErrorChatColor() + "Du har ikke 100g på konto.");
            }
        } else {
            if (!this.buyers.containsKey(player)) {
                player.sendMessage(NO_PROCESS);
                return;
            }
            if (args.length >= 1 && args[0].equalsIgnoreCase("status")) {
                Horse.Variant var = this.getPreparedHorse(uname).getType();
                player.sendMessage(this.getDefaultChatColor() + "Pris: " + this.getVarChatColor() + getPrice(player) + "g");

                if (var != null) {
                    player.sendMessage(this.getDefaultChatColor() + "Type: " + ChatColor.WHITE + var.name().toLowerCase());
                    if (var == Horse.Variant.HORSE) {
                        if (this.getPreparedHorse(uname).getColor().name() != null) {
                            player.sendMessage(this.getDefaultChatColor() + "Farge: " + ChatColor.WHITE + this.getPreparedHorse(uname).getColor().name().toLowerCase());
                        }

                        if (this.getPreparedHorse(uname).getStyle().name() != null) {
                            player.sendMessage(this.getDefaultChatColor() + "Stil: " + ChatColor.WHITE + this.getPreparedHorse(uname).getStyle().name().toLowerCase());
                        }
                    }
                }
                player.sendMessage(ISOK_MESSAGE);
            } else if (args.length >= 1 && args[0].equalsIgnoreCase("avbryt")) {
                this.buyers.remove(player);
                this.plHorse.remove(uname);
                player.sendMessage(this.getDefaultChatColor() + "Du avbrøt prosessen.");
            } else if (args.length >= 1 && (args[0].equalsIgnoreCase("kjøp") || args[0].equalsIgnoreCase("kjop"))) {
                if (!this.buyers.containsKey(player)) {
                    player.sendMessage(NO_PROCESS);
                    return;
                }

                int wants = 1;
                if (args.length >= 2) {
                    if (Minecraftno.canParse(args[1])) {
                        wants = Integer.parseInt(args[1]);
                    } else {
                        player.sendMessage(format("&c&Ugyldig antall: &f&" + args[1]));
                        return;
                    }
                }

                HultbergPreparedHorse ho = this.getPreparedHorse(uname);
                if (ho.getType() == null) {
                    player.sendMessage(NO_PROCESS);
                    return;
                }

                int price = getPrice(player) * wants;
                int amount = this.bankHandler.getAmount(player);
                if (amount >= price) {
                    this.bankHandler.removeAmount(player, price);
                    this.bankHandler.insertAmount(UserHandler.SERVER_USERNAME, price);
                    for (int i = 0; i < wants; i++) {
                        Horse hor = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
                        hor.setVariant(ho.getType());
                        if (ho.getType() == Horse.Variant.HORSE) {
                            if (ho.getStyle() != null) {
                                hor.setStyle(ho.getStyle());
                            }

                            if (ho.getColor() != null) {
                                hor.setColor(ho.getColor());
                            }
                        }
                    }
                    this.buyers.remove(player);
                    this.plHorse.remove(uname);
                    player.sendMessage(this.getInfoChatColor() + "Hesten er betalt og spawnet ved deg.");
                    player.sendMessage(format("&a&Spawnet&f& " + wants + "&a& stykk(er), pris ble&f& " + price + "g&a&."));
                } else {
                    player.sendMessage(format("&c&Du har ikke &f&" + price + "g&c& på konto."));
                }
            } else if (args.length >= 1 && args[0].equalsIgnoreCase("set")) {
                if (args.length > 2 && args[1].equalsIgnoreCase("type")) {
                    if (this.types.containsKey(args[2])) {
                        Horse.Variant var = this.types.get(args[2]);

                        if (var != Horse.Variant.HORSE) {
                            if (var == Horse.Variant.UNDEAD_HORSE || var == Horse.Variant.SKELETON_HORSE) {
                                //this.buyers.put(player, getPrice(player) + 100);
                                player.sendMessage(ChatColor.RED + "Du kan dessverre ikke kjøpe undead- eller skeleton-hest.");
                                return;
                            } else {
                                this.buyers.put(player, getPrice(player) + 50);
                            }
                        }

                        this.getPreparedHorse(uname).setType(var);

                        // Set default values.
                        this.getPreparedHorse(uname).setColor(Color.WHITE);
                        this.getPreparedHorse(uname).setStyle(Style.NONE);

                        player.sendMessage(this.getInfoChatColor() + "Type er satt til: " + ChatColor.WHITE + var.name().toLowerCase().replaceAll("_", ""));
                        player.sendMessage(this.getOkChatColor() + "Pris: " + this.getVarChatColor() + getPrice(player) + "g");
                        if (var == Horse.Variant.HORSE) {
                            player.sendMessage(this.getVarChatColor() + " Nå må du sette hvilken farge hesten din skal ha. For å sette en farge skriv " + this.getCommandChatColor() + "/hest set farge [farge]" + this.getVarChatColor() + ".");
                            player.sendMessage(STATUS_MESSAGE);
                        } else {
                            player.sendMessage(this.getVarChatColor() + " Hester er de eneste som kan egen farge og stil så skriv " + this.getCommandChatColor() + "/hest status" + this.getVarChatColor() + " for å se hva som er satt nå.");
                        }
                    } else {
                        player.sendMessage(cantFind("typen", args[2]));
                    }
                } else if (args.length > 2 && args[1].equalsIgnoreCase("farge")) {
                    if (this.getPreparedHorse(uname).getType() != Horse.Variant.HORSE) {
                        player.sendMessage(ONLYHORSE);
                        return;
                    }
                    if (this.colors.containsKey(args[2])) {
                        Horse.Color col = this.colors.get(args[2]);
                        this.getPreparedHorse(uname).setColor(col);
                        if (col != Horse.Color.WHITE) {
                            this.buyers.put(player, getPrice(player) + 25);
                        }
                        player.sendMessage(this.getInfoChatColor() + "Farge er satt til: " + ChatColor.WHITE + col.name().toLowerCase().replaceAll("_", ""));
                        player.sendMessage(this.getOkChatColor() + "Pris: " + this.getVarChatColor() + getPrice(player) + "g");
                        player.sendMessage(this.getVarChatColor() + " Nå må du sette hvilken stil hesten skal ha. For å sette en stil skriv " + this.getCommandChatColor() + "/hest set stil [stil]" + this.getVarChatColor() + ".");
                        player.sendMessage(STATUS_MESSAGE);
                    } else {
                        player.sendMessage(cantFind("fargen", args[2]));
                    }
                } else if (args.length > 2 && args[1].equalsIgnoreCase("stil")) {
                    if (this.getPreparedHorse(uname).getType() != Horse.Variant.HORSE) {
                        player.sendMessage(ONLYHORSE);
                        return;
                    }
                    if (this.styles.containsKey(args[2])) {
                        Horse.Style sty = this.styles.get(args[2]);
                        this.getPreparedHorse(uname).setStyle(sty);
                        if (sty != Horse.Style.NONE) {
                            this.buyers.put(player, getPrice(player) + 25);
                        }
                        player.sendMessage(this.getInfoChatColor() + "Stil er satt til: " + ChatColor.WHITE + sty.name().toLowerCase().replaceAll("_", ""));
                        player.sendMessage(this.getOkChatColor() + "Pris: " + this.getVarChatColor() + getPrice(player) + "g");
                        player.sendMessage(this.getVarChatColor() + " Nå er du klar for betaling. Bruk " + this.getCommandChatColor() + "/hest kjøp (antall)" + this.getVarChatColor() + " for å kjøpe hesten, antall er ikke påkrevd.");
                    } else {
                        player.sendMessage(cantFind("stilen", args[2]));
                    }
                }
            }
        }
    }

    private HultbergPreparedHorse getPreparedHorse(String uname) {
        return this.plHorse.get(uname);
    }

    private int getPrice(Player p) {
        return this.buyers.get(p);
    }

    private String makeListString(Set<String> set) {
        String typer = "";
        for (String ty : set) {
            typer += this.getVarChatColor() + ty + this.getArgChatColor() + ", ";
        }
        return typer.substring(0, typer.length() - 2) + ".";
    }

    // Functions
    private void listsCommand(Player pl, String topL, String list) {
        pl.sendMessage(this.getDefaultChatColor() + topL + " " + this.getVarChatColor() + list);
    }

    private String STATUS_MESSAGE = format(" &7&For status på prossesen skriv &6&/hest status");
    private String ISOK_MESSAGE = format(" &7&Er alt ok? Skriv &6&/hest kjøp&7& for å spawne og kjøpe hesten/donkeyen/mulen.");
    private String NO_PROCESS = format("&c&Du har ikke startet prosessen enda, skriv &6&/hest start");
    private String ONLYHORSE = format("&c&Hester er de eneste som kan egen farge og stil.");

    private String CANT_FIND = format("&c&Fant ikke #What:&f& #Notfound");

    private String cantFind(String what, String notfound) {
        return CANT_FIND.replaceAll("#What", what).replaceAll("#Notfound", notfound);
    }

    private String AVAILABLE = format("&2&Tilgjenelige #What:");

    private String available(String what) {
        return AVAILABLE.replaceAll("#What", what);
    }

    public String format(String string) {
        String s = string;
        for (ChatColor color : ChatColor.values()) {
            s = s.replaceAll("&" + color.getChar() + "&(?i)", color.toString());
        }
        return s;
    }
}
