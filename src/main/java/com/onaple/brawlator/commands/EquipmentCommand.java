package com.onaple.brawlator.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class EquipmentCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
       /* Optional<MonsterBean> monsterBeanOptional = args.<MonsterBean>getOne("monster");

        Optional<String> emplacementOptionnal = args.<String>getOne("emplacement");

        Optional<EquipmentBean> equipementBean = args.<EquipmentBean>getOne("equipment");

        if (monsterBeanOptional.isPresent()) {
            MonsterBean monsterBean = monsterBeanOptional.get();
            if (emplacementOptionnal.isPresent())
                if (equipementBean.isPresent()) {
                    monsterBean.addEquipement(emplacementOptionnal.get(), equipementBean.get());
                    MonsterDAO.update(monsterBean);
                    src.sendMessage(Text.builder().append(Text.of("Equipment "))
                            .append(Text.builder(equipementBean.get().getName()).color(TextColors.GOLD).build())
                            .append(Text.of(" added to monster "))
                            .append(Text.builder(monsterBean.getName()).color(TextColors.GREEN).build()).build());
                    return CommandResult.success();
                }
                src.sendMessage(Text.of("equipement is missing"));
            return CommandResult.empty();
        }
        src.sendMessage(Text.of("monster is missing"));*/
        return CommandResult.empty();
    }
}