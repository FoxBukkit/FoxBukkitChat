package de.doridian.yiffbukkit.chatcomponent;

import de.doridian.yiffbukkit.chatcomponent.config.Configuration;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisHandler extends JedisPubSub implements Runnable {
	@Override
	public void run() {
		RedisManager.readJedisPool.getResource().subscribe(this, "yiffbukkit:to_server");
	}

	public static void sendMessage(final Player player, final String  message) {
		if(player == null || message == null)
			throw new NullPointerException();
		final Jedis jedis = RedisManager.readJedisPool.getResource();
		jedis.publish("yiffbukkit:from_server", Configuration.getValue("server-name", "Main") + "|" + player.getName() + "|" + message);
		RedisManager.readJedisPool.returnResource(jedis);
	}

	public static void initialize() {
		new Thread(new RedisHandler()).start();
	}

	@Override
	public void onMessage(final String channel, final String c_message) {
		try {
			final String[] split = c_message.split("\\|", 3);

			// SERVER\0 USER\0 message
			final String server = split[0];
			@SuppressWarnings("UnusedDeclaration")
			final String userName = split[1];
			String format = split[2];

			if (!server.equals(Configuration.getValue("server-name", "Main"))) {
				format = "\u00a72[" + server + "]\u00a7f " + format;
			}

			YBChatComponent.instance.getServer().broadcastMessage(format);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPMessage(String s, String s2, String s3) {

	}

	@Override
	public void onSubscribe(String s, int i) {

	}

	@Override
	public void onUnsubscribe(String s, int i) {

	}

	@Override
	public void onPUnsubscribe(String s, int i) {

	}

	@Override
	public void onPSubscribe(String s, int i) {

	}
}