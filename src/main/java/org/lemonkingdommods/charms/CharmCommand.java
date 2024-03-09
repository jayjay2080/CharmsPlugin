package org.lemonkingdommods.charms;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class CharmCommand {
    private Data data;


    public CharmCommand(Data data) {
        this.data = data;
        CommandAPICommand getCharm = new CommandAPICommand("get")
                .withOptionalArguments(new StringArgument("CharmName")
                        .replaceSuggestions(ArgumentSuggestions.strings(data.charmStrings
                        )))
                .executesNative((sender, args) -> {
                    if (sender.getCallee() instanceof Player){
                        if (args.get("CharmName") != null){
                            String charmName = args.get("CharmName").toString();
                            UUID playerID = ((Player)sender.getCallee()).getUniqueId();
                            if (data.hasCharm(playerID,charmName)){
                                return data.getCharm(playerID,charmName).getLevel();
                            }else{
                                return 0;
                            }
                        }else{
                            return 1;
                        }
                    }else{
                        return 0;
                    }
                });
        CommandAPICommand getTempCharm = new CommandAPICommand("getTemp")
                .withArguments(new StringArgument("CharmName")
                        .replaceSuggestions(ArgumentSuggestions.strings(data.charmStrings
                        )))
                .executesNative((sender, args) -> {
                    if (sender.getCallee() instanceof Player){
                        String charmName = args.get("CharmName").toString();
                        UUID playerID = ((Player)sender.getCallee()).getUniqueId();
                        if (data.hasCharm(playerID,charmName)){
                            return data.getCharm(playerID,charmName).isTemp() ? 1:0;
                        }else {
                            return 0;
                        }
                    }else{
                        return 0;
                    }
                });
        CommandAPICommand addCharm = new CommandAPICommand("add")
                .withArguments(new StringArgument("CharmName")
                        .replaceSuggestions(ArgumentSuggestions.strings(data.charmStrings
                        )))
                .withArguments(new IntegerArgument("Level",1,100))
                .executesNative((sender, args) -> {
                    if (data.charmStrings.contains(args.get("CharmName"))) {
                        try {
                            String charmName = (String) args.get("CharmName");
                            int level = (int) args.get("Level");
                            data.addCharm(((Player)sender.getCallee()).getUniqueId(), charmName, level, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw CommandAPI.failWithString("SOMETHING BROKE OH NOES: 1");
                        }
                    }

                });
        CommandAPICommand addTempCharm = new CommandAPICommand("addTemp")
                .withArguments(new StringArgument("CharmName")
                        .replaceSuggestions(ArgumentSuggestions.strings(data.charmStrings
                        )))
                .withArguments(new IntegerArgument("Level",1,100))
                .withArguments(new IntegerArgument("TimeInSeconds",1))
                .executesNative((sender, args) -> {
                    if (data.charmStrings.contains(args.get("CharmName"))) {
                        try {
                            String charmName = (String) args.get("CharmName");
                            int level = (int) args.get("Level");
                            int timeInSeconds = (int) args.get("TimeInSeconds");
                            data.addCharm(((Player)sender.getCallee()).getUniqueId(), charmName, level, true, timeInSeconds);
                        } catch (Exception e) {
                            throw CommandAPI.failWithString("SOMETHING BROKE OH NOES: 3");
                        }
                    }

                });

        CommandAPICommand listCharms = new CommandAPICommand("List")
                .withArguments(new PlayerArgument("target"))
                .executesPlayer((sender, args) -> {
                    Map<String, Data.CharmObj> charms = data.getAllCharms(((Player)args.get("target")).getUniqueId());
                    if (charms != null){
                        for (Map.Entry<String, Data.CharmObj> entry : charms.entrySet()) {
                            sender.sendMessage(entry.getValue().getCharmName() +": Level:"+ String.valueOf(entry.getValue().getLevel()) +" SecondsLeft:"+String.valueOf(entry.getValue().getExtra()));
                        }
                        sender.sendMessage("Other charms and curses:");
                        sender.sendMessage(sender.getScoreboardTags().toString());
                    }else{
                        sender.sendMessage("no Charms");
                    }
                });

        CommandAPICommand removeCharm = new CommandAPICommand("remove")
                .withArguments(new StringArgument("CharmName")
                        .replaceSuggestions(ArgumentSuggestions.strings(data.charmStrings
                        )))
                .executesNative((sender, args) -> {
                    if (data.charmStrings.contains(args.get("CharmName"))) {
                        data.removeCharm(((Player)sender.getCallee()).getUniqueId(), ((String) args.get("CharmName")));
                    }

                });


        new CommandAPICommand("charm")
                .withSubcommand(addCharm)
                .withSubcommand(addTempCharm)
                .withSubcommand(listCharms)
                .withSubcommand(removeCharm)
                .withSubcommand(getCharm)
                .withSubcommand(getTempCharm)
                .withPermission(CommandPermission.OP)// Required permissions
                .register();

    }

}
