package gomapservice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.github.aeonlucid.pogoprotos.GoMap.EncounterData;
import com.relayrides.pushy.apns.ApnsClient;
import com.relayrides.pushy.apns.ApnsClientBuilder;
import com.relayrides.pushy.apns.util.ApnsPayloadBuilder;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;

import io.netty.util.concurrent.Future;

public class GoMapApnsEndpoint {
	
	private static final String BUNDLE_IDENTIFIER = "com.kerr.GoMap";

	private ApnsClient client;

	public GoMapApnsEndpoint(File certificate, String password) {
		try {
			client = new ApnsClientBuilder().setClientCredentials(certificate, password).build();
			final Future<Void> connectFuture = client.connect(ApnsClient.PRODUCTION_APNS_HOST);
			connectFuture.await();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void pushEncounter(EncounterData data, ArrayList<String> pushTokens) {
		final ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();
		
		long expirationTime = (data.getExpirationTime() - System.currentTimeMillis()) / 1000;
		String soundFile = Util.soundFileForPokemonId(data.getPokemonId());
		payloadBuilder.setSoundFileName(soundFile);
		payloadBuilder.setAlertBody(Util.nameForPokemonId(data.getPokemonId())
				+ ":\nTime Remaining: " + (expirationTime / 60) + ":" + String.format("%02d", expirationTime % 60));
	    final String payload = payloadBuilder.buildWithDefaultMaximumLength();
	    for (String token : pushTokens) {
	    	pushPayload(token, payload);
	    }
	}
	
	private void pushPayload(String token, String payload) {
		SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, BUNDLE_IDENTIFIER, payload);
	    client.sendNotification(pushNotification);
		System.out.println(token);
	}
}
