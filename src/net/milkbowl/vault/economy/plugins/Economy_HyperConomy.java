package net.milkbowl.vault.economy.plugins;

import com.iCo6.system.Holdings;
import cosine.boseconomy.BOSEconomy;
import java.util.List;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import regalowl.hyperconomy.Account;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconAPI;

public class Economy_HyperConomy implements Economy {

	private static final Logger log = Logger.getLogger("Minecraft");
	private String name = "HyperConomy";
	private Plugin plugin = null;
	private HyperConomy economy = null;
	//private Account account;
	private HyperEconAPI api;

	public Economy_HyperConomy(Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new Economy_HyperConomy.EconomyServerListener(this), plugin);
		if (economy == null) {
			Plugin hyperconomy = plugin.getServer().getPluginManager().getPlugin("HyperConomy");
			if (hyperconomy != null && hyperconomy.isEnabled() && hyperconomy.getDescription().getVersion().startsWith("0.9")) {
				economy = (HyperConomy) hyperconomy;
				api = HyperConomy.hyperEconAPI;
				//account = new Account();
				log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
			}
		}
	}

	@Override
	public boolean isEnabled() {
		if (economy == null) {
			return false;
		} else {
			return economy.isEnabled();
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasBankSupport() {
		return false;
	}

	@Override
	public int fractionalDigits() {
		return economy.getEconomy().fractionalDigits();
	}

	@Override
	public String format(double amount) {
		return amount;
	}

	@Override
	public String currencyNamePlural() {
		return "";
	}

	@Override
	public String currencyNameSingular() {
		return "";
	}

	@Override
	public boolean hasAccount(String playerName) {
		return api.checkAccount(playerName);
	}

	@Override
	public boolean hasAccount(String playerName, String worldName) {
		return api.checkAccount(playerName);
	}

	@Override
	public double getBalance(String playerName) {
		return api.getBalance(playerName);
	}

	@Override
	public double getBalance(String playerName, String world) {
		return getBalance(playerName);
	}

	@Override
	public boolean has(String playerName, double amount) {
		return getBalance(playerName) >= amount;
	}

	@Override
	public boolean has(String playerName, String worldName, double amount) {
		return has(playerName, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
		}

		if (api.checkFunds(amount, playerName)) {
			api.withdrawAccount(amount, playerName);
			return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
		} else {
			return new EconomyResponse(0, getBalance(playerName), EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
		return withdrawPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount) {
		if (amount < 0) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot desposit negative funds");
		}

		if (hasAccount(playerName)) {
			return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player doesn't have an account.");
		}

		api.depositAccount(amount, playerName);

		return new EconomyResponse(amount, getBalance(playerName), EconomyResponse.ResponseType.SUCCESS, null);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
		return depositPlayer(playerName, amount);
	}

	@Override
	public EconomyResponse createBank(String name, String player) {
		throw new UnsupportedOperationException("Vault-HyperEcon doesn't support banks yet");
	}

	@Override
	public EconomyResponse deleteBank(String name) {
		throw new UnsupportedOperationException("Vault-HyperEcon doesn't support banks yet");
	}

	@Override
	public EconomyResponse bankBalance(String name) {
		throw new UnsupportedOperationException("Vault-HyperEcon doesn't support banks yet");
	}

	@Override
	public EconomyResponse bankHas(String name, double amount) {
		throw new UnsupportedOperationException("Vault-HyperEcon doesn't support banks yet");
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount) {
		throw new UnsupportedOperationException("Vault-HyperEcon doesn't support banks yet");
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount) {
		throw new UnsupportedOperationException("Vault-HyperEcon doesn't support banks yet");
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName) {
		throw new UnsupportedOperationException("Vault-HyperEcon doesn't support banks yet");
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName) {
		throw new UnsupportedOperationException("Vault-HyperEcon doesn't support banks yet");
	}

	@Override
	public List<String> getBanks() {
		throw new UnsupportedOperationException("Vault-HyperEcon doesn't support banks yet");
	}

	@Override
	public boolean createPlayerAccount(String playerName) {
		return api.createAccount(playerName);
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName) {
		return createPlayerAccount(playerName);
	}

	public class EconomyServerListener implements Listener {

		Economy_HyperConomy economy = null;

		public EconomyServerListener(Economy_HyperConomy economy) {
			this.economy = economy;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			if (economy.economy == null) {
				Plugin hyperconomy = event.getPlugin();

				if (hyperconomy.getDescription().getName().equals("HyperConomy")) {
					economy.economy = (HyperConomy) hyperconomy;
					api = HyperConomy.hyperEconAPI;
					log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) {
			if (economy.economy != null) {
				if (event.getPlugin().getDescription().getName().equals("HyperConomy")) {
					economy.economy = null;
					api = null;
					log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
				}
			}
		}
	}
}
