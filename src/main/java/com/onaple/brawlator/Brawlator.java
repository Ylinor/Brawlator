package com.onaple.brawlator;

import com.onaple.brawlator.action.LootAction;
import com.onaple.brawlator.commands.ReloadCommand;
import com.onaple.brawlator.commands.database.SelectMonsterCommand;
import com.onaple.brawlator.data.handler.ConfigurationHandler;
import com.onaple.brawlator.event.SpawnEvent;
import com.onaple.brawlator.action.MonsterAction;
import com.onaple.brawlator.action.SpawnerAction;
import com.onaple.brawlator.commands.InvokeCommand;
import com.onaple.brawlator.exception.PluginNotFoundException;
import com.onaple.brawlator.exception.WorldNotFoundException;

import com.onaple.itemizer.Itemizer;
import com.onaple.itemizer.service.IItemService;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;

import org.spongepowered.api.entity.Entity;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Plugin(id = "brawlator", name = "Brawlator", version = "1.1.0")
public class Brawlator {

	private static Logger logger;

	@Inject
	private void setLogger(Logger logger) {
		Brawlator.logger = logger;
	}
	public static Logger getLogger() {
		return logger;
	}

	@Inject
	@ConfigDir(sharedRoot=true)
	private Path configDir;

	private PluginManager pluginManager = Sponge.getPluginManager();


	private static MonsterAction monsterAction;

	@Inject
	private void setMonsterAction(MonsterAction monsterAction) {
		Brawlator.monsterAction = monsterAction;
	}
	public static MonsterAction getMonsterAction() {
		return monsterAction;
	}

	@Inject
	private static ConfigurationHandler configurationHandler;
	@Inject
	private void setConfigurationHandler(ConfigurationHandler configurationHandler) {
		Brawlator.configurationHandler = configurationHandler;
	}
	public static ConfigurationHandler getConfigurationHandler() {
		return configurationHandler;
	}


	private static Brawlator brawlator;
	public static Brawlator getBrawlator(){return brawlator;}



	@Inject
	private SpawnerAction spawnerAction;


	private static LootAction lootAction;


	@Inject
	private void setLogger(LootAction lootAction) {
		Brawlator.lootAction = lootAction;
	}
	public static LootAction getLootAction() {
		return lootAction;
	}

	private static Optional<IItemService> itemService;
	public static Optional<IItemService> getItemService(){ return itemService;}


	@Listener
	public void onServerStart(GameStartedServerEvent event) {
		brawlator = this;
		logger.info("Brawlator plugin initialized.");
		Sponge.getEventManager().registerListeners(this, new BrawlatorListener());


		if(Sponge.getPluginManager().getPlugin("Itemizer").isPresent()) {
			Optional itemServiceOptional = Sponge.getServiceManager().provide(IItemService.class);
			if (itemServiceOptional.isPresent()) {
				itemService = itemServiceOptional;
			} else {
				itemService = Optional.empty();
				logger.warn("Itemizer dependency not found");
			}
		} else {
			itemService = Optional.empty();
			logger.warn("Itemizer dependency not found");
		}

		try {
			logger.info("Monsters loaded : " + loadMonsters());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		try {
			logger.info("Spaners loaded : " + loadSpawners());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		try {
			logger.info("Loot tables loaded : " + loadLootTables());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}


		CommandSpec invoke = CommandSpec.builder()
				.description(Text.of("Invoke a monster whose id is registered into the database"))
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("id"))))
				.executor(new InvokeCommand()).build();
		Sponge.getCommandManager().register(this, invoke, "invoke");

		CommandSpec monsterSelect = CommandSpec.builder()
				.permission("ylinor.brawlator.database_read")
				.description(Text.of("Select and print a monster from database"))
				.arguments(GenericArguments.onlyOne(GenericArguments.integer(Text.of("id"))))
				.executor(new SelectMonsterCommand()).build();

		CommandSpec monsterDatabase = CommandSpec.builder()
				.description(Text.of("Query database about monsters"))
				.child(monsterSelect, "select").build();
		Sponge.getCommandManager().register(this, monsterDatabase, "monsters");

		CommandSpec reload = CommandSpec.builder()
				.description(Text.of("Invoke a monster whose id is registered into the database"))
				.permission("ylinor.brawlator.administration")
				.executor(new ReloadCommand()).build();
		Sponge.getCommandManager().register(this, reload, "reload-brawlator");

		 Task.builder().execute(()-> spawnerAction.updateSpawner()).delay(20, TimeUnit.SECONDS)
				.interval(10,TimeUnit.SECONDS).name("Spawn monster").submit(this);
	}

	/**
	 * Listener of spawner
	 * @param event event rised
	 */
	@Listener
	public void onSpawnEvent(SpawnEvent event){

				try {
					Optional<Entity> monster = monsterAction.invokeMonster(getWorld("world").getLocation(event.getSpawnerBean().getPosition()), event.getSpawnerBean().getMonsterBean());
					if(!monster.isPresent()){
						getLogger().error("monster invocation failed");
					}
				} catch (Exception e) {
					getLogger().warn(e.getMessage());
				}
		}

	/**
	 * Get the plugin instance
	 * @return the instance
	 */
	public static PluginContainer getInstance() throws PluginNotFoundException {
		return  Sponge.getPluginManager().getPlugin("brawlator").orElseThrow(() -> new PluginNotFoundException("brawlator"));
	}

	/**
	 * Get the current world
	 * @return the world
	 */
	public static World getWorld(String WorldName) throws WorldNotFoundException {
		Optional<World> worldOptional = Sponge.getServer().getWorld(WorldName);
		return worldOptional.orElseThrow(() -> new WorldNotFoundException(WorldName));
	}

	public int loadMonsters() throws Exception {
		initDefaultConfig("monster.conf");
		return configurationHandler.setMonsterList(configurationHandler.loadConfiguration(configDir + "/brawlator/monster.conf"));
	}

	public int loadSpawners() throws Exception {
		initDefaultConfig("spawner.conf");
		return configurationHandler.setSpawnerList(configurationHandler.loadConfiguration(configDir + "/brawlator/spawner.conf"));
	}

	public int loadLootTables() throws Exception {
		initDefaultConfig("loot_table.conf");
		return configurationHandler.setLootTableList(configurationHandler.loadConfiguration(configDir + "/brawlator/loot_table.conf"));
	}

	public void initDefaultConfig(String path){
		if (Files.notExists(Paths.get(configDir+ "/brawlator/" + path))) {
			PluginContainer pluginInstance = null;
			try {
				pluginInstance = getInstance();


			if (pluginInstance!= null) {
				Optional<Asset> itemsDefaultConfigFile = pluginInstance.getAsset(path);
				getLogger().info("No config file set for " + path + " default config will be loaded");
				if (itemsDefaultConfigFile.isPresent()) {
					try {
						itemsDefaultConfigFile.get().copyToDirectory(Paths.get(configDir+"/brawlator/"));
					} catch (IOException e) {
						Itemizer.getLogger().error("Error while setting default configuration : " + e.getMessage());
					}
				} else {
					logger.warn("Item default config not found");
				}
			}
			} catch (PluginNotFoundException e) {
				Brawlator.getLogger().error(e.toString());
			}
		}
	}

}
